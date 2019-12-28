package com.example.demo.serviceimpl;

import com.example.demo.model.*;
import com.example.demo.repository.BlogCategoryMapper;
import com.example.demo.repository.BlogMapper;
import com.example.demo.repository.BlogTagMapper;
import com.example.demo.repository.BlogTagRelationMapper;
import com.example.demo.service.BlogsService;
import com.example.demo.util.PageQueryUtil;
import com.example.demo.util.PageResult;
import com.example.demo.vo.PersonalResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class BlogsServiceImpl implements BlogsService {

    private static final Logger logger = LoggerFactory.getLogger(BlogsServiceImpl.class);

    @Resource
    BlogMapper blogMapper;

    @Resource
    BlogCategoryMapper blogCategoryMapper;

    @Resource
    BlogTagMapper blogTagMapper;

    @Resource
    BlogTagRelationMapper blogTagRelationMapper;

    /*查询数据库的博客显示到页面*/
    @Override
    public List<Blog> getBlogList() {
        BlogExample blogExample = new BlogExample();
        blogExample.setOrderByClause("create_time DESC");
        //        logger.info("查询的数据条数：" + blogList.size());
        return blogMapper.selectByExampleWithBLOBs(blogExample);
    }

    /**
     * 博客详情页
     * @param id 博客id
     * @return
     */
    @Override
    public Blog selectDetail(Long id) {
        BlogExample example = new BlogExample();
        BlogExample.Criteria criteria = example.createCriteria();
        criteria.andBlogIdEqualTo(id);
        List<Blog> blogList = blogMapper.selectByExampleWithBLOBs(example);
        return blogList.get(0);
    }

    /**
     * 统计文章数
     * @return
     */
    @Override
    public Integer countBlog() {
        BlogExample example = new BlogExample();
        return blogMapper.countByExample(example);
    }

    /**
     * 保存文章
     * @param blog  文章
     * @return
     */
    @Override
    @Transactional
    public PersonalResult saveBlog(Blog blog) {
        //1-------查询是否有这个博客文章分类 否则就加到默认的 默认分类
        BlogCategoryExample categoryExample = new BlogCategoryExample();
        BlogCategoryExample.Criteria criteria = categoryExample.createCriteria();
        criteria.andCategoryIdEqualTo(blog.getBlogCategoryId());
        List<BlogCategory> blogCategories = blogCategoryMapper.selectByExample(categoryExample);
        if (blogCategories.size() == 0){
            //没有这个分类  加入默认分类
            blog.setBlogCategoryId(0);
            blog.setBlogCategoryName("默认分类");
        }else {
            //有这个分类 设置成这个分类
            blog.setBlogCategoryName(blogCategories.get(0).getCategoryName());
            //分类的排序值加1  代表使用更多
            blogCategories.get(0).setCategoryRank(blogCategories.get(0).getCategoryRank()+1);
        }
        //更新文章分类 主要是排序值的增加
        blogCategoryMapper.updateByPrimaryKeySelective(blogCategories.get(0));

        //2------处理文章标签问题
        String[] tags = blog.getBlogTags().split(",");
        if (tags.length > 3) {
            return PersonalResult.build(300,"标签数量限制为3");
        }

        //3------保存文章
        int i = blogMapper.insertSelective(blog);
        if (i > 0){
            //创建文章与标签的关系
            //新增的tag对象
            List<BlogTag> blogTagsForInsert = new ArrayList<>();
            //所有的tag对象 用来建立关系数据
            List<BlogTag> allTagsList = new ArrayList<>();
            for (int z = 0; z < tags.length; z++){
                BlogTagExample tagExample = new BlogTagExample();
                BlogTagExample.Criteria criteria1 = tagExample.createCriteria();
                criteria1.andTagNameEqualTo(tags[z]);
                List<BlogTag> tagList = blogTagMapper.selectByExample(tagExample);
                if (tagList.size() == 0){
                    //不存在该标签 就创建该标签
                    BlogTag blogTag = new BlogTag();
                    blogTag.setTagName(tags[z]);
                    blogTagsForInsert.add(blogTag);
                }else {
                    //存在该标签的
                    allTagsList.add(tagList.get(0));
                }
            }
            //批量插入新的标签
            if (!CollectionUtils.isEmpty(blogTagsForInsert)){
                blogTagMapper.batchInsertBlogTag(blogTagsForInsert);
            }

            //创建  文章&标签  关系表 列表
            List<BlogTagRelation> blogTagRelationList = new ArrayList<>();
            //新增关系数据
            allTagsList.addAll(blogTagsForInsert);
            for (BlogTag tag : allTagsList){
                BlogTagRelation blogTagRelation = new BlogTagRelation();
                //获取刚才插入的博客文章的id
                Long blogId = blogMapper.maxNewBlog();
                blogTagRelation.setBlogId(blogId);
                blogTagRelation.setTagId(tag.getTagId());
                blogTagRelationList.add(blogTagRelation);
            }

            //批量插入 文章&标签 表
            int status = blogTagRelationMapper.batchInsert(blogTagRelationList);
            if (status > 0){
                return PersonalResult.build(200,"成功");
            }
        }
        return PersonalResult.build(300,"失败");
    }


    /**
     * 博客文章管理查询博客列表
     * @param pageQueryUtil
     * @return
     */
    @Override
    public PageResult getAllBlog(PageQueryUtil pageQueryUtil) {
        List<Blog> blogList = blogMapper.findBlogList(pageQueryUtil);
//        logger.info("查出来的博客列表：" + blogList.toString());
        int total = blogMapper.getTotalBlogs(pageQueryUtil);
        PageResult pageResult = new PageResult(blogList, total, pageQueryUtil.getLimit(), pageQueryUtil.getPage());
        return pageResult;
    }


    /**
     * 根据文章id查询信息
     * @return
     */
    @Override
    public Blog selectOneById(Long blogId) {
        return blogMapper.selectByPrimaryKey(blogId);
    }
}
