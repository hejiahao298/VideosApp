package com.hjh.controller;

import cn.hutool.json.JSONUtil;
import com.hjh.entity.Category;
import com.hjh.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.stylesheets.LinkStyle;

import javax.annotation.Resource;
import java.util.List;

/**
 * 分类(Category)表控制层
 *
 * @author makejava
 * @since 2022-06-25 23:23:40
 */
@RestController
@RequestMapping("categories")
public class CategoryController {

    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);
    /**
     * 服务对象
     */
    @Resource
    private CategoryService categoryService;

    /**
     * 分类列表
     */
    @GetMapping
    public List<Category> categories(){
        log.info("进入查询类别列表方法..");
        List<Category> categories = categoryService.findAll();
        log.info("查询当前一级类别列表总数为: {}", categories.size());
        return categories;
    }

    /**
     * 根据id查询类别
     */
    @GetMapping("{id}")
    public Category category(@PathVariable("id") Integer id){
        log.info("接收到的类别id: {}", id);
        Category category = categoryService.queryById(id);
        log.info("查询到的类别信息: {}", JSONUtil.parse(category));
        return category;
    }


}

