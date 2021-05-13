package AuD.wechat.pay.core.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * Description: 加密内容:是平台证书内容,也是回调报文的内容
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