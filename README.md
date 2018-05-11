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
## 服务治理
服务治理，一般是服务发现，服务注册，负载均衡等功能，这里我们是用zookeeper来做的。
### 服务注册
每个服务可能拥有多个服务节点，每个服务节点会向zk注册一个临时节点（Create an ephemeral, sequential node），zk会监听这个临时节点的服务是否存活，如果不存活就剔除掉。
### 服务发现与负载均衡
客户端会去zk发现服务节点，如果没有发现如何服务节点就抛出异常，如果发现一个服务节点就直接返回给客户端，如果有多个节点呢？这里涉及到一个负载均衡的问题，如果有多个服务节点和多个客户端，我这边的负载均衡的策略是==随机==返回一个服务节点给到客户端。
代码见:[fly-registry](https://github.com/cxt/fly/tree/master/fly-registry/)。

## 序列化
这里我提供了四种序列化的方法，一种是基于[kryo](https://github.com/EsotericSoftware/kryo)，一种是基于[protostuff](https://github.com/protostuff/protostuff)，一种是基于hessian的，一种是基于fastjson的。
### 服务端的设计
服务类是采用注解的方式，用@RpcService注解。
服务端启动会实例化所有服务类，将所有服务类缓存在一个hashmap<类名+版本号的字符串,服务类>里面，同时会把服务<类名+版本号的字符串，服务的IP和端口号>注册到zk里面。
绑定编码器和解码器和ChannelHandler.
- [编码器的设计](https://github.com/cxt/fly/blob/master/fly-core/src/main/java/com/cxt/fly/codec/RpcProtostuffEncoder.java)

编码时会先序列化对象成字节数组，然后记录字节数组的长度，方便解码器解码，然后记录字节数组。
- [解码器的设计](https://github.com/cxt/fly/blob/master/fly-core/src/main/java/com/cxt/fly/codec/RpcProtostuffDecoder.java)

解码器先判断进来的字节数组是否大于4（因为是先写入的是字节数组的长度），然后再将字节数组反序列化成对象。
- [ChannelHandler的设计](https://github.com/cxt/fly/blob/master/fly-server/src/main/java/com/cxt/fly/server/RpcServerHandler.java)

处理进来的RpcRequest(有请求id，服务名，服务版本，方法，方法参数类型，方法参数值)。
从hashmap里面拿到服务类对象，然后通过反射执行对应的方法，得到结果，然后构造一个RpcResponse对象（请求id，异常，结果）。

### 客户端的设计
首先RpcProxy会通过动态代理创建一个服务类的代理对象。
执行服务类方法时，代理对象执行iinvoke()方法，首先会去zk发现服务，拿到服务的ip和端口号。
然后new一个RpcClient，执行RpcClient.send(RpcRequest)方法。
RpcClient.send(RpcRequest)事实上就是执行netty客户端的一些逻辑，包括绑定编码器解码器，以及ChannelHandler、启动引导类等。
代码见，[RpcProxy](https://github.com/cxt/fly/blob/master/fly-client/src/main/java/com/cxt/fly/client/RpcProxy.java#L51)，[RpcClient](https://github.com/cxt/fly/blob/master/fly-client/src/main/java/com/cxt/fly/client/RpcClient.java#L54)