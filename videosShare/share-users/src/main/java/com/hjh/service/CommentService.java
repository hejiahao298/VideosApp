package com.hjh.service;

import com.hjh.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * 评论(Comment)表服务接口
 *
 * @author makejava
 * @since 2022-06-27 16:47:52
 */
public interface CommentService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Comment queryById(Integer id);

    /**
     * 分页查询
     *
     * @param comment 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    Page<Comment> queryByPage(Comment comment, PageRequest pageRequest);

    /**
     * 新增数据
     *
     * @param comment 实例对象
     * @return 实例对象
     */
    Comment insert(Comment comment);

    /**
     * 修改数据
     *
     * @param comment 实例对象
     * @return 实例对象
     */
    Comment update(Comment comment);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

    /**
     * 根据视频id查询评论总条数
     * @param videoId 视频id
     * @return 评论总条数
     */
    Long findByVideoIdCounts(Integer videoId);

    /**
     * 根据视频id 分页查询视频列表
     * @param videoId 视频id
     * @param page 当前页
     * @param rows 每页数
     * @return 评论列表
     */
    List<Comment> findByVideoId(Integer videoId, Integer page, Integer rows);

    /**
     * 根据父id，查询父评论下的子评论
     * @param id 父id
     * @return 子评论列表
     */
    List<Comment> findByParentId(Integer id);
}
