package com.hjh.service.impl;

import com.hjh.entity.User;
import com.hjh.dao.UserDao;
import com.hjh.feignclients.VideosClient;
import com.hjh.service.UserService;
import com.hjh.vo.VideoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户(User)表服务实现类
 *
 * @author makejava
 * @since 2022-06-25 16:08:55
 */
@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;

    @Autowired
    VideosClient videosClient;


    /**
     * 新增数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    @Override
    public User insert(User user) {
        this.userDao.insert(user);
        return user;
    }

    /**
     * 修改数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    @Override
    public User update(User user) {
        this.userDao.update(user);
        return this.userDao.queryById(user.getId());
    }


    /**
     * 通过手机号查询用户
     * @param phone 手机号
     * @return 用户对象
     */
    @Override
    public User findByPhone(String phone) {
        return this.userDao.findByPhone(phone);
    }

    @Override
    public User queryById(Integer id) {
        return this.userDao.queryById(id);
    }

    /**
     * 根据用户id查询用户发布的视频
     * @return 视频列表
     */
    @Override
    public List<VideoVO> findVideoByUser(Integer userId) {

        return videosClient.userVideo(userId);
    }


}
