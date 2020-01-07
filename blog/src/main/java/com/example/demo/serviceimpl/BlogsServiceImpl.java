package com.example.demo.serviceimpl;

import com.example.demo.model.*;
import com.example.demo.repository.BlogCategoryMapper;
import com.example.demo.repository.BlogMapper;
import com.example.demo.repository.BlogTagMapper;
import com.example.demo.repository.BlogTagRelationMapper;
import com.example.demo.service.BlogsService;
import com.example.demo.util.MarkDownUtil;
import com.example.demo.util.PageQueryUtil;
import com.example.demo.util.PageResult;
import com.example.demo.util.PatternUtil;
import com.example.demo.vo.BlogDetailVO;
import com.example.demo.vo.PersonalResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

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
        BlogExample.Criteria criteria = blogExample.createCriteria();
        criteria.andIsDeletedEqualTo((byte) 0);
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
    public BlogDetailVO selectDetail(Long id) {
        BlogExample example = new BlogExample();
        BlogExample.Criteria criteria = example.createCriteria();
        criteria.andBlogIdEqualTo(id);
        List<Blog> blogList = blogMapper.selectByExampleWithBLOBs(example);
        Blog blog = blogList.get(0);
        BlogDetailVO blogDetailVO = getBlogDetailVO(blog);
        return blogDetailVO;
    }

    /**
     * 方法抽取
     *
     * @param blog
     * @return
     */
    private BlogDetailVO getBlogDetailVO(Blog blog) {
        if (blog != null && blog.getBlogStatus() == 1) {
            //增加浏览量
            blog.setBlogViews(blog.getBlogViews() + 1);
            blogMapper.updateByPrimaryKey(blog);
            BlogDetailVO blogDetailVO = new BlogDetailVO();
            BeanUtils.copyProperties(blog, blogDetailVO);
            blogDetailVO.setBlogContent(MarkDownUtil.mdToHtml(blogDetailVO.getBlogContent()));
            BlogCategory blogCategory = blogCategoryMapper.selectByPrimaryKey(blog.getBlogCategoryId());
            if (blogCategory == null) {
                blogCategory = new BlogCategory();
                blogCategory.setCategoryId(0);
                blogCategory.setCategoryName("默认分类");
                blogCategory.setCategoryIcon("/admin/dist/img/category/00.png");
            }
            //分类信息
            blogDetailVO.setBlogCategoryIcon(blogCategory.getCategoryIcon());
            if (!StringUtils.isEmpty(blog.getBlogTags())) {
                //标签设置
                List<String> tags = Arrays.asList(blog.getBlogTags().split(","));
                blogDetailVO.setBlogTags(tags);
            }
            //设置评论数
//            Map params = new HashMap();
//            params.put("blogId", blog.getBlogId());
//            params.put("commentStatus", 1);//过滤审核通过的数据
//            blogDetailVO.setCommentCount(blogCommentMapper.getTotalBlogComments(params));
            return blogDetailVO;
        }
        return null;
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


    /**
     * 更新编辑的博客文章
     * @param blog
     * @return
     */
    @Override
    @Transactional
    public PersonalResult updateBlog(Blog blog) {
        Blog blogForUpdate = blogMapper.selectByPrimaryKey(blog.getBlogId());
        //不存在文章
        if (blogForUpdate == null){
            PersonalResult.build(300,"文章数据不存在");
        }
        //存在的话就把数据改成后台修改后的数据
        blogForUpdate.setBlogTitle(blog.getBlogTitle());
        blogForUpdate.setBlogSubUrl(blog.getBlogSubUrl());
        blogForUpdate.setBlogContent(blog.getBlogContent());
        blogForUpdate.setBlogCoverImage(blog.getBlogCoverImage());
        blogForUpdate.setBlogStatus(blog.getBlogStatus());
        blogForUpdate.setEnableComment(blog.getEnableComment());

        //处理分类问题 查询是否有这个分类 没有这个分类的话 就归入默认分类
        BlogCategory category = blogCategoryMapper.selectByPrimaryKey(blog.getBlogCategoryId());
        if (category == null){
            blogForUpdate.setBlogCategoryId(0);
            blogForUpdate.setBlogCategoryName("默认分类");
        }else {//有这个分类就写进那个分类
            blogForUpdate.setBlogCategoryId(category.getCategoryId());
            blogForUpdate.setBlogCategoryName(category.getCategoryName());
//            //分类的排序值 + 1
//            category.setCategoryRank(category.getCategoryRank()+1);
        }
//
//        //更新文章分类 主要是排序值的增加
//        blogCategoryMapper.updateByPrimaryKeySelective(category);

        //处理标签问题
        String[] tags = blog.getBlogTags().split(",");
        if (tags.length > 3) {
            return PersonalResult.build(300,"标签数量超长");
        }
        //设置博客文章的标签
        blogForUpdate.setBlogTags(blog.getBlogTags());

        //新增的tag对象
        List<BlogTag> tagListForInsert = new ArrayList<>();
        //存放所有tag的对象，用例建立文章和标签之间的关系
        List<BlogTag> allTagsList = new ArrayList<>();
        //遍历文章的标签
        for (int t = 0; t < tags.length; t++) {
            //根据tag名称去查 是否存在该标签
            BlogTagExample tagExample = new BlogTagExample();
            BlogTagExample.Criteria criteria1 = tagExample.createCriteria();
            criteria1.andTagNameEqualTo(tags[t]);
            List<BlogTag> tagList = blogTagMapper.selectByExample(tagExample);
            if (tagList.size() == 0) {
                //不存在该标签 就创建该标签
                BlogTag blogTag = new BlogTag();
                blogTag.setTagName(tags[t]);
                //加入到新增tag列表
                tagListForInsert.add(blogTag);
            } else {
                //存在该标签的 直接加到所有标签的列表
                allTagsList.add(tagList.get(0));
            }
        }

        //批量插入新的标签
        if (!CollectionUtils.isEmpty(tagListForInsert)){
            blogTagMapper.batchInsertBlogTag(tagListForInsert);
        }

        //用来存放博客和标签的关系的列表
        List<BlogTagRelation> blogTagRelations = new ArrayList<>();
        //新增关系数据 加上存放 新标签 的列表
        allTagsList.addAll(tagListForInsert);

        for (BlogTag tag : allTagsList){
            BlogTagRelation blogTagRelation = new BlogTagRelation();
            blogTagRelation.setBlogId(blog.getBlogId());
            blogTagRelation.setTagId(tag.getTagId());
            blogTagRelations.add(blogTagRelation);
        }

        //删除原有的关系
        BlogTagRelationExample relationExample = new BlogTagRelationExample();
        BlogTagRelationExample.Criteria criteria = relationExample.createCriteria();
        criteria.andBlogIdEqualTo(blog.getBlogId());
        blogTagRelationMapper.deleteByExample(relationExample);

        //建立新的关系
        int status = blogTagRelationMapper.batchInsert(blogTagRelations);
        //保存博客信息
        int updateBlog = blogMapper.updateByPrimaryKeyWithBLOBs(blogForUpdate);
        if (status > 0 && updateBlog > 0){
            return PersonalResult.build(200,"成功");
        }
        return PersonalResult.build(300,"失败");
    }


    /**
     * 删除博客文章 即根据博客文章id修改  是否删除  字段
     * @param ids
     * @return
     */
    @Override
    public Boolean deleteBlog(Integer[] ids) {
        int i = blogMapper.deleteBatch(ids);
        if (i > 0){
            return true;
        }
        return false;
    }


    /**
     * 热门文章 查询5条
     * @return
     */
    @Override
    public List<Blog> hotBlog() {
        return blogMapper.hotBlog();
    }


    /**
     *首页搜索功能
     * @return
     */
    @Override
    public List<Blog> searchBlog(String keyword) {
        //如果符合规则 就查数据库
        if (PatternUtil.validKeyword(keyword)) {
            BlogExample blogExample = new BlogExample();
            blogExample.or().andBlogTitleLike('%'+keyword+'%');
            blogExample.or().andBlogTagsLike('%'+keyword+'%');
            blogExample.or().andBlogCategoryNameLike('%'+keyword+'%');
            blogExample.setOrderByClause("create_time DESC");
            List<Blog> blogList = blogMapper.selectByExampleWithBLOBs(blogExample);
            List<Blog> newBlog = new ArrayList<>();
            for (Blog blog : blogList){
                if (blog.getIsDeleted() == 1){
                    continue;
                }
                newBlog.add(blog);
            }
            return newBlog;
        }
        return new ArrayList<>();
    }
}
