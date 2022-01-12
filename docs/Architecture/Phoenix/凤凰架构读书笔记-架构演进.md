# 凤凰架构读书笔记-架构演进

## 架构演进

### 架构演进流程图

```mermaid
graph LR; 
A["大型机（Mainframe）"]-->B["原始分布式（Distributed）"]-->C["大型单体（Monolithic）"]-->D["面向服务（Service-Oriented）"]-->E["微服务（Microservices）"]-->F["服务网格（ServiceMesh）"]-->G["无服务（Serverless）"]
```

### 原始分布式时代

> UNIX 的分布式设计哲学
>
> Simplicity of both the interface and the implementation are more important than any other attributes of the system — including correctness, consistency, and completeness
>
> 保持接口与实现的简单性，比系统的任何其他属性，包括准确性、一致性和完整性，都来得更加重要。
>
> ​																																							—— [Richard P. Gabriel](https://en.wikipedia.org/wiki/Richard_P._Gabriel)，[The Rise of 'Worse is Better'](https://en.wikipedia.org/wiki/Worse_is_better)，1991



[开放软件基金会](https://zh.wikipedia.org/wiki/開放軟體基金會)（Open Software Foundation，OSF，也即后来的“国际开放标准组织”）

[分布式运算环境](https://en.wikipedia.org/wiki/Distributed_Computing_Environment)（Distributed Computing Environment，DCE）

[简单优先原则](https://en.wikipedia.org/wiki/KISS_principle)（keep it simple,stupid，KISS principle）



> 原始分布式时代的教训
>
> Just because something **can** be distributed doesn’t mean it **should** be distributed. Trying to make a distributed call act like a local call always ends in tears
>
> 某个功能**能够**进行分布式，并不意味着它就**应该**进行分布式，强行追求透明的分布式操作，只会自寻苦果
>
> ​																						—— [Kyle Brown](https://en.wikipedia.org/wiki/Kyle_Brown_(computer_scientist))，IBM Fellow，[Beyond Buzzwords: A Brief History of Microservices Patterns](https://developer.ibm.com/technologies/microservices/articles/cl-evolution-microservices-patterns/)，2016



### 单体系统时代

> 单体架构（Monolithic）
>
> “单体”只是表明系统中主要的过程调用都是进程内调用，不会发生进程间通信，仅此而已。

单体架构易于开发、易于测试、易于部署，且系统中各个功能、模块、方法的调用过程都是进程内调用，不会发生[进程间通信](https://zh.wikipedia.org/wiki/行程間通訊)（Inter-Process Communication，IPC）。

单体系统的不足必须基于软件的性能需求超过了单机，软件的开发人员规模明显超过了“[2 Pizza Team](https://wiki.mbalib.com/wiki/两个披萨原则)”范畴的前提下才有讨论的价值。



> Monolithic Application
>
> Monolith means composed all in one piece. The Monolithic application describes a single-tiered software application in which different components combined into a single program from a single platform.
>
> 单体意味着自包含。单体应用描述了一种由同一技术平台的不同组件构成的单层软件。
>
> ​																																																—— [Monolithic Application](https://en.wikipedia.org/wiki/Monolithic_application)，Wikipedia

- 纵向角度：**分层架构（Layered Architecture）**

![img](https://icyfenix.cn/assets/img/layed-arch.8e054a47.png)

- 横向角度：按照技术、功能、职责等维度，将软件拆分为各个模块，以便代码重用和管理。

单体系统真正的缺陷不在于如何拆分，而在于拆分之后的`隔离`与`自治能力`的欠缺。

譬如：内存泄漏、线程爆炸、阻塞、死循环等问题，都会影响整个程序，而不单单是某一个功能或模块。如果消耗的是层次更高的公共资源，如端口号、数据库连接池泄露，将会波及整台机器，甚至集群中其他的单体副本。

从可维护性来说，单体架构也不占优势，由于代码都在同一个进程，难于阻断错误传播，无法单独停止、更新、升级，做灰度发布、A/B测试也更复杂。



### SOA时代

> SOA 架构（Service-Oriented Architecture）
>
> 面向服务的架构是一次具体地、系统性地成功解决分布式服务主要问题的架构模式。



**三种代表性架构模式**

- [烟囱式架构](https://en.wikipedia.org/wiki/Information_silo)（Information Silo Architecture）：信息烟囱又名信息孤岛（Information Island），使用这种架构的系统被称为孤岛式信息系统或者买烟囱式信息系统。是一种完全不与其他相关信息系统进行互相操作或者协调工作的设计模式。

- [微内核架构](https://en.wikipedia.org/wiki/Microkernel)（Microkernel Architecture）：微内核架构也被称为插件式架构（plug-in Architecture）。将公共服务、资源、数据集中成为一个被所有业务系统共同依赖的核心（Kernel，也称Core System），具体的业务系统以插件模块（plug-in Modules）的形式存在，提供可拓展、天然隔离、灵活的功能特性。

  微内核架构的局限和使用前提是它假设系统中各个插件模块之间是互不认识，不可预知系统将安装哪些模块，这些插件可访问内核中的公共资源，但不会直接交互。

  ![img](https://icyfenix.cn/assets/img/coresystem.f46f7c00.png)

  ​			

- [事件驱动架构](https://en.wikipedia.org/wiki/Event-driven_architecture)（Event-Driven Architecture）：子系统间建立一套事件队列管道（Event Queues），系统外部的消息通过事件的形式发送到管道中，并被订阅消费。事件的消费者高度解耦，但能通过事件管道进行通信。

![img](https://icyfenix.cn/assets/img/eventbus.a0c12890.png)

SOA 的概念最早由 Gartner 公司在 1994 年提出，[Open CSA](http://www.oasis-opencsa.org/)组织（Open Composite Services Architecture），这便是 SOA 的官方管理机构。

SOA针对分布式服务中服务间松耦合、注册、发现、隔离、治理、编排的问题，进行了更具体、更系统的探索。

- `“更具体”`体现在尽管 SOA 本身还是属抽象概念，而不是特指某一种具体的技术，但它比单体架构和前面所列举的三种架构模式的操作性要更强，已经不能简单视其为一种架构风格，而是可以称为一套软件设计的基础平台了。它拥有领导制定技术标准的组织 Open CSA；有清晰软件设计的指导原则，譬如服务的封装性、自治、松耦合、可重用、可组合、无状态，等等；明确了采用 SOAP 作为远程调用的协议，依靠 SOAP 协议族（WSDL、UDDI 和一大票 WS-*协议）来完成服务的发布、发现和治理；利用一个被称为[企业服务总线](https://zh.wikipedia.org/zh-hans/企业服务总线)（Enterprise Service Bus，ESB）的消息管道来实现各个子系统之间的通信交互，令各服务间在 ESB 调度下无须相互依赖却能相互通信，既带来了服务松耦合的好处，也为以后可以进一步实施[业务流程编排](https://zh.wikipedia.org/wiki/业务流程管理)（Business Process Management，BPM）提供了基础；使用[服务数据对象](https://zh.wikipedia.org/wiki/服务数据对象)（Service Data Object，SDO）来访问和表示数据，使用[服务组件架构](https://zh.wikipedia.org/wiki/服务组件架构)（Service Component Architecture，SCA）来定义服务封装的形式和服务运行的容器，等等。
- `“更系统”`指的是 SOA 的宏大理想，它的终极目标是希望总结出一套自上而下的软件研发方法论，希望做到企业只需要跟着 SOA 的思路，就能够一揽子解决掉软件开发过程中的全部问题，譬如该如何挖掘需求、如何将需求分解为业务能力、如何编排已有服务、如何开发测试部署新的功能，等等。这里面技术问题确实是重点和难点，但也仅仅是其中的一个方面，SOA 不仅关注技术，还关注研发过程中涉及到的需求、管理、流程和组织。

`SOAP 协议`被逐渐边缘化的本质原因：过于严格的规范定义带来过度的复杂性。它可以实现多个异构大型系统之间的复杂集成交互，却很难作为一种具有广泛普适性的软件架构风格来推广。



### 微服务时代

> 微服务架构（Microservices）
>
> 微服务是一种通过多个小型服务组合来构建单个应用的架构风格，这些服务围绕业务能力而非特定的技术标准来构建。各个服务可以采用不同的编程语言，不同的数据存储技术，运行在不同的进程之中。服务采取轻量级的通信机制和自动化的部署机制实现通信与运维。

`微服务`由 Peter Rodgers 博士在 2005 年度的云计算博览会（Web Services Edge 2005）上首次使用，当时的说法是“Micro-Web-Service”，指的是一种专注于单一职责的、语言无关的、细粒度 Web 服务（Granular Web Services）。

**微服务的九个核心的业务与技术特征：**

- **围绕业务能力构建**（Organized around Business Capability）。强调了康威定律的重要性，有怎样结构、规模、能力的团队，就会产生出对应结构、规模、能力的产品。
- **分散治理**（Decentralized Governance）。表达“谁家孩子谁来管”的意思，服务对应的开发团队有直接对服务运行质量负责的责任，也应该有着不受外界干预地掌控服务各个方面的权力，譬如选择与其他服务异构的技术来实现自己的服务。
- **通过服务来实现独立自治的组件**（Componentization via Services）。服务是进程外组件，通过远程调用来提供功能。
- **产品化思维**（Products not Projects）。避免把软件研发视作要去完成某种功能，而是视作一种持续改进、提升的过程。
- **数据去中心化**（Decentralized Data Management）。微服务明确地提倡数据应该按领域分散管理、更新、维护、存储，在单体服务中，一个系统的各个功能模块通常会使用同一个数据库。同一个数据实体在不同服务的视角里，它的抽象形态往往也是不同的。
- **强终端弱管道**（Smart Endpoint and Dumb Pipe）。弱管道（Dumb Pipe）几乎算是直接指名道姓地反对 SOAP 和 ESB 的那一堆复杂的通信机制。微服务提倡类似于经典 UNIX 过滤器那样简单直接的通信方式，RESTful 风格的通信在微服务中会是更加合适的选择。
- **容错性设计**（Design for Failure）。要求在微服务的设计中，有自动的机制对其依赖的服务能够进行快速故障检测，在持续出错的时候进行隔离，在服务恢复的时候重新联通。所以“断路器”这类设施，对实际生产环境的微服务来说并不是可选的外围组件，而是一个必须的支撑点，如果没有容错性的设计，系统很容易就会被因为一两个服务的崩溃所带来的雪崩效应淹没。
- **演进式设计**（Evolutionary Design）。容错性设计承认服务会出错，演进式设计则是承认服务会被报废淘汰。
- **基础设施自动化**（Infrastructure Automation）。基础设施自动化，如 CI/CD 的长足发展，显著减少了构建、发布、运维工作的复杂性。

`微服务`追求的是更加自由的架构风格，摒弃了几乎所有 SOA 里可以抛弃的约束和规定，提倡以“实践标准”代替“规范标准”。

服务的`注册发现`、`跟踪治理`、`负载均衡`、`故障隔离`、`认证授权`、`伸缩扩展`、`传输通信`、`事务处理`，等等，这些问题，在微服务中不再会有统一的解决方案。

> 技术架构者的第一职责就是做决策权衡



### 后微服务时代

> 后微服务时代（Cloud Native）
>
> 从软件层面独力应对微服务架构问题，发展到软、硬一体，合力应对架构问题的时代，此即为“后微服务时代”。

​																			传统 Spring Cloud 与 Kubernetes 提供的解决方案对比

|          | Kubernetes              | Spring Cloud              |
| -------- | ----------------------- | ------------------------- |
| 弹性伸缩 | Autoscaling             | N/A                       |
| 服务发现 | KubeDNS / CoreDNS       | Spring Cloud Eureka       |
| 配置中心 | ConfigMap / Secret      | Spring Cloud Config       |
| 服务网关 | Ingress Controller      | Spring Cloud Zuul/Gateway |
| 负载均衡 | Load Balancer           | Spring Cloud Ribbon       |
| 服务安全 | RBAC API                | Spring Cloud Security     |
| 跟踪监控 | Metrics API / Dashboard | Spring Cloud Turbine      |
| 降级熔断 | N/A                     | Spring Cloud Hystrix      |

当虚拟化的基础设施从单个服务的容器扩展至由多个容器构成的服务集群、通信网络和存储设施时，软件与硬件的界限便已经模糊。一旦虚拟化的硬件能够跟上软件的灵活性，那些与业务无关的技术性问题便有可能从软件层面剥离，悄无声息地解决于硬件基础设施之内，让软件得以只专注业务，真正“围绕业务能力构建”团队与产品。

虚拟化的基础设施只能针对容器管理，粒度相对粗犷，而诸如断路器、服务的监控、认证、授权、安全、负载均衡等都需要细化管理，为了解决这类问题，出现了“[服务网格](https://en.wikipedia.org/wiki/Service_mesh)”（Service Mesh）的“边车代理模式”（Sidecar Proxy）。这个代理除了实现正常的服务间通信外（称为数据平面通信），还接收来自控制器的指令（称为控制平面通信），根据控制平面中的配置，对数据平面通信的内容进行分析处理，以实现熔断、认证、度量、监控、负载均衡等各种附加功能。这样便实现了既不需要在应用层面加入额外的处理代码，也提供了几乎不亚于程序代码的精细管理能力。



### 无服务时代

> 无服务架构（Serverless）
>
> 如果说微服务架构是分布式系统这条路的极致，那无服务架构，也许就是“不分布式”的云端系统这条路的起点。

无服务只涉及两块内容：`后端设施`（Backend）和`函数`（Function）。

- **后端设施**是指数据库、消息队列、日志、缓存，等等这一类用于支撑业务逻辑运行，但本身无业务含义的技术组件，这些后端设施都运行在云上，无服务中称其`“后端即服务”`（Backend as a Service，BaaS）。

- **函数**是指业务逻辑代码，其运行在云端，不必考虑算力和容量问题，无服务中称其为`“函数即服务”`（Function as a Service，FaaS）。

无服务的愿景是让开发者只需要纯粹地关注业务，不需要考虑技术组件，后端的技术组件是现成的，可以直接取用，没有采购、版权和选的烦恼；不需要考虑如何部署，部署过程完全是托管到云端的，工作由云端自动完成；不需要考虑算力，有整个数据中心支撑，算力可以认为是无限的；也不需要操心运维，维护系统持续平稳运行是云计算服务商的责任而不再是开发者的责任。
