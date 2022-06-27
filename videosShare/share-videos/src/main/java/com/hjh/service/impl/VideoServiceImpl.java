package com.hjh.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.hjh.controller.VideoServiceController;
import com.hjh.entity.Category;
import com.hjh.entity.Favorite;
import com.hjh.entity.User;
import com.hjh.entity.Video;
import com.hjh.dao.VideoDao;
import com.hjh.feignclients.CategoryClients;
import com.hjh.feignclients.UserClients;
import com.hjh.service.VideoService;
import com.hjh.utils.RedisPrefix;
import com.hjh.utils.RedisUtils;
import com.hjh.vo.VideoDetailVO;
import com.hjh.vo.VideoVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 视频(Video)表服务实现类
 *
 * @author makejava
 * @since 2022-06-26 00:27:50
 */
@Service("videoService")
@Transactional
public class VideoServiceImpl implements VideoService {
    @Resource
    private VideoDao videoDao;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private UserClients userClients;

    @Autowired
    private CategoryClients categoryClients;

    @Autowired
    private RedisUtils redisUtils;

    private static final Logger log = LoggerFactory.getLogger(VideoServiceController.class);

    /**
     * 通过ID查询单条数据
     *
     * @param ids 主键
     * @return 实例对象
     */
    @Override
    public List<VideoVO> queryById(List<Integer> ids) {
        List<Video> videos = new ArrayList<>();
        for (Integer id: ids) {
            videos.add(this.videoDao.queryById(id));
        }
        return getList(videos);
    }


    /**
     * 新增数据
     *
     * @param video 实例对象
     * @return 实例对象
     */
    @Override
    public Video insert(Video video) {
        video.setCreatedAt(new Date());//设置创建日期
        video.setUpdatedAt(new Date());//设置更新日期
        this.videoDao.insert(video);

        //注意:利用MQ异步处理,提升系统响应: 将视频信息写入到ES索引库  1.在es中建立6.x  索引index  类型type   映射概念mapping   2.将视频信息写入es
        rabbitTemplate.convertAndSend("videos", "", JSONUtil.toJsonStr(getVideoVO(video)));
        return video;
    }

    /**
     * 修改数据
     *
     * @param video 实例对象
     * @return 实例对象
     */

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.videoDao.deleteById(id) > 0;
    }

    /**
     * 分页查询所有的视频
     * @param page 当前页
     * @param rows 每页数
     * @return 视频列表
     */
    @Override
    public List<VideoVO> queryAllByLimit(Integer page, Integer rows) {
        Integer start = (page-1)*rows;
        List<Video> videos = this.videoDao.queryAllByLimit(start,rows);
        return getList(videos);
    }

    /**
     * 根据用户id查询用户发布的视频
     * @return 视频列表
     */
    @Override
    public List<VideoVO> queryByUserId(Integer userid) {
        List<Video> videos = this.videoDao.queryByUserId(userid);
        return getList(videos);
    }

    /**
     * 根据分类id 查询该分类下的视频
     * @param page 当前页
     * @param rows 每页数
     * @param categoryId 分类id
     * @return 视频列表
     */
    @Override
    public List<VideoVO> findAllByCategoryId(Integer page, Integer rows, Integer categoryId) {
        Integer start = (page-1)*rows;
        List<Video> videos = videoDao.findAllByCategoryId(start, rows, categoryId);
        return getList(videos);
    }

    /**
     * 根据视频id查询视频详细
     * @param videoId 视频id
     * @param token token令牌
     * @return
     */
    @Override
    public VideoDetailVO queryDetailById(String videoId, String token) {
        //1.根据id查询视频信息
        Video video = videoDao.queryById(Integer.valueOf(videoId));

        //2.创建videoDetailVo
        VideoDetailVO videoDetailVO = new VideoDetailVO();

        //3.将video对象中基本信息复制到VideoDetailVo {id title cover link created_at update_at}
        BeanUtils.copyProperties(video, videoDetailVO);

        //4.设置当前视频类别名称
        Category category = categoryClients.category(video.getCategoryId());
        log.info("根据类别id查询到的类别信息为: {}", JSONUtil.parse(category));
        videoDetailVO.setCategory(category.getName());

        //5.设置用户信息
        User user = userClients.user(video.getUid().toString());//调用用户服务
        log.info("根据id查询用户信息为: {}", JSONUtil.parse(video));
        videoDetailVO.setUploader(user);

        //6.设置视频播放次数
        videoDetailVO.setPlaysCount(0);//初始化默认值为0
        Integer playedCounts = (Integer) redisUtils.hget(RedisPrefix.VIDEOS_PLAYED,RedisPrefix.VIDEO_PLAYED_COUNT_PREFIX+videoId);
        if(!ObjectUtil.isNull(playedCounts)) {
            videoDetailVO.setPlaysCount(Integer.valueOf(playedCounts));
        }

        //7.设置视频点赞数量
        videoDetailVO.setLikesCount(0);
        Integer likedCounts = (Integer) redisUtils.hget(RedisPrefix.VIDEOS_LIKE, RedisPrefix.VIDEO_LIKE_COUNT_PREFIX + videoId);
        if(!ObjectUtil.isNull(likedCounts)) {
            videoDetailVO.setLikesCount(Integer.valueOf(likedCounts));
        }

        //8.设置当前用户是否点赞 ，不喜欢 ，收藏
        User o = (User) redisUtils.get("session_"+token);
        if(!ObjectUtil.isNull(o)){
            log.info("当前查看视频的用户信息为:{}",JSONUtil.parse(o));
            boolean isLiked = redisUtils.sHasKey(RedisPrefix.USER_LIKE_PREFIX+o.getId(),videoId);
            videoDetailVO.setLiked(isLiked);

            boolean isDisliked = redisUtils.sHasKey(RedisPrefix.USER_DISLIKE_PREFIX+o.getId(),videoId);
            videoDetailVO.setDisliked(isDisliked);

            // 收藏判断用户是否收藏
            Favorite favorite = userClients.favorite(videoId, user.getId().toString());
            videoDetailVO.setFavorite(!ObjectUtil.isNull(favorite));
        }

        return videoDetailVO;
    }

    //将list video 转为 list videoVO
    public List<VideoVO> getList(List<Video> videos) {
        //创建VideoVo集合
        List<VideoVO> videoVOS = new ArrayList<>();
        //对video进行遍历 在遍历过程中转为videoVo
        videos.forEach(video -> {
            //video{id title intro cover uid category_id create_at ...}   //videoVo{id title cover category likes uploader create_at}
            VideoVO videoVO = getVideoVO(video);

            videoVOS.add(videoVO);//添加视频
        });
        return videoVOS;
    }

    //将 video 转为 videoVO
    public VideoVO getVideoVO(Video video) {
        //创建VideoVo
        VideoVO videoVO = new VideoVO();
        //复制属性
        BeanUtils.copyProperties(video, videoVO);//复制属性

        //视频服务----->调用用户服务 根据用户id查询用户
        User user = userClients.user(video.getUid().toString());//调用用户服务
        log.info("根据id查询用户信息为: {}", JSONUtil.parse(user));
        videoVO.setUploader(user.getName());//设置用户名

        //视频服务---->调用类别服务  根据类别id查询类别
        Category category = categoryClients.category(video.getCategoryId());
        log.info("根据类别id查询到的类别信息为: {}", JSONUtil.parse(category));
        videoVO.setCategory(category.getName());

        //设置点赞数量
        videoVO.setLikes(0);

//        String counts = stringRedisTemplate.opsForValue().get(RedisPrefix.VIDEO_LIKE_COUNT_PREFIX + video.getId());
//        if (!StringUtils.isEmpty(counts)) {
//            log.info("当前视频点赞数量为: {}", counts);
//            videoVO.setLikes(Integer.valueOf(counts));
//        }
        return videoVO;
    }
}
