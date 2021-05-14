package AuD.wechat.pay.controller;

import AuD.wechat.pay.core.function.model.CallBackSuccess;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Description: TODO
 *
 * @author AuD/胡钊
 * @ClassName ExceptionController
 * @date 2021/4/27 18:06
 * @Version 1.0
 */
@RestController
@RequestMapping("/error")
public class ExceptionController {

    @RequestMapping("/signature/fail")
    public CallBackSuccess handleSignatureFail(HttpServletRequest request){
        return CallBackSuccess.signatureFail();
    }

}
