package com.Silver.service.impl;

import com.Silver.service.PermissionService;
import com.Silver.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("ps")
public class PermissionServiceImpl implements PermissionService {
    public boolean hasPermission(String permission) {
        //判断当前用户是否具有permission
        if (SecurityUtils.isAdmin()) {
            return true;
        }
        List<String> permissions = SecurityUtils.getLoginUser().getPermissions();
        return permissions.contains(permission);
    }
}
