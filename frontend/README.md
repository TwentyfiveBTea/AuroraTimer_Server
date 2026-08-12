# Aurora Timer ⏱️

> AuroraTimer - 计时打卡管理平台

[![Vue](https://img.shields.io/badge/Vue-3.4+-42b883?style=flat&logo=vue.js)](https://vuejs.org)
[![Electron](https://img.shields.io/badge/Electron-28.3-47848f?style=flat&logo=electron)](https://www.electronjs.org)
[![Vite](https://img.shields.io/badge/Vite-5.0-646CFF?style=flat&logo=vite)](https://vitejs.dev)
[![Pinia](https://img.shields.io/badge/Pinia-2.1-ffd859?style=flat&logo=pinia)](https://pinia.vuejs.org)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.0-3178c6?style=flat&logo=typescript)](https://www.typescriptlang.org)
[![SCSS](https://img.shields.io/badge/SCSS-1.69-cc6699?style=flat&logo=sass)](https://sass-lang.com)

Aurora Timer 是一款面向工作室/团队的工时打卡管理桌面应用，支持计时、排行榜、通知发布等功能。

## 🛠️ 技术栈

| 类别 | 技术 |
|------|------|
| 框架 | Vue 3 + Composition API |
| 状态管理 | Pinia |
| 路由 | Vue Router |ß
| HTTP | Axios |
| 桌面打包 | Electron |
| 构建工具 | Vite |
| 样式 | SCSS |

## 📥 下载与选择安装包

### Windows 用户

| 文件 | 适用系统 | 架构 |
|------|----------|------|
| `Aurora Timer-Setup-x.x.x.exe` | Windows 7+ | x64 (Intel/AMD) |

> 💡 直接下载 `.exe` 文件，运行后按提示安装即可。

### macOS 用户

| 文件 | 适用 Mac | 芯片类型 |
|------|----------|----------|
| `Aurora Timer-x.x.x-arm64.dmg` | Mac (M1/M2/M3) | Apple Silicon |
| `Aurora Timer-x.x.x.dmg` | Mac (Intel) | x64 |

#### 如何确认自己的 Mac 芯片？

1. 点击苹果菜单 () → 关于本机
2. 查看"芯片"或"处理器"：
   - **M1/M2/M3** → 下载 `arm64.dmg`
   - **Intel** → 下载普通 `.dmg`

#### ⚠️ macOS 安装注意事项

由于应用未经过 Apple 公证，首次安装时：
1. 打开 `.dmg`，将应用拖入 Applications 文件夹
2. 在 Launchpad 中打开应用
3. 若提示"无法打开"，右键点击应用 → 打开 → 仍要打开
4. 之后即可正常使用

### Linux 用户

| 文件 | 适用系统 |
|------|----------|
| `Aurora Timer-x.x.x.AppImage` | Ubuntu/Debian/Fedora 等 |

> 💡 给 `.AppImage` 文件添加执行权限后即可运行：
> ```bash
> chmod +x "Aurora Timer-x.x.x.AppImage"
> ./"Aurora Timer-x.x.x.AppImage"
> ```

---

## 📦 构建桌面应用

```bash
# 构建生产版本
npm run electron:build
```

### ⚠️ 跨平台打包注意事项

| 操作系统 | 生成安装包 | 说明 |
|----------|------------|------|
| Windows | `.exe` (NSIS) | 直接运行 `npm run electron:build` 即可 |
| Mac | `.dmg` | 需要在 Mac 系统上构建 |
| Linux | `.AppImage` | 需要在 Linux 系统上构建 |

> **注意**：如果你在 Mac 上但需要打包 Windows 的 `.exe`，可以考虑以下方案：
> - 使用 [electron-builder](https://www.electron.build) 的 `--win` 参数（需要在 Mac 安装 Wine）
> - 使用 CI/CD 环境（如 GitHub Actions）进行跨平台打包

## 🔧 配置

### 环境变量

在项目根目录创建 `.env` 文件：

```env
# 开发环境 API 地址
VITE_API_BASE_URL=http://localhost:8088
```

生产环境使用 `.env.production`：

```env
VITE_API_BASE_URL=http://your-server-ip:8088
```

## 📁 项目结构

```
AuroraTimer_Client/
├── electron/           # Electron 主进程
│   ├── main.js         # 主入口
│   └── preload.js      # 预加载脚本
├── src/
│   ├── api/            # API 请求封装
│   ├── assets/         # 静态资源
│   ├── components/     # 公共组件
│   ├── composables/    # 组合式函数
│   ├── constants/     # 常量定义
│   ├── router/         # 路由配置
│   ├── stores/         # Pinia 状态管理
│   ├── styles/         # 全局样式
│   ├── utils/          # 工具函数
│   └── views/          # 页面视图
│       ├── admin/          # 管理后台
│       ├── auth/           # 登录注册
│       ├── dashboard/      # 首页/计时
│       ├── leaderboard/    # 排行榜
│       ├── notifications/  # 通知中心
│       ├── profile/        # 个人中心
│       └── settings/       # 设置
├── index.html
├── vite.config.js
└── package.json
```

## 🔌 API 对接

本项目为纯前端项目，需要搭配后端服务使用。

---

## 🖼️页面图片
- 登录<img width="1400" height="900" alt="WechatIMG869" src="https://github.com/user-attachments/assets/3eacc40a-ef5e-43ad-9ff5-005a0395cdc0" />

- 注册<img width="1400" height="900" alt="WechatIMG871" src="https://github.com/user-attachments/assets/dd022f98-188d-4580-b293-ecf26f0e3de9" />

- 忘记密码<img width="1400" height="900" alt="WechatIMG873" src="https://github.com/user-attachments/assets/36497f9e-1a84-49c5-9db8-3eec2dc130d0" />

- 首页<img width="1400" height="900" alt="WechatIMG875" src="https://github.com/user-attachments/assets/fde03d40-442a-40be-954b-a3a9080b8fb0" />

- 排行榜<img width="1400" height="900" alt="WechatIMG877" src="https://github.com/user-attachments/assets/9e47e5fc-386e-4630-83e9-d28e688aaded" />

- 通知<img width="1400" height="900" alt="WechatIMG879" src="https://github.com/user-attachments/assets/bf5ed09b-44f1-4214-841e-0acb5137257f" />

- 个人中心<img width="1400" height="900" alt="WechatIMG881" src="https://github.com/user-attachments/assets/cf414836-1360-4bf3-9158-3ae7bd82ec19" />

- 设置<img width="1400" height="900" alt="WechatIMG883" src="https://github.com/user-attachments/assets/65f1a539-01bf-4b44-a2b7-754a715e7a1c" />

- 管理员页面<img width="1400" height="900" alt="WechatIMG888" src="https://github.com/user-attachments/assets/acbf1d96-9ed1-4f41-ab80-6aed8aad1c8e" />


---

## 📄 许可证

本项目基于 MIT 许可证开源，详见 [LICENSE](LICENSE)。

---

## 👤 作者

- GitHub: [TwentyfiveBTea](https://github.com/TwentyfiveBTea)

---

## 🙏 感谢

感谢使用 Aurora Timer！
