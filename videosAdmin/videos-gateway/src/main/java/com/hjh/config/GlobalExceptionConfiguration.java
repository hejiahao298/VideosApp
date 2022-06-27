package com.hjh.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hjh.excpetion.IllegalTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class GlobalExceptionConfiguration implements ErrorWebExceptionHandler {

    //日志对象
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionConfiguration.class);

    @Override  //参数1: request response   ex:出现异常时异常对象
    public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable throwable) {
        Map<String,Object> result = new HashMap<>();

        //1.获取响应对象
        ServerHttpResponse response = serverWebExchange.getResponse();

        //2.response是否结束  多个异常处理时候
        if (response.isCommitted()) {
            return Mono.error(throwable);
        }

        //3.设置响应头类型
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        //4.设置响应状态吗
        if (throwable instanceof IllegalTokenException) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
        } else {
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //5.设置响应内容
        return response
                .writeWith(Mono.fromSupplier(() -> {
                    DataBufferFactory bufferFactory = response.bufferFactory();
                    result.put("msg", throwable.getMessage());
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        return bufferFactory.wrap(objectMapper.writeValueAsBytes(result));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        return null;
                    }
                }));
    }
}
