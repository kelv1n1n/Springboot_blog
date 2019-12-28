package com.example.demo.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.model.Blog;
import com.example.demo.service.BlogCategoryService;
import com.example.demo.service.BlogsService;
import com.example.demo.util.PageQueryUtil;
import com.example.demo.util.PageResult;
import com.example.demo.vo.PersonalResult;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminBlogController {

    public static final Logger logger = LoggerFactory.getLogger(AdminBlogController.class);

    @Autowired
    BlogsService blogsService;

    @Autowired
    BlogCategoryService blogCategoryService;

    /**
     * 博客编写
     * @param request
     * @return
     */
    @GetMapping("/blog/edit")
    public String edit(HttpServletRequest request) {
        request.setAttribute("path", "edit");
        //返回博客文章分类列表
        request.setAttribute("categories", blogCategoryService.getAllCategories());
        return "admin/edit";
    }


    /**
     * 保存博客
     * @param blogTitle
     * @param blogSubUrl
     * @param blogCategoryId
     * @param blogTags
     * @param blogContent
     * @param blogCoverImage
     * @param blogStatus
     * @param enableComment
     * @return
     */
    @PostMapping("/blog/save")
    @ResponseBody
    public PersonalResult saveBlog(@RequestParam("blogTitle") String blogTitle,
                                   @RequestParam(name = "blogSubUrl", required = false) String blogSubUrl,
                                   @RequestParam("blogCategoryId") Integer blogCategoryId,
                                   @RequestParam("blogTags") String blogTags,
                                   @RequestParam("blogContent") String blogContent,
                                   @RequestParam("blogCoverImage") String blogCoverImage,
                                   @RequestParam("blogStatus") Byte blogStatus,
                                   @RequestParam("enableComment") Byte enableComment){
//        logger.info("文章对象:" + blogTitle + "  " + blogCategoryId + "  "  + blogTags + "  " + blogContent + "  " + blogCoverImage +  "  " + blogStatus + "  " + enableComment);
        Blog blog = new Blog();
        blog.setBlogTitle(blogTitle);
        blog.setBlogSubUrl(blogSubUrl);
        blog.setBlogCategoryId(blogCategoryId);
        blog.setBlogTags(blogTags);
        blog.setBlogContent(blogContent);
        blog.setBlogCoverImage(blogCoverImage);
        blog.setBlogStatus(blogStatus);
        blog.setEnableComment(enableComment);
        PersonalResult result = blogsService.saveBlog(blog);
        if (result.getStatus() == 200){
            return PersonalResult.build(200,"保存成功");
        }
        return PersonalResult.build(300,result.getMsg());
    }

    /**
     * 管理博客
     * @param request
     * @return
     */
    @GetMapping("/blog")
    public String list(HttpServletRequest request) {
        request.setAttribute("path", "blogs");
        return "admin/blog";
    }


    /**
     * 博客文章图片文件上传
     * @param request
     * @param response
     * @param file
     * @return
     */
    @PostMapping("/blog/md/uploadfile")
    @ResponseBody
    public Map<String,Object> uploadFileByEditormd(HttpServletRequest request, HttpServletResponse response, @RequestParam(name = "editormd-image-file", required = true)
                                             MultipartFile file) {
//        return null;
        Map<String,Object> resultMap = new HashMap<String,Object>();
        //保存
        try {
            File imageFolder= new File(request.getServletContext().getRealPath("img/upload"));
            File targetFile = new File(imageFolder,file.getOriginalFilename());
            if(!targetFile.getParentFile().exists())
                targetFile.getParentFile().mkdirs();
            file.transferTo(targetFile);
            
            resultMap.put("success", 1);
            resultMap.put("message", "上传成功！");
            resultMap.put("url","http://localhost:8080/img/upload/"+file.getOriginalFilename());
        } catch (Exception e) {
            resultMap.put("success", 0);
            resultMap.put("message", "上传失败！");
            e.printStackTrace();
        }
        System.out.println(resultMap.get("success"));
        return resultMap;
    }


    /**
     * 博客文章管理查询博客列表
     * @param params
     * @return
     */
    @GetMapping("/blog/list")
    @ResponseBody
    public PersonalResult blogList(@RequestParam Map<String, Object> params){
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return PersonalResult.build(300,"参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        PageResult allBlog = blogsService.getAllBlog(pageUtil);
        return PersonalResult.build(200,"success",allBlog);
    }


    /**
     * 编辑博客文章
     * @param request
     * @param blogId
     * @return
     */
    @GetMapping("/blog/edit/{blogId}")
    public String edit(HttpServletRequest request, @PathVariable("blogId") Long blogId) {
        request.setAttribute("path", "edit");
        Blog blog = blogsService.selectOneById(blogId);
        if (blog == null) {
            return "error";
        }
        request.setAttribute("blog",blog);
        request.setAttribute("categories", blogCategoryService.getAllCategories());
        return "admin/edit";
    }

}
