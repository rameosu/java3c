# Redis的典型应用场景

## 缓存

redis的五种基础数据类型string、hash、list、set、zset都适合做缓存。

> 例如：热点数据缓存（例如报表、热搜榜），对象缓存、全页缓存、可以提升热点数据的访问数据。

## 分布式数据共享

多个分布式应用之间使用同一个redis服务提供数据共享。

> 例如：分布式Session

```xml
<dependency> 
	<groupId>org.springframework.session</groupId> 
 	<artifactId>spring-session-data-redis</artifactId> 
</dependency>
```

## 分布式锁

String 类型setnx方法，只有不存在时才能添加成功，返回true。但要注意释放锁的`原子性`，可以用`lua脚本`执行释放锁的命令。

也可以使用`reddison`客户端封装好的分布式锁。

## 全局ID

int类型，利用incrby命令的原子性。

```lua
incrby userId 1
```

## 计数器

int类型，incr方法

> 例如：文章的阅读量、微博点赞数、允许一定的延迟，先写入Redis再定时同步到数据库。

## 限流

int类型，incr方法

> 以访问者的ip和用户信息作为key，访问一次增加一次计数，超过次数则返回false。

## 位统计

String类型的bitcount，字符是以8位二进制存储的。

> 例如：在线用户统计，留存用户统计。

```lua
setbit onlineusers 01 
setbit onlineusers 11 
setbit onlineusers 20
```

## 购物车

hash类型，通过一下命令实现商品和数量的增减、全选、全删

> key：用户id；field：商品id；value：商品数量。
> +1：hincr。-1：hdecr。删除：hdel。全选：hgetall。商品数：hlen。

## 用户消息时间线timeline

list，双向链表，有序插入，直接作为timeline就好了。

## 消息队列

List提供了两个阻塞的弹出操作：blpop/brpop，可以设置超时时间。

- **blpop**：blpop key1 timeout 移除并获取列表的第一个元素，如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
- **brpop**：brpop key1 timeout 移除并获取列表的最后一个元素，如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。

上面的操作。其实就是java的`阻塞队列`。

- **队列**：先进先除：rpush blpop，左头右尾，右边进入队列，左边出队列
- **栈**：先进后出：rpush brpop

## 抽奖

`spop`命令：随机移除一个元素。

```lua
spop myset
```

## 点赞、签到、打卡

set类型，例如用 like:t1001 来维护 t1001 这条微博的所有点赞用户。

- 点赞了这条微博：sadd like:t1001 u3001
- 取消点赞：srem like:t1001 u3001
- 是否点赞：sismember like:t1001 u3001
- 点赞的所有用户：smembers like:t1001
- 点赞数：scard like:t1001

## 商品标签

set类型，例如用 tags:i5001 来维护商品所有的标签。

- sadd tags:i5001 画面清晰细腻
- sadd tags:i5001 真彩清晰显示屏
- sadd tags:i5001 流程至极

## 商品筛选

set类型

```lua
// 获取差集
sdiff set1 set2
// 获取交集（intersection ）
sinter set1 set2
// 获取并集
sunion set1 set2
```

例如：iPhone11 上市了

```shell
sadd brand:apple iPhone11

sadd brand:ios iPhone11

sad screensize:6.0-6.24 iPhone11

sad screentype:lcd iPhone 11
```

赛选商品，苹果的、ios的、屏幕在6.0-6.24之间的，屏幕材质是LCD屏幕

```lua
sinter brand:apple brand:ios screensize:6.0-6.24 screentype:lcd
```

## 用户关注、推荐模型

set类型

相互关注：

```lua
sadd 1:follow 2
sadd 2:fans 1
sadd 1:fans 2
sadd 2:follow 1
```

我关注的人也关注了他（取交集）：

```
sinter 1:follow 2:fans
```

可能认识的人：

```lua
用户1可能认识的人(差集)：sdiff 2:follow 1:follow
用户2可能认识的人：sdiff 1:follow 2:follow
```

## 排行榜

sorted set类型，有序

```lua
id 为6001 的新闻点击数加1：zincrby hotNews:20190926 1 n6001

获取今天点击最多的15条：zrevrange hotNews:20190926 0 15 withscores
```
