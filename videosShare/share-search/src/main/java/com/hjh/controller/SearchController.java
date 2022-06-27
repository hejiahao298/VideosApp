package com.hjh.controller;

import com.hjh.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;

    private static final Logger log = LoggerFactory.getLogger(SearchController.class);

    @GetMapping("search/videos")
    public Map<String,Object> videos(@RequestParam(value = "q") String q,
                                     @RequestParam(value = "page",defaultValue = "1") Integer page,
                                     @RequestParam(value = "per_page",defaultValue = "5") Integer rows){

        return searchService.searchVideos(q,page,rows);

    }
}
