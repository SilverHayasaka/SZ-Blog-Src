package com.Silver.controller;

import com.Silver.domain.ResponseResult;
import com.Silver.domain.dto.RoleDto;
import com.Silver.domain.dto.RoleStatusDto;
import com.Silver.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public ResponseResult roleList(Integer pageNum, Integer pageSize, String roleName, String status) {
        return roleService.roleList(pageNum, pageSize, roleName, status);
    }

    @PutMapping("/changeStatus")
    public ResponseResult changeStatus(@RequestBody RoleStatusDto roleStatusDto) {
        return roleService.changeStatus(roleStatusDto);
    }

    @PostMapping
    public ResponseResult addRole(@RequestBody RoleDto roleDto) {
        return roleService.addRole(roleDto);
    }

    @GetMapping("/{id}")
    public ResponseResult getRoleDetail(@PathVariable Long id) {
        return roleService.getRoleDetail(id);
    }

    @PutMapping
    public ResponseResult updateRole(@RequestBody RoleDto roleDto) {
        return roleService.updateRole(roleDto);
    }

    @DeleteMapping("/{id}")
    public ResponseResult deleteRole(@PathVariable Long id){
        return roleService.deleteRole(id);
    }

    @GetMapping("/listAllRole")
    public ResponseResult listAllRole(){
        return roleService.listAllRole();
    }
}
