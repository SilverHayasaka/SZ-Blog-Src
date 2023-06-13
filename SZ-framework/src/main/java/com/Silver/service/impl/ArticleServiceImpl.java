package com.Silver.service.impl;

import com.Silver.constants.SystemConstants;
import com.Silver.domain.ResponseResult;
import com.Silver.domain.dto.AddArticleDto;
import com.Silver.domain.entity.Article;
import com.Silver.domain.entity.ArticleTag;
import com.Silver.domain.entity.Category;
import com.Silver.domain.entity.Tag;
import com.Silver.domain.vo.*;
import com.Silver.mapper.ArticleMapper;
import com.Silver.service.ArticleService;
import com.Silver.service.ArticleTagService;
import com.Silver.service.CategoryService;
import com.Silver.utils.BeanCopyUtils;
import com.Silver.utils.RedisCache;
import com.Silver.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ArticleTagService articleTagService;

    @Override
    public ResponseResult hotArticleList() {

        //查询是否存在缓存
        List<HotArticleVo> vs = (List<HotArticleVo>) redisCache.getCacheObject(SystemConstants.HOT_ARTICLE_CACHE);
        //存在缓存
        if (vs != null && vs.size() > 0) {
            vs.stream()
                    .map(articleVo -> {
                        Integer viewCount = redisCache.getCacheMapValue(SystemConstants.Redis_VIEWCOUNT_KEY, articleVo.getId().toString());
                        return articleVo.setViewCount(viewCount.longValue());
                    })
                    .collect(Collectors.toList());
            return ResponseResult.okResult(vs);
        }

        //查询热门文章，封装成ResponseResult返回
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        //必须是正式文章
        //按照浏览量进行排序
        queryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        //最多只查询10条
        queryWrapper.orderByDesc(Article::getViewCount);
        Page<Article> page = new Page(1, 10);
        page(page, queryWrapper);
        List<Article> articles = page.getRecords();

        List<HotArticleVo> articleVos = new ArrayList<>();

        articleVos.stream()
                .map(articleVo -> {
                    Integer viewCount = redisCache.getCacheMapValue(SystemConstants.Redis_VIEWCOUNT_KEY, articleVo.getId().toString());
                    return articleVo.setViewCount(viewCount.longValue());
                })
                .collect(Collectors.toList());
        //bean拷贝
        vs = BeanCopyUtils.copyBeanList(articles, HotArticleVo.class);

        //写入redis缓存
        redisCache.setCacheObject(SystemConstants.HOT_ARTICLE_CACHE, vs);

        return ResponseResult.okResult(vs);
    }

    @Override
    public ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId) {
        LambdaQueryWrapper<Article> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //如果有categoryId 就要查询时要和传入的相同
        lambdaQueryWrapper.eq(Objects.nonNull(categoryId) && categoryId > 0, Article::getCategoryId, categoryId);
        //状态时正式发布的
        lambdaQueryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        //对isTop进行降序排序
        lambdaQueryWrapper.orderByDesc(Article::getIsTop);
        //分页查询
        Page<Article> page = new Page<>(pageNum, pageSize);
        page(page, lambdaQueryWrapper);


        List<Article> articles = page.getRecords();
        //查询categoryName
        articles.stream()
                .map(article -> article.setCategoryName(categoryService.getById(article.getCategoryId()).getName()))
                .map(article -> {
                    Integer viewCount = redisCache.getCacheMapValue(SystemConstants.Redis_VIEWCOUNT_KEY, article.getId().toString());
                    return article.setViewCount(viewCount.longValue());
                })
                .collect(Collectors.toList());

        //封装查询结果
        List<ArticleListVo> articleListVos = BeanCopyUtils.copyBeanList(page.getRecords(), ArticleListVo.class);


        PageVo pageVo = new PageVo(articleListVos, page.getTotal());

        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult getArticleDetail(Long id) {

        //根据id查询文章
        Article article = getById(id);
        //从redis中获取viewCount
        Integer viewCount = redisCache.getCacheMapValue(SystemConstants.Redis_VIEWCOUNT_KEY, id.toString());

        //查询是否存在缓存
        ArticleDetail articleDetailVo = (ArticleDetail) redisCache.getCacheObject(SystemConstants.ARTICLE_CACHE + id);
        if (articleDetailVo != null) {
            articleDetailVo.setViewCount(viewCount.longValue());
            return ResponseResult.okResult(articleDetailVo);
        }

        article.setViewCount(viewCount.longValue());

        //转化成VO
        articleDetailVo = BeanCopyUtils.copyBean(article, ArticleDetail.class);
        //根据分类id查询分类名称
        Long categoryId = articleDetailVo.getCategoryId();
        Category category = categoryService.getById(categoryId);
        if (category != null) {
            articleDetailVo.setCategoryName(category.getName());
        }

        //写入缓存
        redisCache.setCacheObject(SystemConstants.ARTICLE_CACHE + id, articleDetailVo);

        //封装相应返回
        return ResponseResult.okResult(articleDetailVo);
    }

    @Override
    public ResponseResult updateViewCount(Long id) {
        //更新redis中对应 id的浏览量
        redisCache.incrementCacheMapValue(SystemConstants.Redis_VIEWCOUNT_KEY, id.toString(), 1);
        return ResponseResult.okResult();
    }

    @Override
    @Transactional
    public ResponseResult add(AddArticleDto articleDto) {
        Article article = BeanCopyUtils.copyBean(articleDto, Article.class);
        //保存后会给article的id赋值属性
        save(article);

        //删除缓存
        redisCache.deleteObject(SystemConstants.HOT_ARTICLE_CACHE);
        redisCache.deleteObject(SystemConstants.ARTICLE_LIST_CACHE);

        List<ArticleTag> articleTags = articleDto.getTags().stream()
                .map(tagId -> new ArticleTag(article.getId(), tagId))
                .collect(Collectors.toList());

        articleTagService.saveBatch(articleTags);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult listArticle(Integer pageNum, Integer pageSize, String title, String summary) {
        //查询文章
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(title))
            queryWrapper.like(Article::getTitle, title);
        if (StringUtils.hasText(summary))
            queryWrapper.like(Article::getSummary, summary);

        //分页信息
        Page<Article> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);

        List<AdminArticleVo> articleVos = BeanCopyUtils.copyBeanList(page.getRecords(), AdminArticleVo.class);

        //封装结果
        PageVo pageVo = new PageVo(articleVos, page.getTotal());

        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult updateArticleMessage(Long id) {
        //获取文章
        Article article = getById(id);
        AdminArticleDetailVo articleDetailVo = BeanCopyUtils.copyBean(article, AdminArticleDetailVo.class);

        //获取tag信息
        LambdaQueryWrapper<ArticleTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleTag::getArticleId, id);
        List<ArticleTag> articleTags = articleTagService.list(queryWrapper);
        List<Long> tags = new ArrayList<>();

        for (ArticleTag articleTag : articleTags) {
            tags.add(articleTag.getTagId());
        }

        articleDetailVo.setTags(tags);

        return ResponseResult.okResult(articleDetailVo);
    }

    @Override
    public ResponseResult updateArticle(AddArticleDto articleDto) {
        Article article = BeanCopyUtils.copyBean(articleDto, Article.class);
        LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Article::getId, article.getId());
        updateWrapper.set(Article::getUpdateBy, SecurityUtils.getUserId());
        updateWrapper.set(Article::getUpdateTime, new Date());
        update(article, updateWrapper);

        //删除缓存
        redisCache.deleteObject(SystemConstants.ARTICLE_CACHE + article.getId());
        redisCache.deleteObject(SystemConstants.HOT_ARTICLE_CACHE);

        List<ArticleTag> articleTags = articleDto.getTags().stream()
                .map(tagId -> new ArticleTag(article.getId(), tagId))
                .collect(Collectors.toList());

        //移除旧标签添加新标签
        LambdaQueryWrapper<ArticleTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleTag::getArticleId, article.getId());
        articleTagService.remove(queryWrapper);
        articleTagService.saveBatch(articleTags);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteArticle(Long id) {
        LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.eq(Article::getId, id);
        updateWrapper.set(Article::getDelFlag, SystemConstants.DEL_FLAG);

        update(updateWrapper);

        //删除缓存
        redisCache.deleteObject(SystemConstants.ARTICLE_CACHE + id);
        redisCache.deleteObject(SystemConstants.HOT_ARTICLE_CACHE);

        return ResponseResult.okResult();
    }
}
