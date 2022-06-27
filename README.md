# 视频APP项目

## 项目介绍

> 功能点：
>
> ​      模拟视频App，完整的视频上传，观看流程。后端平台对前端业务的支撑。



> 技术点：
>
> ​          核心技术为springcloud+vue两个全家桶实现，整体技术栈有，阿里云短信服务是收费和阿里云OSS对象存储服务需要收费。
>
> - 核心框架：SpringCloud，SpringcloudAlibaba，SpringBoot
> - 持久层框架：MyBatis
> - 数据库连接池：Alibaba Druid
> - 三方服务： 阿里云OSS对象存储服务、阿里云短信服务
> - 消息中间件：RabbitMq，Redis
> - 搜索引擎服务：ElasticSeasearch



> 总体流程：
>
> - **项目描述**：项目分为前台和后台，使用的都是分布式微服务的架构，已经部署上线。
>
> - **主要使用的技术**：后端：springcloud，redis，nacos，gateway，nginx，rabbitmq，elastiacsearch，docker。
>
> - **项目的总体过程**：项目整体使用的是Maven聚合的方式，前端请求经过nginx反向代理到gateway网关，通过网关的断言和过滤机制，经过nacos最后请求转发到微服务上，微服务返回相应的数据。
>
> - **网站链接**：114.116.65.212            # 登录验证码默认为：1234



## 整体目录结构说明

```
├─videosAdmin----------------------------后台父项目，公共依赖版本管理
│  │
│  │
│  ├─videos-admin-------------------------管理员模块
│  │
│  ├─videos-category-------------------------分类管理模块
│  │
│  ├─videos-commons--------------------------公共模块
│  │
│  ├─videos-gateway--------------------------微服务网关中心
│  │
│  ├─videos-users-------------------------用户管理模块
│  │
│  ├─videos-videos-------------------------视频管理模块
│  │
|  |
|  |
|  |
├─videosShare----------------------------前台父项目，公共依赖版本管理
│  │
│  │
│  ├─share-categorys-------------------------分类模块
│  │
│  ├─share-commons--------------------------公共模块
│  │
│  ├─share-gateway--------------------------微服务网关中心
│  │
│  ├─share-search--------------------------全文搜索模块
│  │
│  ├─share-sms--------------------------短信模块
│  │
│  ├─share-search--------------------------用户模块
│  │
│  ├─share-videos--------------------------视频模块



```


