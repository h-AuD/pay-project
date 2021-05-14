package AuD.wechat.pay.core.function.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.security.cert.X509Certificate;
import java.time.LocalDateTime;

/**
 * Description: TODO
 *
 * @author AuD/胡钊
 * @ClassName PlatformCertData
 * @date 2021/5/13 17:51
 * @Version 1.0
 */
@Data
public class PlatformCertData {

    @JSONField(name = "serial_no")
    private String serialNo;
    @JSONField(name = "effective_time")
    private LocalDateTime effectiveTime;
    @JSONField(name = "expire_time")
    private LocalDateTime expireTime;
    @JSONField(name = "encrypt_certificate")
    private EncryptData encryptCertificate;
    /** 平台证书对象 */
    private X509Certificate certificate;

}
