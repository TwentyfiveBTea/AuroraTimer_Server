import { app, BrowserWindow, ipcMain, shell, dialog, Menu, screen } from 'electron'
import { join } from 'node:path'
import { fileURLToPath } from 'node:url'

const __dirname = fileURLToPath(new URL('.', import.meta.url))

let mainWindow = null

// 获取应用根目录
function getAppPath() {
  return app.isPackaged ? app.getAppPath() : __dirname
}

const isDev = !app.isPackaged

// 创建主窗口
function createWindow() {
  mainWindow = new BrowserWindow({
    width: 1400,
    height: 900,
    minWidth: 1200,
    minHeight: 700,
    backgroundColor: '#FAF4ED',
    frame: true,
    titleBarStyle: 'default',
    titleBarOverlay: {
      color: '#FAF4ED',
      symbolColor: '#2C2C2C',
      height: 40
    },
    webPreferences: {
      nodeIntegration: false,
      contextIsolation: true,
      preload: isDev ? join(__dirname, 'preload.js') : join(getAppPath(), 'electron', 'preload.js')
    },
    show: false,
    autoHideMenuBar: false
  })

  // 开发环境加载 Vite 开发服务器
  if (isDev) {
    mainWindow.loadURL('http://localhost:5173')
    mainWindow.webContents.openDevTools()
  } else {
    // 生产环境加载打包后的文件
    mainWindow.loadFile(join(getAppPath(), 'dist', 'index.html'))
  }

  // 窗口准备好后显示
  mainWindow.once('ready-to-show', () => {
    mainWindow.show()
  })

  // 窗口关闭时退出应用
  mainWindow.on('closed', () => {
    mainWindow = null
    app.quit()
  })

  // 监听全屏状态变化
  mainWindow.on('enter-full-screen', () => {
    mainWindow.webContents.send('window-fullscreen-status', true)
  })

  mainWindow.on('leave-full-screen', () => {
    mainWindow.webContents.send('window-fullscreen-status', false)
  })

  // 拦截外部链接
  mainWindow.webContents.setWindowOpenHandler(({ url }) => {
    shell.openExternal(url)
    return { action: 'deny' }
  })

  // 创建应用菜单
  createMenu()
}

// 创建应用菜单
function createMenu() {
  const template = [
    {
      label: 'Aurora Timer',
      submenu: [
        { role: 'about' },
        { type: 'separator' },
        { role: 'services' },
        { type: 'separator' },
        { role: 'hide' },
        { role: 'hideOthers' },
        { role: 'unhide' },
        { type: 'separator' },
        { role: 'quit' }
      ]
    },
    {
      label: '编辑',
      submenu: [
        { role: 'undo' },
        { role: 'redo' },
        { type: 'separator' },
        { role: 'cut' },
        { role: 'copy' },
        { role: 'paste' },
        { role: 'selectAll' }
      ]
    },
    {
      label: '视图',
      submenu: [
        { role: 'reload' },
        { role: 'forceReload' },
        { role: 'toggleDevTools' },
        { type: 'separator' },
        { role: 'resetZoom' },
        { role: 'zoomIn' },
        { role: 'zoomOut' },
        { type: 'separator' },
        { role: 'togglefullscreen' }
      ]
    },
    {
      label: '窗口',
      submenu: [
        { role: 'minimize' },
        { role: 'zoom' },
        { type: 'separator' },
        { role: 'front' }
      ]
    }
  ]

  if (process.platform === 'darwin') {
    template.unshift({
      label: app.name,
      submenu: [
        { label: '关于 Aurora Timer', click: () => showAboutDialog() },
        { type: 'separator' },
        { label: '偏好设置...', accelerator: 'Cmd+,' },
        { type: 'separator' },
        { role: 'services' },
        { type: 'separator' },
        { role: 'hide' },
        { role: 'hideOthers' },
        { role: 'unhide' },
        { type: 'separator' },
        { role: 'quit' }
      ]
    })
  }

  const menu = Menu.buildFromTemplate(template)
  Menu.setApplicationMenu(menu)
}

// 显示关于对话框
function showAboutDialog() {
  dialog.showMessageBox(mainWindow, {
    type: 'info',
    title: '关于 Aurora Timer',
    message: 'Aurora Timer',
    detail: `版本: ${app.getVersion()}\n\nAurora Studio Community - 工时管理与协作平台\n\n© 2024 Aurora Studio. All rights reserved.`
  })
}

// IPC 通信处理器

// 主题设置
ipcMain.handle('set-theme', async (event, theme) => {
  event.sender.executeJavaScript(`document.documentElement.setAttribute('data-theme', '${theme}')`)
  // 保存到本地存储
  event.sender.executeJavaScript(`localStorage.setItem('app-theme', '${theme}')`)
})

// 获取鼠标位置
ipcMain.handle('get-mouse-point', () => {
  const point = screen.getCursorScreenPoint()
  return { x: point.x, y: point.y }
})

// 开机自启设置
ipcMain.handle('set-auto-start', (event, enable) => {
  app.setLoginItemSettings({
    openAtLogin: enable
  })
  return true
})

// 获取开机自启状态
ipcMain.handle('get-auto-start', () => {
  const settings = app.getLoginItemSettings()
  return settings.openAtLogin
})

// 窗口聚焦
ipcMain.handle('window-focus', () => {
  if (mainWindow) {
    mainWindow.focus()
  }
})

// 窗口控制
ipcMain.handle('window-minimize', () => {
  mainWindow.minimize()
})

ipcMain.handle('window-maximize', () => {
  if (mainWindow.isMaximized()) {
    mainWindow.unmaximize()
  } else {
    mainWindow.maximize()
  }
})

ipcMain.handle('window-toggle-fullscreen', () => {
  if (mainWindow.isFullScreen()) {
    mainWindow.setFullScreen(false)
    mainWindow.webContents.send('window-fullscreen-status', false)
  } else {
    mainWindow.setFullScreen(true)
    mainWindow.webContents.send('window-fullscreen-status', true)
  }
})

ipcMain.handle('window-close', () => {
  mainWindow.close()
})

// 应用启动
app.whenReady().then(createWindow)

// macOS 特性：所有窗口关闭时隐藏窗口而非退出
app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit()
  }
})

app.on('activate', () => {
  if (BrowserWindow.getAllWindows().length === 0) {
    createWindow()
  }
})

// 安全策略
app.on('web-contents-created', (event, contents) => {
  contents.on('will-navigate', (event, navigationUrl) => {
    if (!isDev && !navigationUrl.startsWith('file://')) {
      event.preventDefault()
    }
  })
})
