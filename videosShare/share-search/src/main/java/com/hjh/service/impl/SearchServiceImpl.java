package com.hjh.service.impl;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hjh.config.RestClientConfig;
import com.hjh.service.SearchService;
import com.hjh.vo.VideoVO;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchServiceImpl.class);

    @Qualifier("elasticsearchClient")
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 根据搜索条件查询视频
     * @param q 条件
     * @param page 当前页
     * @param rows 每页数
     * @return Map对象，包含了视频列表和视频总数
     */
    @Override
    public Map<String, Object> searchVideos(String q, Integer page, Integer rows) {
        //定义map  total_count 返回查询总数        items: 返回查询记录
        Map<String, Object> result = new HashMap<>();

        //1.计算分页 起始为0
        int start = (page - 1) * rows;

        //2. 创建es搜索请求对象
        SearchRequest searchRequest = new SearchRequest();

        //3. 搜索源对象
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder
                .from(start)
                .size(rows)
                .query(QueryBuilders.termQuery("title",q));

        //4. 设置搜索对象的索引:“video” , 搜索类型:“video” , 搜索条件:"sourceBuilder"
        searchRequest
                .indices("video")
                .types("video")
                .source(sourceBuilder);

        //5. 搜索响应对象
        SearchResponse searchResponse = null;
        try {
            //6. 执行搜索返回响应对象
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //7.获取符合条件总数
            long totalHits = searchResponse.getHits().totalHits;
            //8.封装结果
            if(totalHits>0){
                result.put("total_count",totalHits);
                SearchHit[] hits = searchResponse.getHits().getHits();
                List<VideoVO> videoVOS = new ArrayList<>();
                //9.遍历符合条件每一个文档封装成videoVO对象
                for (SearchHit hit : hits) {
                    //10.获取文件字符串表现形式  就是json格式
                    String sourceAsString = hit.getSourceAsString();
                    log.info("符合条件的结果: {}", sourceAsString);
                    //11.通过jackson将json格式转为videoVo对象
                    VideoVO videoVO = JSONUtil.toBean(sourceAsString, VideoVO.class);
                    //12.设置videoVo文档id
                    videoVO.setId(Integer.parseInt(hit.getId()));
                    //13.放入videoVo集合
                    videoVOS.add(videoVO);
                }
                result.put("items", videoVOS);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
