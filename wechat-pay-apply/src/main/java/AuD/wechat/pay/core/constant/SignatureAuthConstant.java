package AuD.wechat.pay.core.constant;

/**
 * Description: 签名认证相关常量
 *
 * @author AuD/胡钊
 * @ClassName SignatureAuthConstant
 * @date 2021/5/11 19:45
 * @Version 1.0
 */
public interface SignatureAuthConstant {

    int RANDOM_LEN = 16;
    int TAG_LENGTH_BIT = 128;
    /** 商户号 */
    String MCH_ID = "";
    /** 商户证书 */
    String PRIVATE_KEY_PATH = "";
    /** API_key,由商户在微信商户平台设置 */
    String WC_API_KEY = "";
    /** 商户证书序列号 */
    String PRIVATE_SERIAL_NO = "";

    /* ============= 以下是微信端请求头属性 =============== */
    String WCP_TIMESTAMP = "Wechatpay-Timestamp";
    String WCP_NONCE = "Wechatpay-Nonce";
    String WCP_SERIAL = "Wechatpay-Serial";
    String WCP_SIGNATURE = "Wechatpay-Signature";

}
