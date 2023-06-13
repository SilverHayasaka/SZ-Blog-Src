package com.Silver.mapper;

import com.Silver.domain.entity.Menu;
import com.Silver.domain.vo.MenuTreeVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;


/**
 * 菜单权限表(Menu)表数据库访问层
 *
 * @author makejava
 * @since 2023-03-18 10:52:29
 */
public interface MenuMapper extends BaseMapper<Menu> {

    List<String> selectPermsById(Long id);

    List<Menu> selectAllRouterMenu();

    List<Menu> selectAllMenuTree();

    List<Menu> selectRouterMenuTreeByUserId(Long id);

    List<Long> selectRoleTreeById(Long id);
}


