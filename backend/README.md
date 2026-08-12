# Aurora Timer Server ⏱️

> AuroraTimer - 计时打卡管理平台

[![Java](https://img.shields.io/badge/Java-17-007396?style=flat&logo=java)](https://www.java.com)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-6DB33F?style=flat&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat&logo=mysql)](https://www.mysql.com)
[![Redis](https://img.shields.io/badge/Redis-6.0-DC382D?style=flat&logo=redis)](https://redis.io)
[![JWT](https://img.shields.io/badge/JWT-0.12.3-000000?style=flat&logo=json-web-tokens)](https://jwt.io)

Aurora Timer 后端服务，为桌面端应用提供 RESTful API，支持用户认证、计时管理、数据统计等功能。

## 🛠️ 技术栈

| 类别 | 技术 |
|------|------|
| 核心框架 | Spring Boot 3.3.5 |
| 数据库 | MySQL 8.0 + MyBatis-Plus 3.5.5 |
| 缓存 | Redis (实时状态、Token 黑名单) |
| 安全 | JWT (JJWT 0.12.3) + 自定义拦截器 |
| 文件存储 | 阿里云 OSS |
| 工具类 | Hutool 5.8.24 |
| 代码生成 | Lombok |
| 参数校验 | Jakarta Validation |

## 📥 下载与安装

### 环境要求

| 组件 | 版本要求 |
|------|----------|
| JDK | 17+ |
| Maven | 3.8+ |
| MySQL | 8.0+ |
| Redis | 6.0+ |

### 快速开始

```bash
# 1. 克隆项目
git clone https://github.com/TwentyfiveBTea/AuroraTimer_Server.git
cd AuroraTimer_Server

# 2. 创建数据库
mysql -u root -p < init.sql

# 3. 修改配置文件
# 编辑 src/main/resources/application.yml

# 4. 编译运行
mvn clean install
java -jar target/AuroraTimer_Server-1.0.0.jar
```

## 📦 项目结构

```
AuroraTimer_Server/
├── src/main/java/com/btea/auroratimerserver/
│   ├── AuroraTimerServerApplication.java    # 启动类
│   ├── common/                               # 公共模块
│   │   ├── config/                          # 配置类
│   │   │   ├── JwtConfig.java               # JWT配置属性
│   │   │   ├── WebMvcConfig.java            # MVC配置 & 拦截器
│   │   │   ├── AliyunConfig.java            # 阿里云OSS配置
│   │   │   └── AdminConfig.java             # 管理员配置
│   │   ├── interceptor/                    # 请求拦截器
│   │   │   ├── UserAuthInterceptor.java     # 用户认证拦截器
│   │   │   └── AdminAuthInterceptor.java    # 管理员授权拦截器
│   │   ├── context/                         # 上下文
│   │   │   └── UserContext.java             # 用户信息线程本地存储
│   │   ├── enums/                           # 枚举类
│   │   │   └── JwtRoleEnum.java             # JWT角色枚举
│   │   ├── util/                            # 工具类
│   │   │   └── JwtUtil.java                 # JWT工具类
│   │   ├── constant/                        # 常量
│   │   ├── convention/                      # 约定
│   │   │   ├── errorcode/                   # 错误码
│   │   │   ├── exception/                   # 异常
│   │   │   └── result/                      # 统一响应
│   │   └── handler/                         # 处理器
│   │       ├── GlobalExceptionHandler.java  # 全局异常处理
│   │       └── MyMetaObjectHandler.java     # MyBatis元数据处理器
│   ├── controller/                          # 控制器
│   │   ├── UsersController.java             # 用户接口
│   │   ├── TimerController.java             # 计时器接口
│   │   └── NotificationsController.java     # 通知接口
│   ├── service/                             # 业务层
│   │   ├── UsersService.java                # 用户服务
│   │   ├── TimerService.java                # 计时器服务
│   │   ├── NotificationsService.java        # 通知服务
│   │   └── impl/                            # 实现类
│   ├── dao/                                 # 数据访问层
│   │   ├── entity/                          # 实体类
│   │   │   ├── UsersDO.java                 # 用户实体
│   │   │   ├── TimerSummaryDO.java          # 计时汇总实体
│   │   │   └── TimerRecordsDO.java          # 计时记录实体
│   │   └── mapper/                          # Mapper接口
│   ├── req/                                 # 请求DTO
│   ├── vo/                                  # 响应VO
│   └── task/                                # 定时任务
│       └── WeeklyResetTask.java             # 每周重置任务
└── pom.xml
```

## 🖥️ 部署建议

### 使用 Nginx 反向代理

```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://127.0.0.1:8088;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### Docker 部署

```dockerfile
# 更轻量的版本（JRE 而不是 JDK）
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/AuroraTimer_Server-1.0.0.jar app.jar
EXPOSE 8088
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 📄 许可证

本项目基于 MIT 许可证开源，详见 [LICENSE](LICENSE)。

---

## 👤 作者

- GitHub: [TwentyfiveBTea](https://github.com/TwentyfiveBTea)

---

## 🙏 感谢

感谢使用 Aurora Timer Server！
