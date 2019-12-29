package com.example.demo.serviceimpl;

import com.example.demo.model.BlogTag;
import com.example.demo.model.BlogTagExample;
import com.example.demo.repository.BlogTagMapper;
import com.example.demo.service.BlogTagService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BlogTagServiceImpl implements BlogTagService {

    @Resource
    BlogTagMapper blogTagMapper;

    /**
     * 统计标签的数目
     * @return
     */
    @Override
    public Integer countTag() {
        BlogTagExample example = new BlogTagExample();
        return blogTagMapper.countByExample(example);
    }

    /**
     * 获取标签的名字
     * @return
     */
    @Override
    public List<BlogTag> selectTag() {
        BlogTagExample example = new BlogTagExample();
        return blogTagMapper.selectByExample(example);
    }
}
