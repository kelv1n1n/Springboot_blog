package com.example.demo.controller;

import com.example.demo.model.BlogUser;
import com.example.demo.service.BlogUserService;
import com.example.demo.util.TokenUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class TestController {

    @Autowired
    BlogUserService blogUserService;

    // 登录
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    @ResponseBody
    public BlogUser login(@RequestBody Map<String,Object> param, HttpServletRequest request) {
        String username = (String)param.get("username");
        System.out.println("username=======>"+username);
        BlogUser user = new BlogUser();
        user.setUsername(username);
        request.getSession().setAttribute("user",user.getUsername());
        System.out.println("session======>"+request.getSession().getAttribute("user"));
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("success","200");
        return user;
    }

    @RequestMapping(value = "/testsession",method = RequestMethod.POST)
    @ResponseBody
    public HashMap testSession(){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("success","200");
        return hashMap;
    }

    /*异常捕获处理测试*/
    @RequestMapping("/errorsss")
    public String err(){
        int i = 1 / 0;
        return "main";
    }

    /*短信发送测试*/
    @RequestMapping("/smsverification")
    @ResponseBody
    public Object SmsVerification(@Param("phone") String phone) {
        return blogUserService.SmsVerification(phone);
    }
}
