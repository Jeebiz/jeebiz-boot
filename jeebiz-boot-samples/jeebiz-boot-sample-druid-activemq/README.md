#jeefw-boot-datax

##数据总线（DataHub）
包含数据交换、数据仓库、数据网关逻辑.

jeefw-datahub-parent

参考：
https://help.aliyun.com/document_detail/49954.html

一、数据交换

jeefw-datahub-etl

ETL:
http://bigdata.51cto.com/art/201702/530998.htm
http://www.cnblogs.com/buptzym/p/5320552.html
http://blog.csdn.net/zunguitiancheng/article/details/76377193
http://www.phpchina.com/portal.php?mod=view&aid=40550
http://blog.csdn.net/rick_123/article/details/8025002

1、Kettle 客户端 + Kettle 服务端 = 实现客户端定义行为，服务端负责执行的可扩展模式的数据交换，用于无法重构的旧系统数据抽取和交换
2、Apache Camel + Pf4j = 构建可视化数据交换实现：
该模式以插件形式集成，只需要实现统一的出口，入口，所有事情交给插件完成；用于无法重构但可以写新的代码实现的数据抽取和交换
3、Apache Kafka、RocketMQ 构建异步数据交换机制
基于消息队列的异步数据交换机制，适用于大批量的数据交换实现；同时可结合插件机制实现动态扩展和抽取
4、主要能力
a、数据源注册，多数据源切换
b、插拔式的数据抽取，转换实现

二、数据仓库

jeefw-datahub-warehouse

Data Warehouse（DW或DWH）
https://baike.baidu.com/item/%E6%95%B0%E6%8D%AE%E4%BB%93%E5%BA%93/381916?fr=aladdin
数据交换服务使得交换服务具备了数据落地的能力，可以此为有利条件构建标准的权威数据仓库

jeefw-datahub-gateway

三、数据网关（Data Service Gateway）

1、简介

Data Service Gateway (DSG) is a service-based data exchange middleware providing data services for application
integration across heterogeneous systems.
数据网关是基于服务的数据交换中间件，提供数据服务用于异构系统之间的数据整合与应用集成。

2、对已有的数据或其他数据接口构建统一的交换服务接口和交换机制，如API代理、消息队列等
