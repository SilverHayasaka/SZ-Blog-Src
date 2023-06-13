package com.Silver.controller;

import com.Silver.domain.ResponseResult;
import com.Silver.domain.dto.AddArticleDto;
import com.Silver.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @PostMapping
    public ResponseResult add(@RequestBody AddArticleDto articleDto) {
        return articleService.add(articleDto);
    }

    @GetMapping("/list")
    public ResponseResult list(Integer pageNum, Integer pageSize, String title, String summary) {
        return articleService.listArticle(pageNum, pageSize, title, summary);
    }

    @GetMapping("/{id}")
    public ResponseResult updateArticle(@PathVariable Long id) {
        return articleService.updateArticleMessage(id);
    }

    @PutMapping
    public ResponseResult update(@RequestBody AddArticleDto articleDto) {
        return articleService.updateArticle(articleDto);

    }

    @DeleteMapping("/{id}")
    public ResponseResult delete(@PathVariable Long id) {
        return articleService.deleteArticle(id);
    }
}