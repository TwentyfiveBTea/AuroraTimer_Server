import { createRouter, createWebHashHistory } from 'vue-router'

// 路由配置
const routes = [
  // 认证页面
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/LoginView.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/RegisterView.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('@/views/auth/ForgotPasswordView.vue'),
    meta: { requiresAuth: false }
  },

  // 管理员登录页面
  {
    path: '/admin-login',
    name: 'AdminLogin',
    component: () => import('@/views/auth/AdminLoginView.vue'),
    meta: { requiresAuth: false }
  },

  // 管理员后台页面
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/views/admin/AdminView.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  
  // 主布局页面（需要认证）
  {
    path: '/',
    component: () => import('@/views/layout/MainLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      // 主仪表盘
      {
        path: '',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/DashboardView.vue')
      },
      
      // 设置中心
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('@/views/settings/SettingsView.vue')
      },
      
      // 通知中心
      {
        path: 'notifications',
        name: 'Notifications',
        component: () => import('@/views/notifications/NotificationsView.vue')
      },
      
      // 排行榜
      {
        path: 'leaderboard',
        name: 'Leaderboard',
        component: () => import('@/views/leaderboard/LeaderboardView.vue')
      },
      
      // 个人资料
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/ProfileView.vue')
      }
    ]
  },
  
  // 404 页面
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFoundView.vue')
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('auth_token')
  const adminToken = localStorage.getItem('admin_token')

  if (to.meta.requiresAuth && !token) {
    // 需要认证但未登录，跳转到登录页
    next({ name: 'Login', query: { redirect: to.fullPath } })
  } else if (to.meta.requiresAdmin && !adminToken) {
    // 需要管理员权限但未登录，跳转到管理员登录页
    next({ name: 'AdminLogin' })
  } else if ((to.name === 'Login' || to.name === 'Register') && token) {
    // 已登录但访问登录/注册页，跳转到首页
    next({ name: 'Dashboard' })
  } else {
    next()
  }
})

export default router
