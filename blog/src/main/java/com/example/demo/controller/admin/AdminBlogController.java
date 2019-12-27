package com.example.demo.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.model.Blog;
import com.example.demo.service.BlogCategoryService;
import com.example.demo.service.BlogsService;
import com.example.demo.vo.PersonalResult;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

@Controller
@RequestMapping("/admin")
public class AdminBlogController {

    public static final Logger logger = LoggerFactory.getLogger(AdminBlogController.class);

    @Autowired
    BlogsService blogsService;

    @Autowired
    BlogCategoryService blogCategoryService;

    @GetMapping("/blogs/edit")
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

    @GetMapping("/blog")
    public String list(HttpServletRequest request) {
        request.setAttribute("path", "blogs");
        return "admin/blog";
    }


    @PostMapping("/blog/md/uploadfile")
    @ResponseBody
    public JSONObject uploadFileByEditormd(HttpServletRequest request, HttpServletResponse response, @RequestParam(name = "editormd-image-file", required = true)
                                             MultipartFile file) throws IOException, URISyntaxException {

        String trueFileName = file.getOriginalFilename();

        String suffix = trueFileName.substring(trueFileName.lastIndexOf("."));

        String fileName = System.currentTimeMillis()+"_" + suffix;

        String path = request.getSession().getServletContext().getRealPath("/static/images/blogImg/");
        System.out.println(path);

        File targetFile = new File(path, fileName);
        if(!targetFile.exists()){
            targetFile.mkdirs();
        }

        //保存
        try {
            file.transferTo(targetFile);
        } catch (Exception e) {
            e.printStackTrace();
        }


        JSONObject res = new JSONObject();
        res.put("url", "/static/images/blogImg/"+fileName);
        res.put("success", 1);
        res.put("message", "upload success!");

        return res;
    }

}
