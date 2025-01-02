# SimpleHttpServer

一个简单的 Http 服务器，运行时会将服务器目录 `server-dir` 作为根目录进行使用，并渲染一个简单的文件树目录，实现一个简单文件浏览、访问与下载功能。

> 第二个版本!，现在使用了线程池，更加稳定、支持并发。

## 使用

可创建配置文件 `server.properties` 放置和 `SimpleHttpServer.Jar` 包同目录下，修改配置实现自定义。

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