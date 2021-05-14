package AuD.wechat.pay.core.function.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * Description: 加密内容:微信平台证书内容,微信支付回调报文的内容
 * 使用的是 APIv3key 解密
 *
 * @author AuD/胡钊
 * @ClassName EncryptData
 * @date 2021/5/13 17:49
 * @Version 1.0
 */
@Data
public class EncryptData {
    //
    private String ciphertext;
    @JSONField(name = "associated_data")
    private String associatedData;
    private String nonce;
    private String algorithm;

}
    /* demo
    {
        "original_type":"transaction", // 加密前的对象类型
        "algorithm":"AEAD_AES_256_GCM", // 加密算法
        // Base64编码后的密文
        "ciphertext":"...",
        // 加密使用的随机串初始化向量）
         "nonce":"...",
        // 附加数据包（可能为空）
         "associated_data":""
    }
     */

