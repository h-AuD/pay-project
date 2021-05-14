package AuD.wechat.pay.core.function.component;

import AuD.wechat.pay.api.AcquirePlatformCert;
import AuD.wechat.pay.core.constant.SignatureAuthConstant;
import AuD.wechat.pay.core.constant.WeChatPayApiList;
import AuD.wechat.pay.core.function.WeChatPaySignatureHandle;
import AuD.wechat.pay.core.function.model.SignatureInfoModel;
import AuD.wechat.pay.core.function.component.WeChatCertInfo;
import AuD.wechat.pay.core.util.WeChatPayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.security.cert.X509Certificate;
import java.util.Map;
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

    /** 使用Spring默认注入方式(i.e. 构造器注入) */
    FlushPlatformCert(AcquirePlatformCert platformCertList){
        this.platformCertList = platformCertList;
    }

    /**
     * 刷新平台证书 ==== 自动刷新 & 手动刷新
     * 1.手动刷新:在验签步骤中,需要先判断是否拥有微信支付回调请求头中的证书序列号
     * 2.自动刷新:在敏感信息加密中需要用到证书,但是考虑到证书是否有效,所以存在手动刷新的情况
     */
    public Map<String, X509Certificate> flushCert(){
        final SignatureInfoModel signatureInfoModel = SignatureInfoModel.of(SignatureAuthConstant.MCH_ID,SignatureAuthConstant.PRIVATE_SERIAL_NO,SignatureAuthConstant.GET);
        signatureInfoModel.setRequestUri(WeChatPayApiList.PCL_MAPPING);
        final AcquirePlatformCert.ResultDataFromCertApi resultData = platformCertList.getCertList(WeChatPaySignatureHandle.buildAuthorization(signatureInfoModel,certInfo.getApiPrivateKey()));
        resultData.getData().forEach((certData -> {
            final String certContextData = WeChatPayUtils.decryptCertAndCallBody(certData.getEncryptCertificate());
            X509Certificate certificate = null;
            if(StringUtils.hasText(certContextData) && (certificate = WeChatPayUtils.loadCertificate(new ByteArrayInputStream(certContextData.getBytes())))!=null){
                //X509Certificate certificate = WeChatPayUtils.loadCertificate(new ByteArrayInputStream(certContextData.getBytes()));
                platformCert.put(certData.getSerialNo(),certificate);
            }
        }));

        return platformCert;
    }


}
