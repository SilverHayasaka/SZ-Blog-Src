package com.Silver.controller;

import com.Silver.domain.ResponseResult;
import com.Silver.domain.entity.Link;
import com.Silver.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/link")
public class LinkController {

    @Autowired
    private LinkService linkService;

    @GetMapping("/list")
    public ResponseResult listLink(Integer pageNum, Integer pageSize, String name, String status) {
        return linkService.listLink(pageNum, pageSize, name, status);
    }

    @PostMapping
    public ResponseResult addLink(@RequestBody Link link) {
        return linkService.addLink(link);
    }

    @GetMapping("/{id}")
    public ResponseResult getLink(@PathVariable Long id) {
        return linkService.getLink(id);
    }

    @PutMapping
    public ResponseResult updateLink(@RequestBody Link link) {
        return linkService.updateLink(link);
    }

    @DeleteMapping("/{id}")
    public ResponseResult deleteLink(@PathVariable Long id) {
        return linkService.deleteLink(id);
    }
}
