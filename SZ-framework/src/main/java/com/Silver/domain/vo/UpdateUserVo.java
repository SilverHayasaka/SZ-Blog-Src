package com.Silver.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserVo {

    private UserVo user;

    private List<RoleVo> roles;

    private List<Long> roleIds;
}
