package com.example.demo.controller;

import com.example.demo.model.Blog;
import com.example.demo.service.BlogsService;
import com.example.demo.vo.PersonalResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 主页接口
 */

@Controller
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    BlogsService blogsService;

    /*首页入口*/
    @GetMapping({"/index", "/", "/toMain"})
    public String index(Model model,@RequestParam(defaultValue = "1",value = "pageNum") Integer pageNum){
        PageHelper.startPage(pageNum,5);
        List<Blog> blogList = blogsService.getBlogList();
        logger.info("博客内容:" + blogList.get(0).toString());
        PageInfo<Blog> pageInfo = new PageInfo<>(blogList);
        model.addAttribute("PageInfo",pageInfo);
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

}
