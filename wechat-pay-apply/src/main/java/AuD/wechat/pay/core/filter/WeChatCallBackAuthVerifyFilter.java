package AuD.wechat.pay.core.filter;

import AuD.wechat.pay.core.auth.SignatureInfoModel;
import AuD.wechat.pay.core.auth.WeChatPayAuthHandle;
import AuD.wechat.pay.core.constant.SignatureAuthConstant;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Description: 微信回调请求过滤器
 *
 * @author AuD/胡钊
 * @ClassName WeChatCallBackAuthVerifyFilter
 * @date 2021/5/11 19:43
 * @Version 1.0
 */
public class WeChatCallBackAuthVerifyFilter extends OncePerRequestFilter {


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return super.shouldNotFilter(request);
    }

    /**
     *
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String serial = request.getHeader(SignatureAuthConstant.WCP_SERIAL);
        // TODO 验证证书序列号 ===
        final boolean verify = WeChatPayAuthHandle.verifyWeChatResponse(buildParam(request),"");
        if(verify){
            filterChain.doFilter(request,response);
        }
    }

    private SignatureInfoModel buildParam(HttpServletRequest request){
        final String timestamp = request.getHeader(SignatureAuthConstant.WCP_TIMESTAMP);
        final String nonce = request.getHeader(SignatureAuthConstant.WCP_NONCE);
        final String signature = request.getHeader(SignatureAuthConstant.WCP_SIGNATURE);
        StringBuilder stringBuilder = new StringBuilder();
        try (final BufferedReader reader = request.getReader()) {
            String str = "";
            while ((str = reader.readLine())!=null){
                stringBuilder.append(str);
            }
        }catch (Exception e){

        }
        final SignatureInfoModel signatureInfoModel = SignatureInfoModel.of()
                .setTimestamp(timestamp)
                .setNonceStr(nonce)
                .setSignature(signature)
                .setBody(stringBuilder.toString());
        return signatureInfoModel;
    }

}
