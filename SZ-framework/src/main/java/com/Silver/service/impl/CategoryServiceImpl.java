package com.Silver.service.impl;

import com.Silver.constants.SystemConstants;
import com.Silver.domain.ResponseResult;
import com.Silver.domain.entity.Article;
import com.Silver.domain.entity.Category;
import com.Silver.domain.vo.AdminCategoryVo;
import com.Silver.domain.vo.CategoryVo;
import com.Silver.domain.vo.PageVo;
import com.Silver.mapper.CategoryMapper;
import com.Silver.service.ArticleService;
import com.Silver.service.CategoryService;
import com.Silver.utils.BeanCopyUtils;
import com.Silver.utils.RedisCache;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 分类表(Category)表服务实现类
 *
 * @author makejava
 * @since 2023-03-06 10:16:23
 */
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private RedisCache redisCache;

    @Override
    public ResponseResult getCategoryList() {
        //查询redis中是否有缓存
        List<CategoryVo> categoryVos = (List<CategoryVo>) redisCache.getCacheObject(SystemConstants.REDIS_CATEGORY_CACHE);
        if (categoryVos != null && categoryVos.size() > 0) {
            return ResponseResult.okResult(categoryVos);
        }
        //查询文章表状态为已发布的文章
        LambdaQueryWrapper<Article> articleWrapper = new LambdaQueryWrapper<>();
        articleWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        List<Article> articlesList = articleService.list(articleWrapper);

        //查询有效文章
        articlesList.stream()
                .filter(category ->
                        SystemConstants.STATUS_NORMAL.equals(category.getStatus())
                ).collect(Collectors.toList());

        //获取文章的分类id，并且去重
        Set<Long> categoryIds = articlesList.stream()
                .map(Article::getCategoryId)
                .collect(Collectors.toSet());
        //查询分类表
        List<Category> categories = listByIds(categoryIds);

        //封装vo
        categoryVos = BeanCopyUtils.copyBeanList(categories, CategoryVo.class);

        //写入redis缓存
        redisCache.setCacheObject(SystemConstants.REDIS_CATEGORY_CACHE, categoryVos);

        return ResponseResult.okResult(categoryVos);
    }

    @Override
    public List<CategoryVo> listAllCategory() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getStatus, SystemConstants.NORMAL);
        List<Category> list = list(wrapper);
        List<CategoryVo> categoryVos = BeanCopyUtils.copyBeanList(list, CategoryVo.class);
        return categoryVos;
    }

    @Override
    public ResponseResult listCategory(Integer pageNum, Integer pageSize, String name, String status) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) queryWrapper.like(Category::getName, name);
        if (StringUtils.hasText(status)) queryWrapper.eq(Category::getStatus, status);

        Page<Category> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);

        List<AdminCategoryVo> adminCategoryVos = BeanCopyUtils.copyBeanList(page.getRecords(), AdminCategoryVo.class);

        return ResponseResult.okResult(new PageVo(adminCategoryVos, page.getTotal()));
    }

    @Override
    public ResponseResult addCategory(Category category) {
        save(category);
        //删除redis缓存
        redisCache.deleteObject(SystemConstants.REDIS_CATEGORY_CACHE);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getCategory(Long id) {
        Category category = getById(id);
        AdminCategoryVo adminCategoryVo = BeanCopyUtils.copyBean(category, AdminCategoryVo.class);

        return ResponseResult.okResult(adminCategoryVo);
    }

    @Override
    public ResponseResult updateCategory(Category category) {
        LambdaUpdateWrapper<Category> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Category::getId, category.getId());

        update(category, updateWrapper);
        //删除redis缓存
        redisCache.deleteObject(SystemConstants.REDIS_CATEGORY_CACHE);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteCategory(Long id) {
        LambdaUpdateWrapper<Category> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Category::getId, id);
        updateWrapper.set(Category::getDelFlag, SystemConstants.DEL_FLAG);

        update(updateWrapper);
        //删除redis缓存
        redisCache.deleteObject(SystemConstants.REDIS_CATEGORY_CACHE);
        return ResponseResult.okResult();
    }
}

