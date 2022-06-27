package com.hjh.controller;

import com.hjh.entity.Video;
import com.hjh.service.VideoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 视频(Video)表控制层
 *
 * @author makejava
 * @since 2022-06-24 21:51:10
 */
@RestController
@RequestMapping("videos")
public class VideoController {
    /**
     * 服务对象
     */
    @Resource
    private VideoService videoService;

    private static final Logger log = LoggerFactory.getLogger(VideoController.class);

    @GetMapping
    public Map<String, Object> videos(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                      @RequestParam(value = "per_page", defaultValue = "5") Integer rows,
                                      String id,    //视频id
                                      String name,  //视频名称
                                      @RequestParam(value = "category_id", required = false) String categoryId,  //类别id
                                      @RequestParam(value = "uploader_name", required = false) String username   //根据up主 根据用户名查
    ) {
        Map<String, Object> result = new HashMap<>();
        log.info("当前页为: {}", page);
        log.info("每页显示记录数: {}", rows);
        log.info("搜索条件id是否存在:{}, id为: {}", !ObjectUtils.isEmpty(id), id);
        log.info("搜索条件name是否存在:{}, name为: {}", !ObjectUtils.isEmpty(name), name);
        log.info("搜索条件category_id是否存在:{}, category_id为: {}", !ObjectUtils.isEmpty(categoryId), categoryId);
        log.info("搜索条件uploader_name是否存在:{}, uploader_name为: {}", !ObjectUtils.isEmpty(username), username);
        //根据条件搜索的服务条件的记录
        Long totalCounts = videoService.findTotalCountsByKeywords(id, name, categoryId, username);
        //根据条件搜索的结果集合
        List<Video> items = videoService.findAllByKeywords(page, rows, id, name, categoryId, username);
        log.info("符合条件的总数: {}", totalCounts);
        result.put("total_count", totalCounts);
        result.put("items", items);
        return result;
    }

}

