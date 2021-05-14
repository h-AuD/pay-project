package AuD.wechat.pay.controller;

import AuD.wechat.pay.core.function.model.CallBackSuccess;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: TODO
 *
 * @author AuD/胡钊
 * @ClassName XxxController
 * @date 2021/5/12 15:01
 * @Version 1.0
 */
@RestController
@RequestMapping
public class WeChatPayCallBackController {


    @PostMapping("/back/pay")
    public CallBackSuccess callBack(){
        // TODO 处理微信支付回调逻辑
        return CallBackSuccess.ofSuc();
    }

}
