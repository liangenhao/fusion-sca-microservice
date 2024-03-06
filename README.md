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
├── fusion-sse -- event-stream 实现
```

## 实现功能

- Sentinel 动态数据源持久化改造
  - Sentinel dashboard 动态数据源持久化改造，详见：https://github.com/liangenhao/Sentinel/tree/1.8.6-dashboard-datasource/sentinel-dashboard
  - Sentinel 嵌入模式集群流控动态数据源注册实现，详见 fusion-spring-cloud-alibaba-sentinel
