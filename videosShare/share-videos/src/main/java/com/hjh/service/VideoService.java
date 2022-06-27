package com.hjh.service;

import com.hjh.entity.Video;
import com.hjh.vo.VideoDetailVO;
import com.hjh.vo.VideoVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * 视频(Video)表服务接口
 *
 * @author makejava
 * @since 2022-06-26 00:27:50
 */
public interface VideoService {

    /**
     * 通过ID查询单条数据
     *
     * @param ids 主键
     * @return 实例对象
     */
    List<VideoVO> queryById(List<Integer> ids);

    /**
     * 新增数据
     *
     * @param video 实例对象
     * @return 实例对象
     */
    Video insert(Video video);


    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

    /**
     * 分页查询所有的视频
     * @param page 当前页
     * @param rows 每页数
     * @return 视频列表
     */
    List<VideoVO> queryAllByLimit(Integer page, Integer rows);

    /**
     * 根据用户id查询用户发布的视频
     * @return 视频列表
     */
    List<VideoVO> queryByUserId(Integer userid);

    /**
     * 根据分类id 查询该分类下的视频
     * @param page 当前页
     * @param rows 每页数
     * @param categoryId 分类id
     * @return 视频列表
     */
    List<VideoVO> findAllByCategoryId(Integer page, Integer rows, Integer categoryId);

    /**
     * 根据视频id查询视频详细
     * @param videoId 视频id
     * @param token token令牌
     * @return
     */
    VideoDetailVO queryDetailById(String videoId, String token);
}
