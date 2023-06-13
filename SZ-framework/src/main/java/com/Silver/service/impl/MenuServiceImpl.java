package com.Silver.service.impl;

import com.Silver.domain.ResponseResult;
import com.Silver.domain.entity.Menu;
import com.Silver.domain.vo.MenuTreeVo;
import com.Silver.domain.vo.MenuVo;
import com.Silver.domain.vo.RoleTreeVo;
import com.Silver.enums.AppHttpCodeEnum;
import com.Silver.mapper.MenuMapper;
import com.Silver.service.MenuService;
import com.Silver.utils.BeanCopyUtils;
import com.Silver.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.Silver.constants.SystemConstants.*;

/**
 * 菜单权限表(Menu)表服务实现类
 *
 * @author makejava
 * @since 2023-03-18 10:52:32
 */
@Service("menuService")
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Override
    public List<String> selectPermsByUserId(Long id) {
        //如果是超级管理员，返回所有的权限
        if (SecurityUtils.isAdmin()) {
            LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(Menu::getMenuType, MENU, BUTTON);
            wrapper.eq(Menu::getStatus, STATUS_NORMAL);
            List<Menu> menus = list(wrapper);
            List<String> perms = menus.stream()
                    .map(Menu::getPerms)
                    .collect(Collectors.toList());
            return perms;
        }
        //否则返回所具有的权限
        return getBaseMapper().selectPermsById(id);
    }

    @Override
    public List<Menu> selectRouterMenuTreeByUserId(Long id) {
        MenuMapper menuMapper = getBaseMapper();
        List<Menu> menus = null;
        //判断是否是管理员
        if (SecurityUtils.isAdmin()) {
            //如果是 返回所有符合要求的menu
            menus = menuMapper.selectAllRouterMenu();
        } else {
            //否则 当前用户所具有的menu
            menus = menuMapper.selectRouterMenuTreeByUserId(id);
        }
        //构建tree
        //先找出第一层的菜单 然后去找他们的子菜单设置到children属性中

        return buildMenuTree(menus, 0L);
    }

    @Override
    public ResponseResult listMenu(String status, String menuName) {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(status)) {
            queryWrapper.eq(Menu::getStatus, status);
        }
        if (StringUtils.hasText(menuName)) {
            queryWrapper.like(Menu::getMenuName, menuName);
        }
        queryWrapper.orderByAsc(Menu::getOrderNum);
        List<Menu> menuList = list(queryWrapper);

        return ResponseResult.okResult(menuList);
    }

    @Override
    public ResponseResult addMenu(Menu menu) {
        save(menu);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getMenuDetail(Long id) {
        Menu menu = getById(id);
        MenuVo menuVo = BeanCopyUtils.copyBean(menu, MenuVo.class);

        return ResponseResult.okResult(menuVo);
    }

    @Override
    public ResponseResult updateMenu(Menu menu) {
        if (menu.getParentId().equals(menu.getId())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.MENU_UPDATE_ERROR);
        }

        updateById(menu);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteMenu(Long id) {
        //搜索是否有子菜单
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Menu::getParentId, id);
        List<Menu> menuList = list(queryWrapper);

        //存在子菜单
        if (menuList.size() > 0) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DELETE_MENU_ERROR);
        }

        LambdaUpdateWrapper<Menu> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Menu::getId, id);
        updateWrapper.set(Menu::getDelFlag, DEL_FLAG);

        update(updateWrapper);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getTreeSelect() {
        MenuMapper menuMapper = getBaseMapper();
        List<Menu> menus = menuMapper.selectAllMenuTree();

        //属性拷贝
        List<MenuTreeVo> menuTreeVos = menus.stream()
                .map(menu -> new MenuTreeVo(menu.getId(), menu.getParentId(),
                        menu.getMenuName(), null))
                .collect(Collectors.toList());

        //生成树
        return ResponseResult.okResult(buildMenuTreeVo(menuTreeVos, 0L));

    }

    @Override
    public ResponseResult getRoleTreeSelect(Long id) {
        //查询权限
        List<Long> ids = getBaseMapper().selectRoleTreeById(id);

        //生成返回Vo
        return ResponseResult.okResult(new RoleTreeVo((List<MenuTreeVo>) getTreeSelect().getData(), ids));
    }

    private List<Menu> buildMenuTree(List<Menu> menus, Long parentId) {
        return menus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .map(menu -> menu.setChildren(getChildren(menu, menus)))
                .collect(Collectors.toList());
    }

    private List<MenuTreeVo> buildMenuTreeVo(List<MenuTreeVo> menus, Long parentId) {
        return menus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .map(menu -> menu.setChildren(getChildrenVo(menu, menus)))
                .collect(Collectors.toList());
    }

    private List<MenuTreeVo> getChildrenVo(MenuTreeVo menu, List<MenuTreeVo> menus) {
        return menus.stream()
                .filter(m -> m.getParentId().equals(menu.getId()))
                .map(m -> m.setChildren(getChildrenVo(m, menus)))
                .collect(Collectors.toList());

    }

    /**
     * 获取存入参数的 子Menu集合
     *
     * @param menu
     * @param menus
     * @return
     */
    private List<Menu> getChildren(Menu menu, List<Menu> menus) {
        return menus.stream()
                .filter(m -> m.getParentId().equals(menu.getId()))
                .map(m -> m.setChildren(getChildren(m, menus)))
                .collect(Collectors.toList());
    }
}

