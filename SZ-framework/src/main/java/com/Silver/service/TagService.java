package com.Silver.service;

import com.Silver.domain.ResponseResult;
import com.Silver.domain.dto.TagDto;
import com.Silver.domain.dto.TagListDto;
import com.Silver.domain.entity.Tag;
import com.Silver.domain.vo.PageVo;
import com.Silver.domain.vo.TagVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
 * 标签(Tag)表服务接口
 *
 * @author makejava
 * @since 2023-03-16 20:23:37
 */
public interface TagService extends IService<Tag> {

    ResponseResult<PageVo> pageTagList(Integer pageNum, Integer pageSize, TagListDto tagListDto);

    ResponseResult addTag(TagListDto tagListDto);

    ResponseResult deleteTag(Long id);

    ResponseResult getTag(Long id);

    ResponseResult updateTag(TagDto tagDto);


    List<TagVo> listAllTag();
}

