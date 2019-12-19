package com.example.demo.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin")
public class AdminBlogController {

    @GetMapping("/blogs/edit")
    public String edit(HttpServletRequest request) {
//        request.setAttribute("path", "edit");
//        request.setAttribute("categories", categoryService.getAllCategories());
        return "admin/edit";
    }

}
