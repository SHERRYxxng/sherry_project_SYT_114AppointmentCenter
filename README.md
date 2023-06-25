# sherry_project_SYT_114AppointmentCenter
尚医疗
##尚医疗
# 一、项目概述

## 1、功能简介

尚医通即为网上预约挂号系统，网上预约挂号是近年来开展的一项便民就医服务，旨在缓解看病难、挂号难的就医难题，许多患者为看一次病要跑很多次医院，最终还不一定能保证看得上医生。网上预约挂号全面提供的预约挂号业务从根本上解决了这一就医难题。随时随地轻松挂号！不用排长队！



## 2、业务流程

![img](http://file.xuxianng.xyz/202306251859196.png)   



## 3、系统架构

**架构设计需要考虑的几个方面：**

- **性能：**主要考虑访问频率，每个用户每天的访问次数。项目初始阶段用户的访问量并不大，如果考虑做运营推广，可能会迎来服务器访问量骤增，因此要考虑**分布式部署，引入缓存**
- **可扩展性：**系统功能会随着用户量的增加以及多变的互联网用户需求不断地扩展，因此考虑到系统的可扩展性的要求需要**使用微服务架构，引入消息中间件**
- **高可用：**系统一旦宕机，将会带来不可挽回的损失，因此必须做负载均衡，甚至是异地多活这类复杂的方案。如果数据丢失，修复将会非常麻烦，只能靠人工逐条修复，这个很难接受，因此需要考虑存储高可靠。我们需要考虑多种异常情况：机器故障、机房故障，针对机器故障，我们需要设计 MySQL 同机房主备方案；针对机房故障，我们需要设计 MySQL 跨机房同步方案。
- **安全性：**系统的信息有一定的隐私性，例如用户的个人身份信息，因此使用账号密码管理、数据库访问权限控制即可。
- **成本：**视频类网站的主要成本在于服务器成本、流量成本、存储成本、流媒体研发成本，中小型公司可以考虑使用云服务器和云服务。

![](C:/Users/70208/Desktop/尚医通/讲义/img/ea4abebb-f47a-4641-9fac-212c7baab674.jpg)



# 二、数据库设计

## 1、创建数据库表

![img](http://file.xuxianng.xyz/202306251859196.png) 



## 2、数据库设计规则

以下规约只针对本模块，更全面的文档参考《阿里巴巴Java开发手册》：五、MySQL数据库

1、库名与应用名称尽量一致

2、表名、字段名必须使用小写字母或数字，禁止出现数字开头，

3、表名不使用复数名词  employee  employees

4、表的命名最好是加上“业务名称_表的作用”。如，edu_teacher

5、表必备三字段：id, gmt_create, gmt_modified

说明：

其中 id 必为主键，类型为 bigint unsigned、单表时自增、步长为 1。

（如果使用分库分表集群部署，则id类型为varchar，非自增，业务中使用分布式id生成器）

gmt_create, gmt_modified 的类型均为 datetime 类型，前者现在时表示主动创建，后者过去分词表示被 动更新。

6、单表行数超过 500 万行或者单表容量超过 2GB，才推荐进行分库分表。 说明：如果预计三年后的数据量根本达不到这个级别，请不要在创建表时就分库分表。 

7、表达是与否概念的字段，必须使用 is_xxx 的方式命名，数据类型是 unsigned tinyint （1 表示是，0 表示否）。 

说明：任何字段如果为非负数，必须是 unsigned。 

注意：POJO 类中的任何布尔类型的变量，都不要加 is 前缀。数据库表示是与否的值，使用 tinyint 类型，坚持 is_xxx 的 命名方式是为了明确其取值含义与取值范围。 

正例：表达逻辑删除的字段名 is_deleted，1 表示删除，0 表示未删除。 

8、小数类型为 decimal，禁止使用 float 和 double。 说明：float 和 double 在存储的时候，存在精度损失的问题，很可能在值的比较时，得到不 正确的结果。如果存储的数据范围超过 decimal 的范围，建议将数据拆成整数和小数分开存储。

9、如果存储的字符串长度几乎相等，使用 char 定长字符串类型。 手机号、医院编号、邮政编码...

10、varchar 是可变长字符串，不预先分配存储空间，长度不要超过 5000，如果存储长度大于此值，定义字段类型为 text，独立出来一张表，用主键来对应，避免影响其它字段索引效率。

11、唯一索引名为 uk_字段名；普通索引名则为 idx_字段名。

说明：uk_ 即 unique key；idx_ 即 index 的简称

12、不得使用外键与级联，一切外键概念必须在应用层解决。

外键与级联更新适用于单机低并发，不适合分布式、高并发集群；级联更新是强阻塞，存在数据库更新风暴的风险；外键影响数据库的插入速度。 



# 三、搭建项目工程

## 1、工程结构

![image-20230227180100038](http://file.xuxianng.xyz/202306251859198.png) 

## 2、创建父工程yygh_parent

### 2.1、创建maven工程

![img](http://file.xuxianng.xyz/202306251859200.png)

**配置：**

groupId：com.atguigu

artifactId：yygh_parent

一直下一步到完成

![img](http://file.xuxianng.xyz/202306251859201.png)



### 2.2、在pom.xml中添加依赖

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.2.1.RELEASE</version>
    <relativePath/> <!-- lookup parent from repository -->
</parent>

<properties>
    <java.version>1.8</java.version>
    <cloud.version>Hoxton.RELEASE</cloud.version>
    <alibaba.version>2.2.0.RELEASE</alibaba.version>
    <mybatis-plus.version>3.3.1</mybatis-plus.version>
    <mysql.version>8.0.27</mysql.version>
    <swagger.version>2.7.0</swagger.version>
    <jwt.version>0.7.0</jwt.version>
    <fastjson.version>1.2.29</fastjson.version>
    <httpclient.version>4.5.1</httpclient.version>
    <easyexcel.version>2.2.0-beta2</easyexcel.version>
    <aliyun.version>4.1.1</aliyun.version>
    <oss.version>3.9.1</oss.version>
    <jodatime.version>2.10.1</jodatime.version>
</properties>

<!--配置dependencyManagement锁定依赖的版本-->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-alibaba-dependencies</artifactId>
            <version>${alibaba.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <!--mybatis-plus 持久层-->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>${mysql.version}</version>
        </dependency>
        <!--swagger-->
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
            <version>${swagger.version}</version>
        </dependency>
        <!--swagger ui-->
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger-ui</artifactId>
            <version>${swagger.version}</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt</artifactId>
            <version>${jwt.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpclient</artifactId>
            <version>${httpclient.version}</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>${fastjson.version}</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>easyexcel</artifactId>
            <version>${easyexcel.version}</version>
        </dependency>
        <dependency>
            <groupId>com.aliyun</groupId>
            <artifactId>aliyun-java-sdk-core</artifactId>
            <version>${aliyun.version}</version>
        </dependency>
        <dependency>
            <groupId>com.aliyun.oss</groupId>
            <artifactId>aliyun-sdk-oss</artifactId>
            <version>${oss.version}</version>
        </dependency>
        <!--日期时间工具-->
        <dependency>
            <groupId>joda-time</groupId>
            <artifactId>joda-time</artifactId>
            <version>${jodatime.version}</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```



## 3、搭建model模块

### 3.1、在父工程下创建模块model

**选择 maven类型，点击下一步**

![img](http://file.xuxianng.xyz/202306251859203.png) 

**输入模块名称model，下一步完成创建**

**![img](http://file.xuxianng.xyz/202306251859825.png)


**

### 3.2、添加项目需要的依赖

```xml
<dependencies>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>
    <!--mybatis-plus-->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
        <scope>provided </scope>
    </dependency>
    <!--swagger-->
    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-swagger2</artifactId>
        <scope>provided </scope>
    </dependency>
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>easyexcel</artifactId>
        <scope>provided </scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-mongodb</artifactId>
        <scope>provided </scope>
    </dependency>
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>fastjson</artifactId>
        <scope>provided </scope>
    </dependency>
</dependencies>
```



### 3.3、复制实体类和VO类

![img](http://file.xuxianng.xyz/202306251859832.png) 

![img](http://file.xuxianng.xyz/202306251859875.png) 



## 4、搭建service模块

### 4.1、在父工程下创建模块service

**选择 maven类型，点击下一步**

**输入模块名称 service，下一步完成创建**

![img](http://file.xuxianng.xyz/202306251859363.png) 



### 4.2、添加项目需要的依赖

```xml
<dependencies>
    <dependency>
        <groupId>com.atguigu</groupId>
        <artifactId>model</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    <!--web-->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <!--mybatis-plus-->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
    </dependency>
    <!--mysql-->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>
    <!--开发者工具-->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <optional>true</optional>
    </dependency>
    <!-- 服务调用feign -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
    <!-- 服务注册 -->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    </dependency>
</dependencies>
```



## 5、搭建医院service_hosp模块

### 5.1、在service模块下创建模块

![img](http://file.xuxianng.xyz/202306251859504.png)

**输入模块名称 service_hosp，下一步完成创建**

![img](http://file.xuxianng.xyz/202306251859517.png) 



# 四、医院设置接口（列表和删除）

## 1、医院设置模块需求

医院设置主要是用来保存开通医院的一些基本信息，每个医院一条信息，保存了医院编号（平台分配，全局唯一）和接口调用相关的签名key等信息，是整个流程的第一步，只有开通了医院设置信息，才可以上传医院相关信息。我们所开发的功能就是基于单表的一个CRUD、锁定/解锁和发送签名信息这些基本功能。

**医院设置表结构**

hosname：医院名称

hoscode：医院编号（全局唯一，api接口必填信息）

api_url：医院回调的基础url（如：预约下单，我们要调用该地址去医院下单）

sign_key：双方api接口调用的签名key

contacts_name：医院联系人姓名

contacts_phone：医院联系人手机

status：状态（锁定/解锁）



## 2、医院设置模块环境配置

### 2.1、在service_hosp模块创建配置文件

**resources目录下创建文件 application.properties**

**![img](http://file.xuxianng.xyz/202306251859963.png)**

```properties
# 服务端口
server.port=8201
# 服务名
spring.application.name=service-hosp
# 环境设置：dev、test、prod
spring.profiles.active=dev
# mysql数据库连接
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/yygh_hosp?serverTimezone=GMT%2B8&characterEncoding=utf-8&useSSL=false
spring.datasource.username=root
spring.datasource.password=123456
#返回json的全局时间格式
spring.jackson.date-format=yyyy-MM-dd HH:mm:ss
spring.jackson.time-zone=GMT+8
#mybatis日志
mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
```



### 2.2、创建包结构，创建启动类

创建启动类ServiceHospApplication.java，注意启动类的创建位置

![img](http://file.xuxianng.xyz/202306251859996.png)

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServiceHospApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceHospApplication.class, args);
    }
}
```



### 2.3、创建service

**![img](http://file.xuxianng.xyz/202306251859431.png)**

```java
public interface HospitalSetService extends IService<HospitalSet> {
    
}
@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {
    
}
```



### **2.4、创建mapper**

![img](http://file.xuxianng.xyz/202306251859622.png)

 

```java
public interface HospitalSetMapper extends BaseMapper<HospitalSet> {
}
```

HospitalSetMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.atguigu.yygh.hosp.mapper.HospitalSetMapper">
</mapper>
```



### 2.5、编写controller代码

```java
//医院设置接口
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {
    @Autowired
    private HospitalSetService hospitalSetService;
    //查询所有医院设置
    @GetMapping("findAll")
    public List<HospitalSet> findAll() {
        List<HospitalSet> list = hospitalSetService.list();
        return list;
    }
}
```



### 2.6、创建SpringBoot配置类

创建config包，创建HospConfig.java

```java
@Configuration
@EnableTransactionManagement
@MapperScan("com.atguigu.yygh.hosp.mapper")
public class HospConfig {
    
}
```



### 2.7、运行启动类

访问http://localhost:8201/admin/hosp/hospitalSet/findAll

得到json数据



## 3、医院设置逻辑删除功能

### 3.1、HospitalSetController添加删除方法

```java
@DeleteMapping("{id}")
public boolean removeById(@PathVariable Long id){
    return hospitalSetService.removeById(id);
}
```



## 4、跨域配置 

### 1、什么是跨域

从一个域名的网页去请求另一个域名的资源时，域名、端口、协议任一不同，都是跨域。

它是由浏览器的同源策略造成的，是浏览器对javascript施加的安全限制。

同源策略：是指协议，域名，端口都要相同，其中有一个不同都会产生跨域；

前后端分离开发中，需要考虑ajax跨域的问题。

这里我们可以从服务端解决这个问题。



### 2、配置

在Controller类上添加注解

```
@CrossOrigin //跨域
```



# 五、整合Swagger2

前后端分离开发模式中，api文档是最好的沟通方式。

Swagger 是一个规范和完整的框架，用于生成、描述、调用和可视化 RESTful 风格的 Web 服务。

及时性 (接口变更后，能够及时准确地通知相关前后端开发人员)

规范性 (并且保证接口的规范性，如接口的地址，请求方式，参数及响应格式和错误信息)

一致性 (接口信息一致，不会出现因开发人员拿到的文档版本不一致，而出现分歧)

可测性 (直接在接口文档上进行测试，以方便理解业务)

## 1、配置Swagger2

**1、创建common模块**

**在yygh_parent下创建模块common**

配置：

groupId：com.atguigu

artifactId：common

![img](http://file.xuxianng.xyz/202306251859668.png) 



**2、在common中引入相关依赖**

```xml
 <dependencies>
     <!--service_util下创建全局异常处理器-->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <scope>provided </scope>
    </dependency>
     
    <!--mybatis-plus-->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
        <scope>provided </scope>
    </dependency>
     
     
     
    <!--lombok用来简化实体类：需要安装lombok插件-->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>
     
     
     
    <!--swagger-->
    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-swagger2</artifactId>
    </dependency>
    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-swagger-ui</artifactId>
    </dependency>
     
     
     
     
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>fastjson</artifactId>
    </dependency>
</dependencies>
```



## 2、common下创建模块service_utils

![img](http://file.xuxianng.xyz/202306251859680.png)



## **3、模块service_utils创建配置类**

创建包com.atguigu.yygh.common.config，创建类Swagger2Config

```java
@Configuration
@EnableSwagger2
public class Swagger2Config {
    @Bean
    public Docket webApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("webApi")
                .apiInfo(webApiInfo())
                .select()
                //只显示api路径下的页面
                //.paths(Predicates.and(PathSelectors.regex("/api/.*")))
                .build();
    }
    @Bean
    public Docket adminApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("adminApi")
                .apiInfo(adminApiInfo())
                .select()
                //只显示admin路径下的页面
                .paths(Predicates.and(PathSelectors.regex("/admin/.*")))
                .build();
    }
    private ApiInfo webApiInfo(){
        return new ApiInfoBuilder()
                .title("网站-API文档")
                .description("本文档描述了网站微服务接口定义")
                .version("1.0")
                .contact(new Contact("atguigu", "http://atguigu.com", "493211102@qq.com"))
                .build();
    }
    private ApiInfo adminApiInfo(){
        return new ApiInfoBuilder()
                .title("后台管理系统-API文档")
                .description("本文档描述了后台管理系统微服务接口定义")
                .version("1.0")
                .contact(new Contact("atguigu", "http://atguigu.com", "49321112@qq.com"))
                .build();
    }
}
```



## 4、模块service模块引入service_utils

```
<dependency>
    <groupId>com.atguigu</groupId>
    <artifactId>service_utils</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```



## **5、service_hosp启动类添加注解**

![img](http://file.xuxianng.xyz/202306251859719.png) 



## 6、通过地址访问测试

![img](http://file.xuxianng.xyz/202306251859152.png)

可以添加一些自定义设置，例如：

定义样例数据

```java
@ApiModelProperty(value = "创建时间", example = "2019-01-01 8:00:00")
@TableField(fill = FieldFill.INSERT)
private Date gmtCreate;
@ApiModelProperty(value = "更新时间", example = "2019-01-01 8:00:00")
@TableField(fill = FieldFill.INSERT_UPDATE)
private Date gmtModified;
```

![img](http://file.xuxianng.xyz/202306251859276.png) 



## 7、定义接口说明和参数说明

定义在方法上：@ApiOperation

定义在参数上：@ApiParam

```java
//医院设置接口
@Api(description = "医院设置接口")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {
    
    @Autowired
    private HospitalSetService hospitalSetService;
    
    //查询所有医院设置
    @ApiOperation(value = "医院设置列表")
    @GetMapping("findAll")
    public List<HospitalSet> findAll() {
        List<HospitalSet> list = hospitalSetService.list();
        return list;
    }
    
    @ApiOperation(value = "医院设置删除")
    @DeleteMapping("{id}")
    public boolean removeById(@ApiParam(name = "id", value = "ID", required = true) @PathVariable Long id){
        return hospitalSetService.removeById(id);
    }
}
```

![img](http://file.xuxianng.xyz/202306251859366.png)



# 六、统一返回数据格式

项目中我们会将响应封装成json返回，一般我们会将所有接口的数据格式统一， 使前端(iOS Android, Web)对数据的操作更一致、轻松。

一般情况下，统一返回数据格式没有固定的格式，只要能描述清楚返回的数据状态以及要返回的具体数据就可以。但是一般会包含状态码、返回消息、数据这几部分内容

例如，我们的系统要求返回的基本数据格式如下：

**列表：**

```
{
  "success": true,
  "code": 20000,
  "message": "成功",
  "data": {
    "items": [
      {
        "id": "1",
        "name": "刘德华",
        "intro": "毕业于师范大学数学系，热爱教育事业，执教数学思维6年有余"
      }
    ]
  }
}
```

**分页：**

```
{
  "success": true,
  "code": 20000,
  "message": "成功",
  "data": {
    "total": 17,
    "rows": [
      {
        "id": "1",
        "name": "刘德华",
        "intro": "毕业于师范大学数学系，热爱教育事业，执教数学思维6年有余"
      }
    ]
  }
}
```

**没有返回数据：**

```
{
  "success": true,
  "code": 20000,
  "message": "成功",
  "data": {}
}
```

**失败：**

```
{
  "success": false,
  "code": 20001,
  "message": "失败",
  "data": {}
}
```

因此，我们定义统一结果

```
{
  "success": 布尔, //响应是否成功
  "code": 数字, //响应码
  "message": 字符串, //返回消息
  "data": HashMap //返回数据，放在键值对中
}
```



## 1、创建统一结果返回类 

### 1.1、service_utils创建统一结果返回类

**创建接口定义返回码**

**创建包com.atguigu.yygh.common.result，创建接口 ResultCode.java**

```java
public interface ResultCode {
    public static Integer SUCCESS = 20000;
    public static Integer ERROR = 20001;
}
```



**创建类 R.java**

```java
@Data
public class R {
    @ApiModelProperty(value = "是否成功")
    private Boolean success;
    @ApiModelProperty(value = "返回码")
    private Integer code;
    @ApiModelProperty(value = "返回消息")
    private String message;
    @ApiModelProperty(value = "返回数据")
    private Map<String, Object> data = new HashMap<String, Object>();
    private R(){}
    public static R ok(){
        R r = new R();
        r.setSuccess(true);
        r.setCode(ResultCode.SUCCESS);
        r.setMessage("成功");
        return r;
    }
    public static R error(){
        R r = new R();
        r.setSuccess(false);
        r.setCode(ResultCode.ERROR);
        r.setMessage("失败");
        return r;
    }
    public R success(Boolean success){
        this.setSuccess(success);
        return this;
    }
    public R message(String message){
        this.setMessage(message);
        return this;
    }
    public R code(Integer code){
        this.setCode(code);
        return this;
    }
    public R data(String key, Object value){
        this.data.put(key, value);
        return this;
    }
    public R data(Map<String, Object> map){
        this.setData(map);
        return this;
    }
}
```



## 2、统一返回结果使用



### 2.1、修改Controller中的返回结果 

```java
//查询所有医院设置
@ApiOperation(value = "医院设置列表")
@GetMapping("findAll")
public R findAll() {
    List<HospitalSet> list = hospitalSetService.list();
    return R.ok().data("list",list);
}

@ApiOperation(value = "医院设置删除")
@DeleteMapping("{id}")
public R removeById(@ApiParam(name = "id", value = "讲师ID", required = true) @PathVariable String id){
    hospitalSetService.removeById(id);
    return R.ok();
}
```



# 七、医院设置接口（分页条件查询）

根据医院名称模糊查询，医院编号等值查询。

### 1、HospConfig配置分页插件

```java
/**
 * 分页插件
 */
@Bean
public PaginationInterceptor paginationInterceptor() {
    return new PaginationInterceptor();
}
```



### 2、封装查询条件HospitalSetQueryVo

![img](http://file.xuxianng.xyz/202306251859410.png) 

 已经创建好了。

```java
@Data
public class HospitalSetQueryVo {
    @ApiModelProperty(value = "医院名称")
    private String hosname;
    @ApiModelProperty(value = "医院编号")
    private String hoscode;
}
```



### 3、接口实现

```java
@PostMapping("{page}/{limit}")
public R pageQuery(@PathVariable Integer page, @PathVariable Integer limit,
                   @RequestBody HospitalSetQueryVo hospitalSetQueryVo){

    //1、分页对象
    Page<HospitalSet> hospitalSetPage = new Page<>(page,limit);

    //2、构建查询条件
    QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();
    String hoscode = hospitalSetQueryVo.getHoscode();
    String hosname = hospitalSetQueryVo.getHosname();

    if(!StringUtils.isEmpty(hoscode)){
        queryWrapper.eq("hoscode",hoscode);
    }
    if(!StringUtils.isEmpty(hosname)){
        queryWrapper.like("hosname",hosname);
    }

    hospitalSetService.page(hospitalSetPage,queryWrapper);

    //3、返回值
    List<HospitalSet> list = hospitalSetPage.getRecords();
    long total = hospitalSetPage.getTotal();

    return R.ok().data("rows",list).data("total",total);
}
```



# 八、医院设置接口（添加和修改）

```
/**
     * 开通医院设置接口
     * @param hospitalSet ， 为某个医院开通医院设置，传递的医院设置相关的数据
     *                    开通医院设置的需求：传递的参数包括--hosname+hoscode+apiUrl+signKey+contactsName+contactsPhone
     *                    这些参数从业务的角度，是由医院端提供，管理员负责录入
     *                    开通的医院设置，默认的status状态需要为1，表示正常状态（权限处于正常状态）。
     *                    只有开通了权限的医院，才算入驻到统一挂号平台；才可以上传各种数据
     *
     *                    协和医院的测试数据（大家都是用这个****）：
     *                    hosname=北京协和医院
     *                    hoscode=10000
     *                    apiUrl=http://127.0.0.1:9998
     *                    signKey=1
     *                    contactsName=张三
     *                    contactsPhone=13101102345
     *
                            {
                            "apiUrl": "http://127.0.0.1:9998",
                            "contactsName": "张三",
                            "contactsPhone": "13101102345",
                            "hoscode": "10000",
                            "hosname": "北京协和医院",
                            "signKey": "1"
                            }
     *
     * @return
     */
```



### 1、新增 

```java
@ApiOperation(value = "新增医院设置")
@PostMapping("saveHospSet")
public R save(
    @ApiParam(name = "hospitalSet", value = "医院设置对象", required = true)
    @RequestBody HospitalSet hospitalSet){
    //设置状态 1 使用 0 不能使用
    hospitalSet.setStatus(1);
    hospitalSetService.save(hospitalSet);
    
    //至于返回值的格式和属性值，根据自己实际情况去定（如果开发文档有特殊要求，message=开通成功）
    return R.ok();
}
```



### 2、根据id查询

需求：根据id查询，将医院设置对象封装到data中的item属性上。

```java
@ApiOperation(value = "根据ID查询医院设置")
@GetMapping("getHospSet/{id}")
public R getById(
    @ApiParam(name = "id", value = "医院设置ID", required = true)
    @PathVariable String id){
    HospitalSet hospitalSet = hospitalSetService.getById(id);
    return R.ok().data("item", hospitalSet);
}
```



### 3、根据id修改

```
/**
     * 需求：根据id修改医院设置，参数中包括id值
     * 
     * 示例数据：
             {
             "id":7,
             "contactsName":"李四"
             }
     * 
     * @param hospitalSet
     * @return
     */
```



```java
@ApiOperation(value = "根据ID修改医院设置")
@PostMapping("updateHospSet")
public R updateById(@ApiParam(name = "hospitalSet",value = "医院设置对象",required = true)
                    @RequestBody HospitalSet hospitalSet){
    hospitalSetService.updateById(hospitalSet);
    return R.ok();
}
```



# 九、医院设置接口（批量删除和锁定）

### 1、批量删除 

需求：前端以delete方式发请求，传值格式例如：[1,2,3] ，后端实现批量删除，删除成功，返回R即可。

```java
//批量删除医院设置
@DeleteMapping("batchRemove")
public R batchRemoveHospitalSet(@RequestBody List<Long> idList) {
    hospitalSetService.removeByIds(idList);
    return R.ok();
}
```

### 2、锁定和解锁

![image-20230304152159074](http://file.xuxianng.xyz/202306251859015.png) 

```java
@GetMapping("lockHospitalSet/{id}/{status}")
public R lockHospitalSet( @PathVariable  Long id,@PathVariable  Integer status){

    //1、检查status的值是否合法
    if(status!=0 && status!=1){
        return R.error().message("status状态值只能为1或者0");
    }

    //2、根据id查询医院设置对象
    //原则：获取（查询）一个数据，之后使用这个数据之前，进行判空的校验
    HospitalSet hospitalSet = hospitalSetService.getById(id);

    //3、判断医院设置对象是否存在
    if(hospitalSet==null){
        return R.error().message("该医院设置不存在");
    }

    //4、判断是否重复操作 ==判断两个数字是否相等，如果Integer类型，建议equals去比较，基本数据类型使用==比较
    if (hospitalSet.getStatus().intValue()==status.intValue()){
        return R.error().message(status==0?"请勿重复锁定":"请勿重复解锁");
    }

    //5、实现更新status
    hospitalSet.setStatus(status);
    //hospitalSet.setUpdateTime(null);
    hospitalSet.setUpdateTime(new Date());//更新status时将updateTime一同更新，避免将原值重新写入
    boolean b = hospitalSetService.updateById(hospitalSet);

    return b?R.ok():R.error();
}
```



# 十、统一异常处理

## 1、制造异常

除以0

```
int a = 10/0;
```

![img](http://file.xuxianng.xyz/202306251859133.png) 

我们想让异常结果也显示为统一的返回结果对象，并且统一处理系统的异常信息，那么需要统一异常处理



## 2、实现统一异常处理

### 2.1、创建统一异常处理器

在service_utils中创建统一异常处理类GlobalExceptionHandler.java：

```java
/**
 * 统一异常处理类
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public R error(Exception e){
        e.printStackTrace();
        return R.error();
    }
    
}
```

### 2.2、测试 

返回统一错误结果

![img](http://file.xuxianng.xyz/202306251859203.png) 



## 3、处理特定异常

### 3.1、添加异常处理方法 

GlobalExceptionHandler.java中添加

```java
@ExceptionHandler(ArithmeticException.class)
@ResponseBody
public R error(ArithmeticException e){
    e.printStackTrace();
    return R.error().message("执行了特定异常");
}

@ExceptionHandler(NullPointerException.class)
@ResponseBody
public R error(NullPointerException e){
    e.printStackTrace();
    return R.error().message("空指针异常");
}
```

### 3.2、测试

![img](http://file.xuxianng.xyz/202306251859285.png) 



## 4、自定义异常

### 4.1、创建自定义异常类

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class YyghException extends RuntimeException {
    @ApiModelProperty(value = "状态码")
    private Integer code;
    private String msg;
}
```

### 4.2、业务中抛出Exception

```java
@GetMapping("getHospSet/{id}")
public R getHospSet(@PathVariable Long id){
    HospitalSet hospitalSet = hospitalSetService.getById(id);

    if(hospitalSet==null){
        throw new YyghException(20001,"该医院设置不存在");//抛出自定义异常
    }

    return R.ok().data("item",hospitalSet);
}
```

### 4.3、添加异常处理方法

GlobalExceptionHandler.java中添加

```java
@ExceptionHandler(YyghException.class)
@ResponseBody
public R error(YyghException e){
    e.printStackTrace();
    return R.error().message(e.getMsg()).code(e.getCode());
}
```

### 4.4、测试 

![img](http://file.xuxianng.xyz/202306251859328.png) 



# 十一、统一日志处理

## 1、配置日志级别

日志记录器（Logger）的行为是分等级的。如下表所示：

分为：OFF、FATAL、ERROR、WARN、INFO、DEBUG、ALL

默认情况下，spring boot从控制台打印出来的日志级别只有INFO及以上级别，可以配置日志级别

```properties
# 设置日志级别
logging.level.root=WARN
```

这种方式只能将日志打印在控制台上



## 2、Logback日志

spring boot内部使用Logback作为日志实现的框架。

Logback和log4j非常相似，如果你对log4j很熟悉，那对logback很快就会得心应手。

logback相对于log4j的一些优点：https://blog.csdn.net/caisini_vc/article/details/48551287

### 2.1、配置logback日志

（1）删除application.properties中的日志配置logging.level.root=xx

（2）resources 中创建 logback-spring.xml 

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration  scan="true" scanPeriod="10 seconds">
    <!-- 日志级别从低到高分为TRACE < DEBUG < INFO < WARN < ERROR < FATAL，如果设置为WARN，则低于WARN的信息都不会输出 -->
    <!-- scan:当此属性设置为true时，配置文件如果发生改变，将会被重新加载，默认值为true -->
    <!-- scanPeriod:设置监测配置文件是否有修改的时间间隔，如果没有给出时间单位，默认单位是毫秒。当scan为true时，此属性生效。默认的时间间隔为1分钟。 -->
    <!-- debug:当此属性设置为true时，将打印出logback内部日志信息，实时查看logback运行状态。默认值为false。 -->
    <contextName>logback</contextName>
    <!-- name的值是变量的名称，value的值时变量定义的值。通过定义的值会被插入到logger上下文中。定义变量后，可以使“${}”来使用变量。 -->
    <property name="log.path" value="C:/Users/70208/Desktop/0927" />
    <!-- 彩色日志 -->
    <!-- 配置格式变量：CONSOLE_LOG_PATTERN 彩色日志格式 -->
    <!-- magenta:洋红 -->
    <!-- boldMagenta:粗红-->
    <!-- cyan:青色 -->
    <!-- white:白色 -->
    <!-- magenta:洋红 -->
    <property name="CONSOLE_LOG_PATTERN"
              value="%yellow(%date{yyyy-MM-dd HH:mm:ss}) |%highlight(%-5level) |%blue(%thread) |%blue(%file:%line) |%green(%logger) |%cyan(%msg%n)"/>
    <!--输出到控制台-->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <!--此日志appender是为开发使用，只配置最底级别，控制台输出的日志级别是大于或等于此级别的日志信息-->
        <!-- 例如：如果此处配置了INFO级别，则后面其他位置即使配置了DEBUG级别的日志，也不会被输出 -->
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>INFO</level>
        </filter>
        <encoder>
            <Pattern>${CONSOLE_LOG_PATTERN}</Pattern>
            <!-- 设置字符集 -->
            <charset>UTF-8</charset>
        </encoder>
    </appender>
    <!--输出到文件-->
    <!-- 时间滚动输出 level为 INFO 日志 -->
    <appender name="INFO_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 正在记录的日志文件的路径及文件名 -->
        <file>${log.path}/log_info.log</file>
        <!--日志文件输出格式-->
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
            <charset>UTF-8</charset>
        </encoder>
        <!-- 日志记录器的滚动策略，按日期，按大小记录 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- 每天日志归档路径以及格式 -->
            <fileNamePattern>${log.path}/info/log-info-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <!--日志文件保留天数-->
            <maxHistory>15</maxHistory>
        </rollingPolicy>
        <!-- 此日志文件只记录info级别的 -->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>INFO</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>
    <!-- 时间滚动输出 level为 WARN 日志 -->
    <appender name="WARN_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 正在记录的日志文件的路径及文件名 -->
        <file>${log.path}/log_warn.log</file>
        <!--日志文件输出格式-->
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
            <charset>UTF-8</charset> <!-- 此处设置字符集 -->
        </encoder>
        <!-- 日志记录器的滚动策略，按日期，按大小记录 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${log.path}/warn/log-warn-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <!--日志文件保留天数-->
            <maxHistory>15</maxHistory>
        </rollingPolicy>
        <!-- 此日志文件只记录warn级别的 -->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>warn</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>
    <!-- 时间滚动输出 level为 ERROR 日志 -->
    <appender name="ERROR_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 正在记录的日志文件的路径及文件名 -->
        <file>${log.path}/log_error.log</file>
        <!--日志文件输出格式-->
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
            <charset>UTF-8</charset> <!-- 此处设置字符集 -->
        </encoder>
        <!-- 日志记录器的滚动策略，按日期，按大小记录 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${log.path}/error/log-error-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <!--日志文件保留天数-->
            <maxHistory>15</maxHistory>
        </rollingPolicy>
        <!-- 此日志文件只记录ERROR级别的 -->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>
    <!--
        <logger>用来设置某一个包或者具体的某一个类的日志打印级别、以及指定<appender>。
        <logger>仅有一个name属性，
        一个可选的level和一个可选的addtivity属性。
        name:用来指定受此logger约束的某一个包或者具体的某一个类。
        level:用来设置打印级别，大小写无关：TRACE, DEBUG, INFO, WARN, ERROR, ALL 和 OFF，
              如果未设置此属性，那么当前logger将会继承上级的级别。
    -->
    <!--
        使用mybatis的时候，sql语句是debug下才会打印，而这里我们只配置了info，所以想要查看sql语句的话，有以下两种操作：
        第一种把<root level="INFO">改成<root level="DEBUG">这样就会打印sql，不过这样日志那边会出现很多其他消息
        第二种就是单独给mapper下目录配置DEBUG模式，代码如下，这样配置sql语句会打印，其他还是正常DEBUG级别：
     -->
    <!--开发环境:打印控制台-->
    <springProfile 名字="dev">
        <!--可以输出项目中的debug日志，包括mybatis的sql日志-->
        <logger 名字="com.atguigu" level="INFO" />
        <!--
            root节点是必选节点，用来指定最基础的日志输出级别，只有一个level属性
            level:用来设置打印级别，大小写无关：TRACE, DEBUG, INFO, WARN, ERROR, ALL 和 OFF，默认是DEBUG
            可以包含零个或多个appender元素。
        -->
        <root level="INFO">
            <appender-ref ref="CONSOLE" />
            <appender-ref ref="INFO_FILE" />
            <appender-ref ref="WARN_FILE" />
            <appender-ref ref="ERROR_FILE" />
        </root>
    </springProfile>
    <!--生产环境:输出到文件-->
    <springProfile 名字="pro">
        <root level="INFO">
            <appender-ref ref="CONSOLE" />
            <appender-ref ref="DEBUG_FILE" />
            <appender-ref ref="INFO_FILE" />
            <appender-ref ref="ERROR_FILE" />
            <appender-ref ref="WARN_FILE" />
        </root>
    </springProfile>
</configuration>
```

### 2.2、将错误日志输出到文件

GlobalExceptionHandler.java 中

类上添加注解

```java
 @Slf4j
```

异常输出语句

```java
 log.error(e.getMessage());
```

