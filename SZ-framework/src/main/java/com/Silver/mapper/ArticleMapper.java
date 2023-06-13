package com.Silver.mapper;

import com.Silver.domain.entity.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

public interface ArticleMapper extends BaseMapper<Article> {
}
