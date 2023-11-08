开源在线客服

#### 如果对您有帮助，辛苦帮忙点赞支持下，感谢您的支持！

#### 环境要求
```
   jdk 1.8
   mysql 6.5以上
   配置host  IP(oc-server的IP地址) im.jshii.com.cn
```

### 初始化数据库/配置
```
  1. Mysql 导入oc_db.sql，SQL脚本在oc-server/sources/db/目录下
  2. 配置application-dev.yml或者application-uat.yml文件，
     更新数据库IP/PORT/用户/密码，默认为（127.0.0.1:3306?oc_db&user_name=root&password=123456）
  3. 如果集群部署，请修改hazelcast-dev.xml或者hazelcast-uat.xml，
     具体规则可参考hazelcast官网（https://docs.hazelcast.org/docs/3.12.7/manual/html-single/index.html#setting-up-clusters）
```
      
### oc-server部署步骤
```
 jar包路径: mkdir logs
 jar包路径：java -jar oc-server.jar --spring.profiles.active=dev > ./logs/console.log &
 注意：以上操作请在jar包当前目录
```
### 用户端
   https://github.com/chuangyeifang/oc-customer-client
   
### 客服工作台
   https://github.com/chuangyeifang/oc-waiter-client

#### 系统预览
   阅读CSDN文章：https://blog.csdn.net/chuangyeifang163/article/details/107765411

#### 加入我们
  邮件发送至：chuangyeifang163@163.com
  
#### 反馈交流
  QQ群: 606173512
