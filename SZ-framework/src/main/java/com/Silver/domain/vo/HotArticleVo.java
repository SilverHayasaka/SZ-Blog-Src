package com.Silver.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class HotArticleVo {
    private Long id;
    private String title;

    //访问量
    private Long viewCount;
}
