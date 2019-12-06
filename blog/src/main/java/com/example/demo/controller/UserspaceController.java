package com.example.demo.controller;

import com.example.demo.model.BlogUser;
import com.example.demo.service.BlogUserService;
import com.example.demo.vo.PersonalResult;
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

    @GetMapping("/")
    public String root(){
        return "redirect:main";
    }

    @GetMapping("/main")
    public String index(){
        return "main";
    }

    @GetMapping("/toMain")
    public String toMain(){
        return "main";
    }

    @GetMapping("/toLogin")
    public String toLogin(){
        return "login";
    }

    @GetMapping("/toRegister")
    public String toRegister(){
        return "register";
    }
    @GetMapping("/toForget")
    public String toForget(){
        return "forget";
    }

    @RequestMapping("/quit")
    public String quit(HttpSession session){
        session.removeAttribute("result");
        return "redirect:main";
    }

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

}
