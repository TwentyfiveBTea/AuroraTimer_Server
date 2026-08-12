import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { setStorage, getStorage } from '@/utils'

export const useThemeStore = defineStore('theme', () => {
  // 状态
  const currentTheme = ref('classic')
  const isDarkMode = ref(false)
  const currentFont = ref('system-ui')
  
  // 可用字体列表
  const fonts = [
    { id: 'system-ui', name: '系统默认', fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif' },
    { id: 'pingfang', name: '苹方字体', fontFamily: '"PingFang SC", "苹方-简", "Microsoft YaHei", sans-serif' },
    { id: 'alibaba', name: '阿里巴巴普惠体', fontFamily: '"AlibabaPuHuiTi", "阿里巴巴普惠体", "Microsoft YaHei", sans-serif' },
    { id: 'source-han', name: '思源黑体', fontFamily: '"Source Han Sans", "Noto Sans SC", "思源黑体", "Microsoft YaHei", sans-serif' },
    { id: 'fangzheng', name: '方正黑体', fontFamily: '"FZHei", "方正黑体", "Microsoft YaHei", sans-serif' },
    { id: 'droid-sans', name: 'Droid Sans', fontFamily: '"Droid Sans", "Roboto", "Helvetica Neue", Arial, sans-serif' },
    { id: 'noto-sans', name: 'Noto Sans', fontFamily: '"Noto Sans", "Roboto", "Helvetica Neue", Arial, sans-serif' },
    { id: 'inter', name: 'Inter', fontFamily: '"Inter", -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif' },
    { id: 'monaco', name: 'Monaco', fontFamily: '"Monaco", "Menlo", "Ubuntu Mono", "Consolas", "Source Code Pro", monospace' },
    { id: 'sf-mono', name: 'SF Mono', fontFamily: '"SF Mono", "Monaco", "Menlo", "Consolas", monospace' }
  ]
  
  // 可用主题列表 - Aurora精简主题系统
  const themes = [
    { id: 'classic', name: 'Aurora Classic', color: '#DFA473', group: 'original' },
    { id: 'minimalist', name: 'Minimalist', color: '#333333', group: 'original' },
    { id: 'forest', name: 'Forest', color: '#A3B18A', group: 'original' },
    { id: 'valentine', name: 'Valentine', color: '#E96D7B', group: 'original' },
    { id: 'emerald', name: 'Emerald', color: '#66CC8A', group: 'original' },
    // 新增主题
    { id: 'autumn', name: 'Autumn Warm', color: '#F6A885', group: 'warm' },
    { id: 'macaron', name: 'Macaron Pink', color: '#946EAF', group: 'macaron' },
    { id: 'morandi', name: 'Morandi Contrast', color: '#E77141', group: 'morandi' },
    { id: 'deepblue', name: 'Deep Blue', color: '#35549E', group: 'deepblue' }
  ]
  
  // 主题分组显示
  const themeGroups = [
    { id: 'original', name: '经典主题' },
    { id: 'warm', name: '暖秋风情' },
    { id: 'macaron', name: '马卡龙' },
    { id: 'morandi', name: '莫兰迪' },
    { id: 'deepblue', name: '深蓝治愈' }
  ]
  
  // 计算当前主题对象
  const currentThemeObject = computed(() => {
    return themes.find(t => t.id === currentTheme.value) || themes[0]
  })
  
  // 根据分组获取主题
  function getThemesByGroup(groupId) {
    return themes.filter(t => t.group === groupId)
  }
  
  // 初始化主题
  function initTheme() {
    // 优先从本地存储读取
    const savedTheme = getStorage('app-theme', 'classic')
    setTheme(savedTheme)
    
    // 检测系统深色模式偏好
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
    if (prefersDark && !getStorage('theme-manual-set')) {
      toggleDarkMode(true)
    }
  }
  
  // 设置主题
  function setTheme(themeId) {
    const theme = themes.find(t => t.id === themeId)
    if (theme) {
      currentTheme.value = themeId
      document.documentElement.setAttribute('data-theme', themeId)
      setStorage('app-theme', themeId)
      setStorage('theme-manual-set', true)
    }
  }
  
  // 切换深色模式
  function toggleDarkMode(forceDark = null) {
    if (forceDark !== null) {
      isDarkMode.value = forceDark
    } else {
      isDarkMode.value = !isDarkMode.value
    }
    
    document.documentElement.setAttribute('data-theme', 
      isDarkMode.value ? 'dark' : currentTheme.value)
    
    setStorage('dark-mode', isDarkMode.value)
  }

  // 获取当前字体对象
  const currentFontObject = computed(() => {
    return fonts.find(f => f.id === currentFont.value) || fonts[0]
  })

  // 设置字体
  function setFont(fontId) {
    const font = fonts.find(f => f.id === fontId)
    if (font) {
      currentFont.value = fontId
      document.documentElement.style.setProperty('--font-family-main', font.fontFamily)
      setStorage('app-font', fontId)
    }
  }

  // 初始化字体
  function initFont() {
    const savedFont = getStorage('app-font', 'system-ui')
    setFont(savedFont)
  }
  
  return {
    currentTheme,
    isDarkMode,
    currentFont,
    fonts,
    themes,
    themeGroups,
    currentThemeObject,
    currentFontObject,
    getThemesByGroup,
    initTheme,
    setTheme,
    toggleDarkMode,
    setFont,
    initFont
  }
})
