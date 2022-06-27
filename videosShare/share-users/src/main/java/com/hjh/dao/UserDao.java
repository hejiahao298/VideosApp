package com.hjh.dao;

import com.hjh.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * 用户(User)表数据库访问层
 *
 * @author makejava
 * @since 2022-06-25 16:08:53
 */
@Mapper
public interface UserDao {


    /**
     * 新增数据
     *
     * @param user 实例对象
     * @return 影响行数
     */
    int insert(User user);

    /**
     * 修改数据
     *
     * @param user 实例对象
     * @return 影响行数
     */
    int update(User user);

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    User queryById(Integer id);

    /**
     * 通过手机号查询用户
     * @param phone 手机号
     * @return 用户对象
     */
    User findByPhone(@Param("phone") String phone);
}

