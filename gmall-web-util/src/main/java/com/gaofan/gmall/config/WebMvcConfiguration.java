package com.gaofan.gmall.config;

import com.gaofan.gmall.interceptor.AuthInterceptor;
import com.gaofan.gmall.interceptor.ErrorPageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Autowired
    private ErrorPageInterceptor errorPageInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor).addPathPatterns("/**");
        registry.addInterceptor(errorPageInterceptor).addPathPatterns("/**");
        super.addInterceptors(registry);
    }

}
