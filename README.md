# InfluxDB API

## 目录结构

```text
cn.imcao.influxdbapi
├── config      配置类
├── controller  控制层
├── dao         数据访问层
└── pojo        请求/响应对象
```

## API 文档

http://localhost:8080/swagger-ui.html

## 数据源配置

```yaml
spring:
  influxdb2:
    url: "http://101.132.32.165:8086"
    token: "token"
    org: "NUAA"
    bucket: "MQTT"
```
