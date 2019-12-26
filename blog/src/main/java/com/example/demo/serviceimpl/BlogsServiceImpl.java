package com.example.demo.serviceimpl;

import com.example.demo.model.Blog;
import com.example.demo.model.BlogExample;
import com.example.demo.repository.BlogMapper;
import com.example.demo.service.BlogsService;
import com.example.demo.vo.PersonalResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BlogsServiceImpl implements BlogsService {

    private static final Logger logger = LoggerFactory.getLogger(BlogsServiceImpl.class);

    @Resource
    BlogMapper blogMapper;

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
    public Blog selectDetail(Integer id) {
        BlogExample example = new BlogExample();
        BlogExample.Criteria criteria = example.createCriteria();
        criteria.andBlogIdEqualTo(id);
        List<Blog> blogList = blogMapper.selectByExampleWithBLOBs(example);
        return blogList.get(0);
    }
}
