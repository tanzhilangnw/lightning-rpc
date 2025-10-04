# Lightning RPC Framework

一个高性能、易用的Java RPC框架，支持服务注册与发现、负载均衡、多种序列化方式等特性。

## 特性

- 🚀 **高性能**: 基于Netty的异步非阻塞网络通信
- 🔧 **易用性**: 简洁的API设计，支持注解驱动
- 🔌 **可扩展**: 插件化架构，支持自定义扩展
- 📦 **多序列化**: 支持JSON、Kryo、Protobuf等多种序列化方式
- ⚖️ **负载均衡**: 支持轮询、随机、加权轮询、一致性哈希等算法
- 🏗️ **注册中心**: 支持Zookeeper、Nacos等注册中心
- 🎯 **代理机制**: 支持JDK动态代理和CGLIB代理

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.lightning</groupId>
    <artifactId>rpc-spring</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 服务提供者

```java
@RpcService(version = "1.0.0", group = "default")
@Service
public class UserServiceImpl implements UserService {
    
    @Override
    public User getUserById(Long id) {
        // 业务逻辑
        return userRepository.findById(id);
    }
}
```

### 3. 服务消费者

```java
@Service
public class UserController {
    
    @RpcReference(version = "1.0.0", group = "default")
    private UserService userService;
    
    public User getUser(Long id) {
        return userService.getUserById(id);
    }
}
```

### 4. 配置文件

```yaml
lightning:
  rpc:
    application:
      name: my-application
    server:
      host: 127.0.0.1
      port: 8080
    serialization:
      type: json
    registry:
      type: zookeeper
      address: 127.0.0.1:2181
    load-balancer:
      type: roundRobin
```

## 架构设计

### 模块结构

```
lightning-rpc/
├── rpc-core/           # 核心框架
├── rpc-protocol/       # 网络通信协议
├── rpc-serialization/  # 序列化模块
├── rpc-registry/       # 注册中心
├── rpc-loadbalance/    # 负载均衡
├── rpc-spring/         # Spring集成
└── rpc-example/        # 使用示例
```

### 核心组件

- **RpcRequest/RpcResponse**: RPC请求和响应对象
- **Invoker**: 调用器接口，定义RPC调用方法
- **Registry**: 注册中心接口，支持服务注册与发现
- **LoadBalancer**: 负载均衡器接口，支持多种负载均衡算法
- **Serializer**: 序列化器接口，支持多种序列化方式
- **ProxyFactory**: 代理工厂接口，支持多种代理机制

## 配置说明

### 基础配置

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `lightning.rpc.application.name` | 应用名称 | lightning-rpc |
| `lightning.rpc.server.host` | 服务器地址 | 127.0.0.1 |
| `lightning.rpc.server.port` | 服务器端口 | 8080 |
| `lightning.rpc.serialization.type` | 序列化类型 | json |

### 注册中心配置

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `lightning.rpc.registry.type` | 注册中心类型 | zookeeper |
| `lightning.rpc.registry.address` | 注册中心地址 | 127.0.0.1:2181 |

### 负载均衡配置

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `lightning.rpc.load-balancer.type` | 负载均衡算法 | roundRobin |

## 性能优化

### 1. 网络优化
- 使用Netty的NIO模型
- 支持连接池复用
- 异步非阻塞调用

### 2. 序列化优化
- 支持多种序列化方式
- 根据场景选择合适的序列化器
- 支持压缩传输

### 3. 负载均衡优化
- 支持权重配置
- 支持一致性哈希
- 支持动态权重调整

## 扩展开发

### 自定义序列化器

```java
public class CustomSerializer implements Serializer {
    
    @Override
    public byte[] serialize(Object obj) throws Exception {
        // 自定义序列化逻辑
    }
    
    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws Exception {
        // 自定义反序列化逻辑
    }
    
    @Override
    public SerializerType getType() {
        return SerializerType.CUSTOM;
    }
}
```

### 自定义负载均衡器

```java
public class CustomLoadBalancer implements LoadBalancer {
    
    @Override
    public ServiceInfo select(List<ServiceInfo> services) {
        // 自定义负载均衡逻辑
    }
    
    @Override
    public String getName() {
        return "custom";
    }
}
```

## 最佳实践

### 1. 服务设计
- 保持接口简单，避免复杂对象
- 合理设置超时时间
- 使用版本控制

### 2. 性能调优
- 选择合适的序列化方式
- 配置合适的线程池大小
- 使用连接池

### 3. 监控告警
- 监控服务调用成功率
- 监控响应时间
- 设置告警阈值

## 许可证

MIT License

## 贡献

欢迎提交Issue和Pull Request来帮助改进项目。

## 联系方式

- 项目地址: https://github.com/tanzhilangnw/lightning-rpc
- 问题反馈: https://github.com/tanzhilangnw/lightning-rpc/issues
- 作者: tanzhilangnw
