package AuD.wechat.pay.core.function.component;

import AuD.wechat.pay.core.constant.SignatureAuthConstant;
import AuD.wechat.pay.core.function.model.PlatformCertData;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Description: 用于存储微信支付需要的商户证书和平台证书相关的信息 === 相当于一个存储容器
 *
 * - 设计this-class是因为,每次call微信支付相关API(eg.统一下单接口)或者验证微信支付回调签名或者敏感信息加解密时,
 * - 需要用到PrivateKey & X509Certificate,而这2个需要通过读取文件或者文本来获取,
 * - 考虑到上述2个对象可变频率不大的情况下,没有必要频繁读取文件/文本 === i.e 商户API证书是不可变的,如果遗失或者泄漏,只能重新申请.
 *
 * 实现{@link ApplicationContextAware}是为了在生成apiPrivateKey时出现错误时,让APP exit.
 * 实现{@link InitializingBean} 是为了初始化证书对象
 * ====> 有了上述基础,this class 便扮演了一个"证书容器"角色.
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
    private Map<String, X509Certificate> platformCert = new ConcurrentHashMap<>();
    /** 表示最近启用的平台证书 == 加密请求消息中的敏感信息时,使用最新的平台证书 */
    private PlatformCertData latestUsing;

    private final static ReentrantLock lock = new ReentrantLock();  // 同步锁

    private ApplicationContext applicationContext;

    /** 刷新平台证书实际执行者,即刷新的操作委托给 {@link FlushPlatformCert} */
    @Autowired
    private FlushPlatformCert flushPlatformCert;

    public PrivateKey getApiPrivateKey() {
        return apiPrivateKey;
    }

    public void setPlatformCert(Map<String, X509Certificate> platformCert){
        this.platformCert = platformCert;
    }

    public Map<String, X509Certificate> getPlatformCert(){
        return this.platformCert;
    }

    public PlatformCertData getLatestUsing() {
        return latestUsing;
    }

    public void setLatestUsing(PlatformCertData latestUsing) {
        this.latestUsing = latestUsing;
    }

    /** 判断 this serialNo 是否存在 */
    public boolean isExistKey(String serialNo){
        return platformCert.containsKey(serialNo);
    }

    /** 根据serialNo获取对应的证书 */
    public X509Certificate getCert(String serialNo){
        return platformCert.get(serialNo);
    }

    /** 刷新平台证书,有并发影响,建议处理 */
    public void flushCert(){
        try {
            // 尝试获取锁,获取不到,直接走人
           if(lock.tryLock(5,TimeUnit.SECONDS)){
               flushPlatformCert.flushCert();
           }else {
               return;
           }
        }catch (Exception e){

        }finally {
            if(lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final PrivateKey privateKey = WeChatPayUtils.getPrivateKey(SignatureAuthConstant.PRIVATE_KEY_PATH);
        if(privateKey==null){
            log.error("商户API证书密钥获取失败,应用退出");
            SpringApplication.exit(applicationContext); // 应用退出
        }
        this.apiPrivateKey = privateKey;
        flushPlatformCert.flushCert();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
