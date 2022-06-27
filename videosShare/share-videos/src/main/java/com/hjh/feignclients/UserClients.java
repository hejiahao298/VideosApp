package com.hjh.feignclients;

import com.hjh.entity.Comment;
import com.hjh.entity.Favorite;
import com.hjh.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Component
@FeignClient("API-USERS")
public interface UserClients {

    @GetMapping("/userInfo/{id}")
    User user(@PathVariable("id") String id);

    @GetMapping("/userInfo/favorite")
    Favorite favorite(@RequestParam("videoId") String videoId, @RequestParam("userId") String userId);

    // 用户评论
    @PostMapping("/user/comment/{userId}/{videoId}")
    void comments(@PathVariable("userId") Integer userId, @PathVariable("videoId") Integer videoId, @RequestBody Comment comment);

    // 评论列表
    @GetMapping("/user/comments")
    Map<String, Object> comments(@RequestParam("videoId") Integer videoId,
                                        @RequestParam(value = "page", defaultValue = "1") Integer page,
                                        @RequestParam(value = "per_page", defaultValue = "15") Integer rows);
}
