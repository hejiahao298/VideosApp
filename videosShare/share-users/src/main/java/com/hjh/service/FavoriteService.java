package com.hjh.service;

import com.hjh.entity.Favorite;
import com.hjh.vo.VideoVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * 收藏(Favorite)表服务接口
 *
 * @author makejava
 * @since 2022-06-27 15:47:48
 */
public interface FavoriteService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Favorite queryById(Integer id);

    /**
     * 分页查询
     *
     * @param favorite 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    Page<Favorite> queryByPage(Favorite favorite, PageRequest pageRequest);

    /**
     * 新增数据
     *
     * @param favorite 实例对象
     * @return 实例对象
     */
    Favorite insert(Favorite favorite);

    /**
     * 修改数据
     *
     * @param favorite 实例对象
     * @return 实例对象
     */
    Favorite update(Favorite favorite);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

    /**
     * 根据用户id+视频id 查询一条收藏视频
     * @param videoId 视频id
     * @param uid 用户id
     * @return 实例对象
     */
    Favorite queryByVideoIdAndUserId(Integer videoId, Integer uid);

    /**
     * 根据用户id+视频id 删除一条收藏视频记录
     * @param videoId 视频id
     * @param uid 用户id
     * @return 删除条数
     */
    int deleteByVideoIdAndUserId(Integer videoId, Integer uid);

    /**
     * 根据用户id查询收藏列表u
     * @param uid 用户id
     * @return 收藏列表
     */
    List<VideoVO> findFavoritesByUserId(Integer uid);
}
