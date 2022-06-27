package com.hjh.service;

import com.hjh.entity.Category;

import java.util.List;


/**
 * 分类(Category)表服务接口
 *
 * @author makejava
 * @since 2022-06-25 23:23:43
 */
public interface CategoryService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Category queryById(Integer id);


    /**
     * 新增数据
     *
     * @param category 实例对象
     * @return 实例对象
     */
    Category insert(Category category);

    /**
     * 修改数据
     *
     * @param category 实例对象
     * @return 实例对象
     */
    Category update(Category category);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

    /**
     * 查询所有类别
     * @return 类别列表
     */
    List<Category> findAll();
}
