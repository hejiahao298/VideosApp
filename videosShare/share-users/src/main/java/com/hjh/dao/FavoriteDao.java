package com.hjh.dao;

import com.hjh.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * 收藏(Favorite)表数据库访问层
 *
 * @author makejava
 * @since 2022-06-27 15:47:47
 */
@Mapper
public interface FavoriteDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Favorite queryById(Integer id);

    /**
     * 查询指定行数据
     *
     * @param favorite 查询条件
     * @param pageable         分页对象
     * @return 对象列表
     */
    List<Favorite> queryAllByLimit(Favorite favorite, @Param("pageable") Pageable pageable);

    /**
     * 统计总行数
     *
     * @param favorite 查询条件
     * @return 总行数
     */
    long count(Favorite favorite);

    /**
     * 新增数据
     *
     * @param favorite 实例对象
     * @return 影响行数
     */
    int insert(Favorite favorite);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<Favorite> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<Favorite> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<Favorite> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<Favorite> entities);

    /**
     * 修改数据
     *
     * @param favorite 实例对象
     * @return 影响行数
     */
    int update(Favorite favorite);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

    /**
     * 根据用户id+视频id 查询一条收藏视频
     * @param videoId 视频id
     * @param uid 用户id
     * @return 实例对象
     */
    Favorite queryByVideoIdAndUserId(@Param("videoId") Integer videoId, @Param("uid") Integer uid);

    /**
     * 根据用户id+视频id 删除一条收藏视频记录
     * @param videoId 视频id
     * @param uid 用户id
     * @return 删除条数
     */
    int deleteByVideoIdAndUserId(@Param("videoId") Integer videoId, @Param("uid") Integer uid);

    /**
     * 根据用户id查询收藏列表u
     * @param uid 用户id
     * @return 收藏列表
     */
    List<Favorite> queryByUserId(@Param("uid") Integer uid);
}

