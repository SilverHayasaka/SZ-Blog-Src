package com.Silver.service;

import com.Silver.domain.ResponseResult;
import com.Silver.domain.entity.User;

public interface LoginService {
    ResponseResult login(User user);

    ResponseResult logout(Long userId);
}