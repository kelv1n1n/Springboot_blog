package com.example.demo.serviceimpl;

import com.example.demo.model.BlogCategory;
import com.example.demo.model.BlogCategoryExample;
import com.example.demo.repository.BlogCategoryMapper;
import com.example.demo.service.BlogCategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BlogCategoryServiceImpl implements BlogCategoryService {

    @Resource
    BlogCategoryMapper blogCategoryMapper;

    /**
     * 统计博客分类的总数
     * @return
     */
    @Override
    public Integer countBlogCategory() {
        BlogCategoryExample example = new BlogCategoryExample();
        return blogCategoryMapper.countByExample(example);
    }


    /**
     * 获取分类列表
     * @return
     */
    @Override
    public List<BlogCategory> getAllCategories() {
        BlogCategoryExample example = new BlogCategoryExample();
        return blogCategoryMapper.selectByExample(example);
    }
}
