# SpringBoot Starter开发利器：mica-auto

## SpringBoot Starter的标准开发流程

### 一个完整的 SpringBoot Starter 模块包含以下组件：
- autoconfigure 模块，包含自动配置代码，并将需要启动就加载的自动配置类，配置在META-INF/spring- factories文件中
- starter 模块，它提供对 autoconfigure 模块依赖关系以及类库和常用的其他依赖关系

其依赖关系如下：  

![image](https://user-images.githubusercontent.com/46550155/127732500-e980c561-0cb0-4cfa-9bf6-fdc890c94f04.png)

`如果你不想将这两个模块分开，也可以将自动配置代码和依赖关系管理组合在一个模块中`

### 使用Starter的好处
> 1. 整合了这个模块需要的依赖库
> 2. 提供对模块的配置项给使用者
> 3. 提供自动配置类对模块内的Bean进行自动装配

### Starter的开发步骤
> 1. 新建Maven项目，在项目的POM文件中定义使用的依赖
> 2. 新建配置类，写好配置项和默认的配置值，指明配置项前缀
> 3. 新建自动装配类，使用@Configuration和@Bean来进行自动装配
> 4. 新建spring.factories文件，指定Starter的自动装配类

具体的创建流程和代码演示这里就不细讲了，可以参考这篇博文：https://blog.csdn.net/weixin_38657051/article/details/97487721

因为我们有更加简洁更加高效的方式

## 主角是我：mica-auto
### 功能原理
mica-auto 基于 SpringBoot 配置注解自动生成 SpringBoot 部分配置文件
- 生成 spring.factories
- 生成 spring-devtools.properties
- 生成 FeignClient 到 spring.factories 中，供 mica 中完成 Feign 自动化配置

> 其原理是扫描引用了`@Component`注解或者包含`@Component`的组合注解的类，自动生成相应的配置

基于此特性，我们可以编写自定义Starter时不再需要`autoconfigurer模块`，因为mica-auto会自动为我们扫描引用了`@Configuration`的配置类，而该注解是一个包含了@Component的组合注解，并且自动创建`META-INF/spring.factories`文件，将配置类全路径名自动写入该文件。

### 如何使用
只需在Starter项目中加入依赖
#### maven
```xml
<dependency>
  <groupId>net.dreamlu</groupId>
  <artifactId>mica-auto</artifactId>
  <version>${version}</version>
  <scope>provided</scope>
</dependency>
```
##### gradle
```gradle
annotationProcessor("net.dreamlu:mica-auto:${version}")
```
`注意：`如果在项目中使用了 Lombok 需将 mica-auto 的依赖放置到 `Lombok` 后面。

### 效果展示

![image.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/c51c75568d734b6da795219a23e8a5ff~tplv-k3u1fbpfcp-watermark.image)

![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/247612956f374050a45d58f91ffba299~tplv-k3u1fbpfcp-watermark.image)

![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/890964706eb44a8abe29ef27024e7fee~tplv-k3u1fbpfcp-watermark.image)

![image.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/8c25142854954ad996a959f6c3f290c3~tplv-k3u1fbpfcp-watermark.image)

从idea编译的classes和maven构建的jar包中可以看到mica-auto已经将自动配置类加入到了`META-INF/spring.factories`

### 拓展使用
其实`mica-auto`不只是用于简化Starter的开发，我们日常的开发中经常会遇到多模块项目中，业务模块依赖外部模块，通常他们的包名是不一致的，业务模块要依赖注入外部模块的Component时，主要有四种做法：

- 自定义一个Starter，依赖该Starter
- 写`@EnableXXX`注解，在SpringBoot的启动类引入该注解
- 在SpringBoot的启动类引入`@Import`注解，导入配置类
- 在SpringBoot的启动类引入`@ComponentScan`注解，手动扫描注入外部模块的Component

这四种方法都对代码有一定的侵入，而mica-auto为每个Component都写入到`META-INF/spring.factories`，
SpringBoot在启动时会根据`@SpringBootApplication` -> `@EnableAutoConfiguration` -> `@Import` 
找到AutoConfigurationImportSelector，在他的selectImports方法中会去扫描`META-INF/spring.factories`文件，并注入到Spring的bean容器中。

因此，只需要在被依赖的模块中加入mica-auto，就可以在业务模块中愉快的使用外部Component了。

So Easy!
