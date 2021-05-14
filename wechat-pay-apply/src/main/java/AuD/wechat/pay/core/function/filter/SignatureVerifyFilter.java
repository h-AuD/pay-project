package AuD.wechat.pay.core.function.filter;

import AuD.wechat.pay.core.function.WeChatPaySignatureHandle;
import AuD.wechat.pay.core.function.model.SignatureInfoModel;
import AuD.wechat.pay.core.constant.SignatureAuthConstant;
import AuD.wechat.pay.core.function.component.WeChatCertInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Description: 微信回调请求过滤器,可用于验签
 *
 * @author AuD/胡钊
 * @ClassName WeChatCallBackAuthVerifyFilter
 * @date 2021/5/11 19:43
 * @Version 1.0
 */
@Component
public class SignatureVerifyFilter extends OncePerRequestFilter {

    @Autowired
    private WeChatCertInfo certInfo;

    /** 设置需要拦截的请求,目前设置拦截回调接口(i.e,this app api) */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return super.shouldNotFilter(request);
    }

    /** 拦截,微信支付验签 */
    @Override
    protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String serial = request.getHeader(SignatureAuthConstant.WCP_SERIAL);
        // 商户是否拥有该证书
        if(!certInfo.isExistKey(serial)){
            /* =========================================================================
             * TODO 遗留问题:并发情况如何处理?eg.当证书即将过期,并且多个回调同时 come in
             * 在 flushCert 方法中采取tryLock方式
             * 即希望刷新证书操作执行一次,对于没有机会的刷新的请求直接放行,
             * 但是会导致接下来的verify失败 === TODO new problem
             * =========================================================================*/
            certInfo.flushCert();
        }
        final boolean verify = WeChatPaySignatureHandle.verifyWeChatResponse(buildParam(request),null);
        if(verify){
            filterChain.doFilter(request,response);
        }else {
            response.sendError(500);
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
            signatureInfoModel.setTimestamp(timestamp)
                    .setNonceStr(nonce)
                    .setSignature(signature)
                    .setBody(stringBuilder.toString());
        }catch (Exception e){
            logger.error("error appear in processing of handle request header,info:{}",e);
        }
        return signatureInfoModel;
    }

}
