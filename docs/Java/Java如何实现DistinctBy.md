# Java如何实现DistinctBy？

## 1.前言

在列表中搜索不同元素是我们程序员通常面临的常见任务之一。从包含 Streams 的 Java 8 开始，我们有了一个新的 API 来使用函数式方法处理数据。

在本文中，我们将展示4种使用列表中对象的特定属性过滤集合的方法。

## 2. 使用Stream API

Stream API 提供了 distinct() 方法，该方法基于 Object 类的 equals() 方法返回列表的不同元素。

但是，如果我们想按特定属性进行过滤，它会变得不那么灵活。我们的替代方案之一是编写一个过滤器来维护状态。

### 2.1.使用状态过滤器

解决方案之一是实现有状态的 *Predicate*：

```java
public static <T> Predicate<T> distinctByKey( 
    Function<? super T, ?> keyExtractor) { 
    
    Map<Object, Boolean> seen = new ConcurrentHashMap<>(); 
    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null; 
}
```

为了测试它，我们将使用以下具有属性 age、email 和 name 的 Person 类：

```java
public class Person { 
    private int age; 
    private String name; 
    private String email; 
    // getters and setters 
}
```

按名称获取新的过滤集合

```java
List<Person> personListFiltered = personList.stream()
    .filter(distinctByKey(p -> p.getName()))
    .collect(Collectors.toList());
```

## 3. 使用 Eclipse Collections

Eclipse Collections 是一个Java类库，它提供了在 Java 中处理流和集合的附加方法。

### 3.1.使用 ListIterate.distinct()

ListIterate.distinct() 方法允许我们使用各种 HashingStrategies 过滤流。这些策略可以使用 lambda 表达式或方法引用来定义。

如果我们想按人名过滤：

```java
List<Person> personListFiltered = ListIterate
    .distinct(personList, HashingStrategies.fromFunction(Person::getName));
```

或者，如果我们要使用的属性是原始属性（int、long、double），我们可以使用这样的专用函数：

```java
List<Person> personListFiltered = ListIterate.distinct( 
    personList, HashingStrategies.fromIntFunction(Person::getAge));
```

### 3.2. Maven 依赖

```xml
<dependency> 
    <groupId>org.eclipse.collections</groupId> 
    <artifactId>eclipse-collections</artifactId> 
    <version>8.2.0</version> 
</dependency>
```

## 4. 使用 Vavr (Javaslang)

这是 Java 8 的函数库，提供不可变数据和函数控制结构。

### 4.1.使用 List.distinctBy

为了过滤列表，该类提供了自己的 List 类，该类具有 distinctBy() 方法，允许我们按其包含的对象的属性进行过滤：

```java
List<Person> personListFiltered = List.ofAll(personList) 
    .distinctBy(Person::getName) 
    .toJavaList();
```

### 4.2. Maven 依赖

```xml
<dependency> 
    <groupId>io.vavr</groupId> 
    <artifactId>vavr</artifactId> 
    <version>0.9.0</version> 
</dependency>
```

## 5. 使用 StreamEx

该库为 Java 8 流处理提供了有用的类和方法。

### 5.1.使用 StreamEx.distinct

在提供的类中是 StreamEx，它具有 distinct 方法，我们可以向该方法发送对要区分的属性的引用：

```java
List<Person> personListFiltered = StreamEx.of(personList) 
    .distinct(Person::getName) 
    .toList();
```

### 5.2. Maven 依赖

```xml
<dependency> 
    <groupId>one.util</groupId> 
    <artifactId>streamex</artifactId> 
    <version>0.6.5</version> 
</dependency>
```

