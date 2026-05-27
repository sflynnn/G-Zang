<template>
  <!-- 自定义组件 -->
  <CustomToast />
  <CustomModal />
  <CustomLoading />

  <view class="record-page">
    <!-- ========== 上半部分 ========== -->
    <view class="top-half">
      <!-- 导航栏 -->
      <view class="nav-bar" :style="{ paddingTop: navBarHeight + 'px' }">
        <view class="nav-row">
          <view class="nav-back" @click="goBack">
            <uni-icons type="left" size="22" color="var(--gzang-text-primary)" />
          </view>
          <view class="nav-category" @click="switchCategory">
            <text class="nav-cat-icon">{{ selectedSubCategory?.icon || currentCategory?.icon }}</text>
            <text class="nav-cat-name">{{ selectedSubCategory?.name || currentCategory?.name ||
              t('accounting.categoryRecord.selectCategory') }}</text>
            <uni-icons type="bottom" size="10" color="var(--gzang-text-tertiary)" />
          </view>
          <view class="nav-type-toggle">
            <view class="type-btn" :class="{ active: categoryType === 1 }" @click="switchType(1)">
              {{ t('accounting.income') }}
            </view>
            <view class="type-btn" :class="{ active: categoryType === 2 }" @click="switchType(2)">
              {{ t('accounting.expense') }}
            </view>
          </view>
        </view>
      </view>

      <!-- 金额居中展示 -->
      <view class="amount-area">
        <view class="amount-wrap">
          <text class="amount-symbol">{{ currencySymbol }}</text>
          <text class="amount-value">{{ displayAmount }}</text>
        </view>
      </view>
    </view>

    <!-- ========== 下半部分（固定底部） ========== -->
    <view class="bottom-pane">
      <!-- 预算进度条 -->
      <view class="budget-section" v-if="showBudgetSection">
        <view class="budget-header">
          <view class="budget-info">
            <text class="budget-label">{{ t('accounting.categoryRecord.monthBudget') }}</text>
            <text class="budget-text">
              <text class="budget-spent" :style="{ color: budgetColor }">{{ currencySymbol }}{{ budgetSpent.toFixed(2)
              }}</text>
              <text class="budget-divider"> / </text>
              <text class="budget-total">{{ currencySymbol }}{{ budgetTotal.toFixed(2) }}</text>
            </text>
          </view>
          <text class="budget-percent" :style="{ color: budgetColor }">{{ budgetPercent }}%</text>
        </view>
        <view class="budget-bar-track">
          <view class="budget-bar-fill"
            :style="{ width: Math.min(budgetPercent, 100) + '%', background: budgetGradient }"></view>
          <view v-if="budgetPercent > 100" class="budget-bar-exceed"
            :style="{ width: Math.min(budgetPercent - 100, 100) + '%' }"></view>
        </view>
        <view class="budget-footer">
          <text class="budget-remaining" v-if="budgetRemaining > 0">
            {{ t('accounting.categoryRecord.remaining') }}{{ currencySymbol }}{{ budgetRemaining.toFixed(2) }}
          </text>
          <text class="budget-remaining exceeded" v-else>
            {{ t('accounting.categoryRecord.overrun') }}{{ currencySymbol }}{{ Math.abs(budgetRemaining).toFixed(2) }}
          </text>
        </view>
      </view>

      <!-- 二级分类标签 -->
      <view class="subcategory-section">
        <view class="subcategory-inner">
          <view v-for="sub in currentSubCategories" :key="sub.id" class="subcategory-tag"
            :class="{ selected: selectedSubCategory?.id === sub.id }" :style="selectedSubCategory?.id === sub.id ? {
              background: `linear-gradient(135deg, ${sub.color || currentCategory?.color || '#0F4C5C'}22, ${sub.color || currentCategory?.color || '#0F4C5C'}11)`,
              borderColor: sub.color || currentCategory?.color || '#0F4C5C',
              color: sub.color || currentCategory?.color || '#0F4C5C'
            } : {}" @click="selectSubCategory(sub)">
            <text class="sub-tag-icon">{{ sub.icon }}</text>
            <text class="sub-tag-name">{{ sub.name }}</text>
          </view>
        </view>
      </view>

      <!-- 标签/备注/支付/日期 小矩形横排 -->
      <view class="tile-row">
        <!-- 标签 -->
        <view class="tile-item" @click="openTagsPopup">
          <view class="tile-icon-row">
            <uni-icons type="tag" size="14" color="var(--gzang-secondary)" />
            <text class="tile-label">{{ t('accounting.categoryRecord.tags') }}</text>
          </view>
          <view class="tile-value">
            <text v-if="form.tags.length > 0" class="tile-tags-preview">
              <text v-for="(tag, idx) in form.tags.slice(0, 2)" :key="idx">{{ tag }}</text>
              <text v-if="form.tags.length > 2">+{{ form.tags.length - 2 }}</text>
            </text>
            <text v-else class="tile-placeholder">{{ t('accounting.categoryRecord.addTags') }}</text>
          </view>
        </view>

        <!-- 备注 -->
        <view class="tile-item" @click="openRemarkPopup">
          <view class="tile-icon-row">
            <uni-icons type="compose" size="14" color="var(--gzang-secondary)" />
            <text class="tile-label">{{ t('accounting.categoryRecord.remark') }}</text>
          </view>
          <view class="tile-value">
            <text v-if="form.remark" class="tile-text-content">{{ form.remark }}</text>
            <text v-else class="tile-placeholder">{{ t('accounting.categoryRecord.addRemark') }}</text>
          </view>
        </view>

        <!-- 支付方式 -->
        <view class="tile-item" :class="{ active: expandedField === 'payment' }" @click="openPaymentPopup">
          <view class="tile-icon-row">
            <uni-icons type="wallet" size="14" color="var(--gzang-secondary)" />
            <text class="tile-label">{{ t('accounting.categoryRecord.paymentMethod') }}</text>
          </view>
          <view class="tile-value">
            <view v-if="selectedPayment" class="tile-payment">
              <text>{{ selectedPayment.icon }}</text>
              <text class="tile-text-content">{{ selectedPayment.name }}</text>
            </view>
            <text v-else class="tile-placeholder">{{ t('accounting.categoryRecord.selectPaymentMethod') }}</text>
          </view>
        </view>

        <!-- 日期 -->
        <view class="tile-item tile-date-item" @click="datePickerRef?.show()">
          <view class="tile-icon-row">
            <uni-icons type="calendar" size="14" color="var(--gzang-secondary)" />
            <text class="tile-label">{{ t('accounting.categoryRecord.date') }}</text>
          </view>
          <view class="tile-value">
            <text class="tile-text-content">{{ formatDisplayDate }}</text>
          </view>
        </view>
      </view>

      <!-- 数字键盘 -->
      <view class="keypad-section" :style="{ paddingBottom: safeAreaBottom + 'px' }">
        <view class="keypad-sidebar">
          <view v-for="qa in quickAmounts" :key="qa" class="sidebar-btn" @click="setAmount(qa)">
            <text>{{ currencySymbol }}{{ qa }}</text>
          </view>
          <view class="sidebar-btn currency-btn" @click="openCurrencyPopup">
            <text class="currency-symbol-text">{{ selectedCurrencyCode }}</text>
          </view>
        </view>
        <view class="keypad-main">
          <view class="keypad-row">
            <view class="key-btn" @click="inputDigit('1')"><text class="key-num">1</text></view>
            <view class="key-btn" @click="inputDigit('2')"><text class="key-num">2</text></view>
            <view class="key-btn" @click="inputDigit('3')"><text class="key-num">3</text></view>
          </view>
          <view class="keypad-row">
            <view class="key-btn" @click="inputDigit('4')"><text class="key-num">4</text></view>
            <view class="key-btn" @click="inputDigit('5')"><text class="key-num">5</text></view>
            <view class="key-btn" @click="inputDigit('6')"><text class="key-num">6</text></view>
          </view>
          <view class="keypad-row">
            <view class="key-btn" @click="inputDigit('7')"><text class="key-num">7</text></view>
            <view class="key-btn" @click="inputDigit('8')"><text class="key-num">8</text></view>
            <view class="key-btn" @click="inputDigit('9')"><text class="key-num">9</text></view>
          </view>
          <view class="keypad-row">
            <view class="key-btn" @click="clearAmount"><text class="key-num">C</text></view>
            <view class="key-btn" @click="inputDigit('0')"><text class="key-num">0</text></view>
            <view class="key-btn" @click="inputDigit('.')"><text class="key-num">.</text></view>
          </view>
        </view>
        <view class="keypad-confirm-col">
          <view class="keypad-action-btn delete-btn" @click="deleteDigit">
            <uni-icons type="undo" size="22" color="var(--gzang-text-secondary)" />
          </view>
          <view class="keypad-action-btn confirm-btn" :class="{ ready: canSubmit }" @click="handleSubmit">
            <uni-icons type="checkmarkempty" size="28" color="white" />
          </view>
        </view>
      </view>
    </view>
    <uni-datetime-picker ref="datePickerRef" v-model="form.datetime" type="datetime" :clear-icon="false" :border="false"
      class="tile-datetime-picker" @change="onDateChange" />
    <uni-popup ref="paymentPopupRef" type="bottom">
      <view class="picker-sheet">
        <view class="picker-handle-bar"></view>
        <view class="picker-hd">
          <text class="picker-cancel" @click="closePaymentPopup">{{ t('common.cancel') }}</text>
          <text class="picker-title">{{ t('accounting.categoryRecord.paymentMethod') }}</text>
          <text class="picker-done" @click="closePaymentPopup">{{ t('common.confirm') }}</text>
        </view>
        <view class="payment-grid">
          <view v-for="pm in paymentMethodsFromStore" :key="pm.id" class="payment-cell"
            :class="{ selected: form.paymentMethodId === pm.id }" @click="selectPayment(pm)">
            <text class="payment-cell-icon">{{ pm.icon }}</text>
            <text class="payment-cell-name">{{ pm.name }}</text>
          </view>
        </view>
      </view>
    </uni-popup>

    <!-- 标签弹窗 -->
    <uni-popup ref="tagsPopupRef" type="bottom">
      <view class="picker-sheet">
        <view class="picker-handle-bar"></view>
        <view class="picker-hd">
          <text class="picker-cancel" @click="closeTagsPopup">{{ t('common.cancel') }}</text>
          <text class="picker-title">{{ t('accounting.categoryRecord.tags') }}</text>
          <text class="picker-done" @click="closeTagsPopup">{{ t('common.confirm') }}</text>
        </view>
        <view class="tags-popup-content">
          <view class="tag-input-row">
            <input v-model="tagInput" class="tag-input-field"
              :placeholder="t('accounting.categoryRecord.tagInputPlaceholder')" @confirm="addTag" />
            <view class="tag-add-btn" @click="addTag">
              <uni-icons type="plus" size="14" color="var(--gzang-secondary)" />
            </view>
          </view>
          <view v-if="form.tags.length > 0" class="tag-selected-list">
            <view v-for="(tag, idx) in form.tags" :key="idx" class="tag-selected-item" @click="removeTag(idx)">
              <text>{{ tag }}</text>
              <uni-icons type="close" size="10" color="rgba(255,255,255,0.7)" />
            </view>
          </view>
          <view v-if="quickTags.length > 0" class="quick-tags-section">
            <text class="quick-tags-label">{{ t('accounting.categoryRecord.quickAdd') }}</text>
            <view class="quick-tags-wrap">
              <view v-for="qt in quickTags" :key="qt" class="quick-tag-item"
                :class="{ active: form.tags.includes(qt) }" @click="toggleQuickTag(qt)">{{ qt }}</view>
            </view>
          </view>

          <view v-if="allTags.length > 0" class="all-tags-section">
            <view class="all-tags-header">
              <text class="all-tags-label">{{ t('accounting.categoryRecord.allTags') }}</text>
              <input v-model="tagSearch" class="tag-search-input" :placeholder="t('accounting.categoryRecord.searchTags')" />
            </view>

            <view v-if="tagSearch.trim() && canCreateTagFromSearch" class="tag-create-suggest" @click="createFromSearch">
              <uni-icons type="plus" size="16" color="var(--gzang-secondary)" />
              <text class="tag-create-text">{{ t('accounting.categoryRecord.createTag') }} “{{ tagSearch.trim() }}”</text>
            </view>

            <view v-if="filteredAllTags.length === 0" class="tag-empty">
              <text class="tag-empty-text">{{ t('accounting.categoryRecord.noTagsFound') }}</text>
            </view>

            <view v-else class="all-tags-wrap">
              <view v-for="tag in filteredAllTags" :key="tag.id" class="all-tag-item"
                :class="{ active: form.tags.includes(tag.tagName) }" @click="toggleDbTag(tag)">
                <view class="all-tag-dot" :style="{ backgroundColor: tag.tagColor || '#0F4C5C' }"></view>
                <text class="all-tag-name">{{ tag.tagName }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </uni-popup>

    <!-- 备注弹窗 -->
    <uni-popup ref="remarkPopupRef" type="bottom">
      <view class="picker-sheet">
        <view class="picker-handle-bar"></view>
        <view class="picker-hd">
          <text class="picker-cancel" @click="closeRemarkPopup">{{ t('common.cancel') }}</text>
          <text class="picker-title">{{ t('accounting.categoryRecord.remark') }}</text>
          <text class="picker-done" @click="closeRemarkPopup">{{ t('common.confirm') }}</text>
        </view>
        <view class="remark-popup-content">
          <textarea v-model="form.remark" class="remark-area"
            :placeholder="t('accounting.categoryRecord.remarkPlaceholder')" :maxlength="200" auto-height focus />
          <text class="remark-counter">{{ form.remark.length }}/200</text>
        </view>
      </view>
    </uni-popup>

    <!-- 币种选择弹窗 -->
    <uni-popup ref="currencyPopupRef" type="bottom">
      <view class="picker-sheet">
        <view class="picker-handle-bar"></view>
        <view class="picker-hd">
          <text class="picker-cancel" @click="closeCurrencyPopup">{{ t('common.cancel') }}</text>
          <text class="picker-title">{{ t('accounting.categoryRecord.selectCurrency') }}</text>
          <view class="picker-placeholder"></view>
        </view>
        <view class="currency-grid">
          <view v-for="curr in currencies" :key="curr.code" class="currency-cell"
            :class="{ selected: selectedCurrencyCode === curr.code }" @click="selectCurrency(curr)">
            <text class="currency-cell-symbol">{{ curr.symbol }}</text>
            <text class="currency-cell-code">{{ curr.code }}</text>
            <text class="currency-cell-name">{{ curr.name }}</text>
          </view>
        </view>
      </view>
    </uni-popup>

    <uni-popup ref="categoryPopupRef" type="bottom">
      <view class="picker-sheet">
        <view class="picker-handle-bar"></view>
        <view class="picker-hd">
          <text class="picker-cancel" @click="closeCategoryPopup">{{ t('common.cancel') }}</text>
          <text class="picker-title">{{ t('accounting.categoryRecord.switchCategory') }}</text>
          <text class="picker-done" @click="closeCategoryPopup">{{ t('common.confirm') }}</text>
        </view>
        <scroll-view scroll-y class="category-picker-scroll">
          <view v-for="cat in topLevelCategories" :key="cat.id" class="cat-parent-row">
            <view class="cat-parent-cell" @click="selectParentCategory(cat)">
              <text class="cat-parent-icon">{{ cat.icon }}</text>
              <text class="cat-parent-name">{{ cat.name }}</text>
              <uni-icons v-if="cat.children && cat.children.length > 0" type="right" size="12"
                color="var(--gzang-text-tertiary)" />
            </view>
            <view v-if="selectedParentForSub && selectedParentForSub.id === cat.id" class="cat-children-row">
              <view v-for="sub in cat.children" :key="sub.id" class="cat-child-cell"
                :class="{ selected: form.categoryId === sub.id }" @click="selectChildCategory(sub, cat)">
                <text>{{ sub.icon }} {{ sub.name }}</text>
              </view>
            </view>
          </view>
        </scroll-view>
      </view>
    </uni-popup>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { useBookStore } from '@/stores/book'
import { useAccountingStore } from '@/stores/accounting'
import { useToast } from '@/composables/useToast'
import CustomToast from '@/components/common/CustomToast/index.vue'
import CustomModal from '@/components/common/CustomModal/index.vue'
import CustomLoading from '@/components/common/CustomLoading/index.vue'

const toast = useToast()

interface CategoryItem {
  id: number
  name: string
  icon: string
  color?: string
  type?: number
  parentId?: number
  children?: CategoryItem[]
  budget?: {
    budget: number
    spent: number
    remaining: number
    percentUsed?: number
    warningThreshold?: number
  }
}

interface PaymentItem {
  id: number
  code: string
  name: string
  icon: string
}

const bookStore = useBookStore()
const accountingStore = useAccountingStore()
const { t } = useI18n()

const categoryId = ref(0)
const categoryType = ref(2)
const navBarHeight = ref(44)
const safeAreaBottom = ref(0)

const paymentPopupRef = ref<any>(null)
const categoryPopupRef = ref<any>(null)
const tagsPopupRef = ref<any>(null)
const remarkPopupRef = ref<any>(null)
const currencyPopupRef = ref<any>(null)
const datePickerRef = ref<any>(null)

const expandedField = ref('')
const selectedParentForSub = ref<CategoryItem | null>(null)
const tagInput = ref('')
// 从 store 获取常用标签，不再硬编码
const quickAmounts = [10, 50, 100, 200]

interface CurrencyItem {
  code: string
  symbol: string
  name: string
}

const currencies: CurrencyItem[] = [
  { code: 'CNY', symbol: '¥', name: '人民币' },
  { code: 'USD', symbol: '$', name: '美元' },
  { code: 'EUR', symbol: '€', name: '欧元' },
  { code: 'GBP', symbol: '£', name: '英镑' },
  { code: 'JPY', symbol: '¥', name: '日元' },
  { code: 'HKD', symbol: 'HK$', name: '港币' },
  { code: 'TWD', symbol: 'NT$', name: '新台币' },
  { code: 'KRW', symbol: '₩', name: '韩元' },
]

const selectCurrency = (curr: CurrencyItem) => {
  selectedCurrencyCode.value = curr.code
  if (bookStore.currentBook) {
    bookStore.currentBook.currency = curr.code
  }
  closeCurrencyPopup()
}

// 从 store 获取支付方式
const paymentMethodsFromStore = computed(() => {
  return accountingStore.paymentMethods.map(pm => ({
    id: pm.id,
    code: pm.methodCode,
    name: pm.methodName,
    icon: pm.icon || '💰'
  }))
})

// 从 store 获取常用标签
const quickTags = computed(() => {
  return accountingStore.frequentTags.map(tag => tag.tagName)
})

// 全部标签列表（来自接口）
const allTags = computed(() => accountingStore.tags)
const tagSearch = ref('')
const filteredAllTags = computed(() => {
  const q = tagSearch.value.trim().toLowerCase()
  if (!q) return allTags.value
  return allTags.value.filter(t => (t.tagName || '').toLowerCase().includes(q))
})

const canCreateTagFromSearch = computed(() => {
  const name = tagSearch.value.trim()
  if (!name) return false
  if (form.tags.length >= 8) return false
  if (form.tags.includes(name)) return false
  return !allTags.value.some(t => t.tagName === name)
})

const createFromSearch = async () => {
  const name = tagSearch.value.trim()
  if (!name) return
  tagInput.value = name
  await addTag()
  tagSearch.value = ''
}

const form = reactive({
  amount: '',
  tags: [] as string[],
  remark: '',
  paymentMethodId: undefined as number | undefined,
  paymentMethod: '',
  datetime: '',
  categoryId: 0,
})

const currencySymbol = computed(() => bookStore.currentCurrencySymbol)
const currentBook = computed(() => bookStore.currentBook)
const selectedCurrencyCode = ref(bookStore.currentBook?.currency || 'CNY')

watch(() => bookStore.currentBook?.currency, (newVal) => {
  if (newVal) selectedCurrencyCode.value = newVal
})

const topLevelCategories = computed((): CategoryItem[] => {
  return (accountingStore.categoriesWithChildren as unknown as CategoryItem[]).filter(c => c.type === categoryType.value) || []
})

const currentCategory = computed((): CategoryItem | null => {
  if (!categoryId.value) return null
  return (accountingStore.categoriesWithChildren as unknown as CategoryItem[]).find(c => c.id === categoryId.value) || null
})

const currentSubCategories = computed((): CategoryItem[] => {
  return currentCategory.value?.children || []
})

const selectedSubCategory = ref<CategoryItem | null>(null)

const displayAmount = computed(() => {
  if (!form.amount) return '0.00'
  const n = parseFloat(form.amount)
  return isNaN(n) ? '0.00' : n.toFixed(2)
})

const selectedPayment = computed(() => {
  if (!form.paymentMethodId) return null
  return paymentMethodsFromStore.value.find(p => p.id === form.paymentMethodId) || null
})

const formatDisplayDate = computed(() => {
  if (!form.datetime) return t('accounting.categoryRecord.selectDate')
  const dt = form.datetime.replace(' ', 'T')
  const d = new Date(dt)
  if (isNaN(d.getTime())) return form.datetime
  const today = fmtDate(new Date())
  const yest = fmtDate(new Date(Date.now() - 86400000))
  const dStr = fmtDate(d)
  if (dStr === today) return t('accounting.categoryRecord.today')
  if (dStr === yest) return t('accounting.categoryRecord.yesterday')
  return dStr.replace(/-/g, '/')
})


const accounts = computed(() => accountingStore.accounts || [])

const canSubmit = computed(() => {
  return parseFloat(form.amount) > 0 && (form.categoryId > 0 || categoryId.value > 0)
})

const showBudgetSection = computed(() => {
  if (categoryType.value !== 2) return false
  const cat = currentCategory.value
  return cat?.budget && cat.budget.budget > 0
})

const budgetTotal = computed(() => currentCategory.value?.budget?.budget || 0)
const budgetSpent = computed(() => currentCategory.value?.budget?.spent || 0)
const budgetRemaining = computed(() => budgetTotal.value - budgetSpent.value)
const budgetPercent = computed(() => {
  if (budgetTotal.value <= 0) return 0
  return Math.round((budgetSpent.value / budgetTotal.value) * 100)
})
const budgetColor = computed(() => {
  const pct = budgetPercent.value
  if (pct >= 100) return 'var(--gzang-danger)'
  if (pct >= 80) return 'var(--gzang-warning)'
  return 'var(--gzang-success)'
})
const budgetGradient = computed(() => {
  const pct = budgetPercent.value
  if (pct >= 100) return 'linear-gradient(90deg, var(--gzang-danger), #ff6b6b)'
  if (pct >= 80) return 'linear-gradient(90deg, var(--gzang-warning), #ffd166)'
  return 'linear-gradient(90deg, var(--gzang-secondary), var(--gzang-primary))'
})

const fmtDate = (d: Date) => `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`

const toggleField = (field: string) => {
  expandedField.value = expandedField.value === field ? '' : field
}

const selectSubCategory = (sub: CategoryItem) => {
  selectedSubCategory.value = sub
  form.categoryId = sub.id
  expandedField.value = ''
}

const addTag = async () => {
  const tagName = tagInput.value.trim()
  if (!tagName) return
  if (form.tags.includes(tagName)) {
    tagInput.value = ''
    return
  }
  if (form.tags.length >= 8) return

  // 如果标签库里已有，直接选中
  const existing = allTags.value.find(t => t.tagName === tagName)
  if (existing) {
    form.tags.push(existing.tagName)
    tagInput.value = ''
    return
  }

  try {
    const created = await accountingStore.createTag(tagName)
    form.tags.push(created.tagName)
    tagInput.value = ''
  } catch (e: any) {
    toast.error(e?.message || '创建标签失败')
  }
}

const toggleQuickTag = (tag: string) => {
  const idx = form.tags.indexOf(tag)
  if (idx > -1) form.tags.splice(idx, 1)
  else if (form.tags.length < 8) form.tags.push(tag)
}

const toggleDbTag = (tag: { id: number; tagName: string; tagColor?: string }) => {
  const name = tag.tagName
  if (!name) return
  const idx = form.tags.indexOf(name)
  if (idx > -1) form.tags.splice(idx, 1)
  else if (form.tags.length < 8) form.tags.push(name)
}

const removeTag = (idx: number) => form.tags.splice(idx, 1)

const selectPayment = (pm: PaymentItem) => {
  form.paymentMethodId = pm.id
  form.paymentMethod = pm.code
  closePaymentPopup()
}

const inputDigit = (digit: string) => {
  let cur = form.amount || ''
  if (digit === '00') {
    if (!cur || cur === '0') return
    if (cur.includes('.')) {
      const parts = cur.split('.')
      if (parts[1].length >= 2) return
    }
    if (cur.replace('.', '').length >= 10) return
    cur += '00'
  } else if (digit === '.') {
    if (cur.includes('.')) return
    cur = cur || '0'
    cur += '.'
  } else {
    if (cur === '0' && digit !== '0') cur = digit
    else if (cur === '0' && digit === '0') return
    else {
      if (cur.includes('.')) {
        const parts = cur.split('.')
        if (parts[1].length >= 2) return
      }
      if (cur.replace('.', '').length >= 10) return
      cur += digit
    }
  }
  form.amount = cur
}

const deleteDigit = () => {
  if (form.amount && form.amount.length > 0) form.amount = form.amount.slice(0, -1)
}

const clearAmount = () => { form.amount = '' }

const setAmount = (n: number) => { form.amount = String(n) }

const goBack = () => { uni.navigateBack() }

const switchType = (type: 1 | 2) => {
  categoryType.value = type
  categoryId.value = 0
  form.categoryId = 0
  selectedSubCategory.value = null
  loadCategories()
}

const switchCategory = () => {
  selectedParentForSub.value = null
  categoryPopupRef.value?.open()
}

const selectParentCategory = (cat: CategoryItem) => {
  selectedParentForSub.value = selectedParentForSub.value?.id !== cat.id ? cat : null
}

const selectChildCategory = (sub: CategoryItem, parent: CategoryItem) => {
  categoryId.value = parent.id
  form.categoryId = sub.id
  selectedSubCategory.value = sub
  closeCategoryPopup()
}

const closeCategoryPopup = () => {
  categoryPopupRef.value?.close()
  selectedParentForSub.value = null
}

const onDateChange = (val: string) => {
  if (val) form.datetime = val
}

const openTagsPopup = () => { tagsPopupRef.value?.open() }
const closeTagsPopup = () => {
  tagsPopupRef.value?.close()
  tagSearch.value = ''
}

const openRemarkPopup = () => { remarkPopupRef.value?.open() }
const closeRemarkPopup = () => { remarkPopupRef.value?.close() }

const openCurrencyPopup = () => { currencyPopupRef.value?.open() }
const closeCurrencyPopup = () => { currencyPopupRef.value?.close() }

const openPaymentPopup = () => { paymentPopupRef.value?.open() }
const closePaymentPopup = () => { paymentPopupRef.value?.close() }

const handleSubmit = async () => {
  if (!canSubmit.value) {
    toast.warning(t('accounting.categoryRecord.pleaseEnterAmount'))
    return
  }
  try {
    const txTime = form.datetime ? new Date(form.datetime.replace(' ', 'T')).toISOString() : new Date().toISOString()
    const pm = selectedPayment.value
    const finalCategoryId = form.categoryId || categoryId.value
    if (!finalCategoryId) {
      toast.warning(t('accounting.categoryRecord.selectCategory'))
      return
    }
    const defAccountId = accounts.value[0]?.id
    if (!defAccountId) {
      toast.warning(t('accounting.categoryRecord.pleaseAddAccount'))
      return
    }
    await accountingStore.createTransaction({
      type: categoryType.value,
      amount: parseFloat(form.amount),
      categoryId: finalCategoryId,
      accountId: defAccountId,
      transactionTime: txTime,
      remark: form.remark,
      tags: form.tags,
      paymentMethod: pm?.code || 'other',
    } as any)

    // 记录标签使用次数（用于常用标签排序）
    const tagIdsToUse = form.tags
      .map(name => allTags.value.find(t => t.tagName === name)?.id)
      .filter((id): id is number => typeof id === 'number')

    if (tagIdsToUse.length > 0) {
      await Promise.allSettled(tagIdsToUse.map(id => accountingStore.useTag(id)))
    }

    toast.success(t('accounting.categoryRecord.recordSuccess'))
    setTimeout(() => uni.navigateBack(), 1500)
  } catch (err: any) {
    toast.error(err.message || t('accounting.categoryRecord.recordFailed'))
  }
}

const loadCategories = async () => {
  await accountingStore.loadCategoriesWithChildren(currentBook.value?.id)
}

onMounted(async () => {
  const sysInfo = uni.getSystemInfoSync()
  navBarHeight.value = sysInfo.statusBarHeight || 0
  safeAreaBottom.value = sysInfo.safeAreaInsets?.bottom || 0

  const pages = getCurrentPages()
  const curPage = pages[pages.length - 1] as any
  const opts = curPage?.options || {}
  if (opts.categoryId) categoryId.value = parseInt(opts.categoryId)
  if (opts.type) categoryType.value = parseInt(opts.type)
  form.categoryId = categoryId.value

  const now = new Date()
  form.datetime = now.toISOString().slice(0, 19).replace('T', ' ')

  // 并行加载所有必要数据
  await Promise.all([
    loadCategories(),
    accountingStore.loadAccounts(),
    accountingStore.loadPaymentMethods(),
    accountingStore.loadFrequentTags(8),
    accountingStore.loadTags()
  ])
})
</script>

<script lang="ts">
export default {
  options: { styleIsolation: 'shared' }
}
</script>

<style lang="scss" scoped>
.record-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--gzang-bg);
  overflow: hidden;
}

// ========== 上半部分 ==========
.top-half {
  width: 100%;
  height: 55%;
  display: flex;
  flex-direction: column;
  background: var(--gzang-bg);
  flex-shrink: 0;
}

// ========== 导航栏 ==========
.nav-bar {
  position: relative;
  background: var(--gzang-surface);
  border-bottom: 1rpx solid var(--gzang-border);
  flex-shrink: 0;
}

.nav-row {
  display: flex;
  align-items: center;
  height: 88rpx;
  padding: 0 16rpx;
  gap: 16rpx;
}

.nav-back {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;

  &:active {
    opacity: 0.6;
  }
}

.nav-category {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 8rpx 16rpx;
  background: var(--gzang-bg);
  border-radius: 20rpx;
  max-width: 280rpx;

  &:active {
    opacity: 0.7;
  }
}

.nav-cat-icon {
  font-size: 28rpx;
  flex-shrink: 0;
}

.nav-cat-name {
  font-size: 26rpx;
  font-weight: 600;
  color: var(--gzang-text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 160rpx;
}

.nav-type-toggle {
  display: flex;
  margin-left: auto;
  background: var(--gzang-bg);
  border-radius: 20rpx;
  padding: 4rpx;
  gap: 4rpx;
}

.type-btn {
  padding: 8rpx 20rpx;
  border-radius: 16rpx;
  font-size: 24rpx;
  font-weight: 500;
  color: var(--gzang-text-tertiary);
  transition: all 0.2s ease;

  &.active {
    background: var(--gzang-surface);
    color: var(--gzang-text-primary);
    box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.08);
  }
}

// ========== 金额展示 ==========
.amount-area {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 32rpx;
}

.amount-wrap {
  display: flex;
  align-items: baseline;
  gap: 4rpx;
}

.currency-symbol {
  font-size: 44rpx;
  font-weight: 500;
  color: var(--gzang-text-secondary);
}

.amount-value {
  font-size: 96rpx;
  font-weight: 700;
  color: var(--gzang-primary);
  font-family: var(--font-mono);
  font-variant-numeric: tabular-nums;
  letter-spacing: -2rpx;
  line-height: 1;
}

// ========== 预算进度条 ==========
.budget-section {
  flex-shrink: 0;
  margin: 0 24rpx 16rpx;
  padding: 16rpx 24rpx;
  background: var(--gzang-surface);
  border-radius: 20rpx;
}

.budget-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14rpx;
}

.budget-info {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.budget-label {
  font-size: 24rpx;
  color: var(--gzang-text-tertiary);
}

.budget-text {
  font-size: 26rpx;
}

.budget-spent {
  font-weight: 600;
  font-family: var(--font-mono);
}

.budget-divider {
  color: var(--gzang-text-tertiary);
}

.budget-total {
  color: var(--gzang-text-secondary);
  font-family: var(--font-mono);
}

.budget-percent {
  font-size: 28rpx;
  font-weight: 700;
  font-family: var(--font-mono);
}

.budget-bar-track {
  position: relative;
  height: 8rpx;
  background: var(--gzang-bg);
  border-radius: 4rpx;
  overflow: hidden;
}

.budget-bar-fill {
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  border-radius: 4rpx;
  transition: width 0.4s ease;
}

.budget-bar-exceed {
  position: absolute;
  left: 100%;
  top: 0;
  height: 100%;
  background: #ff4757;
  opacity: 0.5;
}

.budget-footer {
  margin-top: 10rpx;
  text-align: right;
}

.budget-remaining {
  font-size: 22rpx;
  color: var(--gzang-success);

  &.exceeded {
    color: var(--gzang-danger);
  }
}

// ========== 下半部分（固定底部） ==========
.bottom-pane {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  background: var(--gzang-bg);
  border-top: 1rpx solid var(--gzang-border);
  z-index: 10;
}

// ========== 二级分类标签 ==========
.subcategory-section {
  flex-shrink: 0;
  padding: 12rpx 24rpx 10rpx;
}

.subcategory-inner {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.subcategory-tag {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4rpx;
  width: calc((100% - 48rpx) / 5);
  padding: 10rpx 4rpx;
  background: var(--gzang-surface);
  border: 2rpx solid var(--gzang-border);
  border-radius: 16rpx;
  box-sizing: border-box;
  transition: all 0.2s ease;

  &.selected {
    font-weight: 600;
  }

  &:not(.selected):active {
    opacity: 0.7;
  }
}

.sub-tag-icon {
  font-size: 18rpx;
}

.sub-tag-name {
  font-size: 20rpx;
  color: var(--gzang-text-secondary);
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.subcategory-tag.selected .sub-tag-name {
  font-weight: 600;
}

// ========== 小矩形横排 ==========
.tile-row {
  flex-shrink: 0;
  display: flex;
  gap: 10rpx;
  padding: 0 24rpx 10rpx;
}

.tile-item {
  flex: 1;
  min-width: 0;
  background: linear-gradient(145deg, var(--gzang-surface) 0%, color-mix(in srgb, var(--gzang-primary) 8%, var(--gzang-surface)) 100%);
  border-radius: 16rpx;
  padding: 16rpx 12rpx;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  position: relative;
  min-height: 88rpx;
  border: 1rpx solid color-mix(in srgb, var(--gzang-primary) 15%, transparent);
  box-shadow: 0 2rpx 8rpx color-mix(in srgb, var(--gzang-primary) 8%, transparent);
  transition: all 0.25s ease;

  &:active {
    opacity: 0.9;
    transform: scale(0.98);
    box-shadow: 0 1rpx 4rpx color-mix(in srgb, var(--gzang-primary) 5%, transparent);
  }
}

.tile-icon-row {
  display: flex;
  align-items: center;
  gap: 6rpx;
}

.tile-label {
  font-size: 20rpx;
  color: var(--gzang-text-tertiary);
  font-weight: 500;
}

.tile-value {
  display: flex;
  align-items: center;
  flex: 1;
  min-width: 0;
}

.tile-placeholder {
  font-size: 22rpx;
  color: var(--gzang-text-tertiary);
}

.tile-text-content {
  font-size: 22rpx;
  color: var(--gzang-secondary);
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  text-overflow: ellipsis;
  white-space: nowrap;
  width: 100%;
}

.tile-tags-preview {
  font-size: 22rpx;
  color: var(--gzang-text-primary);
  display: flex;
  gap: 4rpx;
  overflow: hidden;
}

.tile-tags-preview text {
  background: var(--gzang-secondary);
  color: white;
  padding: 2rpx 6rpx;
  border-radius: 4rpx;
  flex-shrink: 0;
  font-size: 20rpx;
}

.tile-payment {
  display: flex;
  align-items: center;
  gap: 4rpx;
  font-size: 22rpx;
  color: var(--gzang-text-primary);
}

.tile-expand-panel {
  position: absolute;
  left: 0;
  right: 0;
  top: 100%;
  z-index: 100;
  margin-top: 8rpx;
  background: var(--gzang-surface);
  border-radius: 16rpx;
  padding: 16rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.12);
  border: 1rpx solid var(--gzang-border);
}

.tags-popup-content {
  padding: 16rpx;
}

.remark-popup-content {
  padding: 16rpx 16rpx 32rpx;
}

// ========== 展开面板样式 ==========
.tag-input-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 16rpx;
}

.tag-input-field {
  flex: 1;
  height: 64rpx;
  padding: 0 20rpx;
  background: var(--gzang-bg);
  border: 1rpx solid var(--gzang-border);
  border-radius: 32rpx;
  font-size: 26rpx;
  color: var(--gzang-text-primary);

  &::placeholder {
    color: var(--gzang-text-tertiary);
  }
}

.tag-add-btn {
  width: 64rpx;
  height: 64rpx;
  background: color-mix(in srgb, var(--gzang-secondary) 12%, transparent);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;

  &:active {
    background: color-mix(in srgb, var(--gzang-secondary) 20%, transparent);
  }
}

.tag-selected-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
  margin-bottom: 16rpx;
}

.tag-selected-item {
  display: flex;
  align-items: center;
  gap: 6rpx;
  padding: 8rpx 16rpx;
  background: var(--gzang-secondary);
  color: white;
  border-radius: 8rpx;
  font-size: 24rpx;

  &:active {
    opacity: 0.8;
  }
}

.quick-tags-section {
  display: flex;
  align-items: flex-start;
  gap: 12rpx;
  margin-bottom: 16rpx;
}

.all-tags-section {
  border-top: 1rpx solid var(--gzang-border);
  padding-top: 16rpx;
}

.all-tags-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12rpx;
  margin-bottom: 12rpx;
}

.all-tags-label {
  font-size: 22rpx;
  color: var(--gzang-text-tertiary);
  flex-shrink: 0;
}

.tag-search-input {
  flex: 1;
  height: 56rpx;
  padding: 0 18rpx;
  background: var(--gzang-bg);
  border: 1rpx solid var(--gzang-border);
  border-radius: 28rpx;
  font-size: 24rpx;
  color: var(--gzang-text-primary);

  &::placeholder {
    color: var(--gzang-text-tertiary);
  }
}

.all-tags-wrap {
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
  max-height: 360rpx;
  overflow-y: auto;
}

.tag-empty {
  padding: 20rpx 0;
  display: flex;
  justify-content: center;
}

.tag-empty-text {
  font-size: 22rpx;
  color: var(--gzang-text-tertiary);
}

.tag-create-suggest {
  display: flex;
  align-items: center;
  gap: 10rpx;
  padding: 12rpx 14rpx;
  border-radius: 14rpx;
  border: 1rpx solid color-mix(in srgb, var(--gzang-secondary) 30%, transparent);
  background: color-mix(in srgb, var(--gzang-secondary) 8%, transparent);
  margin-bottom: 12rpx;

  &:active {
    opacity: 0.85;
  }
}

.tag-create-text {
  font-size: 24rpx;
  color: var(--gzang-text-secondary);
}

.all-tag-item {
  display: flex;
  align-items: center;
  gap: 10rpx;
  padding: 8rpx 14rpx;
  border-radius: 999rpx;
  background: var(--gzang-bg);
  border: 1rpx solid transparent;
  color: var(--gzang-text-secondary);
  font-size: 24rpx;

  &.active {
    background: color-mix(in srgb, var(--gzang-secondary) 10%, transparent);
    border-color: color-mix(in srgb, var(--gzang-secondary) 40%, transparent);
    color: var(--gzang-secondary);
  }

  &:active {
    opacity: 0.8;
  }
}

.all-tag-dot {
  width: 14rpx;
  height: 14rpx;
  border-radius: 50%;
  box-shadow: 0 0 0 4rpx color-mix(in srgb, var(--gzang-surface) 75%, transparent);
}

.all-tag-name {
  max-width: 220rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.quick-tags-wrap {
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
}

.quick-tag-item {
  padding: 6rpx 14rpx;
  background: var(--gzang-bg);
  border-radius: 8rpx;
  font-size: 24rpx;
  color: var(--gzang-text-secondary);

  &.active {
    background: color-mix(in srgb, var(--gzang-secondary) 12%, transparent);
    color: var(--gzang-secondary);
  }

  &:active {
    opacity: 0.8;
  }
}

.remark-area {
  width: 100%;
  min-height: 140rpx;
  padding: 16rpx;
  background: var(--gzang-bg);
  border: 1rpx solid var(--gzang-border);
  border-radius: 16rpx;
  font-size: 28rpx;
  color: var(--gzang-text-primary);
  box-sizing: border-box;

  &::placeholder {
    color: var(--gzang-text-tertiary);
  }
}

.remark-counter {
  display: block;
  text-align: right;
  margin-top: 8rpx;
  font-size: 22rpx;
  color: var(--gzang-text-tertiary);
}

.payment-panel {
  padding: 16rpx 32rpx 20rpx;
}

.payment-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12rpx;
  padding: 16rpx;
}

.payment-cell {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6rpx;
  padding: 20rpx 8rpx;
  background: var(--gzang-bg);
  border: 2rpx solid transparent;
  border-radius: 16rpx;
  transition: all 0.2s ease;

  &.selected {
    background: rgba(251, 139, 36, 0.1);
    border-color: var(--gzang-secondary);
  }

  &:active {
    opacity: 0.8;
  }
}

.payment-cell-icon {
  font-size: 36rpx;
}

.payment-cell-name {
  font-size: 22rpx;
  color: var(--gzang-text-secondary);
}

.currency-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12rpx;
  padding: 16rpx 16rpx 32rpx;
}

.currency-cell {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4rpx;
  padding: 16rpx 8rpx;
  background: var(--gzang-bg);
  border: 2rpx solid var(--gzang-border);
  border-radius: 16rpx;
  transition: all 0.2s ease;

  &.selected {
    background: linear-gradient(135deg, rgba(15, 76, 92, 0.15), rgba(15, 76, 92, 0.08));
    border-color: var(--gzang-primary);
    box-shadow: 0 2rpx 8rpx rgba(15, 76, 92, 0.2);
    
    .currency-cell-symbol,
    .currency-cell-code {
      color: var(--gzang-primary);
    }
    
    .currency-cell-name {
      color: var(--gzang-primary);
    }
  }

  &:active {
    opacity: 0.8;
  }
}

.currency-cell-symbol {
  font-size: 32rpx;
  font-weight: 600;
  color: var(--gzang-primary);
}

.currency-cell-code {
  font-size: 22rpx;
  font-weight: 600;
  color: var(--gzang-text-primary);
}

.currency-cell-name {
  font-size: 20rpx;
  color: var(--gzang-text-tertiary);
}

.tile-date-item {
  flex: 1;
  min-width: 0;
}

.tile-datetime-picker {
  position: absolute;
  left: 0;
  top: -100px;
  width: 100%;
  height: 100%;
}

// ========== 数字键盘 ==========
.keypad-section {
  flex-shrink: 0;
  display: flex;
  padding: 10rpx 12rpx 0;
  gap: 10rpx;
  background: var(--gzang-bg);
}

.keypad-sidebar {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  width: 96rpx;
  flex-shrink: 0;
}

.sidebar-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--gzang-surface);
  border-radius: 10rpx;
  font-size: 24rpx;
  font-weight: 500;
  color: var(--gzang-text-secondary);

  &:active {
    background: var(--gzang-border);
  }
}

.currency-btn {
  background: var(--gzang-secondary);
}

.currency-symbol-text {
  font-size: 24rpx;
  font-weight: 700;
  color: white;
  line-height: 1;
}

.keypad-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 1rpx;
  background: var(--gzang-border);
  border-radius: 14rpx;
  overflow: hidden;
}

.keypad-row {
  display: flex;
  gap: 1rpx;
}

.key-btn {
  flex: 1;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--gzang-surface);
  transition: background-color 0.1s ease;

  &:active {
    background: color-mix(in srgb, var(--gzang-border) 50%, var(--gzang-surface));
  }
}

.key-num {
  font-size: 40rpx;
  font-weight: 500;
  color: var(--gzang-text-primary);
  font-family: var(--font-mono);
}

.key-delete {
  background: var(--gzang-bg);

  &:active {
    background: var(--gzang-border);
  }
}

.keypad-confirm-col {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  width: 100rpx;
  flex-shrink: 0;
}

.keypad-action-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 14rpx;
  transition: all 0.2s ease;

  &:active {
    transform: scale(0.96);
    opacity: 0.9;
  }
}

.delete-btn {
  background: var(--gzang-surface);
}

.confirm-btn {
  background: var(--gzang-secondary);
  opacity: 0.5;

  &.ready {
    opacity: 1;
  }
}

.confirm-char {
  font-size: 40rpx;
  font-weight: 700;
  color: white;
  line-height: 1;
}

.confirm-label {
  font-size: 20rpx;
  color: rgba(255, 255, 255, 0.85);
  margin-top: 2rpx;
}

// ========== 弹窗样式 ==========
.picker-sheet {
  background: var(--gzang-surface);
  border-radius: 24rpx 24rpx 0 0;
  overflow: hidden;
  padding-bottom: env(safe-area-inset-bottom);
}

.picker-handle-bar {
  width: 80rpx;
  height: 6rpx;
  background: var(--gzang-border);
  border-radius: 3rpx;
  margin: 16rpx auto 0;
}

.picker-hd {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 32rpx;
  border-bottom: 1rpx solid var(--gzang-border);
}

.picker-cancel {
  font-size: 28rpx;
  color: var(--gzang-text-secondary);
}

.picker-done {
  font-size: 28rpx;
  font-weight: 600;
  color: var(--gzang-secondary);
}

.picker-placeholder {
  width: 120rpx;
}

.picker-title {
  font-size: 30rpx;
  font-weight: 600;
  color: var(--gzang-text-primary);
}

.category-picker-scroll {
  max-height: 600rpx;
}

.cat-parent-row {
  border-bottom: 1rpx solid var(--gzang-border);

  &:last-child {
    border-bottom: none;
  }
}

.cat-parent-cell {
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 24rpx 32rpx;
  transition: background-color 0.2s ease;

  &:active {
    background: var(--gzang-bg);
  }
}

.cat-parent-icon {
  font-size: 32rpx;
}

.cat-parent-name {
  flex: 1;
  font-size: 28rpx;
  font-weight: 500;
  color: var(--gzang-text-primary);
}

.cat-children-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  padding: 0 32rpx 16rpx;
}

.cat-child-cell {
  padding: 8rpx 18rpx;
  background: var(--gzang-bg);
  border-radius: 20rpx;
  font-size: 24rpx;
  color: var(--gzang-text-secondary);
  transition: all 0.2s ease;

  &.selected {
    background: color-mix(in srgb, var(--gzang-secondary) 12%, transparent);
    color: var(--gzang-secondary);
    border: 1rpx solid var(--gzang-secondary);
  }

  &:active {
    opacity: 0.8;
  }
}
</style>
