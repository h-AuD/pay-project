package AuD.wechat.pay.core.function.model;

/**
 * Description: 微信回调成功返回的数据结构
 *
 * @author AuD/胡钊
 * @ClassName CallBackSuccess
 * @date 2021/5/14 16:50
 * @Version 1.0
 */
public class CallBackSuccess {

    private String code;

    private String message;

    private CallBackSuccess(String code,String message){
        this.code = code;
        this.message = message;
    }

    public static CallBackSuccess ofSuc(){
        return new CallBackSuccess("SUCCESS","成功");
    }

    public static CallBackSuccess signatureFail(){
        return new CallBackSuccess("401","signature fail");
    }

}
