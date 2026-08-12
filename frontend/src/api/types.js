/**
 * API 响应数据类型定义
 * 基于后端接口文档生成
 * 
 * 后端 Base URL: http://localhost:8088
 * 统一响应格式: { code: string, message: string, data: any }
 */

// ==================== 通用响应类型 ====================

/**
 * 通用响应（无数据）
 * @typedef {Object} ResultVoid
 * @property {string} code - 返回码
 * @property {string} message - 返回消息
 * @property {null} data - 响应数据
 */
const ResultVoid = {
  code: '',
  message: '',
  data: null
}

/**
 * 通用响应（字符串数据）
 * @typedef {Object} ResultString
 * @property {string} code - 返回码
 * @property {string} message - 返回消息
 * @property {string} data - 响应数据
 */
const ResultString = {
  code: '',
  message: '',
  data: ''
}

/**
 * 通用响应（数字数据）
 * @typedef {Object} ResultInteger
 * @property {string} code - 返回码
 * @property {string} message - 返回消息
 * @property {number} data - 响应数据
 */
const ResultInteger = {
  code: '',
  message: '',
  data: 0
}

/**
 * 通用响应（布尔数据）
 * @typedef {Object} ResultBoolean
 * @property {string} code - 返回码
 * @property {string} message - 返回消息
 * @property {boolean} data - 响应数据
 */
const ResultBoolean = {
  code: '',
  message: '',
  data: true
}

// ==================== 认证相关类型 ====================

/**
 * 注册请求参数
 * @typedef {Object} RegisterReq
 * @property {string} name - 姓名（必填）
 * @property {string} userId - 学号，11位数字（必填）
 * @property {string} email - 邮箱（必填）
 * @property {string} password - 密码，至少8位，且包含字母和数字（必填）
 * @property {string} confirmPassword - 确认密码（必填）
 * @property {string} direction - 方向（可选）
 */
const RegisterReq = {
  name: '',
  userId: '',
  email: 'user@example.com',
  password: '',
  confirmPassword: '',
  direction: ''
}

/**
 * 登录请求参数
 * @typedef {Object} LoginReq
 * @property {string} account - 账号（学号和邮箱二选一）
 * @property {string} password - 密码
 */
const LoginReq = {
  account: '',
  password: ''
}

/**
 * 重置密码请求参数
 * @typedef {Object} ResetPasswordReq
 * @property {string} userId - 学号（必填）
 */
const ResetPasswordReq = {
  userId: ''
}

// ==================== 用户相关类型 ====================

/**
 * 用户信息 VO
 * @typedef {Object} UserInfoVO
 * @property {string} userId - 学号
 * @property {string} name - 姓名
 * @property {string} direction - 方向
 * @property {string} position - 职位
 * @property {string} createTime - 注册时间
 * @property {string} avatar - 头像
 */
const UserInfoVO = {
  userId: '',
  name: '',
  direction: '',
  position: '',
  createTime: '',
  avatar: ''
}

/**
 * 更新用户资料请求参数
 * @typedef {Object} UpdateProfileReq
 * @property {string} userId - 用户 ID（必填）
 * @property {string} direction - 专业方向（可选）
 * @property {string} position - 当前职位（可选）
 * @property {string} email - 邮箱（可选）
 * @property {string} currentPassword - 当前密码，修改密码时必填（可选）
 * @property {string} newPassword - 新密码，修改密码时必填（可选）
 */
const UpdateProfileReq = {
  userId: '',
  direction: '',
  position: '',
  email: 'user@example.com',
  currentPassword: '',
  newPassword: ''
}

// ==================== 计时器相关类型 ====================

/**
 * 计时状态 VO
 * @typedef {Object} TimerStatusVO
 * @property {boolean} isTiming - 是否正在计时
 * @property {string} status - 当前状态: RUNNING, STOPPED
 * @property {number} weekTotalSeconds - 本周总时长（秒）
 * @property {number} totalSeconds - 历史总时长（秒）
 * @property {number} remainingSeconds - 剩余时间（秒）
 */
const TimerStatusVO = {
  isTiming: false,
  status: '',
  weekTotalSeconds: 0,
  totalSeconds: 0,
  remainingSeconds: 0
}

/**
 * 同步工时请求参数
 * @typedef {Object} TimeAddReq
 * @property {string} userId - 用户ID（学号）（必填）
 * @property {number} seconds - 本次增加的秒数，前端固定传60（可选）
 */
const TimeAddReq = {
  userId: '',
  seconds: 60
}

/**
 * 同步工时响应 VO
 * @typedef {Object} TimeAddVO
 * @property {number} addedSeconds - 本次实际增加的秒数
 * @property {number} serverWeekTime - 服务器计算的本周总时长，用于前端校准
 */
const TimeAddVO = {
  addedSeconds: 0,
  serverWeekTime: 0
}

// ==================== 排行榜相关类型 ====================

/**
 * 排行榜条目 VO
 * @typedef {Object} CheckInRankingVO
 * @property {string} name - 姓名
 * @property {string} grade - 年级
 * @property {string} position - 职位
 * @property {string} direction - 方向
 * @property {string} avatar - 头像
 * @property {number} weekTime - 本周时间（秒）
 * @property {number} totalTime - 总时间（秒）
 */
const CheckInRankingVO = {
  name: '',
  grade: '',
  position: '',
  direction: '',
  avatar: '',
  weekTime: 0,
  totalTime: 0
}

/**
 * 排行榜其他数据 VO
 * @typedef {Object} CheckInRankingOtherVO
 * @property {number} avgOnlineDuration - 平均周时长（秒）
 * @property {number} weeklyGoalProgress - 平均达标率
 */
const CheckInRankingOtherVO = {
  avgOnlineDuration: 0,
  weeklyGoalProgress: 0
}

/**
 * 处刑榜条目 VO
 * @typedef {Object} PunishmentVO
 * @property {string} name - 姓名
 * @property {string} direction - 方向
 * @property {number} lastWeekSignInTime - 上周打卡时间（秒）
 */
const PunishmentVO = {
  name: '',
  direction: '',
  lastWeekSignInTime: 0
}

// ==================== 通知相关类型 ====================

/**
 * 通知 VO
 * @typedef {Object} NotificationVO
 * @property {string} type - 通知类型
 * @property {string} title - 通知标题
 * @property {string} content - 通知内容
 * @property {string} meetingLocation - 会议地点（可选）
 * @property {string} meetingTime - 会议时间（可选）
 * @property {string} createTime - 创建时间
 */
const NotificationVO = {
  type: '',
  title: '',
  content: '',
  meetingLocation: '',
  meetingTime: '',
  createTime: ''
}

/**
 * 创建通知请求参数
 * @typedef {Object} CreateNotificationsReq
 * @property {string} type - 通知类型（可选）
 * @property {string} title - 通知标题（可选）
 * @property {string} content - 通知内容（可选）
 * @property {string} meetingLocation - 会议地点（可选）
 * @property {string} meetingTime - 会议时间（可选）
 */
const CreateNotificationsReq = {
  type: '',
  title: '',
  content: '',
  meetingLocation: '',
  meetingTime: ''
}

// ==================== 管理员相关类型 ====================

/**
 * 管理员登录请求参数
 * @typedef {Object} AdminLoginReq
 * @property {string} username - 用户名
 * @property {string} password - 密码
 */
const AdminLoginReq = {
  username: '',
  password: ''
}

// ==================== 导出 ====================

export {
  // 通用响应
  ResultVoid,
  ResultString,
  ResultInteger,
  ResultBoolean,
  
  // 认证
  RegisterReq,
  LoginReq,
  ResetPasswordReq,
  
  // 用户
  UserInfoVO,
  UpdateProfileReq,
  
  // 计时器
  TimerStatusVO,
  TimeAddReq,
  TimeAddVO,
  
  // 排行榜
  CheckInRankingVO,
  CheckInRankingOtherVO,
  PunishmentVO,
  
  // 通知
  NotificationVO,
  CreateNotificationsReq,
  
  // 管理员
  AdminLoginReq
}
