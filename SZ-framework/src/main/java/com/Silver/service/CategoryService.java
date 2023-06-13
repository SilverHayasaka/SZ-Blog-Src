package com.Silver.service;

import com.Silver.domain.ResponseResult;
import com.Silver.domain.entity.Category;
import com.Silver.domain.vo.CategoryVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
 * 分类表(Category)表服务接口
 *
 * @author makejava
 * @since 2023-03-06 10:16:22
 */
public interface CategoryService extends IService<Category> {

    ResponseResult getCategoryList();

    List<CategoryVo> listAllCategory();

    ResponseResult listCategory(Integer pageNum, Integer pageSize, String name, String status);

    ResponseResult addCategory(Category category);

    ResponseResult getCategory(Long id);

    ResponseResult updateCategory(Category category);

    ResponseResult deleteCategory(Long id);
}

