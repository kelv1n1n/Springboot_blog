package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 主页接口
 */

@Controller
public class MainController {

    @GetMapping("/")
    public String root(){
        return "redirect:main";
    }

    @GetMapping("/main")
    public String index(){
        return "main";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }


    @GetMapping("/register")
    public String register(){
        return "register";
    }

    @GetMapping("/forget")
    public String forget(){
        return "forget";
    }
}
