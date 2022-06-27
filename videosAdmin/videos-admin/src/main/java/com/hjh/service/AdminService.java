package com.hjh.service;

import com.hjh.entity.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * (Admin)表服务接口
 *
 * @author makejava
 * @since 2022-06-24 01:27:16
 */
public interface AdminService {

    /**
     * 登录
     * @param admin
     * @return admin
     */
    Admin login(Admin admin);
}
