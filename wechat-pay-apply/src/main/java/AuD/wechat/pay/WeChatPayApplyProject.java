package AuD.wechat.pay;

import com.dtflys.forest.springboot.annotation.ForestScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Description: TODO
 *
 * @author AuD/胡钊
 * @ClassName WeChatPayApplyProject
 * @date 2021/5/11 19:27
 * @Version 1.0
 */
@SpringBootApplication
@MapperScan(value = "AuD.wechat.pay.mapper")
@ForestScan(basePackages = "AuD.wechat.pay.api")
public class WeChatPayApplyProject {
    public static void main(String[] args) {
        SpringApplication.run(WeChatPayApplyProject.class,args);
    }
}
