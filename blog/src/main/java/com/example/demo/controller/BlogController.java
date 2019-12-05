package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 博客列表接口
 */
@Controller
@RequestMapping("/blogs")
public class BlogController {

    @GetMapping//order:排序规则      keyword:关键字搜索
    public String listBlogs(@RequestParam(value = "order",required = false,defaultValue = "new")String order,
                            @RequestParam(value = "keyword",required = false,defaultValue = "")String keyword){
        System.out.println("order=" + order + "tag=" + keyword);
        return "redirect:index?order="+order+"&keyword="+ keyword;
    }


}
