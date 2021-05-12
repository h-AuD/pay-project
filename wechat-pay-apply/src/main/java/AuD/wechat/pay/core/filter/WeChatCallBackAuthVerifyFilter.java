package AuD.wechat.pay.core.filter;

import AuD.wechat.pay.core.auth.SignatureInfoModel;
import AuD.wechat.pay.core.auth.WeChatPayAuthHandle;
import AuD.wechat.pay.core.constant.SignatureAuthConstant;
import org.springframework.stereotype.Component;
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
@Component
public class WeChatCallBackAuthVerifyFilter extends OncePerRequestFilter {

    /** 设置需要拦截的请求 */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return super.shouldNotFilter(request);
    }

    /** 拦截 */
    @Override
    protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String serial = request.getHeader(SignatureAuthConstant.WCP_SERIAL);
        // TODO 验证证书序列号 ===
        final boolean verify = WeChatPayAuthHandle.verifyWeChatResponse(buildParam(request),null);
        if(verify){
            filterChain.doFilter(request,response);
        }
    }

    /** 从 request-header 获取需要的参数 */
    private SignatureInfoModel buildParam(HttpServletRequest request){
        final SignatureInfoModel signatureInfoModel = SignatureInfoModel.of();
        try (final BufferedReader reader = request.getReader()) {
            String str = "";
            StringBuilder stringBuilder = new StringBuilder();
            // 读取请求体内容
            while ((str = reader.readLine())!=null){
                stringBuilder.append(str);
            }
            final long timestamp = Long.parseLong(request.getHeader(SignatureAuthConstant.WCP_TIMESTAMP));  // 这里可能会发生异常,转换异常
            final String nonce = request.getHeader(SignatureAuthConstant.WCP_NONCE);
            final String signature = request.getHeader(SignatureAuthConstant.WCP_SIGNATURE);
            signatureInfoModel.setTimestamp(timestamp).setNonceStr(nonce).setSignature(signature).setBody(stringBuilder.toString());
        }catch (Exception e){
            logger.error("error appear in processing of handle request header,info:{}",e);
        }
        return signatureInfoModel;
    }

}
