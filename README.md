
#jeebiz-boot

基于Spring-Boot基础工程；
该工程完成生产环境中即将使用的各种组件相关配置依赖；

Spring-Boot参考资料：

http://blog.didispace.com/categories/Spring-Boot/
https://git.oschina.net/didispace/SpringBoot-Learning
https://github.com/JeffLi1993/springboot-learning-example

Spring Boot系列博文：

http://blog.didispace.com/categories/Spring-Boot/
http://www.bysocket.com/?p=1666
 
Spring Boot系列博文：http://blog.didispace.com/categories/Spring-Boot/

Spring Boot 官方手册：

https://docs.spring.io/spring-boot/docs/1.5.9.RELEASE/reference/htmlsingle/
	
		
		
|--jeebiz-boot	
	|--jeebiz-boot-samples		： 项目示例，基于Druid、HikariCP两种数据源的多种示例
		|--jeebiz-boot-sample-druid
		|--jeebiz-boot-sample-druid-activemq
		|--jeebiz-boot-sample-druid-amqp
		|--jeebiz-boot-sample-druid-rocketmq
		|--jeebiz-boot-sample-druid-shiro
		|--jeebiz-boot-sample-druid-war
		|--jeebiz-boot-sample-hikaricp
		|--jeebiz-boot-sample-hikaricp-activemq
		|--jeebiz-boot-sample-hikaricp-amqp
		|--jeebiz-boot-sample-hikaricp-rocketmq
		|--jeebiz-boot-sample-hikaricp-shiro
		|--jeebiz-boot-sample-hikaricp-war
		|--jeebiz-boot-sample-flowable
		|--jeebiz-boot-sample-flowable-app
		|--...	
	|--jeebiz-boot-project		： 基于模块，整合常用依赖、通用模块
		|--jeebiz-boot-dependencies
		|--jeebiz-boot-modules
			|--jeebiz-boot-authz-feature  	： 功能菜单
			|--jeebiz-boot-authz-rbac0 		： 简单的RBAC
			|--jeebiz-boot-autoconfigure   	：自动初始化
		|--jeebiz-boot-parent
		|--jeebiz-boot-starters
		|--...