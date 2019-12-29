package com.example.demo.config;

import com.example.demo.interceptor.AdminLoginInterceptor;
import com.example.demo.interceptor.LoginInterceptor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    private LoginInterceptor loginInterceptor;

    private AdminLoginInterceptor adminLoginInterceptor;

    //构造方法
    public InterceptorConfig(LoginInterceptor loginInterceptor,AdminLoginInterceptor adminLoginInterceptor){
        this.loginInterceptor = loginInterceptor;
        this.adminLoginInterceptor = adminLoginInterceptor;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/abc/**")
                .excludePathPatterns("/login");
//                .excludePathPatterns(excludePath);
        registry.addInterceptor(adminLoginInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login")
                .excludePathPatterns("/admin/dist/**")
                .excludePathPatterns("/admin/plugins/**");
        WebMvcConfigurer.super.addInterceptors(registry);

    }

}
