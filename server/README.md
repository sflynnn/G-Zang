# G-Zang Server (后端服务)

G-Zang (归藏) 财务管理系统的后端服务，基于 Spring Boot 3 + MyBatis Plus + MySQL 构建。

## 技术栈

- **Java**: 17
- **Spring Boot**: 3.2.0
- **MyBatis Plus**: 3.5.5
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **安全**: Spring Security + JWT
- **连接池**: Druid

## 项目结构

```
server/
├── src/main/java/com/gzang/app/
│   ├── GZangApplication.java          # 主启动类
│   ├── config/                        # 配置类
│   │   ├── MyBatisPlusConfig.java     # MyBatis Plus 配置
│   │   ├── MyBatisPlusMetaObjectHandler.java  # 自动填充处理器
│   │   └── TenantInterceptor.java     # 多租户拦截器
│   ├── controller/                    # RESTful API 接口层
│   ├── service/                       # 业务逻辑层
│   │   └── impl/                      # 业务实现层
│   ├── mapper/                        # 数据访问层
│   ├── entity/                        # 数据库实体类
│   │   ├── BaseEntity.java           # 基础实体类
│   │   ├── User.java                 # 用户实体
│   │   ├── Role.java                 # 角色实体
│   │   ├── Permission.java           # 权限实体
│   │   ├── RolePermission.java       # 角色权限关联
│   │   ├── Company.java              # 公司实体
│   │   ├── Account.java              # 账户实体
│   │   ├── Category.java             # 分类实体
│   │   └── Transaction.java          # 交易记录实体
│   ├── dto/                          # 数据传输对象
│   ├── vo/                           # 视图对象
│   ├── exception/                    # 异常处理
│   └── util/                         # 工具类
│       └── TenantContextHolder.java  # 多租户上下文
├── src/main/resources/
│   ├── application.yml               # 应用配置
│   └── db/
│       └── schema.sql                # 数据库初始化脚本
└── pom.xml                           # Maven 配置
```

## 核心特性

### 多租户架构
- 基于 `user_id` 和 `company_id` 的数据隔离
- 自动 SQL 注入租户条件
- ThreadLocal 存储当前租户信息

### 实体设计
- 统一的 `BaseEntity` 基类
- 自动填充 `create_time` 和 `update_time`
- Lombok 简化代码

### 安全认证
- JWT Token 认证
- 基于角色的权限控制
- Spring Security 集成

## 数据库设计

详见项目根目录的 `database_design.md` 文件。

### 核心表
- `t_user`: 用户表
- `t_role`: 角色表
- `t_permission`: 权限表
- `t_company`: 公司表
- `t_account`: 账户表
- `t_category`: 分类表
- `t_transaction`: 交易记录表

## 快速开始

### 环境要求
- JDK 17+
- MySQL 8.0+
- Redis (可选)

### 初始化数据库
1. 创建数据库：`CREATE DATABASE g_zang CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`
2. 执行初始化脚本：`server/src/main/resources/db/schema.sql`

### 运行应用
```bash
cd server
mvn spring-boot:run
```

应用将在 `http://localhost:8080` 启动。

## 配置说明

主要配置位于 `application.yml`：

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/g_zang?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 123456

  redis:
    host: localhost
    port: 6379

jwt:
  secret: gzang-secret-key-2024
  expiration: 86400000  # 24小时
```

## 开发规范

- 遵循阿里巴巴 Java 开发规范
- 使用 Lombok 简化代码
- 统一的异常处理和响应格式
- 完整的单元测试覆盖

## API 文档

API 接口规范详见项目根目录的 `api_specification.md` 文件。

## 注意事项

1. 多租户拦截器会自动为指定表添加 `user_id` 和 `company_id` 条件
2. 所有业务表都需要包含租户字段
3. JWT Token 需要在请求头中携带：`Authorization: Bearer {token}`
4. 数据库连接请根据实际情况修改配置


