package com.hjh.dao;

import com.hjh.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 分类(Category)表数据库访问层
 *
 * @author makejava
 * @since 2022-06-24 14:20:51
 */
@Mapper
public interface CategoryDao {

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
     * @return 影响行数
     */
    int insert(Category category);



    /**
     * 修改数据
     *
     * @param category 实例对象
     * @return 影响行数
     */
    int update(Category category);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

    /**
     * 查询分类
     * @return 分类列表
     */
    List<Category> queryByFirstLevel();
}

