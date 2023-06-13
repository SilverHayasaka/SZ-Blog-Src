package com.Silver.service.impl;

import com.Silver.constants.SystemConstants;
import com.Silver.domain.ResponseResult;
import com.Silver.domain.dto.UserDto;
import com.Silver.domain.entity.Role;
import com.Silver.domain.entity.User;
import com.Silver.domain.entity.UserRole;
import com.Silver.domain.vo.*;
import com.Silver.enums.AppHttpCodeEnum;
import com.Silver.exception.SystemException;
import com.Silver.mapper.UserMapper;
import com.Silver.service.RoleService;
import com.Silver.service.UserRoleService;
import com.Silver.service.UserService;
import com.Silver.utils.BeanCopyUtils;
import com.Silver.utils.RedisCache;
import com.Silver.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户表(User)表服务实现类
 *
 * @author makejava
 * @since 2023-03-11 10:37:27
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RedisCache redisCache;

    @Override
    public ResponseResult userInfo() {

        //获取当前用户id
        Long userId = SecurityUtils.getUserId();

        //查询缓存
        UserInfoVo vo = (UserInfoVo) redisCache.getCacheObject(SystemConstants.USER_INFO_CACHE + userId);
        if (vo != null) {
            redisCache.redisTemplate.expire(SystemConstants.USER_INFO_CACHE, Duration.ofMinutes(5));
            return ResponseResult.okResult(vo);
        }

        //根据用户id查询用户信息
        User user = getById(userId);
        //封装成UserInfoVo
        vo = BeanCopyUtils.copyBean(user, UserInfoVo.class);

        //写缓存
        redisCache.setCacheObject(SystemConstants.USER_INFO_CACHE + userId, vo, Duration.ofMinutes(5));
        return ResponseResult.okResult(vo);
    }

    @Override
    public ResponseResult updateUserInfo(User user) {
        updateById(user);

        //删除缓存
        redisCache.deleteObject(SystemConstants.USER_INFO_CACHE + user.getId());
        redisCache.deleteObject(SystemConstants.USER_AVATAR_CACHE + user.getId());

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult register(User user) {
        //判空
        nullJudge(user);
        existJudge(user);
        //对密码进行加密
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        //存入数据库
        save(user);
        return ResponseResult.okResult();
    }

    private void nullJudge(User user) {
        if (!StringUtils.hasText(user.getUserName())) {
            throw new SystemException(AppHttpCodeEnum.USERNAME_NOT_NULL);
        }
        if (!StringUtils.hasText(user.getPassword())) {
            throw new SystemException(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }
        if (!StringUtils.hasText(user.getEmail())) {
            throw new SystemException(AppHttpCodeEnum.EMAIL_NOT_NULL);
        }
        if (!StringUtils.hasText(user.getNickName())) {
            throw new SystemException(AppHttpCodeEnum.NIKENAME_NOT_NULL);
        }
    }

    private void existJudge(User user) {
        //对数据进行是否存在的判断
        if (userNameExist(user.getUserName())) {
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
        if (emailExist(user.getEmail())) {
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }
    }

    @Override
    public ResponseResult listUser(Integer pageNum, Integer pageSize, String userName, String phoneNumber, String status) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(userName)) queryWrapper.like(User::getUserName, userName);
        if (StringUtils.hasText(phoneNumber)) queryWrapper.like(User::getPhonenumber, phoneNumber);
        if (StringUtils.hasText(status)) queryWrapper.eq(User::getStatus, status);

        Page<User> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);

        List<UserVo> userVos = BeanCopyUtils.copyBeanList(page.getRecords(), UserVo.class);

        return ResponseResult.okResult(new PageVo(userVos, page.getTotal()));

    }

    @Override
    @Transactional
    public ResponseResult addUser(UserDto userDto) {

        User user = BeanCopyUtils.copyBean(userDto, User.class);


        nullJudge(user);
        existJudge(user);
        //对密码进行加密
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        //存入数据库
        save(user);

        List<UserRole> userRoleList = new ArrayList<>();

        for (Long id : userDto.getRoleIds()) {
            userRoleList.add(new UserRole(user.getId(), id));
        }

        userRoleService.saveBatch(userRoleList);

        return ResponseResult.okResult();
    }


    @Override
    public ResponseResult userDetail(Long id) {

        //获取userVo
        User user = getById(id);
        UserVo userVo = BeanCopyUtils.copyBean(user, UserVo.class);

        //获取userRole
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId, id);
        List<UserRole> list = userRoleService.list(queryWrapper);
        List<Long> ids = new ArrayList<>();
        for (UserRole userRole : list) {
            ids.add(userRole.getRoleId());
        }

        //获取role列表
        List<Role> roleIds = (List<Role>) roleService.listAllRole().getData();
        List<RoleVo> roleVoList = BeanCopyUtils.copyBeanList(roleIds, RoleVo.class);


        return ResponseResult.okResult(new UpdateUserVo(userVo, roleVoList, ids));
    }

    @Override
    @Transactional
    public ResponseResult updateUser(UserDto userDto) {
        //复制属性
        User user = BeanCopyUtils.copyBean(userDto, User.class);
        user.setPassword(getById(userDto.getId()).getPassword());
        //判空
        nullJudge(user);

        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, user.getId());
        update(user, updateWrapper);
        List<UserRole> userRoleList = new ArrayList<>();

        for (Long id : userDto.getRoleIds()) {
            userRoleList.add(new UserRole(user.getId(), id));
        }

        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId, userDto.getId());
        userRoleService.remove(queryWrapper);

        userRoleService.saveBatch(userRoleList);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteUser(Long id) {
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, id);
        updateWrapper.set(User::getDelFlag, SystemConstants.DEL_FLAG);

        update(updateWrapper);

        redisCache.deleteObject(SystemConstants.USER_INFO_CACHE + id);
        redisCache.deleteObject(SystemConstants.USER_AVATAR_CACHE + id);

        return ResponseResult.okResult();
    }

    @Override
    public String getAvatarUrl(Long userId) {
        String avatarUrl = (String) redisCache.getCacheObject(SystemConstants.USER_AVATAR_CACHE + userId);
        if (StringUtils.hasText(avatarUrl)) {
            redisCache.redisTemplate.expire(SystemConstants.USER_AVATAR_CACHE, Duration.ofMinutes(5));
            return avatarUrl;
        }
        User user = getById(userId);
        String url = user.getAvatar();

        redisCache.setCacheObject(SystemConstants.USER_AVATAR_CACHE + userId, url, Duration.ofMinutes(5));
        return url;
    }

    private boolean emailExist(String email) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);
        return count(queryWrapper) > 0;
    }

    private boolean userNameExist(String userName) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, userName);
        return count(queryWrapper) > 0;
    }
}

