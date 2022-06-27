package com.hjh.service.impl;

import com.hjh.entity.Video;
import com.hjh.dao.VideoDao;
import com.hjh.service.VideoService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 视频(Video)表服务实现类
 *
 * @author makejava
 * @since 2022-06-24 21:51:13
 */
@Service("videoService")
@Transactional
public class VideoServiceImpl implements VideoService {
    @Resource
    private VideoDao videoDao;

    /**
     * 通过id，name...查询视频的总条数
     * @param id
     * @param name
     * @param categoryId
     * @param username
     * @return 视频总数
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Long findTotalCountsByKeywords(String id, String name, String categoryId, String username) {
        return this.videoDao.findTotalCountsByKeywords( id,  name,  categoryId,  username);
    }

    /**
     * 通过id，name...查询视频的总条数
     * @param id
     * @param name
     * @param categoryId
     * @param username
     * @return 视频总数
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Video> findAllByKeywords(Integer page, Integer rows, String id, String name, String categoryId, String username) {
        Integer start = (page-1)*rows;
        return this.videoDao.findAllByKeywords( start,  rows,  id,  name,  categoryId,  username);
    }
}
