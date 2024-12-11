# SimpleHttpServer

一个简单的 Http 服务器

> 这是一个最初版本! 正在持续更新。

## 使用

可创建配置文件 `server.properties` 修改一下配置实现自定义。

```
# 服务器绑定 IP 地址
server-ip=0.0.0.0
# 服务器端口
server-port=8086
# 服务器文件目录
server-dir=./files
```

## 构建

可使用提供的脚本 `构建编译.bat` 进行构建。

**手动构建：**

1. 构建
```
javac -d build src/SimpleHttpServer.java
```

2. 打包
```
jar cfm SimpleHttpServer.jar MANIFEST.MF -C build .
```

3. 运行

```
java -jar SimpleHttpServer.jar
```

## 问题

- 中文文件名导致无法正常解析