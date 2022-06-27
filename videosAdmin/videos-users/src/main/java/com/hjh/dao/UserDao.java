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
 * @since 2022-06-24 21:17:58
 */
@Mapper
public interface UserDao {

    /**
     * 查询所有用户
     * @param start
     * @param rows
     * @param id
     * @param name
     * @param phone
     * @return 用户列表
     */
    List<User> findAllByKeywords(@Param("start") Integer start,
                                 @Param("rows") Integer rows,
                                 @Param("id") String id,
                                 @Param("name")String name,
                                 @Param("phone")String phone);

    /**
     * 通过id，name，phone查询用户总数
     * @param id
     * @param name
     * @param phone
     * @return 用户总数
     */
    Long findTotalCountsByKeywords(@Param("id") String id,
                                   @Param("name")String name,
                                   @Param("phone")String phone);
}

