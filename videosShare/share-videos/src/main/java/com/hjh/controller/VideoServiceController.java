package com.hjh.controller;

import cn.hutool.json.JSONUtil;
import com.hjh.entity.Video;
import com.hjh.service.VideoService;
import com.hjh.vo.VideoVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
public class VideoServiceController {

    /**
     * 服务对象
     */
    @Resource
    private VideoService videoService;
    private static final Logger log = LoggerFactory.getLogger(VideoServiceController.class);

    /**
     * 保存视频
     */
    @PostMapping("publish")
    public Video publish(@RequestBody Video video){
        log.info("接收到的video: {}", JSONUtil.parse(video));
        return videoService.insert(video);
    }

    /**
     * 根据用户id发布的视频
     */
    @GetMapping("video/{userId}")
    public List<VideoVO> userVideo(@PathVariable("userId") Integer userid){
        return videoService.queryByUserId(userid);
    }

    /**
     * 根据视频id 查询视频
     */
    @GetMapping("getVideos")
    public List<VideoVO> getVideos(@RequestParam("ids") List<Integer> ids){
        return videoService.queryById(ids);
    }

}
