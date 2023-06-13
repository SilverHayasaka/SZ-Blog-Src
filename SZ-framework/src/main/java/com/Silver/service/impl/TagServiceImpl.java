package com.Silver.service.impl;

import com.Silver.constants.SystemConstants;
import com.Silver.domain.ResponseResult;
import com.Silver.domain.dto.TagDto;
import com.Silver.domain.dto.TagListDto;
import com.Silver.domain.entity.Tag;
import com.Silver.domain.vo.PageVo;
import com.Silver.domain.vo.TagVo;
import com.Silver.enums.AppHttpCodeEnum;
import com.Silver.exception.SystemException;
import com.Silver.mapper.TagMapper;
import com.Silver.service.TagService;
import com.Silver.utils.BeanCopyUtils;
import com.Silver.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 标签(Tag)表服务实现类
 *
 * @author makejava
 * @since 2023-03-16 20:23:37
 */
@Service("tagService")
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Autowired
    private TagService tagService;

    @Override
    public ResponseResult<PageVo> pageTagList(Integer pageNum, Integer pageSize, TagListDto tagListDto) {
        //分页查询
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(tagListDto.getName()), Tag::getName, tagListDto.getName());
        queryWrapper.like(StringUtils.hasText(tagListDto.getRemark()), Tag::getRemark, tagListDto.getRemark());

        Page<Tag> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        page(page, queryWrapper);
        //封装数据返回
        List<TagDto> tagVos = BeanCopyUtils.copyBeanList(page.getRecords(), TagDto.class);
        PageVo pageVo = new PageVo(tagVos, page.getTotal());

        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult addTag(TagListDto tagListDto) {

        if (!StringUtils.hasText(tagListDto.getName())) {
            throw new SystemException(AppHttpCodeEnum.TAG_NAME_NOT_NULL);
        }
        if (!StringUtils.hasText(tagListDto.getRemark())) {
            throw new SystemException(AppHttpCodeEnum.REMARK_NOT_NULL);
        }

        Tag tag = new Tag();
        tag.setName(tagListDto.getName());
        tag.setRemark(tagListDto.getRemark());
        tag.setCreateBy(SecurityUtils.getUserId());
        tag.setCreateTime(new Date());
        save(tag);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteTag(Long id) {
        LambdaUpdateWrapper<Tag> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Tag::getId, id);
        updateWrapper.set(Tag::getDelFlag, SystemConstants.DEL_FLAG);
        tagService.update(updateWrapper);

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getTag(Long id) {
        Tag tag = getById(id);
        TagDto tagDto = BeanCopyUtils.copyBean(tag, TagDto.class);

        return ResponseResult.okResult(tagDto);
    }

    @Override
    public ResponseResult updateTag(TagDto tagDto) {
        LambdaUpdateWrapper<Tag> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Tag::getId, tagDto.getId());
        updateWrapper.set(Tag::getName, tagDto.getName());
        updateWrapper.set(Tag::getRemark, tagDto.getRemark());
        tagService.update(updateWrapper);

        return ResponseResult.okResult();
    }

    @Override
    public List<TagVo> listAllTag() {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Tag::getId, Tag::getName);
        List<Tag> list = list(wrapper);
        List<TagVo> tagVos = BeanCopyUtils.copyBeanList(list, TagVo.class);
        return tagVos;
    }

}

