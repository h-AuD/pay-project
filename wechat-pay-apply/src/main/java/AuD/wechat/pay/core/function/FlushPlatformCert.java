package AuD.wechat.pay.core.function;

import AuD.wechat.pay.api.AcquirePlatformCert;
import AuD.wechat.pay.core.constant.SignatureAuthConstant;
import AuD.wechat.pay.core.constant.WeChatPayApiList;
import AuD.wechat.pay.core.constant.WeChatPayDataModel;
import AuD.wechat.pay.core.function.auth.SignatureInfoModel;
import AuD.wechat.pay.core.function.auth.WeChatPayAuthHandle;
import AuD.wechat.pay.core.function.component.WeChatCertInfo;
import AuD.wechat.pay.core.util.WeChatPayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: TODO
 *
 * @author AuD/胡钊
 * @ClassName FlushPlatformCert
 * @date 2021/5/13 15:21
 * @Version 1.0
 */
@Component
public class FlushPlatformCert {

    private AcquirePlatformCert platformCertList;

    private Map<String, X509Certificate> platformCert = new ConcurrentHashMap<>();

    @Autowired
    private WeChatCertInfo certInfo;


    FlushPlatformCert(AcquirePlatformCert platformCertList){
        this.platformCertList = platformCertList;
    }

    /** 刷新平台证书 */
    public Map<String, X509Certificate> flushCert(){
        final SignatureInfoModel signatureInfoModel = SignatureInfoModel.of(SignatureAuthConstant.MCH_ID,SignatureAuthConstant.PRIVATE_SERIAL_NO,SignatureAuthConstant.GET);
        signatureInfoModel.setRequestUri(WeChatPayApiList.PCL_MAPPING);
        final WeChatPayDataModel.ResultDataFromCertApi resultData = platformCertList.getCertList(WeChatPayAuthHandle.buildAuthorization(signatureInfoModel,certInfo.getApiPrivateKey()));
        resultData.getData().forEach((certData -> {
            final String certContextData = WeChatPayUtils.decryptCertAndCallBody(certData.getEncryptCertificate());
            if(StringUtils.hasText(certContextData)){
                final Optional<X509Certificate> certificate = WeChatPayUtils.loadCertificate(new ByteArrayInputStream(certContextData.getBytes()));
                platformCert.put(certData.getSerialNo(),certificate.orElse(null));
            }
        }));

        return platformCert;
    }


}
