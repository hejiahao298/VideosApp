package com.hjh.controller;

import cn.hutool.json.JSONUtil;
import com.hjh.entity.Category;
import com.hjh.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 分类(Category)表控制层
 *
 * @author makejava
 * @since 2022-06-24 14:20:50
 */
@RestController
@RequestMapping("/categories")
public class CategoryController {
    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryService categoryService;

    //添加类别接口
    @PostMapping  //{name:"xxx","parent_id":1}
    public Category save(@RequestBody Category category) {
        log.info("添加类别信息: {}", JSONUtil.parse(category));
        category = categoryService.insert(category);
        log.info("添加之后类别信息: {}", JSONUtil.parse(category));
        return category;
    }

    //修改列表接口
    @PatchMapping("/{id}")  //{"name":"","parent_id":..}
    public Category update(@PathVariable("id") Integer id, @RequestBody Category category) {
        log.info("更新类别id: {}", id);
        log.info("更新类别信息: {}", JSONUtil.parse(category));
        //1.更新
        category.setId(id);
        return categoryService.update(category);
    }

    //删除类别
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        log.info("删除类别id: {}", id);
        categoryService.deleteById(id);
    }

    //类别列表
    @GetMapping
    public List<Category> categories() {
        return categoryService.queryByFirstLevel();
    }

}

