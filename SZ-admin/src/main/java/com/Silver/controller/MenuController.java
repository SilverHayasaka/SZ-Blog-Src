package com.Silver.controller;

import com.Silver.domain.ResponseResult;
import com.Silver.domain.entity.Menu;
import com.Silver.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/menu")
@PreAuthorize("@ps.hasPermission('system:menu')")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("list")
    public ResponseResult listMenu(String status, String menuName) {
        return menuService.listMenu(status, menuName);
    }

    @PostMapping
    public ResponseResult addMenu(@RequestBody Menu menu) {
        return menuService.addMenu(menu);
    }

    @GetMapping("/{id}")
    public ResponseResult getMenuDetail(@PathVariable Long id) {
        return menuService.getMenuDetail(id);
    }

    @PutMapping
    public ResponseResult updateMenu(@RequestBody Menu menu) {
        return menuService.updateMenu(menu);
    }

    @DeleteMapping("/{id}")
    public ResponseResult deleteMenu(@PathVariable Long id) {
        return menuService.deleteMenu(id);
    }

    @GetMapping("/treeselect")
    @PreAuthorize("@ps.hasPermission('system:role')")
    public ResponseResult getTreeSelect(){
        return menuService.getTreeSelect();
    }

    @GetMapping("/roleMenuTreeselect/{id}")
    @PreAuthorize("@ps.hasPermission('system:role')")
    public ResponseResult getRoleTreeSelect(@PathVariable Long id){
        return menuService.getRoleTreeSelect(id);
    }
}
