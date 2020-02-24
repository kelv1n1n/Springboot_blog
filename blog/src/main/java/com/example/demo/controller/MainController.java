package com.example.demo.controller;

import com.example.demo.model.Blog;
import com.example.demo.service.BlogTagService;
import com.example.demo.service.BlogsService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @Autowired
    BlogTagService blogTagService;

    /*首页入口*/
    @GetMapping({"/index", "/", "/toMain"})
    public String index(Model model,@RequestParam(defaultValue = "1",value = "pageNum") Integer pageNum){
        PageHelper.startPage(pageNum,9);
        List<Blog> blogList = blogsService.getBlogList();
        PageInfo<Blog> pageInfo = new PageInfo<>(blogList);
//        List<BlogTag> blogTags = blogTagService.selectTag();
//        List<Blog> hotBlogs = blogsService.hotBlog();

        model.addAttribute("PageInfo",pageInfo);
//        model.addAttribute("BlogCount",blogsService.countBlog());
//        model.addAttribute("BlogTagList",blogTags);
//        model.addAttribute("HotBlog",hotBlogs);
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
        return "redirect:index";
    }

//    @GetMapping({"/search/{keyword}"})
//    public String search(Model model,HttpServletRequest request,@RequestParam(defaultValue = "1",value = "pageNum") Integer pageNum, @PathVariable("keyword") String keyword) {
//        return search(model,keyword, pageNum, request);
//    }

    @GetMapping({"/search/{keyword}"})
    public String search(Model model,@PathVariable("keyword") String keyword,@RequestParam(defaultValue = "1",value = "pageNum") Integer pageNum, HttpServletRequest request){
        PageHelper.startPage(pageNum,6);
        List<Blog> blogList = blogsService.searchBlog(keyword);
        PageInfo<Blog> pageInfo = new PageInfo<>(blogList);
        if (blogList.size() == 0){
            model.addAttribute("PageInfo",pageInfo);
            return "blog_search";
        }
        model.addAttribute("PageInfo",pageInfo);
        request.setAttribute("url","search");
        request.setAttribute("keyword",keyword);
        return "blog_search";
    }
}
