package com.hjh.service.impl;

import com.hjh.entity.Comment;
import com.hjh.dao.CommentDao;
import com.hjh.service.CommentService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 评论(Comment)表服务实现类
 *
 * @author makejava
 * @since 2022-06-27 16:47:53
 */
@Service("commentService")
@Transactional
public class CommentServiceImpl implements CommentService {
    @Resource
    private CommentDao commentDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Comment queryById(Integer id) {
        return this.commentDao.queryById(id);
    }

    /**
     * 分页查询
     *
     * @param comment 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    @Override
    public Page<Comment> queryByPage(Comment comment, PageRequest pageRequest) {
        long total = this.commentDao.count(comment);
        return new PageImpl<>(this.commentDao.queryAllByLimit(comment, pageRequest), pageRequest, total);
    }

    /**
     * 新增数据
     *
     * @param comment 实例对象
     * @return 实例对象
     */
    @Override
    public Comment insert(Comment comment) {
        this.commentDao.insert(comment);
        return comment;
    }

    /**
     * 修改数据
     *
     * @param comment 实例对象
     * @return 实例对象
     */
    @Override
    public Comment update(Comment comment) {
        this.commentDao.update(comment);
        return this.queryById(comment.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.commentDao.deleteById(id) > 0;
    }

    /**
     * 根据视频id查询评论总条数
     * @param videoId 视频id
     * @return 评论总条数
     */
    @Override
    public Long findByVideoIdCounts(Integer videoId) {
        return this.commentDao.findByVideoIdCounts(videoId);
    }

    /**
     * 根据视频id 分页查询视频列表
     * @param videoId 视频id
     * @param page 当前页
     * @param rows 每页数
     * @return 评论列表
     */
    @Override
    public List<Comment> findByVideoId(Integer videoId, Integer page, Integer rows) {
        int start = (page - 1) * rows;
        return this.commentDao.findByVideoId(videoId, start, rows);
    }

    /**
     * 根据父id，查询父评论下的子评论
     * @param id 父id
     * @return 子评论列表
     */
    @Override
    public List<Comment> findByParentId(Integer id) {
        return this.commentDao.findByParentId(id);
    }
}
