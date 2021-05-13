package AuD.wechat.pay.core.util;

import AuD.wechat.pay.core.constant.SignatureAuthConstant;
import AuD.wechat.pay.core.constant.WeChatPayDataModel;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Optional;

/**
 * 提供如下function:
 * 1.加载微信支付商户证书私钥 & 加载微信平台证书
 * 2.签名 & 验证
 * 3.平台证书解密
 * 4.敏感信息加解密
 * -----------------------------
 * ps:{@link WeChatPayUtils}中function参考自微信方提供的SDK
 *
 * @author AuD/胡钊
 * @ClassName WeChatPayUtils
 * @date 2021/5/12 11:24
 * @Version 1.0
 */
@Slf4j
public class WeChatPayUtils {

    /**======================================== 签名相关内容 ===================================
     * 1.{@link WeChatPayUtils#sign(java.lang.String, java.lang.String)}
     * - 签名方法,将message内容进行签名运算,并加以base64编码
     *
     * 3.{@link WeChatPayUtils#getPrivateKey(java.lang.String)}
     * - 获取商户证书的私钥
     *
     *=======================================================================================*/

    /**
     * 计算签名 === 使用商户API证书密钥计算待签名的文本内容
     * - 注:签名和验签使用的密钥是不一样的.分别是商户API证书中的私钥,平台证书中的公钥.
     * @param message   带签名的信息内容
     * @param privateKey  商户API证书密钥
     * @return
     */
    public static String sign(String message,PrivateKey privateKey) {
        String signature = null;
        try {
            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initSign(privateKey);
            sign.update(message.getBytes());
            // Base64编码
            signature = Base64.getEncoder().encodeToString(sign.sign());
        } catch (Exception e) {
            log.error("signature fail,error-info is:{}",e.getMessage());
        }
        return signature==null? "" : signature;
    }

    /**
     * 验证微信支付(即WeChat发送的请求)中的签名信息,使用的是微信平台证书
     *
     * @param certificate   平台证书
     * @param message   需要签名的信息,由三部分组成:应答时间戳、应答随机串、应答报文主体
     * @param signature 待验证的签名信息
     * @return
     */
    public static boolean verifySign(X509Certificate certificate, String message, String signature) {
        boolean result = false;
        try {
            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initVerify(certificate);
            sign.update(message.getBytes());
            result = sign.verify(Base64.getDecoder().decode(signature));
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            log.error("signature verify fail,error-info is:{}",e.getMessage());
        }
        return result;
    }

    /**
     * 获取商户证书的私钥。
     * -- 用于API签名和敏感数据加密
     * @param privateKeyPath 私钥文件路径  (required)
     * @return 私钥对象
     */
    public static Optional<PrivateKey> getPrivateKey(String privateKeyPath){
        PrivateKey privateKey = null;
        try {
            String content = new String(Files.readAllBytes(Paths.get(privateKeyPath)), "utf-8");
            String privateKeyContent = content.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            KeyFactory kf = KeyFactory.getInstance("RSA");
            privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent)));
        } catch (Exception e) {
            log.error("generation privateKey fail,error-info is:{}",e.getMessage());
        }
        return Optional.ofNullable(privateKey);
    }

    /**
     * 加载平台证书
     *
     * @param certIn    平台证书的输入流,推荐使用ByteArrayInputStream
     * @return
     */
    public static Optional<X509Certificate> loadCertificate(InputStream certIn) {
        X509Certificate cert = null;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X509");
            cert = (X509Certificate) cf.generateCertificate(certIn);
            cert.checkValidity();
        } catch (CertificateException e) {
            log.error("load certificate fail,error-info is:{}",e.getMessage());
        }
        return Optional.ofNullable(cert);
    }

    /*======================================== 分割线 ===================================*/

    /**==============================================================================
     *
     *===============================================================================*/

    /**
     * 解密平台证书文本内容 & 回调报文
     * - call-API(下载证书)返回的数据(resultData) 包含加密数据,解密cert内容需要使用到 APIkey(商户自行设置的)
     * - 参数都来自resultData
     * @return
     */
    public static String decryptCertAndCallBody(WeChatPayDataModel.EncryptData encryptData){
        String result = null;
        try {
            String associatedData = encryptData.getAssociatedData();
            String nonce = encryptData.getNonce();
            String ciphertext = encryptData.getCiphertext();
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKeySpec key = new SecretKeySpec(SignatureAuthConstant.WC_API_KEY.getBytes(), "AES");
            GCMParameterSpec spec = new GCMParameterSpec(SignatureAuthConstant.TAG_LENGTH_BIT, nonce.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, key, spec);
            cipher.updateAAD(associatedData.getBytes());
            result = new String(cipher.doFinal(Base64.getDecoder().decode(ciphertext)), "utf-8");
        } catch (Exception e) {

        }
        return result==null? "" : result;
    }

    /**
     *
     * @param message
     * @return
     */
    public static String encryptOAEP(String message,X509Certificate certificate) {
        String result = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, certificate.getPublicKey());
            byte[] data = message.getBytes(StandardCharsets.UTF_8);
            byte[] ciphertext = cipher.doFinal(data);
            result = Base64.getEncoder().encodeToString(ciphertext);
        } catch (Exception e) {

        }
        return result==null? "" : result;
    }

    public static String decryptOAEP(String ciphertext, PrivateKey privateKey){
        String result = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] data = Base64.getDecoder().decode(ciphertext);
            result = new String(cipher.doFinal(data), StandardCharsets.UTF_8);
        } catch (Exception e) {

        }
        return result==null? "" : result;
    }

}
