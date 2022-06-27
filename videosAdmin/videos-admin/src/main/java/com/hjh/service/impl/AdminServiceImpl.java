package com.hjh.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.druid.util.StringUtils;
import com.hjh.entity.Admin;
import com.hjh.dao.AdminDao;
import com.hjh.service.AdminService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

/**
 * (Admin)表服务实现类
 *
 * @author makejava
 * @since 2022-06-24 01:27:21
 */
@Service("adminService")
@Transactional
public class AdminServiceImpl implements AdminService {
    @Resource
    private AdminDao adminDao;

    @Override
    public Admin login(Admin admin) {
        //1.根据用户名查询用户
        Admin adminDB = adminDao.findByUserName(admin.getUsername());
        //2.判断用户名是否存在
        if (ObjectUtil.isNull(adminDB)) throw new RuntimeException("用户名错误");
        //3.判断密码
        String password = DigestUtils.md5DigestAsHex(admin.getPassword().getBytes(StandardCharsets.UTF_8));
        if (!StringUtils.equals(password, adminDB.getPassword())) throw new RuntimeException("密码输入错误!");
        return adminDB;
    }
}
