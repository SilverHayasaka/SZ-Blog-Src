package com.Silver.service;

import com.Silver.domain.ResponseResult;
import com.Silver.domain.dto.AddArticleDto;
import com.Silver.domain.entity.Article;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ArticleService extends IService<Article> {
    ResponseResult hotArticleList();

    ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId);

    ResponseResult getArticleDetail(Long id);

    ResponseResult updateViewCount(Long id);

    ResponseResult add(AddArticleDto articleDto);

    ResponseResult listArticle(Integer pageNum, Integer pageSize, String title, String summary);

    ResponseResult updateArticleMessage(Long id);

    ResponseResult updateArticle(AddArticleDto articleDto);

    ResponseResult deleteArticle(Long id);
}
