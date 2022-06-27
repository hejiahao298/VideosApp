package com.hjh.dao;

import com.hjh.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * (Admin)表数据库访问层
 *
 * @author makejava
 * @since 2022-06-24 01:27:08
 */
@Mapper
public interface AdminDao {

    /**
     * 通过名字查询用户
     * @param username
     * @return 当前用户
     */
    Admin findByUserName(String username);
}

