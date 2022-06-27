package com.hjh.gateway.filter.factory;

import com.hjh.constants.RedisPrefix;
import com.hjh.excpetion.IllegalTokenException;
import com.hjh.utils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component//代表在工厂中创建对象   @configuration 配置     @Component  在工厂中创建对象   使用 filtes -Token
public class TokenGatewayFilterFactory extends AbstractGatewayFilterFactory<TokenGatewayFilterFactory.Config> {
    private static final Logger log = LoggerFactory.getLogger(TokenGatewayFilterFactory.class);

    @Autowired
    private RedisUtils redisUtils;

    public TokenGatewayFilterFactory() {
        super(Config.class);
    }

    //Config 参数就是基于当前中Config创建对象
    @Override
    public GatewayFilter apply(Config config) {
        return new GatewayFilter() {    // servlet service httpServletRequest  httpServletResponse 传统web springmvc   springwebflux new web模型 //filter   request response filterChain.dofilter(request,response)
            @Override
            //参数1: exchange 交换机
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                log.info("config required token: {}", config.requiredToken);
                log.info("config name: {}", config.name);
                if (config.requiredToken) {
                    //1.获取token信息
                    if (exchange.getRequest().getQueryParams().get("token") == null)
                        throw new IllegalTokenException("非法令牌!");
                    String token = exchange.getRequest().getQueryParams().get("token").get(0);
                    log.info("token:{}", token);
                    //2.根据token信息去redis获取
                    if (!redisUtils.hasKey(RedisPrefix.TOKEN_KEY + token))
                        throw new IllegalTokenException("不合法的令牌!");
                }
                return chain.filter(exchange);
            }
        };
    }


    //用来配置将使用filter时指定值赋值给Config中哪个属性
    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("requiredToken", "name");
    }


    //自定义配置类
    public static class Config {
        private boolean requiredToken;  //false
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isRequiredToken() {
            return requiredToken;
        }

        public void setRequiredToken(boolean requiredToken) {
            this.requiredToken = requiredToken;
        }
    }
}
