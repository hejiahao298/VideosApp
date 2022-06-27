package com.hjh.feignclients;

import com.hjh.entity.Video;
import com.hjh.vo.VideoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Component
@FeignClient("API-VIDEOS")
public interface VideosClient {

    @PostMapping("publish")
    Video publish(@RequestBody Video video);

    @GetMapping("video/{userId}")
    List<VideoVO> userVideo(@PathVariable("userId") Integer userid);

    @GetMapping("getVideos")
    List<VideoVO> getVideos(@RequestParam("ids") List<Integer> ids);


}
