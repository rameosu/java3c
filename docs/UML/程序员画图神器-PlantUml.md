# ⭐程序员画图神器-PlantUml

## 简单介绍
- PlantUML是一个用来绘制UML图的Java类库。
- 支持的UML图包括但不限于：时序图、用例图、类图、组件图、活动图、脑图、甘特图
- 其可以很方便的集成到绝大部分IDE中使用
- 完全代码实现，提升画图效率

## 使用方法
以IDEA为例
1. Java运行环境
2. 安装Graphviz，https://graphviz.org/download/
3. 安装PlantUml插件
4. 右键新建文件时，选择新建puml文件


![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/8f0757d8331a4d54bf5bc32890b2ad65~tplv-k3u1fbpfcp-watermark.image)

## 学习资料
- 官方文档
- https://real-world-plantuml.com/
- https://github.com/plantuml/plantuml
- 在线使用：https://www.planttext.com/

## 示例展示
`时序图`

![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/0fc38aaca4174c7bb3e76f8d5443a9cc~tplv-k3u1fbpfcp-watermark.image)

`流程图`

![image.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/f7fd893e5ea84ba0907e50a9fb778884~tplv-k3u1fbpfcp-watermark.image)

`脑图`

![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/f407686ae96c45d78b288ea7ad19b34c~tplv-k3u1fbpfcp-watermark.image)

`甘特图`

![screenshot-20210708-181715.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/05c312dc9719458eba3c4913f866d1f9~tplv-k3u1fbpfcp-watermark.image)

`分解图`

![screenshot-20210708-181803.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/356e20b8d1dc4635848c398d7beaa758~tplv-k3u1fbpfcp-watermark.image)

## 结合C4标准库
C4定义参考：https://www.cnblogs.com/xuanye/p/new-style-4-plantuml-and-c4model.html
1. github上将C4标准库clone到本地
2. 项目中引入C4标准库文件
3. 文件开头`!include C4`标准库文件

![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/ac3f45bf062344ccbe638e1e6c4c0b3c~tplv-k3u1fbpfcp-watermark.image)

更多样式库可以在github中搜索，也可以去https://plantuml.com/zh/stdlib

## 小技巧
在Settings中设置每个puml文件都自动引入其他puml

![image.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/30d467d52a39419bbc18bf5314200a69~tplv-k3u1fbpfcp-watermark.image)

## 最后
本人写了几个图例在[github](https://github.com/rameosu/uml)上，觉得有帮助请点个Star哦！