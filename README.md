# fusion-sca-microservice

## 项目介绍

基于Spring Boot + Spring Cloud 微服务脚手架。旨在实现微服务快速开发平台。

## 项目技术

- 服务注册与发现：Nacos
- 配置中心：Nacos
- 服务调用：OpenFeign+dubbo
- 服务网关：Spring Cloud Gateway
- 服务容错：Sentinel
- 负载均衡：Spring Cloud Loadbalancer
- 消息队列：RocketMQ
- 数据库：MySQL
- 缓存：Redis
- 搜索引擎：Elasticsearch
- 定时任务：Xxl-Job
- 文件服务：Minio
- 代码生成器：Mybatis-Plus-Generator
- 持续集成：Jenkins
- 持续部署：Docker+Jenkins
- 容器编排：Kubernetes
- 监控告警：Prometheus+Grafana
- 链路追踪：Skywalking
- 日志收集：Elasticsearch+Logstash+Kibana
- 分布式事务：Seata
- 分布式锁：Redisson

## 项目结构

```
fusion-sca-microservice
├── common -- 公共模块
├── fusion-gateway -- 服务网关
├── fusion-framework -- 框架模块
    ├── fusion-core --  核心模块
    ├── fusion-spring-cloud-alibaba-sentinel -- sentinel 扩展模块
├── fusion-consumer
├── fusion-provider
├── fusion-sse -- 基于 Spring Boot 实现 Server-Sent Events 接口
```

## 实现功能

- Sentinel 动态数据源持久化改造
  - Sentinel dashboard 动态数据源持久化改造，详见：https://github.com/liangenhao/Sentinel/tree/1.8.6-dashboard-datasource/sentinel-dashboard
  - Sentinel 嵌入模式集群流控动态数据源注册实现，详见 fusion-spring-cloud-alibaba-sentinel
- 基于 Spring Boot 实现 Server-Sent Events，详见 fusion-sse
- okhttp3 Spring Boot 整合，详见 fusion-okhttp3-spring-boot-starter
  - [todo] okhttp3 可观测性实现
  - [todo] okhttp3 日志相关
- 基于 Redis 的分布式限流，详见 fusion-rate-limiter
  - [todo] 分布式限流可观测性实现
  - [todo] 限流组件整合 Spring Boot
- 单机版限流实现，详见 fusion-rate-limiter
- 集成 seata 分布式事务
  - AT 模式 + RestTemplate，实现 Spring Cloud 服务下的分布式事务
  - AT 模式 + Feign，实现 Spring Cloud 服务下的分布式事务
  - [todo] AT 模式 + 多数据源，实现单体项目在多数据源下的分布式事务
  - [todo] AT 模式 + Dubbo，实现 Dubbo 服务下的分布式事务
  - [todo] TCC 模式
  - [todo] Saga 模式
  - XA 模式