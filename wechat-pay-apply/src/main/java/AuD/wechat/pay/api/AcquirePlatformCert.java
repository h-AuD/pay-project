package AuD.wechat.pay.api;

import AuD.wechat.pay.core.constant.WeChatPayApiList;
import AuD.wechat.pay.core.model.PlatformCertData;
import com.dtflys.forest.annotation.Get;
import lombok.Data;

import java.util.List;

/**
 * Description: TODO
 *
 * @author AuD/胡钊
 * @ClassName AcquirePlatformCert
 * @date 2021/5/12 17:10
 * @Version 1.0
 */
public interface AcquirePlatformCert {

    @Get(url = WeChatPayApiList.PCL,headers = {"Accept: application/json","Authorization:${authMsg}"})
    public ResultDataFromCertApi getCertList(String authMsg);

    /** 以下是获取平台证书返回的数据结构 */
    @Data
    public class ResultDataFromCertApi{
        private List<PlatformCertData> data;
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
