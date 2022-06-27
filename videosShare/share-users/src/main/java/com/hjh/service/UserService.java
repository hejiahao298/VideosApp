package com.hjh.service;

import com.hjh.entity.User;
import com.hjh.vo.VideoVO;

import java.util.List;

/**
 * 用户(User)表服务接口
 *
 * @author makejava
 * @since 2022-06-25 16:08:55
 */
public interface UserService {


    /**
     * 新增数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    User insert(User user);

    /**
     * 修改数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    User update(User user);

    /**
     * 通过手机号查询用户
     * @param phone 手机号
     * @return 用户对象
     */
    User findByPhone(String phone);

    /**
     * 通过id查询用户
     * @param id
     * @return 用户对象
     */
    User queryById(Integer id);


    /**
     * 根据用户id查询用户发布的视频
     * @return 视频列表
     */
    List<VideoVO> findVideoByUser(Integer userId);
}
