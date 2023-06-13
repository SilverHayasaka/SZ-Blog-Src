package com.Silver.controller;

import com.Silver.domain.ResponseResult;
import com.Silver.domain.dto.UserDto;
import com.Silver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public ResponseResult listUser(Integer pageNum, Integer pageSize, String userName, String phoneNumber, String status) {
        return userService.listUser(pageNum, pageSize, userName, phoneNumber, status);
    }

    @PostMapping
    public ResponseResult addUser(@RequestBody UserDto userDto) {
        return userService.addUser(userDto);
    }

    @GetMapping("/{id}")
    public ResponseResult userDetail(@PathVariable Long id) {
        return userService.userDetail(id);
    }

    @PutMapping
    public ResponseResult updateUser(@RequestBody UserDto userDto) {
        return userService.updateUser(userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseResult deleteUser(@PathVariable Long id){
        return userService.deleteUser(id);
    }
}
