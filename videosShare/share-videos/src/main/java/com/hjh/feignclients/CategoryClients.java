package com.hjh.feignclients;

import com.hjh.entity.Category;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient("API-CATEGORIES")
public interface CategoryClients {
    @GetMapping("/categories/{id}")
    Category category(@PathVariable("id") Integer id);
}
