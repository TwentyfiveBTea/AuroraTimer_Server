<template>
  <div class="settings-page">
    <!-- Settings Panel - 左侧面板 -->
    <aside class="settings-panel">
        <div class="settings-panel__header">
          <h3 class="settings-panel__title">设置中心</h3>
        </div>
        
        <div class="settings-panel__content">
          <!-- 个性化分组 -->
          <div class="settings-group">
            <h4 class="settings-group__header">
              <span class="material-symbols-outlined settings-group__icon">palette</span>
              个性化
            </h4>
            <div class="settings-group__items">
                        <button class="settings-item" :class="{ 'settings-item--active': activeSetting === 'theme' }"
                            @click="activeSetting = 'theme'">
                <span>外观主题</span>
                <span class="material-symbols-outlined settings-item__arrow">chevron_right</span>
              </button>
                        <button class="settings-item" :class="{ 'settings-item--active': activeSetting === 'font' }"
                            @click="activeSetting = 'font'">
                <span>字体设置</span>
                <span class="material-symbols-outlined settings-item__arrow">chevron_right</span>
              </button>
            </div>
          </div>
          
          <div class="settings-divider"></div>
          
          <!-- 系统偏好分组 -->
          <div class="settings-group">
            <h4 class="settings-group__header">
              <span class="material-symbols-outlined settings-group__icon">tune</span>
              系统偏好
            </h4>
            <div class="settings-group__items">
              <!-- 开机自启 -->
              <div class="toggle-row">
                <div class="toggle-row__info">
                  <span class="toggle-row__label">开机自启</span>
                  <span class="toggle-row__hint">随系统自动运行</span>
                </div>
                <label class="toggle-switch">
                                <input type="checkbox" v-model="systemSettings.autoStart"
                                    class="toggle-switch__input" />
                  <span class="toggle-switch__slider"></span>
                </label>
              </div>
              
              <!-- 同步服务器 -->
              <div class="input-row">
                <label class="input-row__label" for="api-url">同步服务器</label>
                            <input id="api-url" type="text" v-model="systemSettings.apiUrl" class="input-row__field"
                                placeholder="aurrlab.top" />
              </div>
            </div>
          </div>
          
          <!-- 数据管理 -->
          <div class="data-management">
            <button class="data-management__btn data-management__btn--admin" @click="handleAdminLogin">
              <span class="material-symbols-outlined data-management__icon">admin_panel_settings</span>
              管理员入口
            </button>
            <button class="data-management__btn" @click="handleLogout">
              <span class="material-symbols-outlined data-management__icon">logout</span>
              退出登录
            </button>
          </div>
        </div>
        
        <div class="settings-panel__footer">
          <span class="settings-panel__version">V1.0.1</span>
          <span class="settings-panel__edition">AuroraTimer</span>
        </div>
      </aside>
      
      <!-- Main Content -->
      <main class="settings-content">
        <header class="settings-content__header">
          <div class="settings-content__title-area">
            <h1 class="settings-content__title">
                        {{ activeSetting === 'theme' ? '主题' : '字体设置' }}
            </h1>
                    <p class="settings-content__subtitle">{{ activeSetting === 'theme' ? '挑选你喜欢的计时器主题' : '选择你喜欢的的字体样式'
                        }}</p>
          </div>
        </header>
        
        <div class="settings-content__body">
          <!-- Theme Grid -->
                <div v-if="activeSetting === 'theme'" class="theme-grid">
            <!-- Aurora Classic (选中态) -->
                    <div class="theme-card"
              :class="{ 'theme-card--selected': currentTheme === 'classic', 'theme-card--active': currentTheme === 'classic' }"
                        @click="selectTheme('classic')">
              <div v-if="currentTheme === 'classic'" class="theme-card__check">
                <span class="material-symbols-outlined theme-card__check-icon">check</span>
              </div>
              <div class="theme-card__header">
                <h4 class="theme-card__name">Aurora Classic</h4>
              </div>
              <div class="theme-card__colors">
                <div class="theme-card__swatch theme-card__swatch--primary"></div>
                <div class="theme-card__swatch theme-card__swatch--panel"></div>
                <div class="theme-card__swatch theme-card__swatch--muted"></div>
              </div>
            </div>
            
            <!-- Minimalist -->
                    <div class="theme-card theme-card--minimalist"
              :class="{ 'theme-card--active': currentTheme === 'minimalist' }"
                        @click="selectTheme('minimalist')">
              <div class="theme-card__header">
                <h4 class="theme-card__name">Minimalist</h4>
              </div>
              <div class="theme-card__colors">
                <div class="theme-card__swatch theme-card__swatch--primary"></div>
                <div class="theme-card__swatch theme-card__swatch--panel"></div>
                <div class="theme-card__swatch theme-card__swatch--muted"></div>
              </div>
            </div>
            
            <!-- Forest -->
                    <div class="theme-card theme-card--forest"
                        :class="{ 'theme-card--active': currentTheme === 'forest' }" @click="selectTheme('forest')">
              <div class="theme-card__header">
                <h4 class="theme-card__name">Forest</h4>
              </div>
              <div class="theme-card__colors">
                <div class="theme-card__swatch theme-card__swatch--primary"></div>
                <div class="theme-card__swatch theme-card__swatch--panel"></div>
                <div class="theme-card__swatch theme-card__swatch--muted"></div>
              </div>
            </div>
            
            <!-- Valentine -->
                    <div class="theme-card theme-card--valentine"
              :class="{ 'theme-card--active': currentTheme === 'valentine' }"
                        @click="selectTheme('valentine')">
              <div class="theme-card__header">
                <h4 class="theme-card__name">Valentine</h4>
              </div>
              <div class="theme-card__colors">
                <div class="theme-card__swatch theme-card__swatch--primary"></div>
                <div class="theme-card__swatch theme-card__swatch--panel"></div>
                <div class="theme-card__swatch theme-card__swatch--muted"></div>
              </div>
            </div>
            
            <!-- Emerald -->
                    <div class="theme-card theme-card--emerald"
                        :class="{ 'theme-card--active': currentTheme === 'emerald' }" @click="selectTheme('emerald')">
              <div class="theme-card__header">
                <h4 class="theme-card__name">Emerald</h4>
              </div>
              <div class="theme-card__colors">
                <div class="theme-card__swatch theme-card__swatch--primary"></div>
                <div class="theme-card__swatch theme-card__swatch--panel"></div>
                <div class="theme-card__swatch theme-card__swatch--muted"></div>
              </div>
            </div>
            
            <!-- Autumn Warm -->
                    <div class="theme-card theme-card--autumn"
                        :class="{ 'theme-card--active': currentTheme === 'autumn' }" @click="selectTheme('autumn')">
              <div class="theme-card__header">
                <h4 class="theme-card__name">Autumn Warm</h4>
              </div>
              <div class="theme-card__colors">
                <div class="theme-card__swatch theme-card__swatch--primary"></div>
                <div class="theme-card__swatch theme-card__swatch--panel"></div>
                <div class="theme-card__swatch theme-card__swatch--muted"></div>
              </div>
            </div>
            
            <!-- Macaron Pink -->
                    <div class="theme-card theme-card--macaron"
                        :class="{ 'theme-card--active': currentTheme === 'macaron' }" @click="selectTheme('macaron')">
              <div class="theme-card__header">
                <h4 class="theme-card__name">Macaron Pink</h4>
              </div>
              <div class="theme-card__colors">
                <div class="theme-card__swatch theme-card__swatch--primary"></div>
                <div class="theme-card__swatch theme-card__swatch--panel"></div>
                <div class="theme-card__swatch theme-card__swatch--muted"></div>
              </div>
            </div>
            
            <!-- Morandi Contrast -->
                    <div class="theme-card theme-card--morandi"
                        :class="{ 'theme-card--active': currentTheme === 'morandi' }" @click="selectTheme('morandi')">
              <div class="theme-card__header">
                <h4 class="theme-card__name">Morandi</h4>
              </div>
              <div class="theme-card__colors">
                <div class="theme-card__swatch theme-card__swatch--primary"></div>
                <div class="theme-card__swatch theme-card__swatch--panel"></div>
                <div class="theme-card__swatch theme-card__swatch--muted"></div>
              </div>
            </div>
            
            <!-- Deep Blue -->
                    <div class="theme-card theme-card--deepblue"
                        :class="{ 'theme-card--active': currentTheme === 'deepblue' }" @click="selectTheme('deepblue')">
              <div class="theme-card__header">
                <h4 class="theme-card__name">Deep Blue</h4>
              </div>
              <div class="theme-card__colors">
                <div class="theme-card__swatch theme-card__swatch--primary"></div>
                <div class="theme-card__swatch theme-card__swatch--panel"></div>
                <div class="theme-card__swatch theme-card__swatch--muted"></div>
                        </div>
                    </div>
                </div>

                <!-- Font Grid -->
            <div v-if="activeSetting === 'font'" class="font-grid">
                <!-- System Default -->
                <div class="font-card" :class="{ 'font-card--selected': currentFont === 'system-ui' }"
                    @click="selectFont('system-ui')">
                    <div v-if="currentFont === 'system-ui'" class="font-card__check">
                        <span class="material-symbols-outlined font-card__check-icon">check</span>
                    </div>
                    <div class="font-card__preview"
                        style="font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;">
                        <span class="font-card__name">系统默认</span>
                        <span class="font-card__sample">Aa Bb Cc 123</span>
                    </div>
                </div>

                <!-- PingFang -->
                <div class="font-card" :class="{ 'font-card--selected': currentFont === 'pingfang' }"
                    @click="selectFont('pingfang')">
                    <div v-if="currentFont === 'pingfang'" class="font-card__check">
                        <span class="material-symbols-outlined font-card__check-icon">check</span>
                    </div>
                    <div class="font-card__preview"
                        style="font-family: 'PingFang SC', '苹方-简', 'Microsoft YaHei', sans-serif;">
                        <span class="font-card__name">苹方字体</span>
                        <span class="font-card__sample">Aa Bb Cc 123</span>
                    </div>
                </div>

                <!-- Alibaba -->
                <div class="font-card" :class="{ 'font-card--selected': currentFont === 'alibaba' }"
                    @click="selectFont('alibaba')">
                    <div v-if="currentFont === 'alibaba'" class="font-card__check">
                        <span class="material-symbols-outlined font-card__check-icon">check</span>
                    </div>
                    <div class="font-card__preview"
                        style="font-family: 'AlibabaPuHuiTi', '阿里巴巴普惠体', 'Microsoft YaHei', sans-serif;">
                        <span class="font-card__name">阿里巴巴普惠体</span>
                        <span class="font-card__sample">Aa Bb Cc 123</span>
                    </div>
                </div>

                <!-- Source Han Sans -->
                <div class="font-card" :class="{ 'font-card--selected': currentFont === 'source-han' }"
                    @click="selectFont('source-han')">
                    <div v-if="currentFont === 'source-han'" class="font-card__check">
                        <span class="material-symbols-outlined font-card__check-icon">check</span>
                    </div>
                    <div class="font-card__preview"
                        style="font-family: 'Source Han Sans', 'Noto Sans SC', '思源黑体', 'Microsoft YaHei', sans-serif;">
                        <span class="font-card__name">思源黑体</span>
                        <span class="font-card__sample">Aa Bb Cc 123</span>
                    </div>
                </div>

                <!-- FZHei -->
                <div class="font-card" :class="{ 'font-card--selected': currentFont === 'fangzheng' }"
                    @click="selectFont('fangzheng')">
                    <div v-if="currentFont === 'fangzheng'" class="font-card__check">
                        <span class="material-symbols-outlined font-card__check-icon">check</span>
                    </div>
                    <div class="font-card__preview"
                        style="font-family: 'FZHei', '方正黑体', 'Microsoft YaHei', sans-serif;">
                        <span class="font-card__name">方正黑体</span>
                        <span class="font-card__sample">Aa Bb Cc 123</span>
                    </div>
                </div>

                <!-- Droid Sans -->
                <div class="font-card" :class="{ 'font-card--selected': currentFont === 'droid-sans' }"
                    @click="selectFont('droid-sans')">
                    <div v-if="currentFont === 'droid-sans'" class="font-card__check">
                        <span class="material-symbols-outlined font-card__check-icon">check</span>
                    </div>
                    <div class="font-card__preview"
                        style="font-family: 'Droid Sans', 'Roboto', 'Helvetica Neue', Arial, sans-serif;">
                        <span class="font-card__name">Droid Sans</span>
                        <span class="font-card__sample">Aa Bb Cc 123</span>
                    </div>
                </div>

                <!-- Noto Sans -->
                <div class="font-card" :class="{ 'font-card--selected': currentFont === 'noto-sans' }"
                    @click="selectFont('noto-sans')">
                    <div v-if="currentFont === 'noto-sans'" class="font-card__check">
                        <span class="material-symbols-outlined font-card__check-icon">check</span>
                    </div>
                    <div class="font-card__preview"
                        style="font-family: 'Noto Sans', 'Roboto', 'Helvetica Neue', Arial, sans-serif;">
                        <span class="font-card__name">Noto Sans</span>
                        <span class="font-card__sample">Aa Bb Cc 123</span>
                    </div>
                </div>

                <!-- Inter -->
                <div class="font-card" :class="{ 'font-card--selected': currentFont === 'inter' }"
                    @click="selectFont('inter')">
                    <div v-if="currentFont === 'inter'" class="font-card__check">
                        <span class="material-symbols-outlined font-card__check-icon">check</span>
                    </div>
                    <div class="font-card__preview"
                        style="font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;">
                        <span class="font-card__name">Inter</span>
                        <span class="font-card__sample">Aa Bb Cc 123</span>
                    </div>
                </div>

                <!-- Monaco -->
                <div class="font-card" :class="{ 'font-card--selected': currentFont === 'monaco' }"
                    @click="selectFont('monaco')">
                    <div v-if="currentFont === 'monaco'" class="font-card__check">
                        <span class="material-symbols-outlined font-card__check-icon">check</span>
                    </div>
                    <div class="font-card__preview"
                        style="font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', 'Consolas', 'Source Code Pro', monospace;">
                        <span class="font-card__name">Monaco</span>
                        <span class="font-card__sample">Aa Bb Cc 123</span>
                    </div>
                </div>

                <!-- SF Mono -->
                <div class="font-card" :class="{ 'font-card--selected': currentFont === 'sf-mono' }"
                    @click="selectFont('sf-mono')">
                    <div v-if="currentFont === 'sf-mono'" class="font-card__check">
                        <span class="material-symbols-outlined font-card__check-icon">check</span>
                    </div>
                    <div class="font-card__preview"
                        style="font-family: 'SF Mono', 'Monaco', 'Menlo', 'Consolas', monospace;">
                        <span class="font-card__name">SF Mono</span>
                        <span class="font-card__sample">Aa Bb Cc 123</span>
              </div>
            </div>
          </div>
        </div>
      </main>
  </div>
</template>

<script setup>
import { ref, reactive, watch, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useThemeStore } from '@/stores/theme'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const themeStore = useThemeStore()
const authStore = useAuthStore()

const currentTheme = ref(themeStore.currentTheme || 'classic')
const currentFont = ref(themeStore.currentFont || 'system-ui')

const activeSetting = ref('theme')

const systemSettings = reactive({
  autoStart: false,
  apiUrl: 'aurrlab.top'
})

// 初始化开机自启状态
onMounted(async () => {
    if (window.electronAPI && window.electronAPI.getAutoStart) {
        systemSettings.autoStart = await window.electronAPI.getAutoStart()
    }
})

// 监听开机自启设置变化
watch(() => systemSettings.autoStart, (newValue) => {
    if (window.electronAPI && window.electronAPI.setAutoStart) {
        window.electronAPI.setAutoStart(newValue)
        console.log('Auto start set to:', newValue)
    }
})

// 获取分组后的主题
const originalThemes = computed(() => themeStore.getThemesByGroup('original'))
const warmThemes = computed(() => themeStore.getThemesByGroup('warm'))
const macaronThemes = computed(() => themeStore.getThemesByGroup('macaron'))
const morandiThemes = computed(() => themeStore.getThemesByGroup('morandi'))
const deepblueThemes = computed(() => themeStore.getThemesByGroup('deepblue'))

function selectTheme(themeId) {
  currentTheme.value = themeId
  themeStore.setTheme(themeId)
  console.log('Theme changed to:', themeId)
}

function selectFont(fontId) {
    currentFont.value = fontId
    themeStore.setFont(fontId)
    console.log('Font changed to:', fontId)
}

function handleLogout() {
    authStore.logout()
    router.push('/login')
}

function handleAdminLogin() {
    router.push('/admin-login')
}

watch(currentTheme, (newTheme) => {
  console.log('Current theme:', newTheme)
})
</script>

<style scoped>
/* Settings Page Layout */
.settings-page {
  display: flex;
  flex: 1;
  gap: var(--spacing-md);
  height: 100%;
  background-color: var(--color-bg-base);
  border-radius: var(--radius-xl);
  overflow: hidden;
}

/* Settings Panel - 左侧面板 */
.settings-panel {
  width: 280px;
  background-color: var(--color-bg-panel);
  border-radius: var(--radius-xl);
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--color-border);
  overflow: hidden;
}

.settings-panel__header {
  padding: var(--spacing-lg) var(--spacing-xl);
  border-bottom: 1px solid var(--color-border);
}

.settings-panel__title {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-text-main);
  margin: 0;
}

.settings-panel__content {
  flex: 1;
  overflow-y: auto;
  padding: var(--spacing-lg);
}

.settings-group {
  margin-bottom: var(--spacing-lg);
}

.settings-group__header {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text-secondary);
  margin: 0 0 var(--spacing-md);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.settings-group__icon {
  font-size: 20px;
  color: var(--color-text-muted);
}

.settings-group__items {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.settings-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: var(--radius-md);
  background-color: transparent;
  border: none;
  cursor: pointer;
  transition: all var(--transition-fast);
  text-align: left;
  font-size: 14px;
  color: var(--color-text-secondary);
}

.settings-item:hover {
  background-color: rgba(0, 0, 0, 0.03);
  color: var(--color-text-main);
}

.settings-item--active {
  background-color: rgba(223, 164, 115, 0.1);
  color: var(--color-primary);
}

.settings-item__arrow {
  font-size: 18px;
  color: var(--color-text-muted);
}

.settings-divider {
  height: 1px;
  background-color: var(--color-border);
  margin: var(--spacing-lg) 0;
}

/* Toggle Row */
.toggle-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-sm) 0;
}

.toggle-row__info {
  display: flex;
  flex-direction: column;
}

.toggle-row__label {
  font-size: 14px;
  color: var(--color-text-main);
  font-weight: 500;
}

.toggle-row__hint {
  font-size: 12px;
  color: var(--color-text-muted);
  margin-top: 2px;
}

.toggle-switch {
  position: relative;
  width: 44px;
  height: 24px;
  cursor: pointer;
}

.toggle-switch__input {
  opacity: 0;
  width: 0;
  height: 0;
}

.toggle-switch__slider {
  position: absolute;
  inset: 0;
  background-color: var(--color-border);
  border-radius: 24px;
  transition: all var(--transition-fast);
}

.toggle-switch__slider::before {
  content: '';
  position: absolute;
  width: 20px;
  height: 20px;
  left: 2px;
  top: 2px;
  background-color: white;
  border-radius: 50%;
  transition: all var(--transition-fast);
  box-shadow: var(--shadow-sm);
}

.toggle-switch__input:checked+.toggle-switch__slider {
  background-color: var(--color-primary);
}

.toggle-switch__input:checked+.toggle-switch__slider::before {
  transform: translateX(20px);
}

/* Input Row */
.input-row {
  margin-top: var(--spacing-md);
}

.input-row__label {
  display: block;
  font-size: 14px;
  color: var(--color-text-main);
  font-weight: 500;
  margin-bottom: var(--spacing-sm);
}

.input-row__field {
  width: 100%;
  padding: var(--spacing-sm) var(--spacing-md);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  font-size: 14px;
  color: var(--color-text-main);
  background-color: var(--color-bg-panel);
  outline: none;
  transition: all var(--transition-fast);
}

.input-row__field:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(223, 164, 115, 0.1);
}

.input-row__field::placeholder {
  color: var(--color-text-muted);
}

/* Data Management */
.data-management {
  margin-top: var(--spacing-lg);
}

.data-management {
  background-color: var(--color-bg-base);
  border-radius: var(--radius-lg);
  padding: var(--spacing-md);
    margin-top: var(--spacing-md);
}

.data-management__label {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text-main);
  margin: 0 0 var(--spacing-sm);
}

.data-management__btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  width: 100%;
  padding: var(--spacing-sm) var(--spacing-md);
  background-color: var(--color-bg-panel);
    border: 2px solid #e74c3c;
  border-radius: var(--radius-md);
  font-size: 14px;
    font-weight: 600;
    color: #e74c3c;
  cursor: pointer;
  transition: all var(--transition-fast);
    position: relative;
    z-index: 10;
}

.data-management__btn:hover {
    background-color: #e74c3c;
    color: white;
}

.data-management__btn--admin {
    margin-bottom: var(--spacing-sm);
    border-color: var(--color-primary);
    color: var(--color-primary);
}

.data-management__btn--admin:hover {
    background-color: var(--color-primary);
    color: white;
}

.data-management__icon {
  font-size: 18px;
}

/* Sidebar Footer */
.settings-panel__footer {
  padding: var(--spacing-lg);
  border-top: 1px solid var(--color-border);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-xs);
}

.settings-panel__version {
  font-size: 12px;
  font-weight: 600;
  color: var(--color-text-main);
}

.settings-panel__edition {
  font-size: 11px;
  color: var(--color-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

/* Main Content */
.settings-content {
  flex: 1;
  background-color: var(--color-bg-card);
  border-radius: var(--radius-xl);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--color-border);
}

.settings-content__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
    padding: var(--spacing-md) var(--spacing-xl);
  border-bottom: 1px solid var(--color-border);
  flex-shrink: 0;
}

.settings-content__title-area {
  display: flex;
  flex-direction: column;
}

.settings-content__title {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text-main);
  margin: 0;
}

.settings-content__badge {
  padding: 4px 10px;
  background: linear-gradient(135deg, var(--color-primary), #E0935C);
  color: white;
  font-size: 11px;
  font-weight: 600;
  border-radius: 9999px;
}

.settings-content__subtitle {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin: var(--spacing-xs) 0 0;
}

.settings-content__user {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.settings-content__user-info {
  text-align: right;
}

.settings-content__user-name {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text-main);
}

.settings-content__user-role {
  display: block;
  font-size: 12px;
  color: var(--color-text-muted);
}

.settings-content__avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
    border: 2px solid var(--color-bg-panel);
  box-shadow: var(--shadow-sm);
}

/* Settings Content Body */
.settings-content__body {
  flex: 1;
  overflow-y: auto;
    padding: 20px var(--spacing-xl) var(--spacing-xl) var(--spacing-xl);
}

/* Theme Grid */
.theme-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: var(--spacing-lg);
}

.theme-card {
  background-color: var(--color-bg-card);
  border-radius: var(--radius-lg);
  padding: var(--spacing-lg);
  cursor: pointer;
  transition: all var(--transition-fast);
  border: 2px solid transparent;
  position: relative;
}

.theme-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.theme-card--active {
  border-color: var(--color-primary);
}

.theme-card--selected {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 1px var(--color-primary);
}

.theme-card__check {
  position: absolute;
  top: -8px;
  right: -8px;
  width: 24px;
  height: 24px;
  background-color: var(--color-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  z-index: 10;
}

.theme-card__check-icon {
  font-size: 14px;
  color: white;
}

.theme-card__header {
  margin-bottom: var(--spacing-md);
}

.theme-card__name {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-main);
  margin: 0;
}

.theme-card__colors {
  display: flex;
  gap: var(--spacing-sm);
}

.theme-card__swatch {
  width: 32px;
  height: 32px;
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
}

/* Theme Colors - Classic (默认) */
.theme-card:not([class*="--minimalist"]):not([class*="--cyberpunk"]):not([class*="--forest"]):not([class*="--valentine"]):not([class*="--synthwave"]):not([class*="--emerald"]):not([class*="--autumn"]):not([class*="--macaron"]):not([class*="--morandi"]):not([class*="--deepblue"]) .theme-card__swatch--primary {
  background-color: #DFA473;
}

.theme-card:not([class*="--minimalist"]):not([class*="--cyberpunk"]):not([class*="--forest"]):not([class*="--valentine"]):not([class*="--synthwave"]):not([class*="--emerald"]):not([class*="--autumn"]):not([class*="--macaron"]):not([class*="--morandi"]):not([class*="--deepblue"]) .theme-card__swatch--panel {
  background-color: #FEFCF9;
}

.theme-card:not([class*="--minimalist"]):not([class*="--cyberpunk"]):not([class*="--forest"]):not([class*="--valentine"]):not([class*="--synthwave"]):not([class*="--emerald"]):not([class*="--autumn"]):not([class*="--macaron"]):not([class*="--morandi"]):not([class*="--deepblue"]) .theme-card__swatch--muted {
  background-color: #8C847C;
}

/* Minimalist Theme */
.theme-card--minimalist .theme-card__swatch--primary {
  background-color: #333333;
}

.theme-card--minimalist .theme-card__swatch--panel {
  background-color: #FFFFFF;
}

.theme-card--minimalist .theme-card__swatch--muted {
  background-color: #999999;
}

/* Forest Theme */
.theme-card--forest .theme-card__swatch--primary {
  background-color: #A3B18A;
}

.theme-card--forest .theme-card__swatch--panel {
  background-color: #3D5A4C;
}

.theme-card--forest .theme-card__swatch--muted {
  background-color: #A8A8A8;
}

/* Valentine Theme */
.theme-card--valentine .theme-card__swatch--primary {
  background-color: #E96D7B;
}

.theme-card--valentine .theme-card__swatch--panel {
  background-color: #FFFFFF;
}

.theme-card--valentine .theme-card__swatch--muted {
  background-color: #A890A0;
}

/* Emerald Theme */
.theme-card--emerald .theme-card__swatch--primary {
  background-color: #66CC8A;
}

.theme-card--emerald .theme-card__swatch--panel {
  background-color: #F8FAFC;
}

.theme-card--emerald .theme-card__swatch--muted {
  background-color: #8899A6;
}

/* Autumn Warm Theme */
.theme-card--autumn .theme-card__swatch--primary {
  background-color: #F6A885;
}

.theme-card--autumn .theme-card__swatch--panel {
  background-color: #F8C8A0;
}

.theme-card--autumn .theme-card__swatch--muted {
  background-color: #B1C1A1;
}

/* Macaron Pink Theme */
.theme-card--macaron .theme-card__swatch--primary {
  background-color: #946EAF;
}

.theme-card--macaron .theme-card__swatch--panel {
  background-color: #EBEBD3;
}

.theme-card--macaron .theme-card__swatch--muted {
  background-color: #D694B3;
}

/* Morandi Theme */
.theme-card--morandi .theme-card__swatch--primary {
  background-color: #E77141;
}

.theme-card--morandi .theme-card__swatch--panel {
  background-color: #F2C2B1;
}

.theme-card--morandi .theme-card__swatch--muted {
  background-color: #9099C5;
}

/* Deep Blue Theme */
.theme-card--deepblue .theme-card__swatch--primary {
  background-color: #35549E;
}

.theme-card--deepblue .theme-card__swatch--panel {
  background-color: #F9D0DF;
}

.theme-card--deepblue .theme-card__swatch--muted {
  background-color: #A9BAB1;
}

/* Font Grid */
.font-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
    gap: var(--spacing-lg);
}

.font-card {
    background-color: var(--color-bg-panel);
    border-radius: var(--radius-lg);
    padding: var(--spacing-lg);
    cursor: pointer;
    transition: all var(--transition-fast);
    position: relative;
}

.font-card:hover {
    border-color: var(--color-primary);
    transform: translateY(-2px);
    box-shadow: var(--shadow-md);
}

.font-card--selected {
    border-color: var(--color-primary);
    box-shadow: 0 0 0 1px var(--color-primary);
}

.font-card__check {
    position: absolute;
    top: -8px;
    right: -8px;
    width: 24px;
    height: 24px;
    background-color: var(--color-primary);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
    z-index: 10;
}

.font-card__check-icon {
    font-size: 14px;
    color: var(--color-bg-panel);
}

.font-card__preview {
    display: flex;
    flex-direction: column;
    gap: var(--spacing-xs);
}

.font-card__name {
    font-size: 14px;
    font-weight: 600;
    color: var(--color-text-main);
}

.font-card__sample {
    font-size: 16px;
    color: var(--color-text-secondary);
    letter-spacing: 0.05em;
}

/* Responsive */
@media (max-width: 1024px) {
  .settings-page {
    flex-direction: column;
  }
  
  .settings-panel {
    width: 100%;
    max-height: 300px;
  }
  
  .settings-content {
    min-height: 400px;
  }
}

@media (max-width: 768px) {
  .theme-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: var(--spacing-md);
  }
  
  .settings-content__header {
    flex-direction: column;
    gap: var(--spacing-md);
    align-items: flex-start;
  }
  
  .settings-content__user {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
