## Jeebiz Boot 简介：

> [danger] Jeebiz Boot 1.x 是以 Spring Boot 2.3.x 为基础，构建的 Spring Boot 项目快速开发脚手架；

- 1、继承 Spring Boot ，具备 Spring Boot 的 所有特性

- 2、集成 Dozer、Fastjson、Jackson、Guava、Mybatis Plus、Okhttp3、Snowflake、Swagger、TrueLicense、Ip2region 等组件

- 3、集成各类三方Maven组件，统一管理版本

- 4、定义基础对象（BaseController、BaseService、BaseMapper、BaseMapper 等）、异常对象、IP地址解析

- 5、支持 WebMvc 和 WebFlux 项目自动初始化，全局异常处理

- 6、自定义项目默认的打包编译方式；快速的构建项目部署包

#### Jeebiz Boot 说明：

该项目，主要用于定义通用的Maven配置、第三方依赖、打包规则等！

**Maven模块**：

| 模块  | 说明  |
| ------------ | ------------ |
| jeebiz-boot-dependencies | pom类型模块，负责配置项目公共依赖，便于依赖组件版本控制  |
| jeebiz-boot-modules  |  项目基础模块，封装基础API、公共对象（BaseController、BaseService、BaseMapper、BaseMapper 等）、异常对象到jar中，以便复用 |
| jeebiz-boot-parent  | pom类型模块，默认配置Maven插件相关参数和打包方式 |
| jeebiz-boot-samples  |  具体服务示例，该模块下的各服务模块，均要继承 `jeebiz-boot-parent` |

**注意事项**：

- 1、新建独立项目需要继承 jeebiz-boot-parent，此处的项目指产品项目
- 2、按目前结构，基本后期都是增加业务代码（即子系统的业务）
- 3、如果一个业务模块是比较多的模块，比如学工服务，创建模块时候应考虑后期子服务拆分的问题，应创建一个多级模块

**结构说明**

```
|--jeebiz-boot
|----jeebiz-boot-dependencies		            #公共依赖，便于依赖组件版本控制
|----jeebiz-boot-modules			            #具体业务服务
|------jeebiz-boot-api				            #基础API、公共对象（BaseController、BaseService、BaseMapper、BaseMapper 等）、异常对象
|------jeebiz-boot-crypto-autoconfigure	        #加解密组件自动初始化
|------jeebiz-boot-external-autoconfigure	    #外部API集成自动初始化
|------jeebiz-boot-jackson-autoconfigure	    #Jackson自动初始化
|------jeebiz-boot-license-autoconfigure        #License自动初始化
|------jeebiz-boot-plugin-api                   #插件API
|------jeebiz-boot-webflux-autoconfigure	    #WebFlux自动初始化
|------jeebiz-boot-webmvc-autoconfigure	        #WebMVC 自动初始化
|----jeebiz-boot-parent			                #子模块的父级工程，定义Maven配置
|----jeebiz-boot-samples				        #具体业务服务
|--------jeebiz-boot-sample-druid		        #集成Druid数据源示例
|--------jeebiz-boot-sample-druid-activemq	    #集成Druid数据源 + ActiveMQ 示例
|--------jeebiz-boot-sample-druid-amqp		    #集成Druid数据源 + RabbitMQ 示例
|--------jeebiz-boot-sample-druid-rocketmq	    #集成Druid数据源 + RocketMQ 示例
|--------jeebiz-boot-sample-druid-war		    #集成Druid数据源打War包示例
|--------jeebiz-boot-sample-hikaricp		    #集成 Hikaricp数据源示例
|--------jeebiz-boot-sample-hikaricp-activemq	#集成 Hikaricp数据源 + ActiveMQ 示例
|--------jeebiz-boot-sample-hikaricp-amqp		#集成 Hikaricp数据源 + RabbitMQ 示例
|--------jeebiz-boot-sample-hikaricp-rocketmq	#集成 Hikaricp数据源 + RocketMQ 示例
|--------jeebiz-boot-sample-hikaricp-war		#集成 Hikaricp数据源打War包示例
|--------jeebiz-boot-sample-r2dbc-webflux	    #集成 R2dbc + WebFlux 示例
```

#### Spring Docs
https://docs.spring.io/spring-boot/docs/2.3.12.RELEASE/reference/html/spring-boot-features.html#boot-features-spring-application