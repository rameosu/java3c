# 如何用docsify改造你的Github-Pages

`说明：`本文只涉及操作，不讲理论。

## docsify的使用

### 安装docsify-cli

执行命令行

```bash
npm i docsify-cli -g
```

### 初始化docsify

随便选一个目录，执行命令行

```bash
docsify init ./docs
```

此时，在该目录下，会生成如图文件

![image.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/408f80113625401290772a4004356d9d~tplv-k3u1fbpfcp-watermark.image)

-   `index.html` ：首页文件，访问入口
-   `README.md` ：会作为主页内容渲染
-   `.nojekyll` 防止 GitHub Pages 忽略以下划线开头的文件

### 启动docsify

```bash
docsify serve docs
```

![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/e57e309adf71400bad9db732479d7046~tplv-k3u1fbpfcp-watermark.image)

### 本地访问

浏览器打开<http://localhost:3000>

### 添加封面和侧边栏

在docs目录下，新建`_coverpage.md`文件和`_sidebar.md`文件

![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/b3ad2e935d0842dda1c48e54952ed027~tplv-k3u1fbpfcp-watermark.image)

并且在index.html文件中增加配置

![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/0a7554e3f9894a9694c59940454c4290~tplv-k3u1fbpfcp-watermark.image)

### 添加搜索功能

在index.html中引入js

```html
<script src="https://cdn.jsdelivr.net/npm/docsify@4/lib/plugins/search.js"></script>
```

并且增加docsify配置项

```html
<script>
    window.$docsify = {
        name: 'Rameo',  // 主页名称
        repo: 'https://github.com/rameosu/rameo',   // 你的github地址
        auto2top: true,
        coverpage: true,    // 封面
        loadSidebar: true,  // 侧边栏
        search: {   // 搜索
            paths: 'auto',
            placeholder: '🔍 Type to search ',
            noData: '😞 No Results! ',
            depth: 6
        },
        plantuml: { // plantuml使用默认皮肤
            skin: 'default',
        },
    }
</script>
```

### 支持流程图

-   `mermaid`

```html
<script src="//unpkg.com/mermaid/dist/mermaid.js"></script>
<script src="//unpkg.com/docsify-mermaid@latest/dist/docsify-mermaid.js"></script>
```

-   `plantuml`

```html
<script src="//unpkg.com/docsify-plantuml/dist/docsify-plantuml.min.js"></script>
```

## 改造Github Pages

1.  将你的docs目录整体添加到你的github项目中

![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/b78c1591eeba4ede89f583e684a1901b~tplv-k3u1fbpfcp-watermark.image)

2.  在GitHub Pages页面，source选项下选择`/docs`目录作为当前要构建的目录

![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/9eaf023a8d584f3ca41dd581afed32cc~tplv-k3u1fbpfcp-watermark.image)

3.  点击你的github pages 网址

    -   首先可以看到封面

    -   往下拉或者点击开始阅读可以看到有搜索框和侧边栏
-   在页面右上角有一个猫咪的三角区域就是你配置的github地址

## 最后

如果想看效果可以访问我的[Github Pages](https://rameosu.github.io/rameo)

需要源码可以访问我的[Github](https://github.com/rameosu/rameo)
