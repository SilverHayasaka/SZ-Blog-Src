package com.Silver.job;

import com.Silver.domain.entity.Article;
import com.Silver.service.ArticleService;
import com.Silver.utils.RedisCache;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UpdateViewCountJob {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ArticleService articleService;

    @Scheduled(cron = "* 0/30 * * * ? ")
    @Transactional
    public void updateViewCount() {
        //获取redis中的浏览量
        Map<String, Integer> viewCountMap = redisCache.getCacheMap("article:viewCount");

        List<Article> articles = viewCountMap.entrySet()
                .stream()
                .map(entry -> new Article(Long.valueOf(entry.getKey()), entry.getValue().longValue()))
                .collect(Collectors.toList());

        //更新到数据库中
        articleService.updateBatchById(articles);

        //因为有tableField自动填充获取不到token，无法更新updateBy，所以不能用上面的方法
        /*for (Article article : articles) {
            LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Article::getId, article.getId());
            updateWrapper.set(Article::getViewCount, article.getViewCount());
            articleService.update(updateWrapper);
        }*/
    }
}