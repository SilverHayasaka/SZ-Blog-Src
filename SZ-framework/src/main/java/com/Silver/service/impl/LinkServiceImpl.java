package com.Silver.service.impl;

import com.Silver.constants.SystemConstants;
import com.Silver.domain.ResponseResult;
import com.Silver.domain.entity.Category;
import com.Silver.domain.entity.Link;
import com.Silver.domain.vo.AdminLinkVo;
import com.Silver.domain.vo.LinkVo;
import com.Silver.domain.vo.PageVo;
import com.Silver.mapper.LinkMapper;
import com.Silver.service.LinkService;
import com.Silver.utils.BeanCopyUtils;
import com.Silver.utils.RedisCache;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 友链(Link)表服务实现类
 *
 * @author makejava
 * @since 2023-03-07 16:26:18
 */
@Service("linkService")
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements LinkService {

    @Autowired
    private RedisCache redisCache;

    @Override
    public ResponseResult getAllLink() {

        //查询是否存在redis缓存
        List<LinkVo> linkVos = (List<LinkVo>) redisCache.getCacheObject(SystemConstants.FRIEND_LINK_CACHE);
        if (linkVos != null && linkVos.size() > 0) {
            return ResponseResult.okResult(linkVos);
        }

        //查询所有审核通过的
        LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Link::getStatus, SystemConstants.LINK_STATUS_NORMAL);
        List<Link> links = list(queryWrapper);
        //转换成vo
        linkVos = BeanCopyUtils.copyBeanList(links, LinkVo.class);

        //写缓存
        redisCache.setCacheObject(SystemConstants.FRIEND_LINK_CACHE, linkVos);

        return ResponseResult.okResult(linkVos);
    }

    @Override
    public ResponseResult listLink(Integer pageNum, Integer pageSize, String name, String status) {
        LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) queryWrapper.like(Link::getName, name);
        if (StringUtils.hasText(status)) queryWrapper.eq(Link::getStatus, status);

        Page<Link> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);

        List<AdminLinkVo> adminLinkVos = BeanCopyUtils.copyBeanList(page.getRecords(), AdminLinkVo.class);

        return ResponseResult.okResult(new PageVo(adminLinkVos, page.getTotal()));
    }

    @Override
    public ResponseResult addLink(Link link) {
        save(link);
        //删缓存
        redisCache.deleteObject(SystemConstants.FRIEND_LINK_CACHE);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getLink(Long id) {
        Link link = getById(id);
        AdminLinkVo adminLinkVo = BeanCopyUtils.copyBean(link, AdminLinkVo.class);

        return ResponseResult.okResult(adminLinkVo);
    }

    @Override
    public ResponseResult updateLink(Link link) {
        LambdaUpdateWrapper<Link> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Link::getId, link.getId());

        update(link, updateWrapper);
        //删缓存
        redisCache.deleteObject(SystemConstants.FRIEND_LINK_CACHE);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteLink(Long id) {
        LambdaUpdateWrapper<Link> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Link::getId, id);
        updateWrapper.set(Link::getDelFlag, SystemConstants.DEL_FLAG);

        update(updateWrapper);
        //删缓存
        redisCache.deleteObject(SystemConstants.FRIEND_LINK_CACHE);

        return ResponseResult.okResult();
    }
}

