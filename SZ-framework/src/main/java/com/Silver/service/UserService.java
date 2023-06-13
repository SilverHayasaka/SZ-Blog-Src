package com.Silver.service;

import com.Silver.domain.ResponseResult;
import com.Silver.domain.dto.UserDto;
import com.Silver.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * 用户表(User)表服务接口
 *
 * @author makejava
 * @since 2023-03-11 10:37:25
 */
public interface UserService extends IService<User> {
    ResponseResult userInfo();

    ResponseResult updateUserInfo(User user);

    ResponseResult register(User user);

    ResponseResult listUser(Integer pageNum, Integer pageSize, String userName, String phoneNumber, String status);

    ResponseResult addUser(UserDto userDto);

    ResponseResult userDetail(Long id);

    ResponseResult updateUser(UserDto userDto);

    ResponseResult deleteUser(Long id);

    String getAvatarUrl(Long userId);
}

