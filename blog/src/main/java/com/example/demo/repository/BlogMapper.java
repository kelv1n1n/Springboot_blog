package com.example.demo.repository;

import com.example.demo.model.Blog;
import com.example.demo.model.BlogExample;
import java.util.List;

import com.example.demo.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

public interface BlogMapper {
    int countByExample(BlogExample example);

    int deleteByExample(BlogExample example);

    int deleteByPrimaryKey(Long blogId);

    int insert(Blog record);

    int insertSelective(Blog record);

    List<Blog> selectByExampleWithBLOBs(BlogExample example);

    List<Blog> selectByExample(BlogExample example);

    Blog selectByPrimaryKey(Long blogId);

    int updateByExampleSelective(@Param("record") Blog record, @Param("example") BlogExample example);

    int updateByExampleWithBLOBs(@Param("record") Blog record, @Param("example") BlogExample example);

    int updateByExample(@Param("record") Blog record, @Param("example") BlogExample example);

    int updateByPrimaryKeySelective(Blog record);

    int updateByPrimaryKeyWithBLOBs(Blog record);

    int updateByPrimaryKey(Blog record);

    //查询最新的blog_id
    Long maxNewBlog();

    //博客文章管理查询博客列表
    List<Blog> findBlogList(PageQueryUtil pageUtil);

    //总的文章数量
    int getTotalBlogs(PageQueryUtil pageUtil);

    //删除博客文章 即根据博客文章id修改  是否删除  字段
    int deleteBatch(Integer[] ids);

    //热门文章
    List<Blog> hotBlog();
}