package com.Silver.service.impl;

import com.Silver.domain.entity.RoleMenu;
import com.Silver.mapper.RoleMenuMapper;
import com.Silver.service.RoleMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 角色和菜单关联表(SysRoleMenu)表服务实现类
 *
 * @author makejava
 * @since 2023-03-25 20:30:30
 */
@Service("sysRoleMenuService")
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {

}

