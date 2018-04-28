# fly
**基于netty的rpc框架，用zk来做服务注册与服务发现，提供多种序列化方案（protostuff、kyro、hessian、fastjson）。**
## Quick Start
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
在fly-example/fly-example-server下面找到FlyBootstrap类执行main方法即可。
### 四、运行客户端
在fly-example/fly-example-client下面找到HelloWorldClient或者HelloWorldClient2,运行其main方法即可。
## 设计
### 服务治理
服务治理，一般是服务发现，服务注册，负载均衡等功能，这里我们是用zookeeper来做的。
- 服务注册
每个服务可能拥有多个服务节点，每个服务节点会向zk注册一个临时节点（Create an ephemeral, sequential node），zk会监听这个临时节点的服务是否存活，如果不存活就剔除掉。
- 服务发现与负载均衡
客户端会去zk发现服务节点，如果没有发现如何服务节点就抛出异常，如果发现一个服务节点就直接返回给客户端，如果有多个节点呢？这里涉及到一个负载均衡的问题，如果有多个服务节点和多个客户端，我这边的负载均衡的策略是==随机==返回一个服务节点给到客户端。
代码见:[fly-registry](https://github.com/cxt/fly/tree/master/fly-registry/)。
