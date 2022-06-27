package com.hjh.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hjh.constants.RedisPrefix;
import com.hjh.dto.AdminDTO;
import com.hjh.entity.Admin;
import com.hjh.service.AdminService;
import com.hjh.utils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * (Admin)表控制层
 *
 * @author makejava
 * @since 2022-06-24 01:26:58
 */
@RestController
public class AdminController {

    @Autowired
    AdminService adminService;

    @Autowired
    RedisUtils redisUtils;

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    //登出接口
    @DeleteMapping("/tokens/{token}")
    public void logout(@PathVariable("token") String token) {
        redisUtils.del(RedisPrefix.TOKEN_KEY + token);
    }

    //登录接口
    @PostMapping("/tokens")
    public Map<String, String> tokens(@RequestBody Admin admin, HttpSession session) {
        Map<String, String> result = new HashMap<>();
        //1.new ObjectMapper fastjson JSONObject.tojsonString(admin)  jackson  new ObjectMapper().writerValueAsString(对象)
        log.info("接收到admin对象为: {}", JSONUtil.parse(admin));
        //2.进行登录
        Admin adminDB = adminService.login(admin);
        //3.登录成功
        String token = session.getId();
        redisUtils.set(RedisPrefix.TOKEN_KEY + token,adminDB,30,TimeUnit.MINUTES);
        result.put("token", token);
        return result;
    }

    //已登录用户信息  //vo value object view object   //dto data transfer object  推荐
    @GetMapping("/k")
    public AdminDTO admin(String token) {
        log.info("当前token信息: {}", token);
        //redisTemplate.setKeySerializer(new StringRedisSerializer());
        Admin admin = (Admin) redisUtils.get(RedisPrefix.TOKEN_KEY + token);
        AdminDTO adminDTO = new AdminDTO();
        //1.属性复制
        BeanUtils.copyProperties(admin, adminDTO);
        return adminDTO;
    }

}

