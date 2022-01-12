# 凤凰架构读书笔记-演示工程

## 前端工程

### 运行程序

- 通过Docker容器运行：

  ```bash
  $ docker run -d -p 80:80 --name bookstore icyfenix/bookstore:frontend
  ```

  然后在浏览器访问：[http://localhost](http://localhost/)

- 通过Git源码，以开发者模式运行：

  ```bash
   # 克隆获取源码
   $ git clone https://github.com/fenixsoft/fenix-bookstore-frontend.git
  
   # 进入工程根目录
   $ cd fenix-bookstore-frontend
  
   # 安装工程依赖
   $ npm install
  
   # 以开发模式运行，地址为localhost:8080
   $ npm run dev
  ```

  然后在浏览器访问：[http://localhost:8080](http://localhost:8080/)

### 构建产品

当你将程序用于正式部署时，一般不应部署开发阶段的程序，而是要进行产品化（Production）与精简化（Minification），你可以通过以下命令，由 node.js 驱动 webpack 来自动完成：

```bash
# 编译前端代码
$ npm run build
```

或者使用--report 参数，同时输出依赖分析报告：

```bash
# 编译前端代码并生成报告
$ npm run build --report
```

编译结果存放在/dist 目录中，应将其拷贝至 Web 服务器的根目录使用。



## 单体架构：Spring Boot

### 运行程序

- 通过 Docker 容器方式运行：

  ```bash
  $ docker run -d -p 8080:8080 --name bookstore icyfenix/bookstore:monolithic
  ```

  然后在浏览器访问：[http://localhost:8080](http://localhost:8080/)，系统预置了一个用户（`user:icyfenix，pw:123456`）

- 通过 Git 上的源码，以 Maven 运行：

  ```bash
  # 克隆获取源码
  $ git clone https://github.com/fenixsoft/monolithic_arch_springboot.git
  
  # 进入工程根目录
  $ cd monolithic_arch_springboot
  
  # 编译打包
  # 采用Maven Wrapper，此方式只需要机器安装有JDK 8或以上版本即可，无需包括Maven在内的其他任何依赖
  # 如在Windows下应使用mvnw.cmd package代替以下命令
  $ ./mvnw package
  
  # 运行程序，地址为localhost:8080
  $ java -jar target/bookstore-1.0.0-Monolithic-SNAPSHOT.jar
  ```

### 工程结构

采用MVVM前后端分离模式，后端参考DDD的分层模式和设计原则

![image-20210813091637083](C:\Users\zhenxin.tang\AppData\Roaming\Typora\typora-user-images\image-20210813091637083.png)

![img](https://icyfenix.cn/images//ddd-arch.png)



- Resource：对应DDD中的User Interface层（用户界面层/表示层），负责向用户显示信息和解释用户发出的指令，用户也就是前端服务的消费者，以RESTful中的核心概念“资源”（Resource）来命名。
- Application：应用层，负责定义软件本身对外暴露的能力，即软件本身可以完成哪些任务，并负责对内协调领域对象来解决问题。根据DDD的原则，应用层应尽量简单，不包含任何业务规则和逻辑，而只为下一层的领域对象协调任务，分配工作，使他们互相协作，在代码上的表现为Application层一般不会存在条件判断语句。在许多项目中，Application层都会被选为包裹事务（代码进入此层开启事务，退出提交或回滚事务）的载体。
- Domain：领域层/模型层，负责业务逻辑的实现，即表达业务概念，处理业务状态信息及业务规则行为，此层是项目的核心。
- Infrastructure：基础设施层，向其他层提供通用的能力，譬如持久化、远程通信、工具集等。

