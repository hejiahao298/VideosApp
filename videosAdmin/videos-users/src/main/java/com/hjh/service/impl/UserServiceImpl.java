package com.hjh.service.impl;

import com.hjh.entity.User;
import com.hjh.dao.UserDao;
import com.hjh.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户(User)表服务实现类
 *
 * @author makejava
 * @since 2022-06-24 21:18:00
 */
@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;

    /**
     * 查询所有用户
     * @param pageNow 当前页
     * @param rows 每页数
     * @param id 通过id查询
     * @param name 通过name查询
     * @param phone 通过phone查询
     * @return 用户列表
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<User> findAllByKeywords(Integer pageNow, Integer rows, String id, String name, String phone) {

        Integer start = (pageNow-1)*rows;

        return this.userDao.findAllByKeywords(start,rows,id, name, phone);
    }

    /**
     * 根据id或者name或者phone查询用户数
     * @param id
     * @param name
     * @param phone
     * @return 用户数量
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS) //SUPPORTS 支持事务： 外层存在事务融入当前事务  外层不存在事务 不会开启新的事务
    public Long findTotalCountsByKeywords(String id, String name, String phone) {
        return this.userDao.findTotalCountsByKeywords( id,  name,  phone);
    }
}
