<template>
  <view class="privacy-page">
    <!-- 内容区域 -->
    <view class="content-card">
      <view class="section">
        <text class="section-title">{{ $t('privacy.section1Title') }}</text>
        <text class="section-content">{{ $t('privacy.section1Content') }}</text>
      </view>

      <view class="section">
        <text class="section-title">{{ $t('privacy.section2Title') }}</text>
        <text class="section-content">{{ $t('privacy.section2Content') }}</text>
      </view>

      <view class="section">
        <text class="section-title">{{ $t('privacy.section3Title') }}</text>
        <text class="section-content">{{ $t('privacy.section3Content') }}</text>
      </view>

      <view class="section">
        <text class="section-title">{{ $t('privacy.section4Title') }}</text>
        <text class="section-content">{{ $t('privacy.section4Content') }}</text>
      </view>

      <view class="section">
        <text class="section-title">{{ $t('privacy.section5Title') }}</text>
        <text class="section-content">{{ $t('privacy.section5Content') }}</text>
      </view>

      <view class="section">
        <text class="section-title">{{ $t('privacy.section6Title') }}</text>
        <text class="section-content">{{ $t('privacy.section6Content') }}</text>
      </view>

      <view class="section">
        <text class="section-title">{{ $t('privacy.section7Title') }}</text>
        <text class="section-content">{{ $t('privacy.section7Content') }}</text>
      </view>

      <view class="section">
        <text class="section-title">{{ $t('privacy.section8Title') }}</text>
        <text class="section-content">{{ $t('privacy.section8Content') }}</text>
      </view>

      <view class="footer">
        <text class="footer-text">{{ $t('privacy.footer') }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
</script>

<style lang="scss" scoped>
.privacy-page {
  min-height: 100vh;
  background: var(--gzang-bg, #F8F9FA);
  padding: 16px;
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
