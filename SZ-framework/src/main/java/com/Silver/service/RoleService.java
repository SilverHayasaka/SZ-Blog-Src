package com.Silver.service;

import com.Silver.domain.ResponseResult;
import com.Silver.domain.dto.RoleDto;
import com.Silver.domain.dto.RoleStatusDto;
import com.Silver.domain.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
 * 角色信息表(Role)表服务接口
 *
 * @author makejava
 * @since 2023-03-18 10:58:21
 */
public interface RoleService extends IService<Role> {

    List<String> selectRoleKeyByUserId(Long id);

    ResponseResult roleList(Integer pageNum, Integer pageSize, String roleName, String status);

    ResponseResult changeStatus(RoleStatusDto roleStatusDto);

    ResponseResult addRole(RoleDto roleDto);

    ResponseResult getRoleDetail(Long id);

    ResponseResult updateRole(RoleDto roleDto);

    ResponseResult deleteRole(Long id);

    ResponseResult listAllRole();

}

