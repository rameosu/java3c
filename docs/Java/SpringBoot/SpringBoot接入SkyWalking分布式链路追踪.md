# Springboot接入SkyWalking分布式链路追踪

## 下载SkyWalking（推荐官网）
https://skywalking.apache.org/downloads/

直接下载tar包  https://www.apache.org/dyn/closer.cgi/skywalking/8.6.0/apache-skywalking-apm-8.6.0.tar.gz

## 本地启动SkyWalking
1. 解压缩tar包，进入apache-skywalking-apm-bin/bin目录
2. windows系统直接运行startup.bat（linux系统运行startup.sh）；出现如下视图，表示启动成功
![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/63341b4eee8e4b7bba47e5aa8d1eaa25~tplv-k3u1fbpfcp-watermark.image)

浏览器打开：http://localhost:8080/ 即可访问SkyWalking的管理界面

![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/b3a60c05a2464c3399fcd23867c8cdc0~tplv-k3u1fbpfcp-watermark.image)

## Springboot接入SkyWalking
以javaagent的方式接入，对代码无侵入

```java
java -javaagent:/usr/skywalking/agent/skywalking-agent.jar  -Dskywalking.agent.service_name=your-service-name -Dskywalking.collector.backend_service=127.0.0.1:11800
```
如果是用idea启动springboot项目，可在VM参数区配置

![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/3765f7f6ac004237a0cfde73314e4292~tplv-k3u1fbpfcp-watermark.image)

启动项目，看到如下拓扑图，即说明你的服务已被监控

![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/8ae7d7e89f62486cbf603f20087b6efc~tplv-k3u1fbpfcp-watermark.image)

## 日志打印trace_id
以logback为例
- pom文件添加依赖
```xml
        <dependency>
            <groupId>org.apache.skywalking</groupId>
            <artifactId>apm-toolkit-logback-1.x</artifactId>
            <version>8.6.0</version>
        </dependency>
        <dependency>
            <groupId>net.logstash.logback</groupId>
            <artifactId>logstash-logback-encoder</artifactId>
        </dependency>
```
- logback配置文件添加配置

```xml
    <!-- 控制台输出 -->
    <appender name="Console" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
            <layout class="org.apache.skywalking.apm.toolkit.log.logback.v1.x.mdc.TraceIdMDCPatternLogbackLayout">
                <Pattern>${logEnv} %d{yyyy-MM-dd HH:mm:ss.SSS} [%X{tid}] [%thread] %-5level %logger{36} -%msg%n
                </Pattern>
            </layout>
        </encoder>
    </appender>

    <!-- 上报给logstash -->
    <appender name="logStash" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
        <destination>${StashUrl}</destination>
        <encoder charset="UTF-8" class="net.logstash.logback.encoder.LogstashEncoder">
            <provider class="org.apache.skywalking.apm.toolkit.log.logback.v1.x.logstash.TraceIdJsonProvider"/>
            <customFields>{"applicationName":"${AppID}-${logEnv}"}</customFields>
        </encoder>
    </appender>
```

- idea启动项目可看到控制台输出

![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/bf9d053104ba447693ac01fea353d567~tplv-k3u1fbpfcp-watermark.image)

- Kibana上查看日志

![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/b4746f37a39b4267b320037731b97313~tplv-k3u1fbpfcp-watermark.image)

- 通过trace_id查看调用链路

![image.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/79c86a3f84d74b88bf3aa4bf2e8159f6~tplv-k3u1fbpfcp-watermark.image)

## 解决项目使用WebFlux框架导致trace_id无法获取的问题
比如使用了Springcloud的gateway组件

![image.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/1076d755469c4359b4cc421dd354909b~tplv-k3u1fbpfcp-watermark.image)

将上图中\apache-skywalking-apm-bin\agent\optional-plugins下的4个可选插件复制到
\apache-skywalking-apm-bin\agent\plugins下即可

![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/fdc581373607441fb69e966cd2c03d07~tplv-k3u1fbpfcp-watermark.image)