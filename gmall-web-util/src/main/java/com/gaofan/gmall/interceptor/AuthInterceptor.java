package com.gaofan.gmall.interceptor;

import com.gaofan.gmall.annotation.LoginRequire;
import com.gaofan.gmall.util.CommonUtil;
import com.gaofan.gmall.util.CookieUtil;
import com.gaofan.gmall.util.HttpClientUtil;
import com.gaofan.gmall.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HandlerMethod method = (HandlerMethod) handler;

        LoginRequire methodAnnotation = method.getMethodAnnotation(LoginRequire.class);
        //不需要登录(无注解)，直接放行
        if(methodAnnotation == null){
            return true;
        }

        String cookietoken = CookieUtil.getCookieValue(request,"userToken",true);
        String urlToken  = request.getParameter("userToken");
        String token = urlToken==null?cookietoken:urlToken;
        StringBuffer oldUrl = request.getRequestURL();
        //有注解的情况
        if(methodAnnotation.requiredLogin() == true && StringUtils.isBlank(token)){
            response.sendRedirect("http://passport.gmall.com:8086/index?returnUrl="+oldUrl);
            return false;
        }else if(StringUtils.isNotBlank(token)){
            String salt = CommonUtil.getIp(request);
            String verify = HttpClientUtil.doGet("http://passport.gmall.com:8086/verify?userToken=" + token + "&salt="+salt);
            if(verify.equals("success")){
                //更新cookie的token
                CookieUtil.setCookie(request,response,"userToken",token,60*60*2,true);

                //将用户信息放入请求域中
                Map user = JwtUtil.decode(CommonUtil.USER_TOKEN_KEY, token, salt);
                request.setAttribute("userId",user.get("userId"));
                request.setAttribute("nickName",user.get("nickName"));

            }else{
                if(methodAnnotation.requiredLogin() == true){
                    //必须登录
                    response.sendRedirect("http://passport.gmall.com:8086/index?returnUrl="+oldUrl);
                    return false;
                }
            }
        }

        return true;
    }
}
