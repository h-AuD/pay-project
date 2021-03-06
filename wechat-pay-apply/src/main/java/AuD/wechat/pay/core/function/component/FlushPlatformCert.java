package AuD.wechat.pay.core.function.component;

import AuD.wechat.pay.api.AcquirePlatformCert;
import AuD.wechat.pay.core.constant.SignatureAuthConstant;
import AuD.wechat.pay.core.constant.WeChatPayApiList;
import AuD.wechat.pay.core.function.WeChatPaySignatureHandle;
import AuD.wechat.pay.core.function.model.PlatformCertData;
import AuD.wechat.pay.core.function.model.SignatureInfoModel;
import AuD.wechat.pay.core.function.component.WeChatCertInfo;
import AuD.wechat.pay.core.util.WeChatPayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.security.cert.X509Certificate;
import java.util.List;
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
    public void flushCert(){
        final SignatureInfoModel signatureInfoModel = SignatureInfoModel.of(SignatureAuthConstant.MCH_ID,SignatureAuthConstant.PRIVATE_SERIAL_NO,SignatureAuthConstant.GET);
        signatureInfoModel.setRequestUri(WeChatPayApiList.PCL_MAPPING);
        final AcquirePlatformCert.ResultDataFromCertApi resultData = platformCertList.getCertList(WeChatPaySignatureHandle.buildAuthorization(signatureInfoModel,certInfo.getApiPrivateKey()));
        Map<String, X509Certificate> platformCert = certInfo.getPlatformCert();
        final List<PlatformCertData> certDataList = resultData.getData();
        certDataList.forEach((certData)->{
            final String certContextData = WeChatPayUtils.decryptCertAndCallBody(certData.getEncryptCertificate());
            X509Certificate certificate = null;
            if(StringUtils.hasText(certContextData) && (certificate = WeChatPayUtils.loadCertificate(new ByteArrayInputStream(certContextData.getBytes())))!=null){
                platformCert.put(certData.getSerialNo(),certificate);
            }
        });
        certInfo.setLatestUsing(findNewest(certDataList));
    }

    private PlatformCertData findNewest(List<PlatformCertData> certDataList){
        certDataList.sort(((o1, o2) -> {
            /** 排序规则默认升序,i.e o1>o2返回1,则在list中,o1排在o2后面,所以若希望以第一个为主,则设置o1>o2返回-1 */
            int res = o1.getEffectiveTime().isAfter(o2.getEffectiveTime()) ? -1:1;
            return res;
        }));
        return certDataList.get(0);
    }



}
