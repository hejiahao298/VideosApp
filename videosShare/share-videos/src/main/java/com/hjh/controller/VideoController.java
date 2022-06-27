package com.hjh.controller;

import cn.hutool.json.JSONUtil;
import com.hjh.entity.Comment;
import com.hjh.entity.User;
import com.hjh.entity.Video;
import com.hjh.feignclients.UserClients;
import com.hjh.service.VideoService;
import com.hjh.utils.RedisUtils;
import com.hjh.vo.VideoDetailVO;
import com.hjh.vo.VideoVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 视频(Video)表控制层
 *
 * @author makejava
 * @since 2022-06-26 00:27:48
 */
@RestController
public class VideoController {
    /**
     * 服务对象
     */
    @Resource
    private VideoService videoService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private UserClients userClients;

    private static final Logger log = LoggerFactory.getLogger(VideoController.class);

    /**
     * 视频首页推荐
     */
    @GetMapping("/recommends")
    public List<VideoVO> recommends(@RequestParam(defaultValue ="1", value = "page") Integer page,
                                    @RequestParam(defaultValue ="1", value = "per_page") Integer rows){

        List<VideoVO> videoVOList = videoService.queryAllByLimit(page, rows);
        log.info("视频推荐列表数量: {}", videoVOList.size());
        return videoVOList;
    }


    /**
     * 查看分类下视频列表
     */
    @GetMapping("/videos")
    public List<VideoVO> videos(@RequestParam(defaultValue ="1", value = "page") Integer page,
                                @RequestParam(defaultValue ="1", value = "per_page") Integer rows,
                                @RequestParam("category") Integer categoryId){

        log.info("当前页:{},每页显示记录数:{},分类id:{}", page, rows, categoryId);
        List<VideoVO> videoVOS = videoService.findAllByCategoryId(page, rows, categoryId);
        log.info("复合条件的记录总数: {}", videoVOS.size());
        return videoVOS;
    }

    /**
     * 查看视频详情
     */
    @GetMapping("/videos/{id}")
    public VideoDetailVO video(@PathVariable("id") String videoId, HttpServletRequest request) {
        log.info("当前接收到的videoId: {}", videoId);
        String token = request.getParameter("token");
        VideoDetailVO videoDetailVO = videoService.queryDetailById(videoId, token);
        log.info("查询到的视频详细对象信息: {}", JSONUtil.parse(videoDetailVO));
        return videoDetailVO;
    }

    /**
     * 评论信息
     */
    @GetMapping("/videos/{videoId}/comments")
    public Map<String, Object> comments(
            @PathVariable("videoId") Integer videoId,
            @RequestParam(defaultValue = "1", value = "page") Integer page,
            @RequestParam(defaultValue = "5", value = "per_page") Integer rows
    ) {
        log.info("视频id: {}", videoId);
        log.info("当前页: {}", page);
        log.info("每页显示记录数: {}", rows);
        //调用用户微服务
        return userClients.comments(videoId, page, rows);
    }

    /**
     * 视频评论
     */
    @PostMapping("/videos/{videoId}/comments")
    public void comments(@PathVariable("videoId") Integer videoId,
                         @RequestBody Comment comment,
                         HttpServletRequest request) {
        //1.获取token
        String token = request.getParameter("token");
        //2.根据tokenKey获取用户信息
        String tokenKey = "session_" + token;
        //3.获取redis登陆用户信息
        User user = (User) redisUtils.get(tokenKey);

        log.info("token为: {}", token);
        log.info("评论的视频id: " + videoId);
        log.info("评论对象内容: " + JSONUtil.parse(comment));
        log.info("当前评论的用户: " + JSONUtil.parse(user));

        //4.调用用户服务视频评论功能
        userClients.comments(user.getId(), videoId, comment);
    }


}

