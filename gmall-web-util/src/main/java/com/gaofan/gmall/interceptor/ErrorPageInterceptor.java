package com.gaofan.gmall.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@Component
public class ErrorPageInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        List<Integer> erroStatus = Arrays.asList(404,403,500,501);
        int status = response.getStatus();
        if(erroStatus.contains(status)){

            response.sendRedirect("/error.html");
            return false;
        }


        return super.preHandle(request, response, handler);
    }
}
