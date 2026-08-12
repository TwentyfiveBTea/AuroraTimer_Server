/**
 * 方向颜色配置
 * 用于各个方向对应的颜色展示
 */

// 方向颜色映射
export const DIRECTION_COLORS = {
  // 前端
  frontend: {
    main: '#4ECDC4',
    light: '#B2EBE7',
    name: '前端',
    nameWithPrefix: '前端'
  },

  // 后端
  backend: {
    main: '#45B7D1',
    light: '#B6E3ED',
    name: '后端',
    nameWithPrefix: '后端'
  },

  // 设计
  design: {
    main: '#FF8B94',
    light: '#FFC8CC',
    name: '设计',
    nameWithPrefix: '设计'
  },

  // 算法
  algorithm: {
    main: '#A8E6CF',
    light: '#D8F5EA',
    name: '算法',
    nameWithPrefix: '算法'
  },

  // 嵌入式
  embedded: {
    main: '#FFD3B6',
    light: '#FFEAD9',
    name: '嵌入式',
    nameWithPrefix: '嵌入式'
  },

  // 数据分析
  dataAnalysis: {
    main: '#6C5CE7',
    light: '#B6AEEF',
    name: '数据分析',
    nameWithPrefix: '数据分析'
  },

  // 网络安全
  cybersecurity: {
    main: '#FFAD60',
    light: '#FFD8B3',
    name: '网络安全',
    nameWithPrefix: '网络安全'
  }
}

// 方向选项列表（用于下拉框等场景）
export const DIRECTION_OPTIONS = [
  { value: '前端', label: '前端' },
  { value: '后端', label: '后端' },
  { value: '设计', label: '设计' },
  { value: '算法', label: '算法' },
  { value: '嵌入式', label: '嵌入式' },
  { value: '数据分析', label: '数据分析' },
  { value: '网络安全', label: '网络安全' }
]

// 简化的方向列表
export const DIRECTION_LIST = [
  { value: '前端', label: '前端' },
  { value: '后端', label: '后端' },
  { value: '设计', label: '设计' },
  { value: '算法', label: '算法' },
  { value: '嵌入式', label: '嵌入式' },
  { value: '数据分析', label: '数据分析' },
  { value: '网络安全', label: '网络安全' }
]

/**
 * 根据方向值获取颜色配置
 * @param {string} direction - 方向值
 * @returns {object} 颜色配置对象
 */
export function getDirectionColor(direction) {
  if (!direction) {
    return {
      main: 'rgba(0, 0, 0, 0.03)',
      light: 'rgba(0, 0, 0, 0.03)',
      name: '未选择',
      nameWithPrefix: '未选择'
    }
  }

  // 处理中文方向名称到键名的映射
  const nameToKeyMap = {
    '前端': 'frontend',
    '后端': 'backend',
    '设计': 'design',
    '算法': 'algorithm',
    '嵌入式': 'embedded',
    '数据分析': 'dataAnalysis',
    '网络安全': 'cybersecurity'
  }

  // 如果传入的是中文名称，转换为键名
  let key = direction
  if (nameToKeyMap[direction]) {
    key = nameToKeyMap[direction]
  }

  // 如果键名在映射表中，直接返回
  if (DIRECTION_COLORS[key]) {
    return DIRECTION_COLORS[key]
  }

  // 否则尝试直接匹配
  for (const [colorKey, colorValue] of Object.entries(DIRECTION_COLORS)) {
    if (colorValue.name === direction || colorValue.nameWithPrefix === direction) {
      return colorValue
    }
  }

  // 未找到匹配，返回默认灰色
  return {
    main: 'rgba(0, 0, 0, 0.03)',
    light: 'rgba(0, 0, 0, 0.03)',
    name: direction,
    nameWithPrefix: direction
  }
}
