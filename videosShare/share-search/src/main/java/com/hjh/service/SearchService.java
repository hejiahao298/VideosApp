package com.hjh.service;

import java.util.Map;

public interface SearchService {

    /**
     * 根据搜索条件查询视频
     * @param q 条件
     * @param page 当前页
     * @param rows 每页数
     * @return Map对象，包含了视频列表和视频总数
     */
    Map<String, Object> searchVideos(String q, Integer page, Integer rows);
}
