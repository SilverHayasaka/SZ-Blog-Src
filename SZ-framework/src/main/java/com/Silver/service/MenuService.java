package com.Silver.service;

import com.Silver.domain.ResponseResult;
import com.Silver.domain.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
 * 菜单权限表(Menu)表服务接口
 *
 * @author makejava
 * @since 2023-03-18 10:52:32
 */
public interface MenuService extends IService<Menu> {

    List<String> selectPermsByUserId(Long id);

    List<Menu> selectRouterMenuTreeByUserId(Long id);


    ResponseResult listMenu(String status, String menuName);

    ResponseResult addMenu(Menu menu);

    ResponseResult getMenuDetail(Long id);

    ResponseResult updateMenu(Menu menu);

    ResponseResult deleteMenu(Long id);

    ResponseResult getTreeSelect();

    ResponseResult getRoleTreeSelect(Long id);
}

