package com.Silver.controller;

import com.Silver.domain.ResponseResult;
import com.Silver.domain.dto.TagDto;
import com.Silver.domain.dto.TagListDto;
import com.Silver.domain.vo.PageVo;
import com.Silver.domain.vo.TagVo;
import com.Silver.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/content/tag")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping("/list")
    public ResponseResult<PageVo> list(Integer pageNum, Integer pageSize, TagListDto tagListDto) {
        return tagService.pageTagList(pageNum, pageSize, tagListDto);
    }

    @PostMapping
    public ResponseResult addTag(TagListDto tagListDto) {
        return tagService.addTag(tagListDto);
    }

    @DeleteMapping("{id}")
    public ResponseResult deleteTag(@PathVariable Long id) {
        return tagService.deleteTag(id);
    }

    @GetMapping("{id}")
    public ResponseResult getTag(@PathVariable Long id) {
        return tagService.getTag(id);
    }

    @PutMapping
    public ResponseResult updateTag(@RequestBody TagDto tagDto) {
        return tagService.updateTag(tagDto);
    }

    @GetMapping("/listAllTag")
    public ResponseResult listAllTag() {
        return ResponseResult.okResult(tagService.listAllTag());
    }
}
