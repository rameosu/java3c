# 凤凰架构读书笔记-远程服务

## 远程服务

远程服务将计算机程序的工作范围从单机扩展到网络，从本地延伸至远程，是构建分布式系统的首要基础。

## 远程服务调用

[远程服务调用](https://en.wikipedia.org/wiki/Remote_procedure_call)（Remote Procedure Call，RPC）。

#### 进程间通信

[进程间通信](https://en.wikipedia.org/wiki/Inter-process_communication)（Inter-Process Communication，IPC）。

- **管道**（Pipe）或者**具名管道**（Named Pipe）：管道类似于两个进程间的桥梁，可通过管道在进程间传递少量的字符流或字节流。管道典型的应用就是命令行中的`|`操作符，譬如：

  ```sh
  ps -ef | grep java
  ```

- **信号**（Signal）：信号用于通知目标进程有某种事件发生，除了用于进程间通信外，进程还可以发送信号给进程自身。信号的典型应用是`kill`命令，譬如：

  ```sh
  kill -9 pid
  ```

- **信号量**（Semaphore）：信号量用于两个进程之间同步协作手段，它相当于操作系统提供的一个特殊变量，程序可以在上面进行`wait()`和`notify()`操作。

- **消息队列**（Message Queue）：以上三种方式只适合传递传递少量信息，POSIX 标准中定义了消息队列用于进程间数据量较多的通信。进程可以向队列添加消息，被赋予读权限的进程则可以从队列消费消息。消息队列克服了信号承载信息量少，管道只能用于无格式字节流以及缓冲区大小受限等缺点，但实时性相对受限。

- **共享内存**（Shared Memory）：允许多个进程访问同一块公共的内存空间，这是效率最高的进程间通信形式。原本每个进程的内存地址空间都是相互隔离的，但操作系统提供了让进程主动创建、映射、分离、控制某一块内存的程序接口。当一块内存被多进程共享时，各个进程往往会与其它通信机制，譬如信号量结合使用，来达到进程间同步及互斥的协调操作。

- **套接字接口**（Socket）：以上两种方式只适合单机多进程间的通信，套接字接口是更为普适的进程间通信机制，可用于不同机器之间的进程通信。



#### 通信的成本

[通过网络进行分布式运算的八宗罪](https://en.wikipedia.org/wiki/Fallacies_of_distributed_computing)（8 Fallacies of Distributed Computing）：

1. The network is reliable —— 网络是可靠的。
2. Latency is zero —— 延迟是不存在的。
3. Bandwidth is infinite —— 带宽是无限的。
4. The network is secure —— 网络是安全的。
5. Topology doesn't change —— 拓扑结构是一成不变的。
6. There is one administrator —— 总会有一个管理员。
7. Transport cost is zero —— 不必考虑传输成本。
8. The network is homogeneous —— 网络是同质化的。



#### 三个基本问题

- **如何表示数据：**数据包括了传递给方法的参数，以及方法执行后的返回值。即数据的`序列化`与`反序列化`。

  每种 RPC 协议都应该要有对应的序列化协议，譬如：

  - ONC RPC 的[External Data Representation](https://en.wikipedia.org/wiki/External_Data_Representation) （XDR）
  - CORBA 的[Common Data Representation](https://en.wikipedia.org/wiki/Common_Data_Representation)（CDR）
  - Java RMI 的[Java Object Serialization Stream Protocol](https://docs.oracle.com/javase/8/docs/platform/serialization/spec/protocol.html#a10258)
  - gRPC 的[Protocol Buffers](https://developers.google.com/protocol-buffers)
  - Web Service 的[XML Serialization](https://docs.microsoft.com/en-us/dotnet/standard/serialization/xml-serialization-with-xml-web-services)
  - 众多轻量级 RPC 支持的[JSON Serialization](https://tools.ietf.org/html/rfc7159)
  - ……

- **如何传递数据：**指如何通过网络，在两个服务的 Endpoint 之间相互操作、交换数据。这里“交换数据”通常指的是应用层协议，实际传输一般是基于标准的 TCP、UDP 等标准的传输层协议来完成的。两个服务交互不是只扔个序列化数据流来表示参数和结果就行的，许多在此之外信息，譬如异常、超时、安全、认证、授权、事务，等等，都可能产生双方需要交换信息的需求。

  “[Wire Protocol](https://en.wikipedia.org/wiki/Wire_protocol)”来用于表示这种两个 Endpoint 之间交换这类数据的行为，常见的 Wire Protocol 有：

  - Java RMI 的[Java Remote Message Protocol](https://docs.oracle.com/javase/8/docs/platform/rmi/spec/rmi-protocol3.html)（JRMP，也支持[RMI-IIOP](https://zh.wikipedia.org/w/index.php?title=RMI-IIOP&action=edit&redlink=1)）
  - CORBA 的[Internet Inter ORB Protocol](https://en.wikipedia.org/wiki/General_Inter-ORB_Protocol)（IIOP，是 GIOP 协议在 IP 协议上的实现版本）
  - DDS 的[Real Time Publish Subscribe Protocol](https://en.wikipedia.org/wiki/Data_Distribution_Service)（RTPS）
  - Web Service 的[Simple Object Access Protocol](https://en.wikipedia.org/wiki/SOAP)（SOAP）
  - 如果要求足够简单，双方都是 HTTP Endpoint，直接使用 HTTP 协议也是可以的（如 JSON-RPC）
  - ……

- **如何确定方法：**[接口描述语言](https://en.wikipedia.org/wiki/Interface_description_language)（Interface Description Language，IDL），成为此后许多 RPC 参考或依赖的基础（如 CORBA 的 OMG IDL）

  - Android 的[Android Interface Definition Language](https://developer.android.com/guide/components/aidl)（AIDL）
  - CORBA 的[OMG Interface Definition Language](https://www.omg.org/spec/IDL)（OMG IDL）
  - Web Service 的[Web Service Description Language](https://zh.wikipedia.org/wiki/WSDL)（WSDL）
  - JSON-RPC 的[JSON Web Service Protocol](https://en.wikipedia.org/wiki/JSON-WSP)（JSON-WSP）
  - ……

#### 统一的RPC

1991 年，[对象管理组织](https://zh.wikipedia.org/wiki/对象管理组织)（Object Management Group，OMG）发布了跨进程的、面向异构语言的、支持面向对象的服务调用协议：CORBA 1.0（Common Object Request Broker Architecture）。是一套由国际标准组织牵头，由多家软件提供商共同参与的分布式规范。

1998 年，XML 1.0 发布，并成为[万维网联盟](https://en.wikipedia.org/wiki/World_Wide_Web_Consortium)（World Wide Web Consortium，W3C）的推荐标准。1999 年末，SOAP 1.0（Simple Object Access Protocol）规范的发布，它代表着一种被称为“Web Service”的全新的 RPC 协议的诞生。Web Service 的一大缺点是它那过于严格的数据和接口定义所带来的性能问题。

简单、普适、高性能这三点，似乎真的难以同时满足。



#### 分裂的RPC

- 朝着**面向对象**发展，不满足于 RPC 将面向过程的编码方式带到分布式，希望在分布式系统中也能够进行跨进程的面向对象编程，代表为 RMI、.NET Remoting，之前的 CORBA 和 DCOM 也可以归入这类，这条线有一个别名叫做[分布式对象](https://en.wikipedia.org/wiki/Distributed_object)（Distributed Object）。
- 朝着**性能**发展，代表为 gRPC 和 Thrift。决定 RPC 性能的主要就两个因素：`序列化效率`和`信息密度`。序列化效率很好理解，序列化输出结果的容量越小，速度越快，效率自然越高；信息密度则取决于协议中有效荷载（Payload）所占总传输数据的比例大小，使用传输协议的层次越高，信息密度就越低，SOAP 使用 XML 拙劣的性能表现就是前车之鉴。gRPC 和 Thrift 都有自己优秀的专有序列化器，而传输协议方面，gRPC 是基于 HTTP/2 的，支持多路复用和 Header 压缩，Thrift 则直接基于传输层的 TCP 协议来实现，省去了额外应用层协议的开销。
- 朝着**简化**发展，代表为 JSON-RPC，说要选功能最强、速度最快的 RPC 可能会很有争议，但选功能弱的、速度慢的，JSON-RPC 肯定会候选人中之一。牺牲了功能和效率，换来的是协议的简单轻便，接口与格式都更为通用，尤其适合用于 Web 浏览器这类一般不会有额外协议支持、额外客户端支持的应用场合。
- ……

最近几年，RPC 框架有明显的朝着更高层次（不仅仅负责调用远程服务，还管理远程服务）与插件化方向发展的趋势，不再追求独立地解决 RPC 的全部三个问题（表示数据、传递数据、表示方法），而是将一部分功能设计成扩展点，让用户自己去选择。框架聚焦于提供核心的、更高层次的能力，譬如提供负载均衡、服务注册、可观察性等方面的支持。



### REST设计风格

REST 与 RPC 在思想上差异的核心是`抽象的目标`不一样，即`面向资源`的编程思想与`面向过程`的编程思想两者之间的区别。

REST 并不是一种远程服务调用协议，协议都带有一定的规范性和强制性，至少有一个规约文档，但是REST并没有定义这些内容。REST是一种风格而不是规范、协议。

REST，即“表征状态转移”的缩写，“REST”（**Re**presentational **S**tate **T**ransfer）实际上是“HTT”（**H**yper**t**ext **T**ransfer）的进一步抽象，两者就如同接口与实现类的关系一般。

#### 理解REST

> Hypertext
>
> By now the word "hypertext" has become generally accepted for branching and responding text, but the corresponding word "hypermedia", meaning complexes of branching and responding graphics, movies and sound – as well as text – is much less used.
>
> 现在，"超文本 "一词已被普遍接受，它指的是能够进行分支判断和差异响应的文本，相应地， "超媒体 "一词指的是能够进行分支判断和差异响应的图形、电影和声音（也包括文本）的复合体。

以上定义描述的“超文本（或超媒体，Hypermedia）”是一种“能够对操作进行判断和响应的文本（或声音、图像等）”。

下面通过阅读文章的例子来理解什么是“表征”以及 REST 中其他关键概念。

- **资源**（Resource）：你现在正在阅读一篇名为《REST 设计风格》的文章，这篇文章的内容（其蕴含的信息、数据）本身，称之为“资源”。
- **表征**（Representation）：指信息与用户交互时的表示形式，这与我们软件分层架构中常说的“表示层”（Presentation Layer）的语义其实是一致的。譬如：服务端向浏览器返回的HTML、PDF、Markdown、RSS等形式的版本，它们是同一个资源多种表征。 
- **状态**（State）：在特定语境中才能产生的上下文信息即被称为“状态”。我们所说的有`状态（Stateful）`抑或是`无状态（Stateless）`，都是只相对于服务端来说的，服务端自己记住用户状态称为`有状态`；客户端记住状态，请求时明确告知服务端，称为`无状态`。
- **转移**（Transfer）：无论状态是由服务端还是客户端来提供的，“取下一篇文章”这个行为逻辑必然只能由服务端来提供，因为只有服务端拥有该资源及其表征形式。服务器通过某种方式，把“用户当前阅读的文章”转变成“下一篇文章”，这就被称为“表征状态转移”。
- **统一接口**（Uniform Interface）：HTTP 协议中已经提前约定好了一套“统一接口”，它包括：GET、HEAD、POST、PUT、DELETE、TRACE、OPTIONS 七种基本操作，任何一个支持 HTTP 协议的服务器都会遵守这套规定，对特定的 URI 采取这些操作，服务器就会触发相应的表征状态转移。
- **超文本驱动**（Hypertext Driven）：浏览器作为所有网站的通用的客户端，任何网站的导航（状态转移）行为都不可能是预置于浏览器代码之中，而是由服务器发出的请求响应信息（超文本）来驱动的。
- **自描述消息**（Self-Descriptive Messages）：由于资源的表征可能存在多种不同形态，在消息中应当有明确的信息来告知客户端该消息的类型以及应如何处理这条消息。一种被广泛采用的自描述方法是在名为“Content-Type”的 HTTP Header 中标识出[互联网媒体类型](https://en.wikipedia.org/wiki/Media_type)（MIME type），譬如“Content-Type : application/json; charset=utf-8”，则说明该资源会以 JSON 的格式来返回，请使用 UTF-8 字符集进行处理。



#### RESTful的系统

Fielding 认为，一套理想的、完全满足 REST 风格的系统应该满足以下六大原则。

1. **服务端与客户端分离**（Client-Server）

   将用户界面所关注的逻辑和数据存储所关注的逻辑分离开来，有助于提高用户界面的跨平台的可移植性。

2. **无状态**（Stateless）

   无状态是 REST 的一条核心原则，REST 希望服务器不要去负责维护状态，每一次从客户端发送的请求中，应包括所有的必要的上下文信息，会话信息也由客户端负责保存维护，服务端依据客户端传递的状态来执行业务处理逻辑，驱动整个应用的状态变迁。

3. **可缓存**（Cacheability）

   无状态服务虽然提升了系统的可见性、可靠性和可伸缩性，但降低了系统的网络性。“降低网络性”的通俗解释是某个功能如果使用有状态的设计只需要一次（或少量）请求就能完成，使用无状态的设计则可能会需要多次请求，或者在请求中带有额外冗余的信息。为了缓解这个矛盾，REST 希望软件系统能够如同万维网一样，允许客户端和中间的通讯传递者（譬如代理）将部分服务端的应答缓存起来。

4. **分层系统**（Layered System）

   指客户端一般不需要知道是否直接连接到了最终的服务器，抑或连接到路径上的中间服务器。中间服务器可以通过负载均衡和共享缓存的机制提高系统的可扩展性，这样也便于缓存、伸缩和安全策略的部署。该原则的典型的应用是内容分发网络（Content Distribution Network，CDN）。

5. **统一接口**（Uniform Interface）

   REST 希望开发者面向资源编程，希望软件系统设计的重点放在抽象系统该有哪些资源上，而不是抽象系统该有哪些行为（服务）上。面向资源编程的`抽象程度`通常更高。抽象程度高意味着坏处是往往距离人类的思维方式更远，而好处是往往通用程度会更好。

6. **按需代码**（[Code-On-Demand](https://en.wikipedia.org/wiki/Code_on_demand)）

   指任何按照客户端（譬如浏览器）的请求，将可执行的软件程序从服务器发送到客户端的技术，按需代码赋予了客户端无需事先知道所有来自服务端的信息应该如何处理、如何运行的宽容度。

#### RESTful的好处

- 降低的服务接口的学习成本。

  统一接口（Uniform Interface）是 REST 的重要标志，将对资源的标准操作都映射到了标准的 HTTP 方法上去，这些方法对于每个资源的用法都是一致的，语义都是类似的，不需要刻意去学习。

- 资源天然具有集合与层次结构。

  以资源为中心抽象的接口，由于资源是名词，天然就可以产生集合与层次结构。

- REST 绑定于 HTTP 协议。

  REST 复用 HTTP 协议中已经定义的概念和相关基础支持来解决问题。



#### RMM成熟度

《[RESTful Web APIs](https://book.douban.com/subject/22139962/)》和《[RESTful Web Services](https://book.douban.com/subject/2054201/)》的作者 Leonard Richardson 曾提出过一个衡量“服务有多么 REST”的 Richardson `成熟度模型`（[Richardson Maturity Model](https://martinfowler.com/articles/richardsonMaturityModel.html)），便于那些原本不使用 REST 的系统，能够逐步地导入 REST。Richardson 将服务接口“REST 的程度”从低到高，分为 0 至 3 级：

1. The Swamp of [Plain Old XML](https://en.wikipedia.org/wiki/Plain_Old_XML)：完全不 REST。另外，关于 Plain Old XML 这说法，SOAP 表示[感觉有被冒犯到](https://baike.baidu.com/item/感觉有被冒犯到)。
2. Resources：开始引入资源的概念。
3. HTTP Verbs：引入统一接口，映射到 HTTP 协议的方法上。
4. Hypermedia Controls：超媒体控制在本文里面的说法是“超文本驱动”，在 Fielding 论文里的说法是“Hypertext As The Engine Of Application State，HATEOAS”，其实都是指同一件事情。



#### 不足与争议

- **面向资源的编程思想只适合做 CRUD，面向过程、面向对象编程才能处理真正复杂的业务逻辑**

  用户是可以使用自定义方法的，按 Google 推荐的 REST API 风格，[自定义方法](https://cloud.google.com/apis/design/custom_methods)应该放在资源路径末尾，嵌入冒号加自定义动词的后缀。譬如，我可以把删除操作映射到标准 DELETE 方法上，如果此外还要提供一个恢复删除的 API，那它可能会被设计为：

  ```
  POST /user/user_id/cart/book_id:undelete
  ```

​		如果你不想使用自定义方法，那就设计一个回收站的资源，在那里保留着还能被恢复的商品，将恢复删除视为对该资源某个`状态值`的修改，映射到 PUT 或者 		PATCH 方法上，这也是一种完全可行的设计。

- **REST 与 HTTP 完全绑定，不适合应用于要求高性能传输的场景中**

  对于需要直接控制传输，如二进制细节、编码形式、报文格式、连接方式等细节的场景中，REST 确实不合适，这些场景往往存在于服务集群的内部节点之间

- **REST 不利于事务支持**

- **REST 没有传输可靠性支持**

  应对传输可靠性最简单粗暴的做法是把消息再重发一遍。这种简单处理能够成立的前提是服务应具有[幂等性](https://zh.wikipedia.org/wiki/冪等)（Idempotency），即服务被重复执行多次的效果与执行一次是相等的。

- **REST 缺乏对资源进行“部分”和“批量”的处理能力**

