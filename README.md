# fly
**基于netty 异步非阻塞的rpc框架，用zk来做服务注册与服务发现，提供两种序列化方案（protostuff、kyro）。**
## 学习使用
### 一、拉代码导入ide
`git clone https://github.com/cxt/fly.git`
### 二、配置并启动zookeeper
conf/zoo.cfg配置为

`
 tickTime=2000
 initLimit=10
 syncLimit=5
 dataDir=/tmp/zookeeper
 clientPort=2181
`

启动zookeeper

cmd命令行进入bin目录，执行

`
zkServer.cmd
`

### 三、运行服务端
在fly-example-server下面找到FlyBootstrap类执行main方法即可。
### 四、运行客户端
在fly-example-client下面找到HelloWorldClient或者HelloWorldClient2,运行其main方法即可。

