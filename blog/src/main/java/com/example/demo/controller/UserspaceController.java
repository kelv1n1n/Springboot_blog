package com.example.demo.controller;

import com.example.demo.model.BlogUser;
import com.example.demo.service.BlogUserService;
import com.example.demo.vo.PersonalResult;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 用户主页控制器
 */

@Controller
public class UserspaceController {

    @Autowired
    BlogUserService blogUserService;


    /**
     * 登录
     * @param user
     * @param session
     * @return
     */
    @RequestMapping("/login")
    @ResponseBody
    public String login(BlogUser user, HttpSession session){
        PersonalResult result = blogUserService.login(user);
        session.setAttribute("result",result);
        return result.getStatus().toString();
    }

    /**
     * 注册
     * @param user
     * @param session
     * @return
     */
    @RequestMapping("/register")
    @ResponseBody
    public PersonalResult register(BlogUser user, HttpSession session){
        PersonalResult result = new PersonalResult();
        result = blogUserService.register(user);
        return result;
    }

    /*短信发送测试*/
    @RequestMapping("/sendCode")
    @ResponseBody
    public Object SmsVerification(HttpServletRequest request) {
//        System.out.println("手机号码：" + request.getParameter("phone"));
        String phone = request.getParameter("phone");
        return blogUserService.SmsVerification(phone);
    }


    /*修改密码*/
    /*验证手机验证码*/
    @RequestMapping("/forget")
    public String forget(BlogUser user, HttpServletRequest request){
        PersonalResult result = new PersonalResult();
        String code = request.getParameter("code");
        result = blogUserService.reSetPassword(user,code);
        return result.getStatus().toString();
    }

}