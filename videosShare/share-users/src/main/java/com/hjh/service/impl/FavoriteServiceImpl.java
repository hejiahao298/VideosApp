package com.hjh.service.impl;

import com.hjh.entity.Favorite;
import com.hjh.dao.FavoriteDao;
import com.hjh.feignclients.VideosClient;
import com.hjh.service.FavoriteService;
import com.hjh.vo.VideoVO;
import org.aspectj.lang.annotation.AfterThrowing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 收藏(Favorite)表服务实现类
 *
 * @author makejava
 * @since 2022-06-27 15:47:48
 */
@Service("favoriteService")
@Transactional
public class FavoriteServiceImpl implements FavoriteService {
    @Resource
    private FavoriteDao favoriteDao;

    @Autowired
    private VideosClient videosClient;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Favorite queryById(Integer id) {
        return this.favoriteDao.queryById(id);
    }

    /**
     * 分页查询
     *
     * @param favorite 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    @Override
    public Page<Favorite> queryByPage(Favorite favorite, PageRequest pageRequest) {
        long total = this.favoriteDao.count(favorite);
        return new PageImpl<>(this.favoriteDao.queryAllByLimit(favorite, pageRequest), pageRequest, total);
    }

    /**
     * 新增数据
     *
     * @param favorite 实例对象
     * @return 实例对象
     */
    @Override
    public Favorite insert(Favorite favorite) {
        this.favoriteDao.insert(favorite);
        return favorite;
    }

    /**
     * 修改数据
     *
     * @param favorite 实例对象
     * @return 实例对象
     */
    @Override
    public Favorite update(Favorite favorite) {
        this.favoriteDao.update(favorite);
        return this.queryById(favorite.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.favoriteDao.deleteById(id) > 0;
    }

    /**
     * 根据用户id+视频id 查询一条收藏视频
     * @param videoId 视频id
     * @param uid 用户id
     * @return 实例对象
     */
    @Override
    public Favorite queryByVideoIdAndUserId(Integer videoId, Integer uid) {
        return this.favoriteDao.queryByVideoIdAndUserId(videoId,uid);
    }

    /**
     * 根据用户id+视频id 删除一条收藏视频记录
     * @param videoId 视频id
     * @param uid 用户id
     * @return 删除条数
     */
    @Override
    public int deleteByVideoIdAndUserId(Integer videoId, Integer uid) {
        return this.favoriteDao.deleteByVideoIdAndUserId(videoId,uid);
    }

    /**
     * 根据用户id查询收藏列表u
     * @param uid 用户id
     * @return 收藏列表
     */
    @Override
    public List<VideoVO> findFavoritesByUserId(Integer uid) {
        // 1. 查询该用户收藏的视频
        List<Favorite> favorites = this.favoriteDao.queryByUserId(uid);

        //2. 获取到该用户收藏的视频id列表
        List<Integer> ids = favorites.stream().map(favorite -> favorite.getVideoId()).collect(Collectors.toList());
        return videosClient.getVideos(ids);
    }
}
