package com.hjh.service;

import com.hjh.entity.Played;
import com.hjh.vo.VideoVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * 播放历史(Played)表服务接口
 *
 * @author makejava
 * @since 2022-06-26 22:46:25
 */
public interface PlayedService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Played queryById(Integer id);

    /**
     * 分页查询
     *
     * @param played 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    Page<Played> queryByPage(Played played, PageRequest pageRequest);

    /**
     * 新增数据
     *
     * @param played 实例对象
     * @return 实例对象
     */
    Played insert(Played played);

    /**
     * 修改数据
     *
     * @param played 实例对象
     * @return 实例对象
     */
    Played update(Played played);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

    /**
     * 根据用户id分页查询历史记录
     */
    List<VideoVO> queryByUserId(Integer userId, Integer page, Integer rows);
}
