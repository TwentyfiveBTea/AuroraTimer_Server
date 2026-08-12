const { contextBridge, ipcRenderer } = require('electron')

// 安全暴露 API 给渲染进程
contextBridge.exposeInMainWorld('electronAPI', {
  // 主题控制
  setTheme: (theme) => ipcRenderer.invoke('set-theme', theme),
  
  // 窗口控制
  windowMinimize: () => ipcRenderer.invoke('window-minimize'),
  windowMaximize: () => ipcRenderer.invoke('window-maximize'),
  windowClose: () => ipcRenderer.invoke('window-close'),
  windowFocus: () => ipcRenderer.invoke('window-focus'),
  
  // 平台检测
  platform: process.platform,
  
  // 版本信息
  appVersion: () => ipcRenderer.invoke('app.getVersion'),
  
  // 获取鼠标位置
  getMousePoint: () => ipcRenderer.invoke('get-mouse-point'),

  // 开机自启
  setAutoStart: (enable) => ipcRenderer.invoke('set-auto-start', enable),
  getAutoStart: () => ipcRenderer.invoke('get-auto-start')
})

// 添加全局样式初始化
window.addEventListener('DOMContentLoaded', () => {
  // 初始化主题
  const savedTheme = localStorage.getItem('app-theme') || 'orange'
  document.documentElement.setAttribute('data-theme', savedTheme)
})
