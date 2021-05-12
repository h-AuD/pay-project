package AuD.wechat.pay.core.api;

import AuD.wechat.pay.core.constant.WeChatPayApiList;
import com.alibaba.fastjson.annotation.JSONField;
import com.dtflys.forest.annotation.Get;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Description: TODO
 *
 * @author AuD/胡钊
 * @ClassName WeChatPlatformCertApi
 * @date 2021/5/12 17:10
 * @Version 1.0
 */
public interface WeChatPlatformCertApi {

    @Get(url = WeChatPayApiList.PLATFORM_CERT_LIST, headers = {"Accept: application/json","Authorization:${authMsg}"})
    public ResultDataFromCertApi downloadCert(String authMsg);

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
        @JSONField(name = "effective_time ")
        private LocalDateTime effectiveTime;
        @JSONField(name = "expire_time ")
        private LocalDateTime expireTime;
        @JSONField(name = "encrypt_certificate")
        private EncryptCertificate encryptCertificate;
    }

    /** TODO 注意:不仅仅是平台证书内容,也是回调报文的内容 */
    @Data
    public class EncryptCertificate{
        //
        private String ciphertext;
        @JSONField(name = "associated_data")
        private String associatedData;
        private String nonce;
        private String algorithm;

    }

/* data-demo:
{
  "data": [
      {
          "serial_no": "5157F09EFDC096DE15EBE81A47057A7232F1B8E1",
          "effective_time ": "2018-06-08T10:34:56+08:00",
          "expire_time ": "2018-12-08T10:34:56+08:00",
          "encrypt_certificate": {
              "algorithm": "AEAD_AES_256_GCM",
              "nonce": "61f9c719728a",
              "associated_data": "certificate",
              "ciphertext": "sRvt… "
          }
      },
      {
          "serial_no": "50062CE505775F070CAB06E697F1BBD1AD4F4D87",
          "effective_time ": "2018-12-07T10:34:56+08:00",
          "expire_time ": "2020-12-07T10:34:56+08:00",
          "encrypt_certificate": {
              "algorithm": "AEAD_AES_256_GCM",
              "nonce": "35f9c719727b",
              "associated_data": "certificate",
              "ciphertext": "aBvt… "
          }
      }
  ]
}
*/

}
