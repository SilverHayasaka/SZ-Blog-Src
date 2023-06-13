package com.Silver.service.impl;

import com.Silver.constants.SystemConstants;
import com.Silver.domain.ResponseResult;
import com.Silver.domain.dto.RoleDto;
import com.Silver.domain.dto.RoleStatusDto;
import com.Silver.domain.entity.Role;
import com.Silver.domain.entity.RoleMenu;
import com.Silver.domain.vo.PageVo;
import com.Silver.domain.vo.RoleVo;
import com.Silver.mapper.RoleMapper;
import com.Silver.service.RoleMenuService;
import com.Silver.service.RoleService;
import com.Silver.utils.BeanCopyUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色信息表(Role)表服务实现类
 *
 * @author makejava
 * @since 2023-03-18 10:58:21
 */
@Service("roleService")
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RoleMenuService roleMenuService;

    @Override
    public List<String> selectRoleKeyByUserId(Long id) {
        //判断是否是管理员 如果是返回集合中有admin
        if (id == 1L) {
            List<String> roleKeys = new ArrayList<>();
            roleKeys.add("admin");
            return roleKeys;
        }
        //否则查询用户所具有的角色信息
        return getBaseMapper().selectRoleKeyByUserId(id);
    }

    @Override
    public ResponseResult roleList(Integer pageNum, Integer pageSize, String roleName, String status) {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        //roleName名字模糊查询
        if (StringUtils.hasText(roleName)) queryWrapper.like(Role::getRoleName, roleName);
        if (StringUtils.hasText(status)) queryWrapper.eq(Role::getStatus, status);

        //按rolesort升序
        queryWrapper.orderByAsc(Role::getRoleSort);

        //分页
        Page<Role> page = new Page(pageNum, pageSize);
        page(page, queryWrapper);

        //封装成RoleVo
        List<RoleVo> roleVoList = BeanCopyUtils.copyBeanList(page.getRecords(), RoleVo.class);

        return ResponseResult.okResult(new PageVo(roleVoList, page.getTotal()));

    }

    @Override
    public ResponseResult changeStatus(RoleStatusDto roleStatusDto) {
        LambdaUpdateWrapper<Role> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Role::getId, roleStatusDto.getId());
        updateWrapper.set(Role::getStatus, roleStatusDto.getStatus());

        update(updateWrapper);
        return ResponseResult.okResult();
    }

    @Override
    @Transactional
    public ResponseResult addRole(RoleDto roleDto) {
        //复制属性
        Role role = BeanCopyUtils.copyBean(roleDto, Role.class);

        save(role);

        List<RoleMenu> roleMenus = new ArrayList<>();

        for (Long menuId : roleDto.getMenuIds()) {
            roleMenus.add(new RoleMenu(role.getId(), menuId));
        }

        roleMenuService.saveBatch(roleMenus);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getRoleDetail(Long id) {
        Role role = getById(id);
        RoleVo roleVo = BeanCopyUtils.copyBean(role, RoleVo.class);

        return ResponseResult.okResult(roleVo);
    }

    @Override
    @Transactional
    public ResponseResult updateRole(RoleDto roleDto) {
        //复制属性
        Role role = BeanCopyUtils.copyBean(roleDto, Role.class);
        LambdaUpdateWrapper<Role> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Role::getId, role.getId());

        update(role, updateWrapper);

        List<RoleMenu> roleMenus = new ArrayList<>();

        for (Long menuId : roleDto.getMenuIds()) {
            roleMenus.add(new RoleMenu(role.getId(), menuId));
        }

        LambdaQueryWrapper<RoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoleMenu::getRoleId, role.getId());
        roleMenuService.remove(queryWrapper);
        roleMenuService.saveBatch(roleMenus);

        return ResponseResult.okResult();
    }

    @Override
    @Transactional
    public ResponseResult deleteRole(Long id) {
        LambdaUpdateWrapper<Role> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Role::getId, id);
        updateWrapper.set(Role::getDelFlag, SystemConstants.DEL_FLAG);

        update(updateWrapper);

        LambdaQueryWrapper<RoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoleMenu::getRoleId, id);
        roleMenuService.remove(queryWrapper);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult listAllRole() {
        List<Role> list = list();
        List<RoleVo> roleVoList = BeanCopyUtils.copyBeanList(list, RoleVo.class);

        return ResponseResult.okResult(roleVoList);
    }
}

