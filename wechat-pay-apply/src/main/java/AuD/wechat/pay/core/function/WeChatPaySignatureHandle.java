package AuD.wechat.pay.core.function;

import AuD.wechat.pay.core.function.model.SignatureInfoModel;
import AuD.wechat.pay.core.util.WeChatPayUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * Description: 调用微信支付API时,所需要的认证相关操作。
 * 1.签名 - signature
 * - 构造签名串
 * {@link WeChatPaySignatureHandle#evalReqSignature(SignatureInfoModel, java.security.PrivateKey)}
 * - 构建请求头中的签名信息
 * {@link WeChatPaySignatureHandle#buildAuthorization(SignatureInfoModel, java.security.PrivateKey)}
 * -
 * -- 验证微信端应答签名
 * @author AuD/胡钊
 * @ClassName WeChatPaySignatureHandle
 * @date 2021/5/11 15:56
 * @Version 1.0
 */
@Slf4j
public class WeChatPaySignatureHandle {

    /** 换行符 */
    private final static String NEW_LINE = "\n";

    /**======================================================
     * 以下是请求头中的认证(Authorization)属性所需要的常量
     * 参考文档:https://pay.weixin.qq.com/wiki/doc/apiv3/wechatpay/wechatpay4_0.shtml
     *======================================================*/
    private final static String AUTH_TYPE = "WECHATPAY2-SHA256-RSA2048";  // 认证类型

    /**
     * 构建请求头中的签名信息
     * 形如:(属性名 - Authorization)
     * WECHATPAY2-SHA256-RSA2048 mchid="xx",nonce_str="xx",signature="xx",timestamp="xx",serial_no="xx"
     * 注:以上五项签名信息,无顺序要求,nonce_str,timestamp是参与签名的随机字符串和时间戳
     *
     * @param signatureInfoModel
     * @param privateKey
     * @return
     */
    public static String buildAuthorization(SignatureInfoModel signatureInfoModel,PrivateKey privateKey){
        evalReqSignature(signatureInfoModel,privateKey);
        return AUTH_TYPE+"  "+signatureInfoModel.buildSignatureAuth();
    }

    /**
     * 验证微信端回调请求签名
     * @param signatureInfoModel
     * @param certificate
     * @return
     */
    public static boolean verifyWeChatResponse(SignatureInfoModel signatureInfoModel, X509Certificate certificate){
        String respSignature = signatureInfoModel.getSignature();
        if(StringUtils.hasText(respSignature)){
            return false;
        }
        return WeChatPayUtils.verifySign(certificate,evalRespSignature(signatureInfoModel),respSignature);
    }

    /**
     * 构造请求签名串,这里通过封装了{@link SignatureInfoModel} 对象保存签名相关信息
     * 签名串规范:签名串一共有五行,每一行为一个参数.行尾以 "\n" (换行符)结束,包括最后一行.如果参数本身以\n结束,也需要附加一个\n。
     * 1.获取HTTP请求的方法(GET,POST,PUT)等
     * 2.获取请求的绝对URL,并去除域名部分得到参与签名的URL.如果请求中有查询参数,URL末尾应附加有'?'和对应的查询字符串。
     * 3.获取发起请求时的系统当前时间戳(单位秒)。-- 微信支付会拒绝处理很久之前发起的请求
     * 4.生成一个请求随机串
     * 5.获取请求中的请求报文主体(request body) -- 若请求没有body(eg.GET),直接\n
     * ---- 以 " www.xxx.com/abc/?v1=v1&v2=v2 " GET请求为例:签名串如下
     * GET\n
     * /abc/?v1=v1&v2=v2\n
     * 1554208460\n
     * 593BEC0C930BF1AFEB40B4A08C8FB242\n
     * \n
     * @param signatureInfoModel
     * @param privateKey    - 商户API证书密钥
     * @return
     */
    private static String evalReqSignature(SignatureInfoModel signatureInfoModel,PrivateKey privateKey){
        String signatureMessage = signatureInfoModel.getRequestMethod() + NEW_LINE
                + signatureInfoModel.getRequestUri() + NEW_LINE
                + signatureInfoModel.getTimestamp() + NEW_LINE
                + signatureInfoModel.getNonceStr() + NEW_LINE;
        final String body = signatureInfoModel.getBody();
        signatureMessage = StringUtils.hasText(body) ? (signatureMessage + body + NEW_LINE) : (signatureMessage+ NEW_LINE);
        final String signature = WeChatPayUtils.sign(signatureMessage, privateKey);
        signatureInfoModel.setSignature(signature);
        return signature;
    }

    /**
     * 构造验签名串
     *
     * @param signatureInfoModel
     * @return
     */
    private static String evalRespSignature(SignatureInfoModel signatureInfoModel){
        String signatureMessage = signatureInfoModel.getTimestamp() + NEW_LINE
                + signatureInfoModel.getNonceStr() + NEW_LINE;
        final String body = signatureInfoModel.getBody();
        signatureMessage = StringUtils.hasText(body) ? (signatureMessage + body + NEW_LINE) : (signatureMessage+ NEW_LINE);
        return signatureMessage;
    }


}
