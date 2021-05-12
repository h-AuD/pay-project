package AuD.wechat.pay.core.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
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
 *
 * @author AuD/胡钊
 * @ClassName WeChatPayUtils
 * @date 2021/5/12 11:24
 * @Version 1.0
 */
@Slf4j
public class WeChatPayUtils {


    /**
     * 封装了计算签名逻辑,使用密钥计算待签名的文本内容
     *
     * @param message   带签名的信息内容
     * @param privateKeyPath  密钥文件路径(使用绝对路径)
     *                  -- 注:请求签名和验证签名使用的密钥是不一样的.分别是商户证书中的私钥,平台证书中的公钥.
     * @return
     */
    public static Optional<String> sign(String message,String privateKeyPath) {
        String signature = null;
        try {
            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initSign(getPrivateKey(privateKeyPath).get());
            sign.update(message.getBytes());
            // Base64编码
            signature = Base64.getEncoder().encodeToString(sign.sign());
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException | IOException e) {
            log.error("signature fail,error-info is:{}",e.getMessage());
        }
        return Optional.ofNullable(signature);
    }

    /**
     *
     * @param certPath
     * @param message
     * @param signature
     * @return
     */
    public static boolean verifySign(String certPath, String message, String signature) {
        boolean result = false;
        final X509Certificate certificate = loadCertificate(certPath).get();
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
     * 获取私钥。
     *
     * @param privateKeyPath 私钥文件路径  (required)
     * @return 私钥对象
     */
    private static Optional<PrivateKey> getPrivateKey(String privateKeyPath) throws IOException {
        PrivateKey privateKey = null;
        String content = new String(Files.readAllBytes(Paths.get(privateKeyPath)), "utf-8");
        try {
            String privateKeyContent = content.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            KeyFactory kf = KeyFactory.getInstance("RSA");
            privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent)));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("generation privateKey fail,error-info is:{}",e.getMessage());
        }
        return Optional.ofNullable(privateKey);
    }

    /**
     * 加载平台证书   // TODO 存在问题:平台证书需要定期更换,并且通过API获取证书列表,使用certPath不合适
     * @param certPath
     * @return
     */
    private static Optional<X509Certificate> loadCertificate(String certPath) {
        X509Certificate cert = null;
        try(InputStream inputStream = new FileInputStream(certPath)) {
            CertificateFactory cf = CertificateFactory.getInstance("X509");
            cert = (X509Certificate) cf.generateCertificate(inputStream);
            cert.checkValidity();
        } catch (CertificateException | IOException e) {
            log.error("load certificate fail,error-info is:{}",e.getMessage());
        }
        return Optional.ofNullable(cert);
    }

}
