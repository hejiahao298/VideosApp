package com.hjh.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.hjh.entity.Played;
import com.hjh.dao.PlayedDao;
import com.hjh.feignclients.VideosClient;
import com.hjh.service.PlayedService;
import com.hjh.vo.VideoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 播放历史(Played)表服务实现类
 *
 * @author makejava
 * @since 2022-06-26 22:46:25
 */
@Service("playedService")
@Transactional
public class PlayedServiceImpl implements PlayedService {
    @Resource
    private PlayedDao playedDao;

    @Autowired
    private VideosClient videosClient;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Played queryById(Integer id) {
        return this.playedDao.queryById(id);
    }

    /**
     * 分页查询
     *
     * @param played 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    @Override
    public Page<Played> queryByPage(Played played, PageRequest pageRequest) {
        long total = this.playedDao.count(played);
        return new PageImpl<>(this.playedDao.queryAllByLimit(played, pageRequest), pageRequest, total);
    }

    /**
     * 新增数据
     * 添加历史记录，但不能重复添加
     * @param played 实例对象
     * @return 实例对象
     */
    @Override
    public Played insert(Played played) {
        //1.根据用户id  视频id 判断是否存在当前记录播放历史
        Played playedOld = this.playedDao.queryByUserAndVideoId(played.getUid(), played.getVideoId());
        //2. 如果不存在记录，则添加历史记录
        if(ObjectUtil.isEmpty(playedOld)){
            played.setCreatedAt(new Date());
            played.setUpdatedAt(new Date());
            this.playedDao.insert(played);
            return played;
        }else{
            //3. 如果存在记录，则更新世界
            playedOld.setUpdatedAt(new Date());
            this.playedDao.update(playedOld);
            return playedOld;
        }

    }

    /**
     * 修改数据
     *
     * @param played 实例对象
     * @return 实例对象
     */
    @Override
    public Played update(Played played) {
        this.playedDao.update(played);
        return this.queryById(played.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.playedDao.deleteById(id) > 0;
    }


    /**
     * 根据用户id分页查询历史记录
     * @param userId 用户id
     * @param page 当前页
     * @param rows 每页数
     * @return 视频列表
     */
    @Override
    public List<VideoVO> queryByUserId(Integer userId, Integer page, Integer rows) {
        int start = (page - 1) * rows;
        //播放历史列表  collection  stream
        List<Played> playeds = playedDao.queryByUserId(userId, start, rows);
        //stream  筛选 对象  ---> 对象某个属性  stream 20  map reduce  filter distinct
        List<Integer> ids = playeds.stream().map(played -> played.getVideoId()).collect(Collectors.toList());
        return videosClient.getVideos(ids);
    }
}
