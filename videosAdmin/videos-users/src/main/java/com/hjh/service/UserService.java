package com.hjh.service;

import com.hjh.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * 用户(User)表服务接口
 *
 * @author makejava
 * @since 2022-06-24 21:17:59
 */
public interface UserService {

    List<User> findAllByKeywords(Integer pageNow, Integer rows, String id, String name, String phone);

    Long findTotalCountsByKeywords(String id, String name, String phone);
}
