package AuD.wechat.pay.core.start;

import AuD.wechat.pay.core.api.WeChatPlatformCertApi;
import AuD.wechat.pay.core.auth.SignatureInfoModel;
import AuD.wechat.pay.core.auth.WeChatPayAuthHandle;
import AuD.wechat.pay.core.constant.SignatureAuthConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Description: TODO
 *
 * @author AuD/胡钊
 * @ClassName DownLoadCert
 * @date 2021/5/12 17:08
 * @Version 1.0
 */
@Component
public class DownLoadCert implements CommandLineRunner {

    @Autowired
    private WeChatPlatformCertApi certApi;

    /**
     * 项目启动时,下载平台证书,以及保存证书信息的相关逻辑
     */
    @Override
    public void run(String... args) throws Exception {
        final SignatureInfoModel signatureInfoModel = SignatureInfoModel.of(SignatureAuthConstant.MCH_ID, SignatureAuthConstant.PRIVATE_SERIAL_NO);
        signatureInfoModel.setRequestMethod("GET").setRequestUri("/v3/certificates");
        final WeChatPlatformCertApi.ResultDataFromCertApi resultData = certApi.downloadCert(WeChatPayAuthHandle.buildAuthorization(signatureInfoModel, SignatureAuthConstant.PRIVATE_KEY_PATH));
        // TODO 待续.....
        resultData.getData().forEach((certData -> {}));
    }
}
