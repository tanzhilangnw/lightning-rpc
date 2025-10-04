# Lightning RPC 部署指南

> ⚠️ **注意**: 本项目目前处于开发阶段（v1.0.0-SNAPSHOT），API可能会发生变化，不建议在生产环境中使用。

## 📦 发布到Maven仓库

### 方式一：本地安装（推荐用于开发测试）

```bash
# 1. 克隆项目
git clone https://github.com/tanzhilangnw/lightning-rpc.git
cd lightning-rpc

# 2. 安装到本地Maven仓库
mvn clean install

# 3. 在其他项目中使用
```

**使用示例**：
```xml
<dependency>
    <groupId>com.lightning</groupId>
    <artifactId>rpc-spring</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 方式二：发布到GitHub Packages

#### 1. 配置GitHub Token

在GitHub上创建Personal Access Token：
- 访问：Settings → Developer settings → Personal access tokens
- 选择：packages (read/write)
- 复制生成的token

#### 2. 配置Maven settings.xml

在 `~/.m2/settings.xml` 中添加：

```xml
<settings>
    <servers>
        <server>
            <id>github</id>
            <username>tanzhilangnw</username>
            <password>YOUR_GITHUB_TOKEN</password>
        </server>
    </servers>
</settings>
```

#### 3. 发布到GitHub Packages

```bash
# 发布到GitHub Packages
mvn clean deploy
```

#### 4. 在其他项目中使用GitHub Packages

在项目的 `pom.xml` 中添加仓库配置：

```xml
<repositories>
    <repository>
        <id>github</id>
        <name>GitHub Packages</name>
        <url>https://maven.pkg.github.com/tanzhilangnw/lightning-rpc</url>
    </repository>
</repositories>
```

### 方式三：发布到Maven中央仓库

#### 1. 注册Sonatype账号
- 访问：https://issues.sonatype.org/
- 创建账号并申请GroupId

#### 2. 配置GPG签名
```bash
# 生成GPG密钥
gpg --gen-key

# 发布公钥
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
```

#### 3. 配置pom.xml
```xml
<distributionManagement>
    <snapshotRepository>
        <id>ossrh</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
    </snapshotRepository>
    <repository>
        <id>ossrh</id>
        <url>https://oss.sonatype.org/service/local/staging/deploy/maven2/</url>
    </repository>
</distributionManagement>
```

#### 4. 发布到中央仓库
```bash
mvn clean deploy
```

## 🚀 自动化发布

项目已配置GitHub Actions，当推送代码到main分支时会自动：
1. 运行测试
2. 构建项目
3. 发布到GitHub Packages

## 📋 使用步骤总结

### 快速开始（本地开发）

1. **安装到本地**：
   ```bash
   mvn clean install
   ```

2. **在项目中使用**：
   ```xml
   <dependency>
       <groupId>com.lightning</groupId>
       <artifactId>rpc-spring</artifactId>
       <version>1.0.0</version>
   </dependency>
   ```

### 生产环境使用

1. **发布到仓库**：
   ```bash
   mvn clean deploy
   ```

2. **配置仓库地址**（如果使用私有仓库）

3. **添加依赖**：
   ```xml
   <dependency>
       <groupId>com.lightning</groupId>
       <artifactId>rpc-spring</artifactId>
       <version>1.0.0-SNAPSHOT</version>
   </dependency>
   ```

## 🔧 版本管理

- **快照版本**：`1.0.0-SNAPSHOT`（开发中）
- **正式版本**：`1.0.0`（稳定版）
- **发布版本**：`1.0.1`、`1.1.0`等（功能更新）

## 📝 注意事项

1. **权限配置**：确保有仓库的写权限
2. **Token安全**：不要在代码中硬编码Token
3. **版本冲突**：注意依赖版本兼容性
4. **测试覆盖**：发布前确保所有测试通过

## 🆘 常见问题

### Q: 发布失败怎么办？
A: 检查网络连接、Token权限、仓库配置

### Q: 依赖找不到？
A: 确认仓库地址配置正确，检查版本号

### Q: 如何回滚版本？
A: 发布新版本覆盖，或删除错误版本（如果仓库支持）
