<template>
    <view class="home-page">
        <!-- 顶部状态栏 -->
        <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>

        <!-- 顶部导航栏 -->
        <view class="nav-bar" :style="{ height: navBarHeight + 'px' }">
            <view class="nav-content">
                <view class="nav-left">
                    <text class="nav-title">G-Zang</text>
                </view>
                <view class="nav-right">
                    <view class="nav-action" @click="goToSearch">
                        <uni-icons type="search" size="20" color="#333"></uni-icons>
                    </view>
                    <view class="nav-action" @click="goToSettings">
                        <uni-icons type="settings" size="20" color="#333"></uni-icons>
                    </view>
                </view>
            </view>
        </view>

        <!-- 主要内容区域 -->
        <scroll-view class="main-content" scroll-y="true" :style="{ top: totalHeaderHeight + 'px' }"
            @scrolltolower="onScrollToLower" :scroll-with-animation="true">
            <!-- 资产概览卡片 -->
            <view class="asset-card">
                <view class="asset-header" @click="goToAccounts">
                    <text class="asset-title">总资产</text>
                    <view class="asset-action">
                        <uni-icons type="right" size="16" color="#666"></uni-icons>
                    </view>
                </view>

                <view class="asset-amount">
                    <text class="amount-text">{{ formatAmount(assets.totalAssets) }}</text>
                </view>

                <view class="asset-change" v-if="assets.changeAmount !== 0">
                    <text class="change-label">较昨日</text>
                    <text class="change-value"
                        :class="{ positive: assets.changeAmount > 0, negative: assets.changeAmount < 0 }">
                        {{ assets.changeAmount > 0 ? '+' : '' }}{{ formatAmount(assets.changeAmount) }}
                    </text>
                </view>
            </view>

            <!-- 快捷操作 -->
            <view class="quick-actions">
                <view class="actions-grid">
                    <view v-for="action in quickActions" :key="action.key" class="action-item"
                        @click="handleAction(action)">
                        <view class="action-icon" :class="action.color">
                            <uni-icons :type="action.icon" size="24"></uni-icons>
                        </view>
                        <text class="action-text">{{ action.text }}</text>
                    </view>
                </view>
            </view>

            <!-- 今日收支概览 -->
            <view class="daily-summary">
                <view class="summary-header">
                    <text class="summary-title">今日收支</text>
                </view>

                <view class="summary-content">
                    <view class="summary-item">
                        <view class="summary-value income">
                            +{{ formatAmount(dailySummary.income) }}
                        </view>
                        <text class="summary-label">收入</text>
                    </view>

                    <view class="summary-divider"></view>

                    <view class="summary-item">
                        <view class="summary-value expense">
                            -{{ formatAmount(dailySummary.expense) }}
                        </view>
                        <text class="summary-label">支出</text>
                    </view>

                    <view class="summary-divider"></view>

                    <view class="summary-item">
                        <view class="summary-value balance">
                            {{ formatAmount(dailySummary.balance) }}
                        </view>
                        <text class="summary-label">结余</text>
                    </view>
                </view>
            </view>

            <!-- 最近交易记录 -->
            <view class="recent-transactions">
                <view class="section-header">
                    <text class="section-title">最近记录</text>
                    <view class="section-action" @click="goToBills">
                        <text class="action-text">查看全部</text>
                        <uni-icons type="right" size="14" color="#666"></uni-icons>
                    </view>
                </view>

                <view v-if="recentTransactions.length > 0" class="transactions-list">
                    <view v-for="transaction in recentTransactions" :key="transaction.id" class="transaction-item"
                        @click="goToTransactionDetail(transaction)">
                        <view class="transaction-icon">
                            <uni-icons :type="getCategoryIcon(transaction.categoryId)" size="20"
                                color="#666"></uni-icons>
                        </view>

                        <view class="transaction-content">
                            <text class="transaction-title">{{ transaction.categoryName }}</text>
                            <text class="transaction-time">{{ formatRelativeTime(transaction.transactionTime) }}</text>
                        </view>

                        <view class="transaction-amount">
                            <text class="amount-text"
                                :class="{ income: transaction.type === 1, expense: transaction.type === 2 }">
                                {{ transaction.type === 1 ? '+' : '-' }}{{ formatAmount(transaction.amount) }}
                            </text>
                        </view>
                    </view>
                </view>

                <view v-else class="empty-state">
                    <view class="empty-icon">
                        <uni-icons type="compose" size="48" color="#ccc"></uni-icons>
                    </view>
                    <text class="empty-text">暂无交易记录</text>
                    <text class="empty-desc">开始记账，记录您的收支情况</text>
                </view>
            </view>

            <!-- 底部安全区域 -->
            <view class="bottom-safe-area" :style="{ height: safeAreaBottom + 'px' }"></view>
        </scroll-view>

        <!-- 加载状态 -->
        <view v-if="loading" class="loading-overlay">
            <view class="loading-content">
                <uni-load-more status="loading" />
                <text class="loading-text">加载中...</text>
            </view>
        </view>
    </view>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useAppStore } from '@/stores/app'
import { useAuthStore } from '@/stores/auth'
import { formatAmount, formatRelativeTime } from '@/utils/format'
import { DeviceUtils } from '@/utils/device'

// 状态管理
const appStore = useAppStore()
const authStore = useAuthStore()

// 响应式数据
const loading = ref(false)
const refreshing = ref(false)

// 设备信息
const statusBarHeight = ref(0)
const navBarHeight = ref(44)
const safeAreaBottom = ref(0)
const totalHeaderHeight = computed(() => statusBarHeight.value + navBarHeight.value)

// 资产数据
const assets = reactive({
    totalAssets: 12580.50,
    changeAmount: 250.00
})

// 今日收支
const dailySummary = reactive({
    income: 500.00,
    expense: 120.50,
    balance: 379.50
})

// 快捷操作
const quickActions = [
    { key: 'income', icon: 'plus', text: '记收入', color: 'success' },
    { key: 'expense', icon: 'minus', text: '记支出', color: 'danger' },
    { key: 'transfer', icon: 'exchange', text: '转账', color: 'primary' },
    { key: 'scan', icon: 'scan', text: '扫码记账', color: 'info' },
    { key: 'voice', icon: 'volume-o', text: '语音记账', color: 'warning' },
    { key: 'photo', icon: 'photograph', text: '拍照识别', color: 'secondary' },
    { key: 'template', icon: 'compose', text: '使用模板', color: 'default' },
    { key: 'more', icon: 'more', text: '更多', color: 'default' }
]

// 最近交易记录
const recentTransactions = ref([
    {
        id: 12345,
        categoryId: 101,
        categoryName: '餐饮',
        transactionTime: '2024-01-15 12:30:00',
        amount: 25.50,
        type: 2
    },
    {
        id: 12346,
        categoryId: 102,
        categoryName: '交通',
        transactionTime: '2024-01-15 08:15:00',
        amount: 8.00,
        type: 2
    },
    {
        id: 12347,
        categoryId: 201,
        categoryName: '工资',
        transactionTime: '2024-01-14 18:00:00',
        amount: 5000.00,
        type: 1
    }
])

// 获取分类图标
const getCategoryIcon = (categoryId: number) => {
    const iconMap: Record<number, string> = {
        101: 'restaurant', // 餐饮
        102: 'location-o', // 交通
        201: 'moneybag'    // 工资
    }
    return iconMap[categoryId] || 'circle'
}

// 处理快捷操作
const handleAction = (action: typeof quickActions[0]) => {
    switch (action.key) {
        case 'income':
            uni.navigateTo({ url: '/pages/accounting/index?type=income' })
            break
        case 'expense':
            uni.navigateTo({ url: '/pages/accounting/index?type=expense' })
            break
        case 'transfer':
            uni.navigateTo({ url: '/pages/accounting/index?type=transfer' })
            break
        case 'scan':
            // 扫码记账
            uni.scanCode({
                success: (res) => {
                    console.log('扫码结果:', res)
                    // 处理扫码结果
                }
            })
            break
        case 'voice':
            appStore.showInfo('语音记账功能开发中')
            break
        case 'photo':
            appStore.showInfo('拍照识别功能开发中')
            break
        case 'template':
            appStore.showInfo('模板功能开发中')
            break
        case 'more':
            uni.showActionSheet({
                itemList: ['预算设置', '分类管理', '账户管理'],
                success: (res) => {
                    console.log('选中了:', res.tapIndex)
                }
            })
            break
    }
}

// 跳转到搜索
const goToSearch = () => {
    uni.navigateTo({ url: '/pages/search/index' })
}

// 跳转到设置
const goToSettings = () => {
    uni.navigateTo({ url: '/pages/settings/index' })
}

// 跳转到账户管理
const goToAccounts = () => {
    uni.navigateTo({ url: '/pages/accounts/index' })
}

// 跳转到账单列表
const goToBills = () => {
    uni.switchTab({ url: '/pages/bills/index' })
}

// 跳转到交易详情
const goToTransactionDetail = (transaction: any) => {
    uni.navigateTo({
        url: `/pages/bills/detail?id=${transaction.id}`
    })
}

// 下拉刷新
const onPullDownRefresh = async () => {
    try {
        refreshing.value = true
        // 重新加载数据
        await loadData()
    } finally {
        refreshing.value = false
        uni.stopPullDownRefresh()
    }
}

// 滚动到底部加载更多
const onScrollToLower = () => {
    // 加载更多数据逻辑
    console.log('滚动到底部')
}

// 加载数据
const loadData = async () => {
    try {
        loading.value = true
        // 模拟加载数据
        await new Promise(resolve => setTimeout(resolve, 1000))

        // 更新数据
        // 这里应该调用实际的API
    } catch (error) {
        console.error('加载数据失败:', error)
        appStore.showError('加载数据失败')
    } finally {
        loading.value = false
    }
}

// 初始化
onMounted(async () => {
    // 获取设备信息
    const deviceInfo = await DeviceUtils.getDeviceInfo()
    statusBarHeight.value = deviceInfo.statusBarHeight
    safeAreaBottom.value = deviceInfo.safeAreaInsets.bottom

    // 检查登录状态
    if (!authStore.isAuthenticated) {
        uni.reLaunch({ url: '/pages/login/index' })
        return
    }

    // 加载数据
    await loadData()
})

// 页面显示时刷新数据
uni.onShow = () => {
    if (authStore.isAuthenticated) {
        loadData()
    }
}

// 启用下拉刷新
uni.startPullDownRefresh = onPullDownRefresh
</script>

<style lang="scss" scoped>
.home-page {
    height: 100vh;
    background-color: #f5f5f5;
    position: relative;
}

.status-bar {
    background-color: #fff;
}

.nav-bar {
    background-color: #fff;
    border-bottom: 1px solid #e5e5e5;
}

.nav-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 100%;
    padding: 0 16px;
}

.nav-title {
    font-size: 18px;
    font-weight: 600;
    color: #333;
}

.nav-action {
    padding: 8px;
    margin-left: 8px;
    border-radius: 6px;
    transition: background-color 0.2s;

    &:active {
        background-color: #f0f0f0;
    }
}

.main-content {
    position: absolute;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: #f5f5f5;
}

.asset-card {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    margin: 16px;
    border-radius: 16px;
    padding: 24px;
    color: white;
    position: relative;
    overflow: hidden;

    &::before {
        content: '';
        position: absolute;
        top: -50%;
        right: -50%;
        width: 200%;
        height: 200%;
        background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
        border-radius: 50%;
    }
}

.asset-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    position: relative;
    z-index: 1;
}

.asset-title {
    font-size: 16px;
    opacity: 0.9;
}

.asset-action {
    padding: 4px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.2);
}

.asset-amount {
    margin-bottom: 8px;
    position: relative;
    z-index: 1;
}

.amount-text {
    font-size: 28px;
    font-weight: bold;
}

.asset-change {
    display: flex;
    align-items: center;
    position: relative;
    z-index: 1;
}

.change-label {
    font-size: 12px;
    opacity: 0.8;
    margin-right: 8px;
}

.change-value {
    font-size: 14px;
    font-weight: 600;

    &.positive {
        color: #52c41a;
    }

    &.negative {
        color: #ff4d4f;
    }
}

.quick-actions {
    margin: 16px;
}

.actions-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;
}

.action-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 16px 8px;
    background: white;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    transition: all 0.2s;

    &:active {
        transform: scale(0.95);
        box-shadow: 0 1px 4px rgba(0, 0, 0, 0.12);
    }
}

.action-icon {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 8px;

    &.success {
        background-color: #e8f5e8;
    }

    &.danger {
        background-color: #ffebe9;
    }

    &.primary {
        background-color: #e6f4ff;
    }

    &.info {
        background-color: #e6f7ff;
    }

    &.warning {
        background-color: #fffbe6;
    }

    &.secondary {
        background-color: #f5f5f5;
    }

    &.default {
        background-color: #fafafa;
    }
}

.action-text {
    font-size: 12px;
    color: #666;
    text-align: center;
}

.daily-summary {
    background: white;
    margin: 16px;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.summary-header {
    margin-bottom: 16px;
}

.summary-title {
    font-size: 16px;
    font-weight: 600;
    color: #333;
}

.summary-content {
    display: flex;
    justify-content: space-around;
    align-items: center;
}

.summary-item {
    flex: 1;
    text-align: center;
}

.summary-value {
    font-size: 18px;
    font-weight: 600;
    margin-bottom: 4px;

    &.income {
        color: #52c41a;
    }

    &.expense {
        color: #ff4d4f;
    }

    &.balance {
        color: #1989fa;
    }
}

.summary-label {
    font-size: 12px;
    color: #999;
}

.summary-divider {
    width: 1px;
    height: 40px;
    background-color: #e5e5e5;
}

.recent-transactions {
    background: white;
    margin: 16px;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    overflow: hidden;
}

.section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 20px 0 20px;
    margin-bottom: 16px;
}

.section-title {
    font-size: 16px;
    font-weight: 600;
    color: #333;
}

.section-action {
    display: flex;
    align-items: center;
    cursor: pointer;
}

.action-text {
    font-size: 14px;
    color: #666;
    margin-right: 4px;
}

.transactions-list {
    padding: 0 20px 20px 20px;
}

.transaction-item {
    display: flex;
    align-items: center;
    padding: 16px 0;
    border-bottom: 1px solid #f0f0f0;

    &:last-child {
        border-bottom: none;
    }

    &:active {
        background-color: #f9f9f9;
    }
}

.transaction-icon {
    width: 40px;
    height: 40px;
    border-radius: 8px;
    background-color: #f5f5f5;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 12px;
}

.transaction-content {
    flex: 1;
}

.transaction-title {
    font-size: 15px;
    color: #333;
    display: block;
    margin-bottom: 4px;
}

.transaction-time {
    font-size: 12px;
    color: #999;
}

.transaction-amount {
    text-align: right;
}

.amount-text {
    font-size: 16px;
    font-weight: 600;

    &.income {
        color: #52c41a;
    }

    &.expense {
        color: #ff4d4f;
    }
}

.empty-state {
    padding: 40px 20px;
    text-align: center;
}

.empty-icon {
    margin-bottom: 16px;
}

.empty-text {
    font-size: 16px;
    color: #666;
    display: block;
    margin-bottom: 8px;
}

.empty-desc {
    font-size: 12px;
    color: #999;
}

.bottom-safe-area {
    background-color: #f5f5f5;
}

.loading-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(255, 255, 255, 0.8);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 9999;
}

.loading-content {
    display: flex;
    flex-direction: column;
    align-items: center;
}

.loading-text {
    margin-top: 8px;
    font-size: 14px;
    color: #666;
}
</style>
