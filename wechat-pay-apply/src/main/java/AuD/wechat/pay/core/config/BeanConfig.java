package AuD.wechat.pay.core.config;

import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Description: TODO
 *
 * @author AuD/胡钊
 * @ClassName BeanConfig
 * @date 2021/5/11 19:39
 * @Version 1.0
 */
@Configuration
public class BeanConfig {

    /**
     * {@link RestTemplateAutoConfiguration} 自动装配
     * @param restTemplateBuilder
     * @return
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder){
        return restTemplateBuilder.build();
    }

}
