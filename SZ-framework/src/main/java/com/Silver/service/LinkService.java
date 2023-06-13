package com.Silver.service;

import com.Silver.domain.ResponseResult;
import com.Silver.domain.entity.Link;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * 友链(Link)表服务接口
 *
 * @author makejava
 * @since 2023-03-07 16:26:17
 */
public interface LinkService extends IService<Link> {

    ResponseResult getAllLink();

    ResponseResult listLink(Integer pageNum, Integer pageSize, String name, String status);

    ResponseResult addLink(Link link);

    ResponseResult getLink(Long id);

    ResponseResult updateLink(Link link);

    ResponseResult deleteLink(Long id);
}

