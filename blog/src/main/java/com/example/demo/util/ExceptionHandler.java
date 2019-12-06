package com.example.demo.util;

import com.example.demo.vo.PersonalResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全局异常捕获
 */
@ControllerAdvice
public class ExceptionHandler {

    public static final String PERSONAL_ERROR_VIEW = "error";

    @org.springframework.web.bind.annotation.ExceptionHandler(value = Exception.class)
    public Object ErrorHandler(HttpServletRequest request, HttpServletResponse response,Exception e) throws Exception{
        e.printStackTrace();//打印错误到控制台
        if (isAjax(request)){
            System.out.println("ajax请求错误");
            return PersonalResult.errorException(e.getMessage());
        }else {
            System.out.println("普通请求错误");
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("exception",e);//错误信息
            modelAndView.addObject("url",request.getRequestURL());//请求的地址
            modelAndView.setViewName(PERSONAL_ERROR_VIEW);//显示的页面
            return modelAndView;
        }

    }

    /*判断是否是ajax请求*/
    private boolean isAjax(HttpServletRequest request) {
        return (request.getHeader("X-Requested-with")!=null&&"XMLHttpRequest".equals(request.getHeader("X-Requested-with").toString()));
    }
}
