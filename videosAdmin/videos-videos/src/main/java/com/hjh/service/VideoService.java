package com.hjh.service;

import com.hjh.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * 视频(Video)表服务接口
 *
 * @author makejava
 * @since 2022-06-24 21:51:13
 */
public interface VideoService {


    /**
     * 通过id，name...查询视频的总条数
     * @param id
     * @param name
     * @param categoryId
     * @param username
     * @return 视频总数
     */
    Long findTotalCountsByKeywords(String id, String name, String categoryId, String username);

    /**
     * 通过id，name...查询所有视频
     * @param page
     * @param rows
     * @param id
     * @param name
     * @param categoryId
     * @param username
     * @return 视频列表
     */
    List<Video> findAllByKeywords(Integer page, Integer rows, String id, String name, String categoryId, String username);
}
