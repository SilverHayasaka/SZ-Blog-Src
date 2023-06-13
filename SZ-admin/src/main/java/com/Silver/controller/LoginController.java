package com.Silver.controller;

import com.Silver.domain.ResponseResult;
import com.Silver.domain.entity.LoginUser;
import com.Silver.domain.entity.Menu;
import com.Silver.domain.entity.User;
import com.Silver.domain.vo.AdminUserInfoVo;
import com.Silver.domain.vo.RoutersVo;
import com.Silver.domain.vo.UserInfoVo;
import com.Silver.enums.AppHttpCodeEnum;
import com.Silver.exception.SystemException;
import com.Silver.service.BlogLoginService;
import com.Silver.service.LoginService;
import com.Silver.service.MenuService;
import com.Silver.service.RoleService;
import com.Silver.utils.BeanCopyUtils;
import com.Silver.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.util.List;

@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private RoleService roleService;


    @PostMapping("/user/login")
    public ResponseResult login(@RequestBody User user) {
        if (!StringUtils.hasText(user.getUserName())) {
            //提示 必须要用户名
            //throw new RuntimeException();
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        return loginService.login(user);
    }

    @GetMapping("/getInfo")
    public ResponseResult<AdminUserInfoVo> getInfo() {
        //获取当前登录的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        //根据用户id查询权限信息
        List<String> perms = menuService.selectPermsByUserId(loginUser.getUser().getId());
        //根据用户id查询角色信息
        List<String> roleKeyList = roleService.selectRoleKeyByUserId(loginUser.getUser().getId());

        //获取用户信息
        User user = loginUser.getUser();
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(user, UserInfoVo.class);
        //封装数据返回

        AdminUserInfoVo adminUserInfoVo = new AdminUserInfoVo(perms, roleKeyList, userInfoVo);
        return ResponseResult.okResult(adminUserInfoVo);
    }

    @GetMapping("/getRouters")
    public ResponseResult<RoutersVo> getRouters() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        //查询menu 记过是tree的形式
        List<Menu> menus = menuService.selectRouterMenuTreeByUserId(loginUser.getUser().getId());
        //封装数据返回
        return ResponseResult.okResult(new RoutersVo(menus));
    }

    @PostMapping("/user/logout")
    public ResponseResult logout() {
        //获取当前用户登录的id
        Long userId = SecurityUtils.getUserId();
        //删除redis中对应的值
        return loginService.logout(userId);
    }
}
