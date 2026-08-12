<template>
  <Teleport to="body">
    <!-- 遮罩层 -->
    <div class="avatar-cropper__overlay" @click.self="handleCancel">
      <div class="avatar-cropper__modal">
        <!-- 标题栏 -->
        <div class="avatar-cropper__header">
          <h3 class="avatar-cropper__title">修改头像</h3>
          <button class="avatar-cropper__close" @click="handleCancel">
            <span class="material-symbols-outlined">close</span>
          </button>
        </div>

        <!-- 上传区域 -->
        <div v-if="!imageSrc" class="avatar-cropper__upload">
          <input
            ref="fileInput"
            type="file"
            accept="image/*"
            class="avatar-cropper__file-input"
            @change="handleFileSelect"
          />
          <div class="avatar-cropper__upload-content">
            <span class="material-symbols-outlined avatar-cropper__upload-icon">cloud_upload</span>
            <p class="avatar-cropper__upload-text">点击或拖拽图片到这里</p>
            <p class="avatar-cropper__upload-hint">支持 JPG、PNG 格式</p>
          </div>
        </div>

        <!-- 裁剪区域 -->
        <div v-else class="avatar-cropper__content">
          <!-- 左侧：原始图片 + 裁剪框 -->
          <div class="avatar-cropper__canvas-container">
            <div class="avatar-cropper__canvas-wrapper" ref="canvasWrapper">
              <img
                ref="imageRef"
                :src="imageSrc"
                class="avatar-cropper__source-image"
                alt="Source"
                @load="handleImageLoad"
                @mousedown="startDrag"
                @mousemove="handleDrag"
                @mouseup="stopDrag"
                @mouseleave="stopDrag"
              />
              <!-- 裁剪遮罩 -->
              <div class="avatar-cropper__crop-overlay">
                <div 
                  class="avatar-cropper__crop-circle" 
                  :style="cropCircleStyle"
                  @mousedown="startCropDrag"
                  @mousemove="handleCropDrag"
                  @mouseup="stopCropDrag"
                  @mouseleave="stopCropDrag"
                >
                  <div class="avatar-cropper__crop-lines">
                    <div class="avatar-cropper__crop-line avatar-cropper__crop-line--h"></div>
                    <div class="avatar-cropper__crop-line avatar-cropper__crop-line--v"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 右侧：预览和操作 -->
          <div class="avatar-cropper__sidebar">
            <!-- 预览区域 -->
            <div class="avatar-cropper__preview-section">
              <p class="avatar-cropper__preview-label">预览</p>
              <div class="avatar-cropper__preview-container">
                <div class="avatar-cropper__preview-circle">
                  <img v-if="croppedImage" :src="croppedImage" class="avatar-cropper__preview-img" alt="Preview" />
                </div>
              </div>
              <p class="avatar-cropper__preview-hint">圆形头像效果</p>
            </div>

            <!-- 大小调节 -->
            <div class="avatar-cropper__size-section">
              <p class="avatar-cropper__size-label">头像大小</p>
              <div class="avatar-cropper__size-controls">
                <span class="material-symbols-outlined avatar-cropper__size-icon">remove_circle</span>
                <input
                  type="range"
                  v-model="cropSize"
                  :min="minCropSize"
                  :max="maxCropSize"
                  class="avatar-cropper__size-slider"
                />
                <span class="material-symbols-outlined avatar-cropper__size-icon">add_circle</span>
              </div>
            </div>

            <!-- 缩放图片 -->
            <div class="avatar-cropper__zoom-section">
              <p class="avatar-cropper__zoom-label">图片缩放</p>
              <div class="avatar-cropper__zoom-controls">
                <span class="material-symbols-outlined avatar-cropper__zoom-icon">zoom_out</span>
                <input
                  type="range"
                  v-model="zoomLevel"
                  :min="minZoom"
                  :max="maxZoom"
                  step="0.1"
                  class="avatar-cropper__zoom-slider"
                />
                <span class="material-symbols-outlined avatar-cropper__zoom-icon">zoom_in</span>
              </div>
            </div>
            
            <!-- 大小调节 -->
            <div class="avatar-cropper__size-section">
              <p class="avatar-cropper__size-label">头像大小</p>
              <div class="avatar-cropper__size-controls">
                <span class="material-symbols-outlined avatar-cropper__size-icon">remove_circle</span>
                <input
                  type="range"
                  v-model="cropSize"
                  :min="minCropSize"
                  :max="maxCropSize"
                  class="avatar-cropper__size-slider"
                />
                <span class="material-symbols-outlined avatar-cropper__size-icon">add_circle</span>
              </div>
            </div>

            <!-- 缩放图片 -->
            <div class="avatar-cropper__zoom-section">
              <p class="avatar-cropper__zoom-label">图片缩放</p>
              <div class="avatar-cropper__zoom-controls">
                <span class="material-symbols-outlined avatar-cropper__zoom-icon">zoom_out</span>
                <input
                  type="range"
                  v-model="zoomLevel"
                  :min="minZoom"
                  :max="maxZoom"
                  step="0.1"
                  class="avatar-cropper__zoom-slider"
                />
                <span class="material-symbols-outlined avatar-cropper__zoom-icon">zoom_in</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 底部按钮 -->
        <div v-if="imageSrc" class="avatar-cropper__footer">
          <button class="avatar-cropper__btn avatar-cropper__btn--secondary" @click="handleCancel">
            取消
          </button>
          <button class="avatar-cropper__btn avatar-cropper__btn--secondary" @click="handleReset">
            <span class="material-symbols-outlined">refresh</span>
            重置
          </button>
          <button class="avatar-cropper__btn avatar-cropper__btn--primary" @click="handleConfirm">
            <span class="material-symbols-outlined">check</span>
            确定修改
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'

const emit = defineEmits(['close', 'confirm'])

// refs
const fileInput = ref(null)
const imageRef = ref(null)
const canvasWrapper = ref(null)

// 状态
const imageSrc = ref('')
const imageRect = ref({ left: 0, top: 0, width: 0, height: 0 })
const imagePosition = ref({ x: 0, y: 0 })
const isDragging = ref(false)
const dragStart = ref({ x: 0, y: 0 })

// 裁剪框位置（相对于wrapper中心）
const cropPosition = ref({ x: 0, y: 0 })
const isCropDragging = ref(false)
const cropDragStart = ref({ x: 0, y: 0 })

// 裁剪参数
const cropSize = ref(150) // 裁剪框大小（像素）
const zoomLevel = ref(1)
const minCropSize = 80
const maxCropSize = ref(200) // 动态计算，不能超过图片尺寸
const minZoom = 0.5
const maxZoom = 3

// 图片实际尺寸（用于限制裁剪框大小）
const imageNaturalSize = ref({ width: 0, height: 0 })

// 计算属性
const cropCircleStyle = computed(() => {
  const size = cropSize.value * zoomLevel.value
  return {
    width: `${size}px`,
    height: `${size}px`,
    transform: `translate(calc(-50% + ${cropPosition.value.x}px), calc(-50% + ${cropPosition.value.y}px))`
  }
})

// 图片裁剪后的预览图
const croppedImage = computed(() => {
  if (!imageSrc.value || !imageRef.value) return ''
  return generateCroppedImage()
})

// 生成裁剪后的图片
function generateCroppedImage() {
  const img = imageRef.value
  if (!img) return ''

  const canvas = document.createElement('canvas')
  const ctx = canvas.getContext('2d')

  // 设置裁剪后的尺寸（放大2倍以保持清晰度）
  const size = cropSize.value * 2
  canvas.width = size
  canvas.height = size

  const zoom = zoomLevel.value

  // 获取wrapper和图片的尺寸
  const wrapper = canvasWrapper.value
  if (!wrapper) return ''

  const wrapperRect = wrapper.getBoundingClientRect()
  const imgRect = img.getBoundingClientRect()

  // 计算wrapper中心在图片上的位置
  const wrapperCenterX = wrapperRect.width / 2
  const wrapperCenterY = wrapperRect.height / 2

  // 图片当前显示的尺寸
  const displayedWidth = imgRect.width
  const displayedHeight = imgRect.height

  // 计算图片左上角在wrapper中的位置
  const imgLeftInWrapper = imgRect.left - wrapperRect.left
  const imgTopInWrapper = imgRect.top - wrapperRect.top

  // 计算wrapper中心相对于图片左上角的位置
  const relativeX = wrapperCenterX - imgLeftInWrapper + cropPosition.value.x
  const relativeY = wrapperCenterY - imgTopInWrapper + cropPosition.value.y

  // 缩放因子（显示尺寸 / 原始尺寸）
  const scaleX = img.naturalWidth / displayedWidth
  const scaleY = img.naturalHeight / displayedHeight

  // 计算wrapper中心在原始图片上的位置
  const sourceCenterX = relativeX * scaleX
  const sourceCenterY = relativeY * scaleY

  // 计算源图片上的裁剪区域
  const sourceCropHalfSize = (cropSize.value / 2) * zoom * scaleX
  const sourceX = sourceCenterX - sourceCropHalfSize
  const sourceY = sourceCenterY - sourceCropHalfSize
  const sourceSize = sourceCropHalfSize * 2

  // 创建圆形裁剪
  ctx.beginPath()
  ctx.arc(size / 2, size / 2, size / 2, 0, Math.PI * 2)
  ctx.closePath()
  ctx.clip()

  // 绘制图片
  ctx.drawImage(
    img,
    sourceX, sourceY, sourceSize, sourceSize,
    0, 0, size, size
  )

  return canvas.toDataURL('image/png')
}

// 处理文件选择
function handleFileSelect(event) {
  const file = event.target.files[0]
  if (file) {
    const reader = new FileReader()
    reader.onload = (e) => {
      imageSrc.value = e.target.result
      resetState()
    }
    reader.readAsDataURL(file)
  }
}

// 重置状态
function resetState() {
  zoomLevel.value = 1
  cropSize.value = 150
  imagePosition.value = { x: 0, y: 0 }
}

// 图片加载完成
function handleImageLoad(event) {
  const img = event.target
  const wrapper = canvasWrapper.value
  if (!wrapper || !img) return
  
  // 保存图片原始尺寸，用于限制裁剪框大小
  imageNaturalSize.value = {
    width: img.naturalWidth,
    height: img.naturalHeight
  }
  
  // 根据图片尺寸计算最大裁剪框大小
  // 裁剪框不能超过图片的较短边
  const minSide = Math.min(img.naturalWidth, img.naturalHeight)
  // 允许的最大裁剪框尺寸（考虑一定的边距）
  const maxAllowed = Math.floor(minSide * 0.9)
  maxCropSize.value = Math.max(minCropSize, maxAllowed)
  
  // 如果当前裁剪框超过最大值，则调整
  if (cropSize.value > maxCropSize.value) {
    cropSize.value = maxCropSize.value
  }
  
  // 获取容器和图片的尺寸
  const wrapperRect = wrapper.getBoundingClientRect()
  const imgRect = img.getBoundingClientRect()
  
  // 计算图片在容器中的位置
  imageRect.value = {
    left: imgRect.left - wrapperRect.left,
    top: imgRect.top - wrapperRect.top,
    width: imgRect.width,
    height: imgRect.height
  }
  
  // 初始居中
  const containerCenterX = wrapperRect.width / 2
  const containerCenterY = wrapperRect.height / 2
  imagePosition.value = {
    x: containerCenterX - imgRect.width / 2,
    y: containerCenterY - imgRect.height / 2
  }
}

// 拖拽相关
function startDrag(event) {
  isDragging.value = true
  dragStart.value = {
    x: event.clientX - imagePosition.value.x,
    y: event.clientY - imagePosition.value.y
  }
}

function handleDrag(event) {
  if (!isDragging.value) return
  
  const wrapper = canvasWrapper.value
  if (!wrapper) return
  
  const wrapperRect = wrapper.getBoundingClientRect()
  const img = imageRef.value
  if (!img) return
  
  const imgRect = img.getBoundingClientRect()
  
  // 更新图片位置
  imagePosition.value = {
    x: event.clientX - dragStart.value.x,
    y: event.clientY - dragStart.value.y
  }
  
  // 计算新的图片位置
  const newLeft = event.clientX - dragStart.value.x
  const newTop = event.clientY - dragStart.value.y
  
  // 限制拖拽范围（让图片不被完全移出裁剪框）
  const cropHalfSize = cropSize.value / 2 * zoomLevel.value
  const imgCenterX = newLeft + imgRect.width / 2
  const imgCenterY = newTop + imgRect.height / 2
  
  // 计算容器中心
  const containerCenterX = wrapperRect.width / 2
  const containerCenterY = wrapperRect.height / 2
  
  // 限制图片中心在裁剪框周围移动
  const maxOffsetX = cropHalfSize + imgRect.width / 2 - 10
  const maxOffsetY = cropHalfSize + imgRect.height / 2 - 10
  
  imagePosition.value = {
    x: Math.max(containerCenterX - imgRect.width / 2 - maxOffsetX, 
                Math.min(containerCenterX - imgRect.width / 2 + maxOffsetX, newLeft)),
    y: Math.max(containerCenterY - imgRect.height / 2 - maxOffsetY, 
                Math.min(containerCenterY - imgRect.height / 2 + maxOffsetY, newTop))
  }
}

function stopDrag() {
  isDragging.value = false
}

// 裁剪框拖拽相关
function startCropDrag(event) {
  event.preventDefault()
  event.stopPropagation()
  isCropDragging.value = true
  cropDragStart.value = {
    x: event.clientX - cropPosition.value.x,
    y: event.clientY - cropPosition.value.y
  }
}

function handleCropDrag(event) {
  if (!isCropDragging.value) return
  
  const wrapper = canvasWrapper.value
  if (!wrapper) return
  
  const wrapperRect = wrapper.getBoundingClientRect()
  const wrapperCenterX = wrapperRect.width / 2
  const wrapperCenterY = wrapperRect.height / 2
  
  const newX = event.clientX - cropDragStart.value.x
  const newY = event.clientY - cropDragStart.value.y
  
  // 限制裁剪框在图片范围内
  const img = imageRef.value
  if (img) {
    const imgRect = img.getBoundingClientRect()
    const imgLeftInWrapper = imgRect.left - wrapperRect.left
    const imgTopInWrapper = imgRect.top - wrapperRect.top
    
    // 计算图片边缘相对于wrapper中心的位置
    const imgLeft = imgLeftInWrapper - wrapperCenterX
    const imgTop = imgTopInWrapper - wrapperCenterY
    const imgRight = imgLeft + imgRect.width
    const imgBottom = imgTop + imgRect.height
    
    const cropHalfSize = (cropSize.value * zoomLevel.value) / 2
    
    // 限制裁剪框范围
    const maxX = Math.min(cropHalfSize - 10, -imgLeft + cropHalfSize - 10)
    const maxY = Math.min(cropHalfSize - 10, -imgTop + cropHalfSize - 10)
    
    cropPosition.value = {
      x: Math.max(-maxX, Math.min(maxX, newX)),
      y: Math.max(-maxY, Math.min(maxY, newY))
    }
  }
}

function stopCropDrag() {
  isCropDragging.value = false
}

// 重新上传
function handleReupload() {
  fileInput.value?.click()
  imageSrc.value = ''
}

// 重置
function handleReset() {
  resetState()
}

// 取消
function handleCancel() {
  emit('close')
}

// 确认
function handleConfirm() {
  const cropped = generateCroppedImage()
  emit('confirm', cropped)
}

// 监听缩放变化
watch(zoomLevel, () => {
  // 重新计算图片尺寸
  if (imageRef.value) {
    handleImageLoad({ target: imageRef.value })
  }
})

// 监听裁剪框大小变化
watch(cropSize, () => {
  // cropSize 变化会自动触发 croppedImage computed 更新
})
</script>

<style scoped>
/* 遮罩层 */
.avatar-cropper__overlay {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
}

/* 弹窗 */
.avatar-cropper__modal {
  background-color: var(--color-bg-panel, #fff);
  border-radius: var(--radius-xl, 20px);
  width: 90%;
  max-width: 900px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-shadow: var(--shadow-lg, 0 20px 60px rgba(0, 0, 0, 0.3));
}

/* 标题栏 */
.avatar-cropper__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-lg, 20px) var(--spacing-xl, 24px);
  border-bottom: 1px solid var(--color-border, #e0e0e0);
}

.avatar-cropper__title {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text-main, #333);
  margin: 0;
}

.avatar-cropper__close {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-text-secondary, #666);
  transition: all 0.2s ease;
}

.avatar-cropper__close:hover {
  background-color: var(--color-bg-base, #f5f5f5);
  color: var(--color-text-main, #333);
}

.avatar-cropper__close .material-symbols-outlined {
  font-size: 24px;
}

/* 上传区域 */
.avatar-cropper__upload {
  padding: 60px 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.avatar-cropper__file-input {
  position: absolute;
  width: 100%;
  height: 100%;
  opacity: 0;
  cursor: pointer;
}

.avatar-cropper__upload-content {
  text-align: center;
  padding: 60px 80px;
  border: 2px dashed var(--color-border, #ccc);
  border-radius: var(--radius-xl, 20px);
  cursor: pointer;
  transition: all 0.3s ease;
}

.avatar-cropper__upload-content:hover {
  border-color: var(--color-primary, #dfa473);
  background-color: rgba(223, 164, 115, 0.05);
}

.avatar-cropper__upload-icon {
  font-size: 64px;
  color: var(--color-text-secondary, #999);
  margin-bottom: var(--spacing-md, 16px);
}

.avatar-cropper__upload-text {
  font-size: 16px;
  font-weight: 500;
  color: var(--color-text-main, #333);
  margin: 0 0 var(--spacing-sm, 8px);
}

.avatar-cropper__upload-hint {
  font-size: 14px;
  color: var(--color-text-secondary, #999);
  margin: 0;
}

/* 裁剪内容区 */
.avatar-cropper__content {
  display: flex;
  flex: 1;
  overflow: hidden;
  min-height: 400px;
}

/* 左侧画布区 */
.avatar-cropper__canvas-container {
  flex: 1;
  background-color: var(--color-bg-base, #f5f5f5);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xl, 24px);
  overflow: hidden;
}

.avatar-cropper__canvas-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  max-width: 500px;
  max-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-cropper__source-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  cursor: move;
  user-select: none;
}

/* 裁剪遮罩 */
.avatar-cropper__crop-overlay {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.avatar-cropper__crop-circle {
  position: absolute;
  top: 50%;
  left: 50%;
  border: 2px solid white;
  box-shadow: 0 0 0 9999px rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  cursor: move;
  pointer-events: auto;
  transition: transform 0.05s ease-out;
}

.avatar-cropper__crop-circle--square {
  border-radius: 8px;
}

.avatar-cropper__crop-lines {
  position: absolute;
  inset: 0;
  opacity: 0.3;
}

.avatar-cropper__crop-line {
  position: absolute;
  background-color: white;
}

.avatar-cropper__crop-line--h {
  width: 100%;
  height: 1px;
  top: 50%;
  transform: translateY(-50%);
}

.avatar-cropper__crop-line--v {
  width: 1px;
  height: 100%;
  left: 50%;
  transform: translateX(-50%);
}

/* 右侧边栏 */
.avatar-cropper__sidebar {
  width: 240px;
  padding: var(--spacing-xl, 24px);
  background-color: var(--color-bg-panel, #fff);
  border-left: 1px solid var(--color-border, #e0e0e0);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg, 20px);
}

/* 预览区 */
.avatar-cropper__preview-section {
  text-align: center;
}

.avatar-cropper__preview-label {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-secondary, #666);
  margin: 0 0 var(--spacing-md, 16px);
}

.avatar-cropper__preview-container {
  display: flex;
  justify-content: center;
}

.avatar-cropper__preview-circle {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid var(--color-border, #e0e0e0);
  background-color: var(--color-bg-base, #f5f5f5);
}

.avatar-cropper__preview-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-cropper__preview-hint {
  font-size: 12px;
  color: var(--color-text-muted, #999);
  margin: var(--spacing-sm, 8px) 0 0;
}

/* 大小调节 */
.avatar-cropper__size-section,
.avatar-cropper__zoom-section {
  padding-top: var(--spacing-md, 16px);
  border-top: 1px solid var(--color-border, #eee);
}

.avatar-cropper__size-label,
.avatar-cropper__zoom-label {
  font-size: 12px;
  font-weight: 500;
  color: var(--color-text-secondary, #666);
  margin: 0 0 var(--spacing-sm, 8px);
}

.avatar-cropper__size-controls,
.avatar-cropper__zoom-controls {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm, 8px);
}

.avatar-cropper__size-icon,
.avatar-cropper__zoom-icon {
  font-size: 20px;
  color: var(--color-text-secondary, #999);
}

.avatar-cropper__size-slider,
.avatar-cropper__zoom-slider {
  flex: 1;
  height: 4px;
  appearance: none;
  background: var(--color-border, #ddd);
  border-radius: 2px;
  outline: none;
}

.avatar-cropper__size-slider::-webkit-slider-thumb,
.avatar-cropper__zoom-slider::-webkit-slider-thumb {
  appearance: none;
  width: 16px;
  height: 16px;
  background: var(--color-primary, #dfa473);
  border-radius: 50%;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.avatar-cropper__size-slider::-webkit-slider-thumb:hover,
.avatar-cropper__zoom-slider::-webkit-slider-thumb:hover {
  transform: scale(1.2);
}

/* 操作按钮 */
.avatar-cropper__actions {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm, 8px);
  margin-top: auto;
}

.avatar-cropper__btn {
  padding: 10px 16px;
  border-radius: var(--radius-md, 12px);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm, 8px);
  border: none;
}

.avatar-cropper__btn .material-symbols-outlined {
  font-size: 18px;
}

.avatar-cropper__btn--secondary {
  background-color: var(--color-bg-base, #f5f5f5);
  color: var(--color-text-main, #333);
}

.avatar-cropper__btn--secondary:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.avatar-cropper__btn--primary {
  background-color: var(--color-primary, #dfa473);
  color: white;
}

.avatar-cropper__btn--primary:hover {
  filter: brightness(1.1);
}

.avatar-cropper__btn--primary:active {
  transform: scale(0.98);
}

/* 底部按钮 */
.avatar-cropper__footer {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-md, 16px);
  padding: var(--spacing-lg, 20px) var(--spacing-xl, 24px);
  border-top: 1px solid var(--color-border, #e0e0e0);
}

.avatar-cropper__footer .avatar-cropper__btn {
  min-width: 120px;
}

/* 响应式 */
@media (max-width: 768px) {
  .avatar-cropper__modal {
    width: 100%;
    height: 100%;
    max-width: none;
    max-height: none;
    border-radius: 0;
  }
  
  .avatar-cropper__content {
    flex-direction: column;
  }
  
  .avatar-cropper__sidebar {
    width: 100%;
    flex-direction: row;
    flex-wrap: wrap;
    border-left: none;
    border-top: 1px solid var(--color-border, #e0e0e0);
  }
  
  .avatar-cropper__preview-section {
    width: 100%;
    order: -1;
  }
  
  .avatar-cropper__size-section,
  .avatar-cropper__zoom-section {
    flex: 1;
  }
  
  .avatar-cropper__actions {
    width: 100%;
    flex-direction: row;
  }
}
</style>
