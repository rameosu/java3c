# 还在重复造轮子？duck不必，hutool一键搞定👍

## 一、前言
Hello 大家好，我是`java3c`，今天给大家分享一个国产开源的顶级项目，Java开发的利器👉【Hutool】。

> 本文已收录到 [Github-java3c](https://github.com/rameosu/java3c) ，里面有我的系列文章，欢迎大家Star。

## 二、Hutool简介

Hutool是一个小而全的Java工具类库，通过静态方法封装，降低相关API的学习成本，提高工作效率，使Java拥有函数式语言般的优雅，让Java语言也可以“甜甜的”。

Hutool中的工具方法来自每个用户的精雕细琢，它涵盖了Java开发底层代码中的方方面面，它既是大型项目开发中解决小问题的利器，也是小型项目中的效率担当；

-   Web开发
-   与其它框架无耦合
-   高度可替换

Hutool由上百个工具类组成，涵盖了日常开发中的方方面面，可谓工具类集大成者。

![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/47c25f6fc4434f9cbb0cd48499a26811~tplv-k3u1fbpfcp-watermark.image?)

## 三、Hutool设计哲学
Hutool的设计思想是尽量减少重复的定义，让项目中的util这个package尽量少，总的来说有如下的几个思想：

-   方法优先于对象
-   自动识别优于用户定义
-   便捷性与灵活性并存
-   适配与兼容
-   可选依赖原则
-   无侵入原则

> 开发者不再需要重复造轮子，或引入各种依赖包，Hutool帮您 All In One。

## 四、包含组件
一个Java基础工具类，对文件、流、加密解密、转码、正则、线程、XML等JDK方法进行封装，组成各种Util工具类，同时提供以下组件：

| 模块               | 介绍                                                         |
| ------------------ | ------------------------------------------------------------ |
| hutool-aop         | JDK动态代理封装，提供非IOC下的切面支持                       |
| hutool-bloomFilter | 布隆过滤，提供一些Hash算法的布隆过滤                         |
| hutool-cache       | 简单缓存实现                                                 |
| hutool-core        | 核心，包括Bean操作、日期、各种Util等                         |
| hutool-cron        | 定时任务模块，提供类Crontab表达式的定时任务                  |
| hutool-crypto      | 加密解密模块，提供对称、非对称和摘要算法封装                 |
| hutool-db          | JDBC封装后的数据操作，基于ActiveRecord思想                   |
| hutool-dfa         | 基于DFA模型的多关键字查找                                    |
| hutool-extra       | 扩展模块，对第三方封装（模板引擎、邮件、Servlet、二维码、Emoji、FTP、分词等） |
| hutool-http        | 基于HttpUrlConnection的Http客户端封装                        |
| hutool-log         | 自动识别日志实现的日志门面                                   |
| hutool-script      | 脚本执行封装，例如Javascript                                 |
| hutool-setting     | 功能更强大的Setting配置文件和Properties封装                  |
| hutool-system      | 系统参数调用封装（JVM信息等）                                |
| hutool-json        | JSON实现                                                     |
| hutool-captcha     | 图片验证码实现                                               |
| hutool-poi         | 针对POI中Excel和Word的封装                                   |
| hutool-socket      | 基于Java的NIO和AIO的Socket封装                               |
| hutool-jwt         | JSON Web Token (JWT)封装实现                                 |

可以根据需求对每个模块单独引入，也可以通过引入`hutool-all`方式引入所有模块。

## 五、安装
### [🍊Maven](https://www.hutool.cn/docs/#/?id=%f0%9f%8d%8amaven)

在项目的pom.xml的dependencies中加入以下内容:

```
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-all</artifactId>
    <version>5.7.15</version>
</dependency>Copy to clipboardErrorCopied
```

### [🍐Gradle](https://www.hutool.cn/docs/#/?id=%f0%9f%8d%90gradle)

```
implementation 'cn.hutool:hutool-all:5.7.15'
```
### [📥下载jar](https://www.hutool.cn/docs/#/?id=%f0%9f%93%a5%e4%b8%8b%e8%bd%bdjar)

点击以下链接，下载`hutool-all-X.X.X.jar`即可：

-   [Maven中央库](https://repo1.maven.org/maven2/cn/hutool/hutool-all/5.7.15/)

> 🔔️注意 Hutool 5.x支持JDK8+，对Android平台没有测试，不能保证所有工具类或工具方法可用。 如果你的项目使用JDK7，请使用Hutool 4.x版本（不再更新）

### [🚽编译安装](https://www.hutool.cn/docs/#/?id=%f0%9f%9a%bd%e7%bc%96%e8%af%91%e5%ae%89%e8%a3%85)

访问Hutool的Gitee主页：<https://gitee.com/dromara/hutool> 下载整个项目源码（v5-master或v5-dev分支都可）然后进入Hutool项目目录执行：

```
./hutool.sh installCopy to clipboardErrorCopied
```

然后就可以使用Maven引入了。

## 六、常用轮子
### [类型转换工具类-Convert](https://www.hutool.cn/docs/#/core/%E7%B1%BB%E5%9E%8B%E8%BD%AC%E6%8D%A2/%E7%B1%BB%E5%9E%8B%E8%BD%AC%E6%8D%A2%E5%B7%A5%E5%85%B7%E7%B1%BB-Convert?id=%e7%b1%bb%e5%9e%8b%e8%bd%ac%e6%8d%a2%e5%b7%a5%e5%85%b7%e7%b1%bb-convert)
Convert类可以说是一个工具方法类，里面封装了针对Java常见类型的转换，用于简化类型转换。
``` java
//转换为字符串
int a = 1;
//aStr为"1"
String aStr = Convert.toStr(a);

//转换为指定类型数组
String[] b = {"1", "2", "3", "4"}; 
Integer[] bArr = Convert.toIntArray(b);

//转换为日期对象
String dateStr = "2017-05-06"; 
Date date = Convert.toDate(dateStr); 

//转换为列表 
String[] strArr = {"a", "b", "c", "d"}; 
List<String> strList = Convert.toList(String.class, strArr);
```
### [日期时间工具-DateUtil](https://www.hutool.cn/docs/#/core/%E6%97%A5%E6%9C%9F%E6%97%B6%E9%97%B4/%E6%97%A5%E6%9C%9F%E6%97%B6%E9%97%B4%E5%B7%A5%E5%85%B7-DateUtil?id=%e6%97%a5%e6%9c%9f%e6%97%b6%e9%97%b4%e5%b7%a5%e5%85%b7-dateutil)
主要提供日期和字符串之间的转换，以及提供对日期的定位（一个月前等等）。
```java
//当前时间 
Date date = DateUtil.date(); 

//Calendar转Date
Date date = DateUtil.date(Calendar.getInstance()); 

//时间戳转Date
Date date = DateUtil.date(System.currentTimeMillis()); 

//自动识别格式转换 
String dateStr = "2017-03-01"; 
Date date = DateUtil.parse(dateStr); 

//自定义格式化转换 
Date date = DateUtil.parse(dateStr, "yyyy-MM-dd"); 

//格式化输出日期 
String format = DateUtil.format(date, "yyyy-MM-dd"); 

//获得年的部分 
int year = DateUtil.year(date); 

//获得月份，从0开始计数 
int month = DateUtil.month(date); 

//获取某天的开始、结束时间 
Date beginOfDay = DateUtil.beginOfDay(date); 
Date endOfDay = DateUtil.endOfDay(date); 

//计算偏移后的日期时间 
Date newDate = DateUtil.offset(date, DateField.DAY_OF_MONTH, 2); 

//计算日期时间之间的偏移量 
long betweenDay = DateUtil.between(date, newDate, DateUnit.DAY);
```

### [文件工具类-FileUtil](https://www.hutool.cn/docs/#/core/IO/%E6%96%87%E4%BB%B6%E5%B7%A5%E5%85%B7%E7%B1%BB-FileUtil?id=%e6%96%87%e4%bb%b6%e5%b7%a5%e5%85%b7%e7%b1%bb-fileutil)
在IO操作中，文件的操作相对来说是比较复杂的，但也是使用频率最高的部分，我们几乎所有的项目中几乎都躺着一个叫做FileUtil或者FileUtils的工具类，我想Hutool应该将这个工具类纳入其中，解决用来解决大部分的文件操作问题。

总体来说，FileUtil类包含以下几类操作工具：

1.  文件操作：包括文件目录的新建、删除、复制、移动、改名等
2.  文件判断：判断文件或目录是否非空，是否为目录，是否为文件等等。
3.  绝对路径：针对ClassPath中的文件转换为绝对路径文件。
4.  文件名：主文件名，扩展名的获取
5.  读操作：包括类似IoUtil中的getReader、readXXX操作
6.  写操作：包括getWriter和writeXXX操作
```java
//新建文件夹
FileUtil.mkdir("D:\");

//新建文件
FileUtil.file("D:\", "F:\");

//新建临时文件
FileUtil.createTempFile(FileUtil.file("D:\"));

//是否非空，是否为目录，是否为文件
FileUtil.isEmpty(FileUtil.file("D:\"));
FileUtil.isDirectory("D:\");
FileUtil.isFile("D:\");
```

### [字符串工具-StrUtil](https://www.hutool.cn/docs/#/core/%E5%B7%A5%E5%85%B7%E7%B1%BB/%E5%AD%97%E7%AC%A6%E4%B8%B2%E5%B7%A5%E5%85%B7-StrUtil?id=%e5%ad%97%e7%ac%a6%e4%b8%b2%e5%b7%a5%e5%85%b7-strutil)
这个工具的用处类似于[Apache Commons Lang](http://commons.apache.org/)中的`StringUtil`，之所以使用`StrUtil`而不是使用`StringUtil`是因为前者更短，而且`Str`这个简写我想已经深入人心了，大家都知道是字符串的意思。常用的方法例如`isBlank`、`isNotBlank`、`isEmpty`、`isNotEmpty`这些我就不做介绍了，判断字符串是否为空，下面我说几个比较好用的功能。

```java
//判断是否为空字符串 String str = "test"; 
StrUtil.isEmpty(str); 
StrUtil.isNotEmpty(str); 

//去除字符串的前后缀 
StrUtil.removeSuffix("a.jpg", ".jpg"); 
StrUtil.removePrefix("a.jpg", "a."); 

//格式化字符串 
String template = "这只是个占位符:{}"; 
String str2 = StrUtil.format(template, "我是占位符"); 
```
### [反射工具-ReflectUtil](https://www.hutool.cn/docs/#/core/%E5%B7%A5%E5%85%B7%E7%B1%BB/%E5%8F%8D%E5%B0%84%E5%B7%A5%E5%85%B7-ReflectUtil?id=%e5%8f%8d%e5%b0%84%e5%b7%a5%e5%85%b7-reflectutil)
Java的反射机制，可以让语言变得更加灵活，对对象的操作也更加“动态”，因此在某些情况下，反射可以做到事半功倍的效果。Hutool针对Java的反射机制做了工具化封装，封装包括：

1.  获取构造方法
2.  获取字段
3.  获取字段值
4.  获取方法
5.  执行方法（对象方法和静态方法）

```java
//获取某个类的所有方法 
Method[] methods = ReflectUtil.getMethods(PmsBrand.class); 

//获取某个类的指定方法 
Method method = ReflectUtil.getMethod(PmsBrand.class, "getId"); 

//使用反射来创建对象 
PmsBrand pmsBrand = ReflectUtil.newInstance(PmsBrand.class); 

//反射执行对象的方法 
ReflectUtil.invoke(pmsBrand, "setId", 1);
```

### [数字工具-NumberUtil](https://www.hutool.cn/docs/#/core/%E5%B7%A5%E5%85%B7%E7%B1%BB/%E6%95%B0%E5%AD%97%E5%B7%A5%E5%85%B7-NumberUtil?id=%e6%95%b0%e5%ad%97%e5%b7%a5%e5%85%b7-numberutil)
数字工具针对数学运算做工具性封装

```java
double n1 = 1.234; 
double n2 = 1.234; double result; 

//对float、double、BigDecimal做加减乘除操作 
result = NumberUtil.add(n1, n2); 
result = NumberUtil.sub(n1, n2); 
result = NumberUtil.mul(n1, n2); 
result = NumberUtil.div(n1, n2); 

//保留两位小数 
BigDecimal roundNum = NumberUtil.round(n1, 2); 
String n3 = "1.234"; 

//判断是否为数字、整数、浮点数 
NumberUtil.isNumber(n3); 
NumberUtil.isInteger(n3); 
NumberUtil.isDouble(n3);
```
### [数组工具-ArrayUtil](https://www.hutool.cn/docs/#/core/%E5%B7%A5%E5%85%B7%E7%B1%BB/%E6%95%B0%E7%BB%84%E5%B7%A5%E5%85%B7-ArrayUtil?id=%e6%95%b0%e7%bb%84%e5%b7%a5%e5%85%b7-arrayutil)

数组工具类主要针对原始类型数组和泛型数组相关方案进行封装。

```java
//判空
int[] a = {}; 
int[] b = null;
ArrayUtil.isEmpty(a); 
ArrayUtil.isEmpty(b);

//新建泛型数组
String[] newArray = ArrayUtil.newArray(String.class, 3);
//泛型数组合并
int[] result = ArrayUtil.addAll(a, b);

//过滤
Integer[] a = {1,2,3,4,5,6}; 
// [2,4,6] 
Integer[] filter = ArrayUtil.filter(a, (Editor<Integer>) t -> (t % 2 == 0) ? t : null);

//是否包含
boolean flag = ArrayUtil.contains(a, 1);
//使用间隔符将一个数组转为字符串
String result = ArrayUtil.join(new int[]{1,2,3,4}, "-");
```
### [信息脱敏工具-DesensitizedUtil](https://www.hutool.cn/docs/#/core/%E5%B7%A5%E5%85%B7%E7%B1%BB/%E4%BF%A1%E6%81%AF%E8%84%B1%E6%95%8F%E5%B7%A5%E5%85%B7-DesensitizedUtil?id=%e4%bf%a1%e6%81%af%e8%84%b1%e6%95%8f%e5%b7%a5%e5%85%b7-desensitizedutil)
在数据处理或清洗中，可能涉及到很多隐私信息的脱敏工作，因此Hutool针对常用的信息封装了一些脱敏方法。

现阶段支持的脱敏数据类型包括：

1.  用户id
2.  中文姓名
3.  身份证号
4.  座机号
5.  手机号
6.  地址
7.  电子邮件
8.  密码
9.  中国大陆车牌，包含普通车辆、新能源车辆
10.  银行卡

整体来说，所谓脱敏就是隐藏掉信息中的一部分关键信息，用`*`代替，自定义隐藏可以使用`StrUtil.hide`方法完成。

```java
// 身份证脱敏 5***************1X 
DesensitizedUtil.idCardNum("51343620000320711X", 1, 2);

// 手机号脱敏 180****1999 
DesensitizedUtil.mobilePhone("18049531999");

// 密码脱敏 ********** 
DesensitizedUtil.password("1234567890");
```
### [身份证工具-IdcardUtil](https://www.hutool.cn/docs/#/core/%E5%B7%A5%E5%85%B7%E7%B1%BB/%E8%BA%AB%E4%BB%BD%E8%AF%81%E5%B7%A5%E5%85%B7-IdcardUtil?id=%e8%ba%ab%e4%bb%bd%e8%af%81%e5%b7%a5%e5%85%b7-idcardutil)
`IdcardUtil`现在支持大陆15位、18位身份证，港澳台10位身份证。

工具中主要的方法包括：

1.  `isValidCard` 验证身份证是否合法
2.  `convert15To18` 身份证15位转18位
3.  `getBirthByIdCard` 获取生日
4.  `getAgeByIdCard` 获取年龄
5.  `getYearByIdCard` 获取生日年
6.  `getMonthByIdCard` 获取生日月
7.  `getDayByIdCard` 获取生日天
8.  `getGenderByIdCard` 获取性别
9.  `getProvinceByIdCard` 获取省份

```java
String ID_18 = "321083197812162119"; 
String ID_15 = "150102880730303"; 

//是否有效 boolean valid = IdcardUtil.isValidCard(ID_18); 
boolean valid15 = IdcardUtil.isValidCard(ID_15); 

//转换 
String convert15To18 = IdcardUtil.convert15To18(ID_15); 
Assert.assertEquals(convert15To18, "150102198807303035"); 

//年龄 
DateTime date = DateUtil.parse("2017-04-10");
int age = IdcardUtil.getAgeByIdCard(ID_18, date); 
Assert.assertEquals(age, 38); 
int age2 = IdcardUtil.getAgeByIdCard(ID_15, date); 
Assert.assertEquals(age2, 28); 

//生日 
String birth = IdcardUtil.getBirthByIdCard(ID_18); 
Assert.assertEquals(birth, "19781216"); 
String birth2 = IdcardUtil.getBirthByIdCard(ID_15); 
Assert.assertEquals(birth2, "19880730");

//省份 
String province = IdcardUtil.getProvinceByIdCard(ID_18); 
Assert.assertEquals(province, "江苏"); 
String province2 = IdcardUtil.getProvinceByIdCard(ID_15); 
Assert.assertEquals(province2, "内蒙古");
```
### [Bean工具-BeanUtil](https://www.hutool.cn/docs/#/core/JavaBean/Bean%E5%B7%A5%E5%85%B7-BeanUtil?id=bean%e5%b7%a5%e5%85%b7-beanutil)

```java
// Bean转Map
SubPerson person = new SubPerson(); 
person.setAge(14); 
person.setOpenid("11213232"); 
person.setName("测试A11"); 
person.setSubName("sub名字"); 
Map<String, Object> map = BeanUtil.beanToMap(person);

//Map转Bean 
SubPerson person = BeanUtil.mapToBean(map, SubPerson.class, false); 

//Bean属性拷贝 
SubPerson copyPerson = new SubPerson(); 
BeanUtil.copyProperties(person, copyPerson); 
```

### [集合工具-CollUtil](https://www.hutool.cn/docs/#/core/%E9%9B%86%E5%90%88%E7%B1%BB/%E9%9B%86%E5%90%88%E5%B7%A5%E5%85%B7-CollUtil?id=%e9%9b%86%e5%90%88%e5%b7%a5%e5%85%b7-collutil)
这个工具主要增加了对数组、集合类的操作。

```java
//数组转换为列表 
String[] array = new String[]{"a", "b", "c", "d", "e"}; 
List<String> list = CollUtil.newArrayList(array); 

//join：数组转字符串时添加连接符号 
String joinStr = CollUtil.join(list, ","); 

//将以连接符号分隔的字符串再转换为列表 
List<String> splitList = StrUtil.split(joinStr, ','); 

//创建新的Map、Set、List 
HashMap<Object, Object> newMap = CollUtil.newHashMap(); 
HashSet<Object> newHashSet = CollUtil.newHashSet(); 
ArrayList<Object> newList = CollUtil.newArrayList(); 

//判断列表是否为空 
CollUtil.isEmpty(list);
```
### [Map工具-MapUtil](https://www.hutool.cn/docs/#/core/Map/Map%E5%B7%A5%E5%85%B7-MapUtil?id=map%e5%b7%a5%e5%85%b7-maputil)
MapUtil是针对Map的一一列工具方法的封装，包括getXXX的快捷值转换方法。
-   `isEmpty`、`isNotEmpty` 判断Map为空和非空方法，空的定义为null或没有值
-   `newHashMap` 快速创建多种类型的HashMap实例
-   `createMap` 创建自定义的Map类型的Map
-   `of` 此方法将一个或多个键值对加入到一个新建的Map中，下面是栗子:

```java
Map<Object, Object> colorMap = MapUtil.of(new String[][] { 
    {"RED", "#FF0000"}, 
    {"GREEN", "#00FF00"}, 
    {"BLUE", "#0000FF"} 
});
```
### [网络工具-NetUtil](https://www.hutool.cn/docs/#/core/%E7%BD%91%E7%BB%9C/%E7%BD%91%E7%BB%9C%E5%B7%A5%E5%85%B7-NetUtil?id=%e7%bd%91%e7%bb%9c%e5%b7%a5%e5%85%b7-netutil)
`NetUtil` 工具中主要的方法包括：

1.  `longToIpv4` 根据long值获取ip v4地址
2.  `ipv4ToLong` 根据ip地址计算出long型的数据
3.  `isUsableLocalPort` 检测本地端口可用性
4.  `isValidPort` 是否为有效的端口
5.  `isInnerIP` 判定是否为内网IP
6.  `localIpv4s` 获得本机的IP地址列表
7.  `toAbsoluteUrl` 相对URL转换为绝对URL
8.  `hideIpPart` 隐藏掉IP地址的最后一部分为 * 代替
9.  `buildInetSocketAddress` 构建InetSocketAddress
10.  `getIpByHost` 通过域名得到IP
11.  `isInner` 指定IP的long是否在指定范围内

```java
String ip= "127.0.0.1"; 
long iplong = 2130706433L; 

//根据long值获取ip v4地址 
String ip= NetUtil.longToIpv4(iplong); 

//根据ip地址计算出long型的数据 
long ip= NetUtil.ipv4ToLong(ip); 

//检测本地端口可用性 
boolean result= NetUtil.isUsableLocalPort(6379); 

//是否为有效的端口 
boolean result= NetUtil.isValidPort(6379); 

//隐藏掉IP地址 
String result =NetUtil.hideIpPart(ip);
```
### [图片工具-ImgUtil](https://www.hutool.cn/docs/#/core/%E5%9B%BE%E7%89%87/%E5%9B%BE%E7%89%87%E5%B7%A5%E5%85%B7-ImgUtil?id=%e5%9b%be%e7%89%87%e5%b7%a5%e5%85%b7-imgutil)
针对awt中图片处理进行封装，这些封装包括：缩放、裁剪、转为黑白、加水印等操作。

```java
//缩放图片
ImgUtil.scale( 
    FileUtil.file("d:/face.jpg"), 
    FileUtil.file("d:/face_result.jpg"), 
    0.5f //缩放比例
);

//裁剪图片
ImgUtil.cut( 
    FileUtil.file("d:/face.jpg"), 
    FileUtil.file("d:/face_result.jpg"), 
    new Rectangle(200, 200, 100, 100) //裁剪的矩形区域 
);
//`slice` 按照行列剪裁切片（将图片分为20行和20列）
ImgUtil.slice(FileUtil.file("e:/test2.png"), FileUtil.file("e:/dest/"), 10, 10);

//转换图片格式
ImgUtil.convert(FileUtil.file("e:/test2.png"), FileUtil.file("e:/test2Convert.jpg"));

//转黑白
ImgUtil.gray(FileUtil.file("d:/logo.png"), FileUtil.file("d:/result.png"));

//添加文字水印
ImgUtil.pressText(
    FileUtil.file("e:/pic/face.jpg"), 
    FileUtil.file("e:/pic/test2_result.png"),
    "版权所有", Color.WHITE, //文字 
    new Font("黑体", Font.BOLD, 100), //字体 
    0, //x坐标修正值。 默认在中间，偏移量相对于中间偏移 
    0, //y坐标修正值。 默认在中间，偏移量相对于中间偏移 
    0.8f//透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字 
);

//添加图片水印
ImgUtil.pressImage( 
    FileUtil.file("d:/picTest/1.jpg"), 
    FileUtil.file("d:/picTest/dest.jpg"), 
    ImgUtil.read(FileUtil.file("d:/picTest/1432613.jpg")), //水印图片 
    0, //x坐标修正值。 默认在中间，偏移量相对于中间偏移 
    0, //y坐标修正值。 默认在中间，偏移量相对于中间偏移 
    0.1f 
);

// 旋转180度
BufferedImage image = ImgUtil.rotate(ImageIO.read(FileUtil.file("e:/pic/366466.jpg")), 180); ImgUtil.write(image, FileUtil.file("e:/pic/result.png"));
```
### [JSON工具-JSONUtil](https://www.hutool.cn/docs/#/json/JSONUtil?id=json%e5%b7%a5%e5%85%b7-jsonutil)

```java
//Json字符解析
String html = "{\"name\":\"Something must have been changed since you leave\"}"; 
JSONObject jsonObject = JSONUtil.parseObj(html); jsonObject.getStr("name");

//Json字符创建
SortedMap<Object, Object> sortedMap = new TreeMap<Object, Object>() { 
    private static final long serialVersionUID = 1L; 
    { 
        put("attributes", "a"); 
        put("b", "b"); 
        put("c", "c"); 
    }
}; 
JSONUtil.toJsonStr(sortedMap);

//XML字符转json
String s = "<sfzh>123</sfzh><sfz>456</sfz><name>aa</name><gender>1</gender>"; 
JSONObject json = JSONUtil.parseFromXml(s); 
json.get("sfzh"); 
json.get("name");

//json转Bean
String json = "{\"ADT\":[[{\"BookingCode\":[\"N\",\"N\"]}]]}"; 
Price price = JSONUtil.toBean(json, Price.class); 
price.getADT().get(0).get(0).getBookingCode().get(0);
```
### [加密解密工具-SecureUtil](https://www.hutool.cn/docs/#/crypto/%E5%8A%A0%E5%AF%86%E8%A7%A3%E5%AF%86%E5%B7%A5%E5%85%B7-SecureUtil?id=%e5%8a%a0%e5%af%86%e8%a7%a3%e5%af%86%e5%b7%a5%e5%85%b7-secureutil)
#### [对称加密](https://www.hutool.cn/docs/#/crypto/%E5%8A%A0%E5%AF%86%E8%A7%A3%E5%AF%86%E5%B7%A5%E5%85%B7-SecureUtil?id=%e5%af%b9%e7%a7%b0%e5%8a%a0%e5%af%86)

-   `SecureUtil.aes`
-   `SecureUtil.des`

#### [摘要算法](https://www.hutool.cn/docs/#/crypto/%E5%8A%A0%E5%AF%86%E8%A7%A3%E5%AF%86%E5%B7%A5%E5%85%B7-SecureUtil?id=%e6%91%98%e8%a6%81%e7%ae%97%e6%b3%95)

-   `SecureUtil.md5`
-   `SecureUtil.sha1`
-   `SecureUtil.hmac`
-   `SecureUtil.hmacMd5`
-   `SecureUtil.hmacSha1`

#### [非对称加密](https://www.hutool.cn/docs/#/crypto/%E5%8A%A0%E5%AF%86%E8%A7%A3%E5%AF%86%E5%B7%A5%E5%85%B7-SecureUtil?id=%e9%9d%9e%e5%af%b9%e7%a7%b0%e5%8a%a0%e5%af%86)

-   `SecureUtil.rsa`
-   `SecureUtil.dsa`

#### [UUID](https://www.hutool.cn/docs/#/crypto/%E5%8A%A0%E5%AF%86%E8%A7%A3%E5%AF%86%E5%B7%A5%E5%85%B7-SecureUtil?id=uuid)

-   `SecureUtil.simpleUUID` 方法提供无“-”的UUID

#### [密钥生成](https://www.hutool.cn/docs/#/crypto/%E5%8A%A0%E5%AF%86%E8%A7%A3%E5%AF%86%E5%B7%A5%E5%85%B7-SecureUtil?id=%e5%af%86%e9%92%a5%e7%94%9f%e6%88%90)

-   `SecureUtil.generateKey` 针对对称加密生成密钥
-   `SecureUtil.generateKeyPair` 生成密钥对（用于非对称加密）
-   `SecureUtil.generateSignature` 生成签名（用于非对称加密）

### [Http客户端工具类-HttpUtil](https://www.hutool.cn/docs/#/http/Http%E5%AE%A2%E6%88%B7%E7%AB%AF%E5%B7%A5%E5%85%B7%E7%B1%BB-HttpUtil?id=http%e5%ae%a2%e6%88%b7%e7%ab%af%e5%b7%a5%e5%85%b7%e7%b1%bb-httputil)
HttpUtil是应对简单场景下Http请求的工具类封装，此工具封装了*HttpRequest*对象常用操作，可以保证在一个方法之内完成Http请求。

此模块基于JDK的HttpUrlConnection封装完成，完整支持https、代理和文件上传。

```java
// 最简单的HTTP请求，可以自动通过header等信息判断编码，不区分HTTP和HTTPS 
String result1= HttpUtil.get("https://www.baidu.com");

// Post请求文件上传
HashMap<String, Object> paramMap = new HashMap<>(); 
//文件上传只需将参数中的键指定（默认file），值设为文件对象即可，对于使用者来说，文件上传与普通表单提交并无区别 
paramMap.put("file", FileUtil.file("D:\\face.jpg")); 
String result= HttpUtil.post("https://www.baidu.com", paramMap);

//文件下载
String fileUrl = "http://mirrors.sohu.com/centos/8.4.2105/isos/x86_64/CentOS-8.4.2105-x86_64-dvd1.iso"; 
//将文件下载后保存在E盘，返回结果为下载文件大小 
long size = HttpUtil.downloadFile(fileUrl, FileUtil.file("e:/")); 
System.out.println("Download size: " + size);
```
### [Excel工具-ExcelUtil](https://www.hutool.cn/docs/#/poi/Excel%E5%B7%A5%E5%85%B7-ExcelUtil?id=excel%e5%b7%a5%e5%85%b7-excelutil)

```java
//读取excel文件
ExcelReader reader = ExcelUtil.getReader("d:/aaa.xlsx"); 
List<List<Object>> readAll = reader.read();
List<Map<String,Object>> readAll = reader.readAll();
List<Person> all = reader.readAll(Person.class);

//写出到客户端
// 通过工具类创建writer，默认创建xls格式 
ExcelWriter writer = ExcelUtil.getWriter(); 
// 一次性写出内容，使用默认样式，强制输出标题 
writer.write(rows, true); 
//out为OutputStream，需要写出到的目标流 
//response为HttpServletResponse对象 
response.setContentType("application/vnd.ms-excel;charset=utf-8"); 
//test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码 
response.setHeader("Content-Disposition","attachment;filename=test.xls"); 
ServletOutputStream out=response.getOutputStream(); 
writer.flush(out, true); 
// 关闭writer，释放内存 
writer.close(); 
//此处记得关闭输出Servlet流 
IoUtil.close(out);
```

### [图形验证码-CaptchaUtil](https://www.hutool.cn/docs/#/captcha/%E6%A6%82%E8%BF%B0)

![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/e60cf2d46c7744d78f07781e5d1d4f83~tplv-k3u1fbpfcp-watermark.image?)
```java
//定义图形验证码的长和宽 
LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 100); 
//图形验证码写出，可以写出到文件，也可以写出到流 
lineCaptcha.write("d:/line.png"); 
//输出code 
Console.log(lineCaptcha.getCode()); 
//验证图形验证码的有效性，返回boolean值 
lineCaptcha.verify("1234"); 
//重新生成验证码 
lineCaptcha.createCode(); 
lineCaptcha.write("d:/line.png"); 
//新的验证码 
Console.log(lineCaptcha.getCode()); 
//验证图形验证码的有效性，返回boolean值 
lineCaptcha.verify("1234");
```

## 七、项目地址
- 官网地址：https://www.hutool.cn/  
- API文档地址：https://apidoc.gitee.com/dromara/hutool/  
- 参考文档地址：https://www.hutool.cn/docs/#/  
- GitHub地址：https://github.com/dromara/hutool/  
- Gitee地址：https://gitee.com/dromara/hutool/  
- 视频介绍地址：https://www.bilibili.com/video/BV1bQ4y1M7d9?p=2



