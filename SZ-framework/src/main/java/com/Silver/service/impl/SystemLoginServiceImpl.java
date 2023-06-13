package com.Silver.service.impl;

import com.Silver.domain.ResponseResult;
import com.Silver.domain.entity.LoginUser;
import com.Silver.domain.entity.User;
import com.Silver.service.LoginService;
import com.Silver.utils.JwtUtil;
import com.Silver.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class SystemLoginServiceImpl implements LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Override
    public ResponseResult login(User user) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        //判断是否认准通过
        if (Objects.isNull(authenticate)) {
            throw new RuntimeException("用户名或密码错误");
        }
        //获取userid生成token
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = loginUser.getUser().getId().toString();
        String jwt = JwtUtil.createJWT(userId);
        //把用户信息存入redis
        redisCache.setCacheObject("login:" + userId, loginUser);

        //把token封装 返回
        Map<String, String> map = new HashMap<>();
        map.put("token", jwt);

        return ResponseResult.okResult(map);
    }

    @Override
    public ResponseResult logout(Long userId) {
        redisCache.deleteObject("login:" + userId);
        return ResponseResult.okResult();
    }
}
