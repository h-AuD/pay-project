package AuD.wechat.pay.core.function.component;

import AuD.wechat.pay.core.constant.SignatureAuthConstant;
import AuD.wechat.pay.core.function.FlushPlatformCert;
import AuD.wechat.pay.core.util.WeChatPayUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Optional;

/**
 * Description: 用于存储微信支付需要的商户证书和平台证书相关的信息 === 相当于一个存储容器
 *
 * 设计this-class是因为,每次call微信支付相关API(i.e. 统一下单接口)或者验证微信支付回调签名或者敏感信息加解密时,
 * 需要用到PrivateKey & X509Certificate,而这2个需要通过读取文件或者文本来获取,
 * 考虑到上述2个可变频率不大的情况下,没有必要频繁读取文件/文本 === i.e 商户API证书是不可变的,如果遗失或者泄漏,只能重新申请.
 *
 * @author AuD/胡钊
 * @ClassName WeChatCertInfo
 * @date 2021/5/13 13:52
 * @Version 1.0
 */
@Component
@Slf4j
public class WeChatCertInfo implements ApplicationContextAware,InitializingBean {
    /** 商户API私钥,存在于商户API证书中,用于签名 */
    private PrivateKey apiPrivateKey;
    /** 用于存储平台证书,其中key为serialNo(证书序列号) === 使用 ConcurrentHashMap */
    private Map<String, X509Certificate> platformCert;

    private ApplicationContext applicationContext;

    @Autowired
    private FlushPlatformCert flushPlatformCert;

    public PrivateKey getApiPrivateKey() {
        return apiPrivateKey;
    }

    /** 判断this serialNo 是否存在 */
    public boolean isExistKey(String serialNo){
        return platformCert.containsKey(serialNo);
    }

    /** 根据serialNo获取对应的证书 */
    public X509Certificate getCert(String serialNo){
        return platformCert.get(serialNo);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final Optional<PrivateKey> optional = WeChatPayUtils.getPrivateKey(SignatureAuthConstant.PRIVATE_KEY_PATH);
        if(!optional.isPresent()){
            log.error("商户API证书密钥获取失败,应用退出");
            SpringApplication.exit(applicationContext); // 应用退出
        }
        platformCert = flushPlatformCert.flushCert();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
