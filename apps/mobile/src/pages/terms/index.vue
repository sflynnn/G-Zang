<template>
  <view class="terms-page">
    <!-- 内容区域 -->
    <view class="content-card">
      <view class="section">
        <text class="section-title">{{ $t('terms.section1Title') }}</text>
        <text class="section-content">{{ $t('terms.section1Content') }}</text>
      </view>

      <view class="section">
        <text class="section-title">{{ $t('terms.section2Title') }}</text>
        <text class="section-content">{{ $t('terms.section2Content') }}</text>
      </view>

      <view class="section">
        <text class="section-title">{{ $t('terms.section3Title') }}</text>
        <text class="section-content">{{ $t('terms.section3Content') }}</text>
      </view>

      <view class="section">
        <text class="section-title">{{ $t('terms.section4Title') }}</text>
        <text class="section-content">{{ $t('terms.section4Content') }}</text>
      </view>

      <view class="section">
        <text class="section-title">{{ $t('terms.section5Title') }}</text>
        <text class="section-content">{{ $t('terms.section5Content') }}</text>
      </view>

      <view class="section">
        <text class="section-title">{{ $t('terms.section6Title') }}</text>
        <text class="section-content">{{ $t('terms.section6Content') }}</text>
      </view>

      <view class="section">
        <text class="section-title">{{ $t('terms.section7Title') }}</text>
        <text class="section-content">{{ $t('terms.section7Content') }}</text>
      </view>

      <view class="footer">
        <text class="footer-text">{{ $t('terms.footer') }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
</script>

<style lang="scss" scoped>
.terms-page {
  min-height: 100vh;
  background: var(--gzang-bg, #F8F9FA);
  padding: 16px 16px 40px 16px;
}

.content-card {
  background: var(--gzang-surface, #FFFFFF);
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.section {
  margin-bottom: 24px;

  &:last-of-type {
    margin-bottom: 0;
  }
}

.section-title {
  display: block;
  font-size: 16px;
  font-weight: 600;
  color: var(--gzang-text-primary, #1F2937);
  margin-bottom: 10px;
}

.section-content {
  display: block;
  font-size: 14px;
  line-height: 1.8;
  color: var(--gzang-text-secondary, #6B7280);
}

.footer {
  margin-top: 32px;
  padding-top: 16px;
  border-top: 1px solid var(--gzang-border, #E5E7EB);
  text-align: center;
}

.footer-text {
  font-size: 12px;
  color: var(--gzang-text-secondary, #9CA3AF);
}
</style>
