import { reactive, ref } from 'vue'

// 全局消息状态
const dialogState = reactive({
  visible: false,
  type: 'info',
  title: '',
  message: '',
  confirmText: '确定',
  cancelText: '取消',
  showCancel: false,
  closeOnOverlay: true,
  resolve: null
})

// 是否正在显示
const isShowing = ref(false)

/**
 * 显示消息对话框
 * @param {Object} options - 配置选项
 * @param {string} options.type - 消息类型: success, error, warning, info, confirm
 * @param {string} options.title - 标题
 * @param {string} options.message - 消息内容
 * @param {string} [options.confirmText] - 确定按钮文字
 * @param {string} [options.cancelText] - 取消按钮文字
 * @param {boolean} [options.showCancel] - 是否显示取消按钮
 * @param {boolean} [options.closeOnOverlay] - 点击遮罩是否关闭
 * @returns {Promise<boolean>} - 用户点击确认返回 true，取消返回 false
 */
export function showDialog(options) {
  return new Promise((resolve) => {
    // 设置对话框状态
    dialogState.type = options.type || 'info'
    dialogState.title = options.title || getDefaultTitle(options.type)
    dialogState.message = options.message || ''
    dialogState.confirmText = options.confirmText || '确定'
    dialogState.cancelText = options.cancelText || '取消'
    dialogState.showCancel = options.showCancel || false
    dialogState.closeOnOverlay = options.closeOnOverlay !== false
    dialogState.resolve = resolve
    
    dialogState.visible = true
    isShowing.value = true
  })
}

/**
 * 获取默认标题
 */
function getDefaultTitle(type) {
  const titleMap = {
    success: '操作成功',
    error: '操作失败',
    warning: '温馨提示',
    info: '提示信息',
    confirm: '确认操作'
  }
  return titleMap[type] || '提示'
}

/**
 * 快捷方法：显示成功对话框
 */
export function showSuccess(message, title = '操作成功') {
  return showDialog({ type: 'success', title, message })
}

/**
 * 快捷方法：显示错误对话框
 */
export function showError(message, title = '操作失败') {
  return showDialog({ type: 'error', title, message })
}

/**
 * 快捷方法：显示警告对话框
 */
export function showWarning(message, title = '温馨提示') {
  return showDialog({ type: 'warning', title, message })
}

/**
 * 快捷方法：显示信息对话框
 */
export function showInfo(message, title = '提示信息') {
  return showDialog({ type: 'info', title, message })
}

/**
 * 快捷方法：显示确认对话框
 */
export function showConfirm(message, title = '确认操作', options = {}) {
  return showDialog({ 
    type: 'confirm', 
    title, 
    message,
    showCancel: true,
    ...options
  })
}

/**
 * 处理用户确认
 */
export function handleConfirm() {
  if (dialogState.resolve) {
    dialogState.resolve(true)
  }
  closeDialog()
}

/**
 * 处理用户取消
 */
export function handleCancel() {
  if (dialogState.resolve) {
    dialogState.resolve(false)
  }
  closeDialog()
}

/**
 * 关闭对话框
 */
export function closeDialog() {
  dialogState.visible = false
  isShowing.value = false
}

export { dialogState, isShowing }
