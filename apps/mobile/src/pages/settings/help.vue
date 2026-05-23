<template>
  <PageTransition>
    <view class="help-page apple-style">
      <view class="nav-large-title">
        <view class="nav-header">
          <view class="nav-back" @click="goBack">
            <AppleIcon name="left" :size="20" color="var(--gzang-text-primary)" />
          </view>
          <text class="nav-title">{{ t('settings.helpFeedback') }}</text>
          <view style="width: 36px;"></view>
        </view>
      </view>

      <view class="main-content">
        <!-- Quick FAQ -->
        <view class="section-title">常见问题</view>

        <view class="faq-list">
          <view
            v-for="(item, index) in faqItems"
            :key="index"
            class="faq-item"
            @click="toggleFaq(index)"
          >
            <view class="faq-header">
              <text class="faq-q">{{ item.q }}</text>
              <AppleIcon
                :name="expandedIndex === index ? 'chevron-up' : 'chevron-down'"
                :size="16"
                color="var(--gzang-text-tertiary)"
              />
            </view>
            <view v-if="expandedIndex === index" class="faq-a">
              <text>{{ item.a }}</text>
            </view>
          </view>
        </view>

        <!-- Contact -->
        <view class="section-title" style="margin-top: var(--apple-space-5)">联系客服</view>
        <view class="contact-card">
          <view class="contact-item" @click="handleEmail">
            <view class="contact-icon" style="background: rgba(15, 76, 92, 0.1)">
              <AppleIcon name="mail" :size="20" color="var(--gzang-primary)" />
            </view>
            <view class="contact-info">
              <text class="contact-label">邮箱反馈</text>
              <text class="contact-value">support@gzang.app</text>
            </view>
            <AppleIcon name="chevron-right" :size="16" color="var(--gzang-text-tertiary)" />
          </view>

          <view class="contact-divider"></view>

          <view class="contact-item" @click="handleWechat">
            <view class="contact-icon" style="background: rgba(6, 214, 160, 0.1)">
              <AppleIcon name="chat" :size="20" color="var(--gzang-success)" />
            </view>
            <view class="contact-info">
              <text class="contact-label">微信客服</text>
              <text class="contact-value">G-Zang-Financial</text>
            </view>
            <AppleIcon name="chevron-right" :size="16" color="var(--gzang-text-tertiary)" />
          </view>

          <view class="contact-divider"></view>

          <view class="contact-item" @click="handleFeedback">
            <view class="contact-icon" style="background: rgba(251, 139, 36, 0.1)">
              <AppleIcon name="edit" :size="20" color="var(--gzang-secondary)" />
            </view>
            <view class="contact-info">
              <text class="contact-label">意见建议</text>
              <text class="contact-value">提交功能建议和 Bug 反馈</text>
            </view>
            <AppleIcon name="chevron-right" :size="16" color="var(--gzang-text-tertiary)" />
          </view>
        </view>

        <!-- Version -->
        <view class="version-info">
          <text>G-Zang 归藏 v1.0.0</text>
        </view>
      </view>
    </view>
  </PageTransition>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import PageTransition from '@/components/common/PageTransition/index.vue'
import AppleIcon from '@/components/common/AppleIcon/index.vue'

const { t } = useI18n()

const expandedIndex = ref<number | null>(null)

const faqItems = [
  {
    q: '如何添加新的账户？',
    a: '进入"账户"页面，点击右上角的"+"按钮，选择账户类型（现金、银行卡、支付宝等），填写账户名称和初始余额即可创建新账户。'
  },
  {
    q: '如何设置每月预算？',
    a: '进入"预算"页面，点击右上角的"+"按钮，选择预算周期（周/月/年），再选择要预算的分类，设定金额即可。系统会在超支时自动提醒您。'
  },
  {
    q: '支持哪些账单数据导出格式？',
    a: '目前支持导出为 CSV 格式，可用于 Excel 等表格软件打开。您也可以通过分享功能将账单数据发送给其他人。'
  },
  {
    q: '如何切换深色模式？',
    a: '进入"设置"页面，点击"主题设置"，可以选择浅色、深色或跟随系统三种模式。'
  },
  {
    q: '记账数据会自动备份吗？',
    a: '是的，所有记账数据都会实时同步到云端。换设备登录同一账号后，数据会自动恢复，无需手动备份。'
  },
  {
    q: '如何删除或编辑已记账的记录？',
    a: '进入"账单"页面，点击任意一条记录即可查看详情，在详情页可以编辑或删除该记录。'
  },
]

function toggleFaq(index: number) {
  expandedIndex.value = expandedIndex.value === index ? null : index
}

function goBack() { uni.navigateBack() }

function handleEmail() {
  uni.setClipboardData({
    data: 'support@gzang.app',
    success: () => uni.showToast({ title: '邮箱已复制', icon: 'success' })
  })
}

function handleWechat() {
  uni.setClipboardData({
    data: 'G-Zang-Financial',
    success: () => uni.showToast({ title: '微信号已复制', icon: 'success' })
  })
}

function handleFeedback() {
  uni.showToast({ title: '意见反馈功能即将上线', icon: 'none' })
}
</script>

<style lang="scss" scoped>
.help-page {
  min-height: 100vh;
  background: var(--gzang-bg);
}

.nav-large-title {
  background: var(--gzang-bg);
  padding: 0 var(--apple-space-4);
  padding-top: calc(constant(safe-area-inset-top) + var(--apple-space-3));
}

.nav-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--apple-space-3);
}

.nav-back {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--gzang-surface);
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-title {
  font-size: var(--apple-text-lg);
  font-weight: var(--apple-font-semibold);
  color: var(--gzang-text-primary);
}

.main-content {
  padding: 0 var(--apple-space-4);
}

.section-title {
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-semibold);
  color: var(--gzang-text-secondary);
  margin-bottom: var(--apple-space-3);
}

.faq-list {
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-xl);
  overflow: hidden;
  box-shadow: var(--apple-shadow-sm);
}

.faq-item {
  border-bottom: 0.5px solid var(--gzang-border);

  &:last-child { border-bottom: none; }
}

.faq-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--apple-space-4);
}

.faq-q {
  flex: 1;
  font-size: var(--apple-text-base);
  font-weight: var(--apple-font-medium);
  color: var(--gzang-text-primary);
}

.faq-a {
  padding: 0 var(--apple-space-4) var(--apple-space-4);
  font-size: var(--apple-text-sm);
  color: var(--gzang-text-secondary);
  line-height: 1.6;
}

.contact-card {
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-xl);
  padding: var(--apple-space-3) var(--apple-space-4);
  box-shadow: var(--apple-shadow-sm);
}

.contact-item {
  display: flex;
  align-items: center;
  padding: var(--apple-space-4) 0;
}

.contact-icon {
  width: 44px;
  height: 44px;
  border-radius: var(--apple-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: var(--apple-space-3);
  flex-shrink: 0;
}

.contact-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.contact-label {
  font-size: var(--apple-text-base);
  font-weight: var(--apple-font-medium);
  color: var(--gzang-text-primary);
}

.contact-value {
  font-size: var(--apple-text-sm);
  color: var(--gzang-text-tertiary);
  margin-top: 2px;
}

.contact-divider {
  height: 1px;
  background: var(--gzang-border);
}

.version-info {
  text-align: center;
  padding: var(--apple-space-8) 0 var(--apple-space-4);
  font-size: var(--apple-text-xs);
  color: var(--gzang-text-tertiary);
}
</style>
