package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 后台管理员控制器
 */

@Controller
@RequestMapping("/admins")
public class AdminController {

    @GetMapping
    public String listUsers(Model model){
        model.addAttribute("menuList",model);
        return "admins/index";
    }

}
