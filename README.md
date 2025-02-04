# SimpleHttpServer

一个简单、便携的 HttpWeb 服务器(只有10Kb)，运行时会将服务器目录 `server-dir` 作为根目录进行使用，并渲染一个简单的文件树目录，实现一个简单文件浏览、访问与下载功能。

## 它能干什么

它将对指定的目录做出一个非常简单的目录列表Web页面，即文件目录树，通过创建 `server.properties` 并修改配置项 `server-dir=你的目录路径` 作为 Index 页面。

例如:
![SimpleHttpServer-Index](/images/index.png)

## 使用

1. (必要) 安装 Java21 或更高版本环境。
2. (可选) 可创建配置文件 `server.properties` 放置和 `SimpleHttpServer.Jar` 包同目录下，修改配置实现自定义。

```
# 服务器绑定 IP 地址
server-ip=0.0.0.0
# 服务器端口
server-port=8086
# 服务器目录
server-dir=./files
```

## 构建

**脚本构建:**

可使用提供的脚本 `build-jar.bat` 进行构建。

**使用 IntelliJ IDEA 构建:**

1. 菜单栏 - 构建(B)
2. 选项 - 编译 Artifacts...

**手动构建:**

1. 构建
```
javac -d ./out/build ./src/*.java
```

2. 打包
```
jar cfm ./SimpleHttpServer.jar ./src/META-INF/MANIFEST.MF -C ./out/build .
```

3. 运行

```
java -jar SimpleHttpServer.jar
```

## 问题

> 暂未发现其它问题, 有问题请提交 [issues](https://github.com/FastChen/SimpleHttpServer/issues).