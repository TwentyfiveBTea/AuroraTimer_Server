# AuroraTimer

AuroraTimer 是一款面向工作室和团队的计时打卡管理平台。本仓库同时包含 Electron 桌面客户端和 Spring Boot 服务端。

## 项目结构

```text
AuroraTimer/
├── frontend/          # Vue 3 + Electron 桌面客户端
│   ├── electron/      # Electron 主进程与预加载脚本
│   ├── src/           # 前端业务代码
│   ├── test/          # Node.js 测试
│   └── package.json
├── backend/           # Spring Boot 服务端
│   ├── src/           # Java 源码与配置
│   └── pom.xml
├── .github/workflows/ # GitHub Actions
└── LICENSE
```

## 前端

前端位于 `frontend/`，提供计时、排行榜、通知、个人资料、设置和管理后台等桌面端功能。

### 技术栈

| 类别 | 技术 |
| --- | --- |
| UI 框架 | Vue 3、Composition API |
| 状态管理 | Pinia |
| 路由 | Vue Router |
| HTTP 客户端 | Axios |
| 桌面应用 | Electron 28 |
| 构建工具 | Vite 5 |
| 样式 | CSS、Sass |
| 测试 | Node.js Test Runner |

### 环境要求

- Node.js 20+
- npm 10+

### 开发与构建

```bash
cd frontend
npm ci

# 启动 Web 开发服务器
npm run dev

# 启动 Electron 开发环境
npm run electron:dev

# 运行测试
npm test

# 构建 Web 资源
npm run build

# 构建桌面安装包
npm run electron:build
```

开发环境可在 `frontend/.env` 中配置服务端地址：

```env
VITE_API_BASE_URL=http://localhost:8088
```

## 后端

后端位于 `backend/`，为客户端提供用户认证、计时管理、排行榜、通知和数据统计等 REST API。

### 技术栈

| 类别 | 技术 |
| --- | --- |
| 核心框架 | Spring Boot 3.3.5 |
| Java | JDK 17 |
| 数据访问 | MyBatis-Plus 3.5.5、MySQL |
| 缓存 | Spring Data Redis |
| 身份认证 | JJWT 0.12.3、自定义拦截器 |
| 文件存储 | 阿里云 OSS |
| 工具与校验 | Hutool、Jakarta Validation、Lombok |
| 构建工具 | Maven |

### 环境要求

- JDK 17
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+

### 配置与运行

在 `backend/src/main/resources/application.yml` 中配置数据库、Redis、JWT、管理员账号和阿里云 OSS 等服务参数。该文件包含本地敏感配置，已被 Git 忽略。

```bash
cd backend

# 运行测试并构建
mvn clean package

# 启动开发服务
mvn spring-boot:run

# 或运行构建产物
java -jar target/AuroraTimer_Server-1.0.0.jar
```

服务默认由项目配置监听 `8088` 端口，前端的 `VITE_API_BASE_URL` 应指向该服务地址。

## 许可证

本项目基于 [MIT License](LICENSE) 开源。

## 作者

[TwentyfiveBTea](https://github.com/TwentyfiveBTea)
