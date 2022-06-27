package com.hjh.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hjh.annotations.RequiredToken;
import com.hjh.entity.Favorite;
import com.hjh.entity.Played;
import com.hjh.entity.User;
import com.hjh.entity.Video;
import com.hjh.feignclients.VideosClient;
import com.hjh.service.FavoriteService;
import com.hjh.service.PlayedService;
import com.hjh.service.UserService;
import com.hjh.utils.ImageUtils;
import com.hjh.utils.OSSUtils;
import com.hjh.utils.RedisPrefix;
import com.hjh.utils.RedisUtils;
import com.hjh.vo.MsgVO;
import com.hjh.vo.VideoVO;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 用户(User)表控制层
 *
 * @author makejava
 * @since 2022-06-25 16:08:53
 */
@RestController
public class UserController {
    /**
     * 服务对象
     */
    @Resource
    private UserService userService;

    @Autowired
    private PlayedService playedService;

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private OSSUtils ossUtils;

    @Autowired
    private VideosClient videosClient;

    @Autowired
    private FavoriteService favoriteService;



    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    /**
     * 用户登录
     */
    @PostMapping("tokens")
    public Map<String, Object> tokens(@RequestBody MsgVO msgVO, HttpServletRequest request) {
        HashMap<String, Object> result = new HashMap<>();
        //1. 获取用户手机号
        String phone = msgVO.getPhone();
        //2. 获取验证码
        String captcha = msgVO.getCaptcha();

        log.info("手机号: {},验证码:{}", phone, captcha);

        String phoneKey = "phone_" + phone;
        //3.根据手机号判断redis中是否还存在该手机号验证码,如果不存在说明验证码已经过期!
        if (!redisUtils.hasKey(phoneKey)) {
            throw new RuntimeException("提示：验证码已过期");
        }

        //4. 根据手机号获取redis中验证码
        String redisCaptcha = (String) redisUtils.get(phoneKey);

        // 由于没有开通短信服务验证码,默认为1234
        if(captcha.equals("1234")){

        }else if(!StringUtils.equals(captcha,redisCaptcha)) {   //5. 比较验证码是否和redis中一样
            throw new RuntimeException("提示：验证码输入错误");
        }


        //6. 判断用户是否为新用户
        User user = userService.findByPhone(phone);
        if (ObjectUtils.isEmpty(user)) {
            user = new User();//创建一个用户对象
            user.setName(phone);
            user.setCreatedAt(new Date());//设置创建时间
            user.setUpdatedAt(new Date());//设置更新时间
            user.setPhone(phone); //设置用户的手机号
            user.setIntro("");//设置简介为空
            //初始化默认头像
            user.setAvatar(ImageUtils.getPhoto());//随机初始化头像
            user.setPhoneLinked(1);//是否绑定手机
            user.setWechatLinked(0);//是否绑定微信
            user.setFollowersCount(0);//设置粉丝数
            user.setFollowingCount(0);//设置关注数
            user = userService.insert(user);//保存用户信息
        }

        //7. 保存用户登录标识token
        String token = request.getSession().getId();
        String tokenKey = "session_"+token;
        log.info("生成token: {}", token);
        redisUtils.set(tokenKey,user,7, TimeUnit.DAYS);

        //8. 返回token
        result.put("token",token);
        return result;
    }

    /**
     * 注销登录
     */
    @DeleteMapping("tokens")
    public void logout(String token) {
        log.info("当前获获取的token信息: {}", token);
        //1.根据接收token拼接对应TokenKey
        String tokenKey = "session_" + token;
        //2.根据TokenKey在redis中删除
        redisUtils.del(tokenKey);
    }

    /**
     * 已登录的用户信息
     */
    @GetMapping("user")
    @RequiredToken
    public User user(HttpServletRequest request) throws JsonProcessingException {
        User user = (User) request.getAttribute("user");
        log.info("获取的用户信息为: {}", JSONUtil.parse(user));
        return user;
    }

    /**
     * 修改用户信息
     *
     */
    @PatchMapping("user")
    @RequiredToken
    public User user(@RequestBody User user, HttpServletRequest request) {
        //1.获取token信息
        String token = (String) request.getAttribute("token");

        User userOld = (User) request.getAttribute("user");

        //2. 修改手机号
        if (!StringUtils.isEmpty(user.getPhone())) {
            String phoneKey = "phone_"+user.getPhone();
            if(!redisUtils.hasKey(phoneKey)) {throw new RuntimeException("提示:验证码已过期!");}
            String redisCaptcha = (String) redisUtils.get(phoneKey);
            if (!StringUtils.equals(redisCaptcha, user.getCaptcha())) throw new RuntimeException("提示:验证码输入错误!");
            userOld.setPhone(user.getPhone());
        }

        //3. 未修改手机号
        if (!StringUtils.isEmpty(user.getName())) {userOld.setName(user.getName());}
        if (!StringUtils.isEmpty(user.getIntro())) {userOld.setIntro(user.getIntro());}

        //4.更新用户信息
        userService.update(userOld);

        return null;
    }

    /**
     * 用户发布视频
     * MultipartFile file:用来接收上传视频信息
     * 使用video对象接收 视频标题  视频简介  video{title,intro}
     * category_id 代表当前视频分类id
     * request:    当前请求上下文中存在用户信息
     */
    @PostMapping("/user/videos")
    @RequiredToken
    public Video publishVideos(MultipartFile file, Video video, Integer category_id, HttpServletRequest request) throws IOException {
        //1.获取文件原始名称
        String originalFilename = file.getOriginalFilename();
        log.info("接收文件名称: {}", originalFilename);
        log.info("接收到视频信息: " + JSONUtil.parseObj(video));
        log.info("类别id: {}", category_id);
        log.info("文件大小为: {}", file.getSize());

        //2.获取文件后缀 mp4 avi ....
        String baseName = FilenameUtils.getExtension(originalFilename);

        //3.生成uuid
        String uuidFileName = UUID.randomUUID().toString().replace("-", "");

        //4.生成uuid文件名名称
        String newFileName = uuidFileName + '.' + baseName;

        //5.上传阿里云oss 返回文件在oss地址
        String url = ossUtils.upload(file.getInputStream(), "videos", newFileName);
        log.info("上传成功返回的地址: {}", url);

        //阿里云oss截取视频中某一帧作为封面
        String cover = url + "?x-oss-process=video/snapshot,t_5000,f_jpg,w_0,h_0,m_fast,ar_auto";
        log.info("阿里云oss根据url截取视频封面: {}", cover);

        //6.设置视频信息
        video.setCover(cover);//设置视频封面
        video.setLink(url);//设置视频地址
        video.setCategoryId(category_id);//设置类别id

        //7. 获取用户信息
        User user = (User) request.getAttribute("user");
        video.setUid(user.getId());//设置发布用户id

        //8. 调用视频服务
        Video videoResult = videosClient.publish(video);
        log.info("视频发布成功之后返回的视频信息: {}", JSONUtil.parse(videoResult));
        return videoResult;
    }

    /**
     * 用户发布的视频
     */
    @GetMapping("user/videos")
    @RequiredToken
    public List<VideoVO> userVideos(HttpServletRequest request){
        //1. 获取用户信息
        User user = (User) request.getAttribute("user");
        //2. 调用视频服务获取视频
        List<VideoVO> videoVOS = userService.findVideoByUser(user.getId());

        return videoVOS;
    }

    /**
     * 用户播放视频。。。 将记录到用户的播放历史
     */
    @PutMapping("/user/played/{id}")
    public void played(@PathVariable("id") String videoId, HttpServletRequest request) {
        // 1.播放次数加1
        redisUtils.hincr(RedisPrefix.VIDEOS_PLAYED, RedisPrefix.VIDEO_PLAYED_COUNT_PREFIX +videoId,1);

        // 2. 获取到当前用户
        User user = getUser(request);
        // 3. 如果是登录用户，则将添加播放历史记录
        if (!ObjectUtils.isEmpty(user)) {
            Played played = new Played();
            played.setUid(user.getId());
            played.setVideoId(Integer.valueOf(videoId));
            played = playedService.insert(played);
            log.info("当前用户的播放记录保存成功,信息为: {}", JSONUtil.parse(played));
        }
    }

    /**
     * 用户播放历史列表
     */
    @GetMapping("/user/played")
    @RequiredToken
    public List<VideoVO> played(HttpServletRequest request,
                                @RequestParam(value = "page", defaultValue = "1") Integer page,
                                @RequestParam(value = "per_page", defaultValue = "5") Integer rows) {
        log.info("当前页: {} 每页显示记录: {}", page, rows);
        User user = (User) request.getAttribute("user");
        List<VideoVO> videoVOS = playedService.queryByUserId(user.getId(), page, rows);
        log.info("当前用户播放历史的视频为: {}", JSONUtil.parse(videoVOS));
        return videoVOS;
    }

    /**
     * 用户点赞
     */
    @PutMapping("/user/liked/{id}")
    @RequiredToken
    public void liked(@PathVariable("id") String videoId, HttpServletRequest request) {
        //1.获取当前登陆用户信息
        User user = (User) request.getAttribute("user");
        log.info("接收的到视频id: {}", videoId);

        //2.将当前用户点赞视频列表放入redis中
        redisUtils.sSet(RedisPrefix.USER_LIKE_PREFIX + user.getId(), videoId);

        //3. 判断用户是否已经点赞过
        boolean b = redisUtils.sHasKey(RedisPrefix.USER_LIKE_PREFIX + user.getId(),videoId);

        //4.将视频点赞次数+1
        redisUtils.hincr(RedisPrefix.VIDEOS_LIKE,RedisPrefix.VIDEO_LIKE_COUNT_PREFIX+videoId,1);

        //5.将不喜欢列表中视频删除
        if (redisUtils.sHasKey(RedisPrefix.USER_DISLIKE_PREFIX + user.getId(), videoId)) {
            redisUtils.setRemove(RedisPrefix.USER_DISLIKE_PREFIX + user.getId(), videoId);
        }
    }

    /**
     * 用户取消点赞视频
     */
    @DeleteMapping("/user/liked/{id}")
    @RequiredToken
    public void cancelLiked(@PathVariable("id") Integer videoId, HttpServletRequest request) {
        //1.获取用户信息
        User user = (User) request.getAttribute("user");
        log.info("接收的到视频id: {}", videoId);

        //2.将当前用户的点赞列表中移除
        redisUtils.setRemove(RedisPrefix.USER_LIKE_PREFIX + user.getId(), videoId.toString());

        //3.将视频点赞次数-1
        redisUtils.hdecr(RedisPrefix.VIDEOS_LIKE,RedisPrefix.VIDEO_LIKE_COUNT_PREFIX+videoId,1);
    }

    /**
     * 用户点击不喜欢
     */
    @PutMapping("/user/disliked/{id}")
    @RequiredToken
    public void disliked(@PathVariable("id") String videoId, HttpServletRequest request) {
        // 1.获取当前点击用户的信息
        User user = (User) request.getAttribute("user");

        //2.放入当前用户不喜欢的列表
        redisUtils.sSet(RedisPrefix.USER_DISLIKE_PREFIX + user.getId(), videoId);

        //3.判断之前是否点赞视频
        if (redisUtils.sHasKey(RedisPrefix.USER_LIKE_PREFIX + user.getId(), videoId)) {
            redisUtils.setRemove(RedisPrefix.USER_LIKE_PREFIX + user.getId(), videoId);//从喜欢中列表中删除
            redisUtils.hdecr(RedisPrefix.VIDEOS_LIKE,RedisPrefix.VIDEO_LIKE_COUNT_PREFIX+videoId,1);//当前视频喜欢次数-1
        }

    }

    /**
     * 用户取消不喜欢
     */
    @DeleteMapping("/user/disliked/{id}")
    @RequiredToken
    public void cancelDisliked(@PathVariable("id") String videoId, HttpServletRequest request) {
        //1.获取当前用户信息
        User user = (User) request.getAttribute("user");
        //2.将当前视频从用户不喜欢的列表中移除掉
        if (redisUtils.sHasKey(RedisPrefix.USER_DISLIKE_PREFIX + user.getId(), videoId)) {
            redisUtils.setRemove(RedisPrefix.USER_DISLIKE_PREFIX + user.getId(), videoId);
        }
    }


    /**
     * 用户收藏视频
     *
     */
    @PutMapping("/user/favorites/{video_id}")
    @RequiredToken
    public void createFavorites(@PathVariable("video_id") Integer videoId, HttpServletRequest request) {
        log.info("收藏的视频id: {}", videoId);
        //1. 获取当前用户信息
        User user = (User) request.getAttribute("user");

        //2. 判断用户是否收藏过该视频 ，通过(用户id，视频id)判断
        Favorite favorite = favoriteService.queryByVideoIdAndUserId(videoId, user.getId());

        //3. 如果没有收藏，则加入收藏列表
        if(ObjectUtil.isNull(favorite)){
            favorite = new Favorite();
            favorite.setCreatedAt(new Date());
            favorite.setUpdatedAt(new Date());
            favorite.setUid(user.getId());
            favorite.setVideoId(videoId);
            log.info("收藏视频成功: {}", JSONUtil.parse(favorite));
            favoriteService.insert(favorite);
        }
    }

    /**
     * 用户取消收藏
     */
    @DeleteMapping("/user/favorites/{id}")
    @RequiredToken
    public void cancelFavorites(@PathVariable("id") Integer videoId, HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        log.info("取消收藏的视频id: {}", videoId);
        int i = favoriteService.deleteByVideoIdAndUserId(videoId, user.getId());
        log.info("取消视频收藏成功:, {}", i > 0);

    }

    //根据请求参数中token获取用户信息
    private User getUser(HttpServletRequest request) {
        String token = request.getParameter("token");
        log.info("token为: {}", token);
        String tokenKey = "session_" + token;
        return (User) redisUtils.get(tokenKey);
    }

    /**
     * 用户收藏列表
     */
    @GetMapping("/user/favorites")
    @RequiredToken
    public List<VideoVO> favorites(HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        List<VideoVO> videoVOS = favoriteService.findFavoritesByUserId(user.getId());
        log.info("当前用户收藏的视频为: {}", JSONUtil.parse(videoVOS));
        return videoVOS;
    }



}

