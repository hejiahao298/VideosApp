package com.hjh.dao;

import com.hjh.entity.Video;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * 视频(Video)表数据库访问层
 *
 * @author makejava
 * @since 2022-06-24 21:51:11
 */
@Mapper
public interface VideoDao {

    /**
     * 通过id，name...查询视频的总条数
     * @param id
     * @param title
     * @param categoryId
     * @param username
     * @return 视频总数
     */
    Long findTotalCountsByKeywords(@Param("id") String id,
                                   @Param("title")String title,
                                   @Param("categoryId")String categoryId,
                                   @Param("username")String username);

    /**
     * 通过id，name...查询所有视频
     * @param start
     * @param rows
     * @param id
     * @param title
     * @param categoryId
     * @param username
     * @return 视频列表
     */
    List<Video> findAllByKeywords(
            @Param("start")Integer start,
            @Param("rows")Integer rows,
            @Param("id")String id,
            @Param("title")String title,
            @Param("categoryId") String categoryId,
            @Param("username") String username);
}

