package com.Silver.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleTreeVo {

    private List<MenuTreeVo> menus;
    private List<Long> checkedKeys;
}
