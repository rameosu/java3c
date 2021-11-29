# 一文看懂死锁

## 背景

线程同步是克服多线程程序中竞争条件的好工具。但是，它也有阴暗的一面：`死锁`，难以发现、重现和修复的严重错误。防止它们发生的唯一可靠方法是正确设计您的代码，这是本文的主题。我们将看看死锁的起源，找到一种发现现有代码中潜在死锁的方法，并提出设计无死锁同步的实用方法。

假设读者已经熟悉多线程编程，并且对 Java 中的线程同步原语有很好的理解。在接下来的部分中，我们不会区分同步语句和锁 API，使用术语“锁”来表示这两种类型的可重入锁。

## 死锁机制

现在有以下两种方法：

```java
void increment() {
    synchronized(lock1) {
        synchronized(lock2) {
            variable++;
        }
    }
}

void decrement() {
    synchronized(lock2) {
        synchronized(lock11) {
            variable--;
        }
    }
}
```

这两个方法被有意设计为产生死锁，以便于我们能详细考虑这是如何发生的。

increment() 和 decrement() 基本上都由以下 5 个步骤组成：

| Step | increment()       | decrement()       |
| ---- | ----------------- | ----------------- |
| 1    | Acquire lock1     | Acquire lock2     |
| 2    | Acquire lock2     | Acquire lock1     |
| 3    | Perform increment | Perform decrement |
| 4    | Release lock2     | Release lock1     |
| 5    | Release lock1     | Release lock2     |

显然，这两种方法中的第 1 步和第 2 步只有在相应的锁空闲时才能通过，否则，执行线程将不得不等待它们的释放。

假设有两个并行线程，一个执行 `increment()`，另一个执行 `decrement()`。每个线程的步骤会按正常顺序执行，但是，如果我们将两个线程放在一起考虑，一个线程的步骤将与另一个线程的步骤随机交错。随机性来自系统线程调度程序强加的不可预测的延迟。可能的交织模式或场景非常多并且可以分为两组。第一组是其中一个线程足够快以获取两个锁的地方。

比如下面的表格：

| No deadlock          |                      |                        |
| -------------------- | -------------------- | ---------------------- |
| Thread-1             | Thread-2             | Result                 |
| 1: Acquire lock1     |                      | lock1 busy             |
| 2: Acquire lock2     |                      | lock2 busy             |
|                      | 1: Acquire lock2     | wait for lock2 release |
| 3: Perform increment | Waiting at lock2     |                        |
| 4: Release lock2     | Intercept   lock2    | lock2 changed owner    |
|                      | 2: Acquire lock1     | wait for lock1 release |
| 5: Release lock1     | Intercept lock1      | lock1 changed owner    |
|                      | 3: Perform decrement |                        |
|                      | 4: Release lock1     | lock1 free             |
|                      | 5: Release lock2     | lock2 free             |

该组中的所有案例均成功完成。

在第二组中，两个线程都成功获取了锁。结果见下表：

| Deadlock         |                  |                        |
| ---------------- | ---------------- | ---------------------- |
| Thread-1         | Thread-2         | Result                 |
| 1: Acquire lock1 |                  | lock1 busy             |
|                  | 1: Acquire lock2 | Lock2 busy             |
| 2: Acquire lock2 |                  | wait for lock2 release |
|                  | 2: Acquire lock1 | wait for lock1 release |
| Waiting at lock2 | Waiting at lock1 |                        |
| …                | …                |                        |

该组中的所有情况都会导致第一个线程等待第二个线程拥有的锁，而第二个线程等待第一个线程拥有的锁的情况，因此两个线程都无法进一步进行：

![image.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/f9dee00e649f46d6959b4da92629111b~tplv-k3u1fbpfcp-watermark.awebp?)

这是一个典型的死锁情况。它至少满足一下3点：

- **至少有两个线程，每个线程至少占用两个锁。**
- **死锁只发生在特定的线程时序组合中。**
- **死锁的发生取决于锁定顺序。**

第二个属性意味着死锁不能随意重现。此外，它们的再现性取决于操作系统、CPU 频率、CPU 负载和其他因素。后者意味着软件测试的概念不适用于死锁，因为相同的代码可能在一个系统上完美运行而在另一个系统上失败。

因此，**交付正确应用程序的唯一方法是通过设计消除死锁**。这种设计有两种基本方法，现在，让我们从更简单的方法开始。

## 粗粒度同步

如果我们的应用程序中的任何线程都不允许同时持有多个锁，则不会发生死锁。但是我们应该使用多少锁以及将它们放在哪里？

最简单和最直接的答案是用一个锁保护所有事务。例如，为了保护一个复杂的数据对象，您可以将其所有公共方法声明为同步的。 java.util.Hashtable 中使用了这种方法。简单的代价是由于缺乏并发而导致的性能损失，因为所有方法都是相互阻塞的。

幸运的是，在许多情况下，粗粒度同步可以以较少限制的方式执行，从而允许一些并发和更好的性能。为了解释它，我们应该引入一个事务连接变量的概念。假设如果满足两个条件中的任何一个，则两个变量在事务上连接：

1. **存在涉及两个变量的交易**。
2. **两个变量都连接到第三个变量（传递性）**。

因此，您首先以这样一种方式对变量进行分组，即同一组中的任何两个变量在事务上都是连接的，而不同组中的任何两个变量都没有。然后通过单独的专用锁保护每个组：

![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/d599fc35715d4c1c8d692db0a5c911bc~tplv-k3u1fbpfcp-watermark.awebp?)

这种粗粒度同步的一个很好的现实例子是 `java.util.concurrent.ConcurrentHashMap`。在这个对象内部，有许多相同的数据结构（“桶”），每个桶都由自己的锁保护。事务被分派到由键的哈希码确定的存储桶。因此，具有不同键的交易大多会进入不同的存储桶，这使得它们可以并发执行而不会牺牲线程安全性，由于存储桶的事务独立性，这是可能的。

## 死锁分析

假设需要确定给定的代码是否包含潜在的死锁。我们称这种任务为“同步分析”或“死锁分析”。我们要如何处理这个问题？

最有可能的是，我们会尝试对线程争用锁的所有可能场景进行排序，试图找出是否存在不良场景。在`死锁机制`一节中，我们采用了最简单的方法，结果发现场景太多了。即使在最简单的情况下，也有 **252** 个，因此彻底检查它们是不可能的。在实践中，您可能最终只会考虑几个场景，并希望自己没有遗漏一些重要的事情。换句话说，公平的死锁分析无法通过初级的方法完成，我们需要一种专门的、更有效的方法。

### 锁定图

此方法包括构建`锁定图`并检查它是否存在循环依赖关系。**锁定图是显示锁和线程在这些锁上的交互的图形**。此类图中的每个闭环都表示可能存在死锁，并且没有闭环保证了代码的死锁安全性。

如何画锁定图？我们以死锁机制一节中的代码为例：

1. 对于代码中的每个锁，在图表上放置一个相应的节点；在示例中，这些是 `lock1` 和 `lock2`
2. 对于所有线程试图在已经持有锁 A 的情况下获取锁 B 的语句，画一个从节点 A 到节点 B 的箭头；在这个例子中， `increment()` 中有 `lock1 -> lock2`， `decrement()` 中有 `lock2 -> lock1`。如果一个线程按顺序使用多个锁，则为每两个连续的锁绘制一个箭头。

![image.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/de74da727e164ca08381ba62b21aec98~tplv-k3u1fbpfcp-watermark.awebp?)

这里形成了一个闭环：lock1 -> lock2 -> lock1，告诉我们代码包含潜在的死锁。

### 更复杂的例子

```java
void transaction1(int amount) {
    synchronized(lock1) {
        synchronized(lock2) {
            // do something;
        }
    }
}

void transaction2(int amount) {
    synchronized(lock2) {
        synchronized(lock13) {
            // do something;
        }
    }
}

void transaction3(int amount) {
    synchronized(lock3) {
        synchronized(lock11) {
            // do something;
        }
    }
}
```

让我们看看这段代码是否是死锁安全的。有3个锁：`lock1`、`lock2`、`lock3`和3条锁路径：`lock1 -> lock2` in `transaction1()`，`lock2 -> lock3` in `transaction2()`，`lock3 -> lock1` in `transaction3()`。

绘制锁定图如下：

![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/7f997b7f478f4e54a11701c8562f311f~tplv-k3u1fbpfcp-watermark.awebp?)

同样，图 A 表明我们的设计包含潜在的死锁。但是，不仅如此。它还提示我们如何修复设计；**我们只需要打破循环**！例如，我们可以在方法 transaction3() 中交换锁。相应的箭头改变方向，图 B 中的图变为无循环，保证了固定代码的死锁安全性。

## 带锁排序的细粒度同步

采取尽可能细粒度的同步方式，希望得到最大可能的事务并发度作为回报。这种设计基于两个原则。

**第一个原则是禁止任何变量同时参与多个事务**。

为了实现这一点，我们将每个变量与一个唯一的锁相关联，并通过获取与相关变量关联的所有锁来启动每个事务。以下代码说明了这一点：

```java
void transaction(Item i1, Item i2, Item i3, double amount) {
    synchronized(i1.lock) {
        synchronized(i2.lock) {
            synchronized(i3.lock) {
                // do something;
            }
        }
    }
}
```

一旦获得了锁，其他事务就不能访问这些变量，因此它们不会被并发修改。这意味着系统中的所有事务都是一致的。同时，允许在不相交变量集上的事务并发运行。因此，我们获得了一个高度并发但线程安全的系统。

但是，这样的设计会立即导致死锁的可能性，因为现在我们处理多个线程和每个线程的多个锁。此时，我们就需要用到第二个设计原则。

**第二个设计原则是必须以规范的顺序获取锁以防止死锁**。

这意味着我们将每个锁与一个唯一的常量索引相关联，并始终按照它们的索引定义的顺序获取锁。将这个原理应用到上面的代码中，我们得到了细粒度设计的完整说明：

```java
void transaction(Item i1, Item i2, Item i3, double... amounts) {
    // 使用item的id属性作为锁的索引
    Item[] order = {i1, i2, i3};
    Arrays.sort(order, (a,b) -> Long.compare(a.id, b.id));
    synchronized(order, [0].lock) {
        synchronized(order, [1].lock) {
            synchronized(order, [2].lock) {
                // do something;
            }
        }
    }
}
```

但是，确定规范排序确实可以防止死锁吗？我们能证明吗？答案是肯定的，我们可以使用锁定图来完成。

假设我们有一个有 N 个变量的系统，所以有 N 个关联的锁，因此图中有 N 个节点。如果没有强制排序，锁会以随机顺序被抓取，所以在图中，会有双向随机箭头，并且肯定会存在表示死锁的闭环：

![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/d89e9629b879488b977cdde1ba8dc95a~tplv-k3u1fbpfcp-watermark.awebp?)

如果我们强制执行锁排序，从高到低索引的锁路径将被排除，所以唯一剩下的箭头将是那些从左到右的箭头：

![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/b9f4580880a94313ae345b102cf1df2f~tplv-k3u1fbpfcp-watermark.awebp?)

在上图中我们找不到一个闭环，因为只有当箭头双向流动时，闭环才可能存在，没有闭环意味着没有死锁。

通过使用细粒度锁和锁排序，我们可以构建一个高并发、线程安全和无死锁的系统。但是，提高并发性是否需要付出代价？

首先，在低并发的情况下，与粗粒度的方法相比，存在一定的速度损失。每个锁捕获是一个相当昂贵的操作，但细粒度设计假设锁捕获至少是两倍。但是，随着并发请求数量的增加，由于使用了多个 CPU 内核，细粒度设计很快就会变得更好。

其次，由于大量的锁对象，存在内存开销。幸运的是，这很容易解决。如果受保护的变量是对象，我们可以摆脱单独的锁对象，并将变量本身用作自己的锁。否则，例如如果变量是原始数组元素，我们可能只需要有限数量的额外对象。为此，我们定义了从变量 ID 到中等大小的锁数组的映射。在这种情况下，锁必须按它们的实际索引排序，而不是按变量 ID。

## 总结

在本文中，我们探讨了多线程编程中的死锁问题。我们发现如果按照一定的设计模式编写同步代码，可以完全避免死锁。我们还研究了此类设计为何以及如何工作，其适用性的限制是什么，以及如何有效地发现和修复现有代码中的潜在死锁。预计所提供的材料为设计完美的无死锁同步提供了足够的实用指南。

