package com.example.demo.controller;

import com.example.demo.model.Blog;
import com.example.demo.service.BlogsService;
import com.example.demo.vo.BlogDetailVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 博客列表接口
 */
@Controller
@RequestMapping("/blog")
public class BlogController {

    private static final Logger logger = LoggerFactory.getLogger(BlogController.class);

    @Autowired
    BlogsService blogsService;

    /*点击进入博客详细页*/
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long blogId,Model model){
//        logger.info("博客id:" + blogId);
        //根据博客的id查出该条博客
        BlogDetailVO blog = blogsService.selectDetail(blogId);
//        logger.info(blog.toString());
        model.addAttribute("Blog",blog);
        return "blog_detail";
    }


    /*搜索博客*/
    @GetMapping("/search")
    public String searchBlogs(HttpServletRequest request,
                              Model model,
                              @RequestParam(defaultValue = "1",value = "pageNum") Integer pageNum){

        String content = request.getParameter("content");
        logger.info("content:=====>" + content);

        PageHelper.startPage(pageNum,5);
        List<Blog> blogList = blogsService.getBlogList();
        List<Blog> blogList1  = new ArrayList<>();
        blogList1.add(blogList.get(0));
        PageInfo<Blog> pageInfo = new PageInfo<>(blogList1);
        model.addAttribute("PageInfo",pageInfo);
        return "null";
    }

}
