package com.hjh.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    @GetMapping("/test")
    public String test(){
        logger.info("服务器启动成功");
        return "服务启动成功";
    }
}
