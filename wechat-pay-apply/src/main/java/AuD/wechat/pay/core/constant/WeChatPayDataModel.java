package AuD.wechat.pay.core.constant;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Description: TODO
 *
 * @author AuD/胡钊
 * @ClassName WeChatPayDataModel
 * @date 2021/5/13 15:29
 * @Version 1.0
 */
public class WeChatPayDataModel {

    /** 以下是获取平台证书返回的数据结构 */
    @Data
    public class ResultDataFromCertApi{
        private List<CertData> data;
    }

    @Data
    public class CertData{
        /** 解密后的证书内容 */
        private String certContext;
        @JSONField(name = "serial_no")
        private String serialNo;
        @JSONField(name = "effective_time")
        private LocalDateTime effectiveTime;
        @JSONField(name = "expire_time")
        private LocalDateTime expireTime;
        @JSONField(name = "encrypt_certificate")
        private EncryptData encryptCertificate;
    }

    /**
     * 加密内容:是平台证书内容,也是回调报文的内容
     * 使用的是 APIv3key 解密
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


}
