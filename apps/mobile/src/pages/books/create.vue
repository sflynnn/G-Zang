<template>
  <view class="create-book-page">
    <!-- 顶部导航 -->
    <view class="nav-bar">
      <view class="nav-left" @click="goBack">
        <uni-icons type="left" size="20" color="#333"></uni-icons>
      </view>
      <view class="nav-title">{{ isEdit ? '编辑账本' : '创建账本' }}</view>
      <view class="nav-right"></view>
    </view>

    <scroll-view class="main-content" scroll-y="true">
      <!-- 账本图标 -->
      <view class="form-section">
        <view class="section-title">选择图标</view>
        <view class="icon-grid">
          <view
            v-for="icon in presetIcons"
            :key="icon"
            class="icon-item"
            :class="{ active: form.icon === icon }"
            @click="form.icon = icon"
          >
            <text class="icon-emoji">{{ icon }}</text>
          </view>
        </view>
      </view>

      <!-- 账本名称 -->
      <view class="form-section">
        <view class="section-title">账本名称</view>
        <view class="input-wrapper">
          <input
            v-model="form.name"
            class="text-input"
            placeholder="输入账本名称，如：日常开销"
            maxlength="20"
          />
          <text class="char-count">{{ form.name.length }}/20</text>
        </view>
      </view>

      <!-- 主题颜色 -->
      <view class="form-section">
        <view class="section-title">主题颜色</view>
        <view class="color-grid">
          <view
            v-for="color in presetColors"
            :key="color"
            class="color-item"
            :class="{ active: form.color === color }"
            :style="{ backgroundColor: color }"
            @click="form.color = color"
          >
            <uni-icons v-if="form.color === color" type="checkmark" size="16" color="#fff"></uni-icons>
          </view>
        </view>
      </view>

      <!-- 货币选择 -->
      <view class="form-section">
        <view class="section-title">货币单位</view>
        <view class="currency-grid">
          <view
            v-for="currency in presetCurrencies"
            :key="currency.code"
            class="currency-item"
            :class="{ active: form.currency === currency.code }"
            @click="selectCurrency(currency)"
          >
            <text class="currency-symbol">{{ currency.symbol }}</text>
            <text class="currency-name">{{ currency.name }}</text>
            <text class="currency-code">{{ currency.code }}</text>
          </view>
        </view>
      </view>

      <!-- 账本种类 -->
      <view class="form-section">
        <view class="section-title">账本种类</view>
        <view class="type-grid">
          <view
            v-for="type in bookTypes"
            :key="type.value"
            class="type-item"
            :class="{ active: form.type === type.value }"
            @click="form.type = type.value"
          >
            <text class="type-icon">{{ type.icon }}</text>
            <text class="type-name">{{ type.name }}</text>
          </view>
        </view>
      </view>

      <!-- 设为默认 -->
      <view class="form-section">
        <label class="switch-item">
          <view class="switch-info">
            <text class="switch-title">设为默认账本</text>
            <text class="switch-desc">默认账本将在打开应用时自动选中</text>
          </view>
          <switch
            :checked="form.isDefault"
            color="#0F4C5C"
            @change="form.isDefault = $event.detail.value"
          />
        </label>
      </view>

      <!-- 预览 -->
      <view class="form-section">
        <view class="section-title">预览效果</view>
        <view
          class="book-preview"
          :style="{
            background: `linear-gradient(135deg, ${form.color} 0%, ${darkenColor(form.color)} 100%)`
          }"
        >
          <view class="preview-header">
            <view class="preview-icon">{{ form.icon || '📒' }}</view>
            <view class="preview-info">
              <text class="preview-name">{{ form.name || '账本名称' }}</text>
              <text class="preview-currency">{{ getCurrencySymbol(form.currency) }}{{ form.name ? ' 0.00' : '' }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 底部按钮 -->
      <view class="bottom-bar">
        <button
          v-if="isEdit"
          class="delete-btn"
          :disabled="submitting"
          @click="handleDelete"
        >
          删除账本
        </button>
        <button
          class="submit-btn"
          :disabled="!isFormValid || submitting"
          :loading="submitting"
          @click="handleSubmit"
        >
          {{ isEdit ? '保存修改' : '创建账本' }}
        </button>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useBookStore } from '@/stores/book'
import { useAppStore } from '@/stores/app'
import { PRESET_CURRENCIES, PRESET_BOOK_ICONS, PRESET_BOOK_COLORS } from '@/types/book'
import type { BookForm, Book } from '@/types/book'

const bookStore = useBookStore()
const appStore = useAppStore()

// 判断是编辑还是创建
const isEdit = computed(() => !!editBookId.value)
const editBookId = ref<number | null>(null)
const submitting = ref(false)

// 表单数据
const form = reactive<BookForm>({
  name: '',
  icon: '📒',
  color: '#0F4C5C',
  currency: 'CNY',
  currencySymbol: '¥',
  type: 'PERSONAL',
  isDefault: false,
})

// 预设数据
const presetIcons = PRESET_BOOK_ICONS
const presetColors = PRESET_BOOK_COLORS
const presetCurrencies = PRESET_CURRENCIES

const bookTypes = [
  { value: 'PERSONAL', name: '个人', icon: '👤' },
  { value: 'FAMILY', name: '家庭', icon: '👨‍👩‍👧' },
  { value: 'TEAM', name: '团队', icon: '👥' },
  { value: 'BUSINESS', name: '业务', icon: '💼' },
  { value: 'OTHER', name: '其他', icon: '📋' },
]

// 计算属性
const isFormValid = computed(() => {
  return form.name.trim().length > 0 && form.icon && form.color && form.currency
})

// 选择货币
const selectCurrency = (currency: { code: string; symbol: string }) => {
  form.currency = currency.code
  form.currencySymbol = currency.symbol
}

// 获取货币符号
const getCurrencySymbol = (code: string) => {
  const currency = presetCurrencies.find(c => c.code === code)
  return currency?.symbol || '¥'
}

// 加深颜色
const darkenColor = (hex: string) => {
  const color = hex.replace('#', '')
  const r = Math.max(0, parseInt(color.substring(0, 2), 16) - 30)
  const g = Math.max(0, parseInt(color.substring(2, 4), 16) - 30)
  const b = Math.max(0, parseInt(color.substring(4, 6), 16) - 30)
  return `#${r.toString(16).padStart(2, '0')}${g.toString(16).padStart(2, '0')}${b.toString(16).padStart(2, '0')}`
}

// 返回
const goBack = () => {
  uni.navigateBack()
}

// 提交
const handleSubmit = async () => {
  if (!isFormValid.value || submitting.value) return

  try {
    submitting.value = true

    if (isEdit.value && editBookId.value) {
      await bookStore.updateBook(editBookId.value, form)
      appStore.showSuccess('账本已更新')
    } else {
      await bookStore.createBook(form)
      appStore.showSuccess('账本创建成功')
    }

    setTimeout(() => {
      uni.navigateBack()
    }, 1000)
  } catch (error: any) {
    appStore.showError(error.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

// 删除
const handleDelete = async () => {
  if (!editBookId.value) return

  uni.showModal({
    title: '确认删除',
    content: '删除后账本数据将无法恢复，确定要删除吗？',
    confirmColor: '#EF476F',
    success: async (res) => {
      if (res.confirm) {
        try {
          await bookStore.deleteBook(editBookId.value!)
          appStore.showSuccess('账本已删除')
          setTimeout(() => {
            uni.navigateBack()
          }, 1000)
        } catch (error: any) {
          appStore.showError(error.message || '删除失败')
        }
      }
    },
  })
}

onMounted(async () => {
  // 获取页面参数
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const options = currentPage.options || {}

  // 如果有 id 参数，则是编辑模式
  if (options.id) {
    editBookId.value = parseInt(options.id as string)
    await bookStore.loadBooks()

    const book = bookStore.books.find(b => b.id === editBookId.value)
    if (book) {
      form.name = book.name
      form.icon = book.icon
      form.color = book.color
      form.currency = book.currency
      form.currencySymbol = book.currencySymbol
      form.type = book.type
      form.isDefault = book.isDefault
    }
  }
})
</script>

<style lang="scss" scoped>
.create-book-page {
  min-height: 100vh;
  background-color: #F8F9FA;
  display: flex;
  flex-direction: column;
}

.nav-bar {
  background-color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #E5E5E5;
}

.nav-left, .nav-right {
  width: 60px;
}

.nav-left {
  display: flex;
  align-items: center;
}

.nav-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.main-content {
  flex: 1;
  padding-bottom: 120px;
}

.form-section {
  background: #fff;
  padding: 20px 16px;
  margin-bottom: 12px;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
}

// 图标选择
.icon-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 12px;
}

.icon-item {
  aspect-ratio: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #F8F9FA;
  border-radius: 12px;
  border: 2px solid transparent;
  transition: all 0.2s;

  &.active {
    border-color: #0F4C5C;
    background: rgba(15, 76, 92, 0.1);
  }

  &:active {
    transform: scale(0.95);
  }
}

.icon-emoji {
  font-size: 28px;
}

// 输入框
.input-wrapper {
  position: relative;
}

.text-input {
  width: 100%;
  padding: 14px 16px;
  background: #F8F9FA;
  border-radius: 10px;
  font-size: 16px;
  color: #333;
  border: 1px solid transparent;

  &:focus {
    border-color: #0F4C5C;
    background: #fff;
  }
}

.char-count {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 12px;
  color: #999;
}

// 颜色选择
.color-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 12px;
}

.color-item {
  aspect-ratio: 1;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid transparent;
  transition: all 0.2s;

  &.active {
    border-color: #333;
    transform: scale(1.1);
  }
}

// 货币选择
.currency-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.currency-item {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  background: #F8F9FA;
  border-radius: 10px;
  border: 2px solid transparent;
  transition: all 0.2s;

  &.active {
    border-color: #0F4C5C;
    background: rgba(15, 76, 92, 0.1);
  }
}

.currency-symbol {
  font-size: 18px;
  font-weight: 600;
  color: #0F4C5C;
  margin-right: 10px;
}

.currency-name {
  flex: 1;
  font-size: 14px;
  color: #333;
}

.currency-code {
  font-size: 12px;
  color: #999;
}

// 账本种类
.type-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 12px;
}

.type-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 8px;
  background: #F8F9FA;
  border-radius: 10px;
  border: 2px solid transparent;
  transition: all 0.2s;

  &.active {
    border-color: #0F4C5C;
    background: rgba(15, 76, 92, 0.1);
  }
}

.type-icon {
  font-size: 24px;
  margin-bottom: 6px;
}

.type-name {
  font-size: 12px;
  color: #666;

  .active & {
    color: #0F4C5C;
    font-weight: 600;
  }
}

// 开关
.switch-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.switch-info {
  flex: 1;
  margin-right: 16px;
}

.switch-title {
  display: block;
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.switch-desc {
  font-size: 13px;
  color: #999;
}

// 预览
.book-preview {
  border-radius: 16px;
  padding: 20px;
  color: #fff;
}

.preview-header {
  display: flex;
  align-items: center;
}

.preview-icon {
  width: 48px;
  height: 48px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-right: 12px;
}

.preview-info {
  display: flex;
  flex-direction: column;
}

.preview-name {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 4px;
}

.preview-currency {
  font-size: 14px;
  opacity: 0.8;
}

// 底部按钮
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  gap: 12px;
  padding: 16px;
  background: #fff;
  border-top: 1px solid #E5E5E5;
}

.delete-btn {
  flex: 1;
  height: 48px;
  background: #fff;
  color: #EF476F;
  border: 1px solid #EF476F;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;

  &:active {
    background: #FFF5F5;
  }
}

.submit-btn {
  flex: 2;
  height: 48px;
  background: #0F4C5C;
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;

  &:disabled {
    background: #ccc;
  }

  &:not(:disabled):active {
    background: #0A3A47;
  }
}
</style>
