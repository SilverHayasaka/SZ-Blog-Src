package com.Silver.controller;

import com.Silver.domain.ResponseResult;
import com.Silver.domain.entity.Category;
import com.Silver.domain.vo.AdminCategoryVo;
import com.Silver.domain.vo.CategoryVo;
import com.Silver.domain.vo.ExcelCategoryVo;
import com.Silver.enums.AppHttpCodeEnum;
import com.Silver.service.CategoryService;
import com.Silver.utils.BeanCopyUtils;
import com.Silver.utils.WebUtils;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.alibaba.fastjson.JSON.*;

@RestController
@RequestMapping("/content/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/listAllCategory")
    public ResponseResult listAllCategory() {
        return ResponseResult.okResult(categoryService.listAllCategory());
    }

    @PreAuthorize("@ps.hasPermission('content:category:export')")
    @GetMapping("/export")
    public void export(HttpServletResponse response) {

        try {
            //设置请求头
            WebUtils.setDownLoadHeader("分类.xlsx", response);
            //获取需要到处的数据
            List<Category> category = categoryService.list();

            List<ExcelCategoryVo> excelCategoryVos = BeanCopyUtils.copyBeanList(category, ExcelCategoryVo.class);
            //把数据写入到Excel中
            EasyExcel.write(response.getOutputStream(), ExcelCategoryVo.class).autoCloseStream(Boolean.FALSE).sheet("分类导出")
                    .doWrite(excelCategoryVos);
        } catch (IOException e) {
            response.reset();
            ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
            WebUtils.renderString(response, toJSONString(result));
        }

        //如果出现异常也要响应json数据
        ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
        WebUtils.renderString(response, JSON.toJSONString(result));
    }


    @GetMapping("/list")
    public ResponseResult listCategory(Integer pageNum, Integer pageSize, String name, String status) {
        return categoryService.listCategory(pageNum, pageSize, name, status);
    }

    @PostMapping
    public ResponseResult addCategory(@RequestBody Category category){
        return categoryService.addCategory(category);
    }

    @GetMapping("/{id}")
    public ResponseResult getCategory(@PathVariable Long id){
        return categoryService.getCategory(id);
    }

    @PutMapping
    public ResponseResult updateCategory(@RequestBody Category category){
        return categoryService.updateCategory(category);
    }

    @DeleteMapping("/{id}")
    public ResponseResult deleteCategory(@PathVariable Long id){
        return categoryService.deleteCategory(id);
    }
}
