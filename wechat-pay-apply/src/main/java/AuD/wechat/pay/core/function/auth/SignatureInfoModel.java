package AuD.wechat.pay.core.function.auth;

import AuD.component.common.utils.RandomStrUtil;
import AuD.wechat.pay.core.constant.SignatureAuthConstant;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Description: 微信签名信息模型
 *
 * @author AuD/胡钊
 * @ClassName SignatureInfoModel
 * @date 2021/5/11 16:21
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
public class SignatureInfoModel {

    /* 以下用于计算签名所需要的属性 */

    /** 请求方法,eg.GET、POST等等 */
    private String requestMethod;
    /** 请求的URI,eg.请求路径为 "www.xxx.com/abc/?a=v" 则URI=/abc/?a=v */
    private String requestUri;
    /** 发送请求的时间戳,单位秒 */
    private long timestamp;
    /** 随机字符串 === 使用随机算法生成 */
    private String nonceStr;
    /** 请求体内容,GET请求为null */
    private String body;

    /** 签名结果值 */
    private String signature;
    /** 商户私钥,证书序列号 */
    private String serialNo;
    /** 发起请求的商户（包括直连商户、服务商或渠道商）的商户号 */
    private String mchId;

    /**
     * 私有化构造器,禁止new 对象,
     * 使用{@link SignatureInfoModel#of()}构建对象
     */
    private SignatureInfoModel(){

    }

    /**
     * mchId & serialNo 属于常量,传入这2个参数,仅仅为了方便构建API请求体的认证信息
     * 即,若想要构建Authorization信息,使用 {@link WeChatPayAuthHandle#buildAuthorization(AuD.wechat.pay.core.function.auth.SignatureInfoModel, java.security.PrivateKey)}
     *
     * @return
     */
    public static SignatureInfoModel of(String mchId,String serialNo,String requestMethod){
        SignatureInfoModel signatureInfoModel = of()
                .setRequestMethod(requestMethod)
                .setMchId(mchId)
                .setSerialNo(serialNo)
                .setNonceStr(RandomStrUtil.ofStr(SignatureAuthConstant.RANDOM_LEN))
                .setTimestamp((System.currentTimeMillis()/1000));
        return signatureInfoModel;
    }

    /**
     * 构建应答签名信息
     * @return
     */
    public static SignatureInfoModel of(){
        SignatureInfoModel signatureInfoModel = new SignatureInfoModel();
        return signatureInfoModel;
    }

    /** 构建认证部分信息 */
    public String buildSignatureAuth() {
        return "timestamp=\"" + timestamp + "\""+
                ", nonce_str=\"" + nonceStr + "\""+
                ", signature=\"" + signature + "\""+
                ", serial_no=\"" + serialNo + "\""+
                ", mchid=\"" + mchId + "\"";
    }



}
