package com.Silver.service;

import com.Silver.domain.ResponseResult;
import com.Silver.domain.entity.User;

public interface BlogLoginService {
    ResponseResult login(User user);

    ResponseResult logout();
}
