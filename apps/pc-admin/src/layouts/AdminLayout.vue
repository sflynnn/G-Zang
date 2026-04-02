<template>
  <div class="admin-layout min-h-screen flex" :class="{ 'dark': isDark }">
    <!-- 侧边栏 -->
    <aside 
      class="sidebar flex flex-col fixed left-0 top-0 h-full z-40 transition-all duration-300"
      :class="[
        isDark ? 'bg-primary-dark' : 'bg-primary',
        collapsed ? 'w-16' : 'w-60'
      ]"
    >
      <!-- Logo区域 -->
      <div 
        class="h-16 flex items-center px-4 border-b shrink-0"
        :class="isDark ? 'border-white/10' : 'border-white/20'"
      >
        <div class="flex items-center gap-3 overflow-hidden">
          <div class="w-8 h-8 rounded-lg bg-white/20 flex items-center justify-center shrink-0">
            <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"/>
            </svg>
          </div>
          <Transition name="fade-slide">
            <span v-if="!collapsed" class="text-white font-semibold text-lg whitespace-nowrap">
              {{ $t('common.adminPanel') }}
            </span>
          </Transition>
        </div>
      </div>

      <!-- 菜单区域 -->
      <nav class="flex-1 overflow-y-auto py-4 px-2">
        <ul class="space-y-1">
          <li v-for="item in filteredMenuOptions" :key="item.key">
            <button
              @click="handleMenuClick(item.key)"
              class="w-full flex items-center gap-3 px-3 py-2.5 rounded-lg transition-all duration-200 group"
              :class="[
                activeKey === item.key
                  ? 'bg-white/20 text-white'
                  : 'text-white/70 hover:bg-white/10 hover:text-white'
              ]"
              :title="collapsed ? item.label : ''"
            >
              <n-icon 
                :component="getMenuIcon(item.key)" 
                :size="20" 
                class="shrink-0"
              />
              <Transition name="fade-slide">
                <span v-if="!collapsed" class="text-sm font-medium whitespace-nowrap">
                  {{ item.label }}
                </span>
              </Transition>
              <!-- 激活指示器 -->
              <div 
                v-if="activeKey === item.key && !collapsed"
                class="absolute left-0 w-1 h-6 bg-secondary rounded-r-full"
              ></div>
            </button>
          </li>
        </ul>
      </nav>

      <!-- 折叠控制条 -->
      <div 
        class="absolute -right-3 top-1/2 -translate-y-1/2 z-50"
      >
        <button
          @click="toggleSidebar"
          class="w-6 h-12 rounded-full bg-primary dark:bg-primary-dark shadow-lg flex items-center justify-center hover:scale-110 hover:shadow-xl transition-all duration-200"
          :title="collapsed ? $t('common.expand') : $t('common.collapse')"
        >
          <n-icon 
            :component="collapsed ? ChevronForwardOutline : ChevronBackOutline" 
            :size="14"
            class="text-white/90"
          />
        </button>
      </div>
    </aside>

    <!-- 主内容区域 -->
    <div 
      class="flex-1 flex flex-col transition-all duration-300"
      :class="collapsed ? 'ml-16' : 'ml-60'"
    >
      <!-- 顶部导航栏 -->
      <header 
        class="h-16 sticky top-0 z-30 flex items-center justify-between px-6 border-b backdrop-blur-lg"
        :class="isDark 
          ? 'bg-dark-surface/80 border-dark-border' 
          : 'bg-surface/80 border-border'"
      >
        <!-- 左侧：面包屑 -->
        <div class="flex items-center gap-4">
          <!-- 移动端菜单按钮 -->
          <button
            @click="toggleSidebar"
            class="lg:hidden p-2 rounded-lg transition-colors"
            :class="isDark ? 'hover:bg-dark-surface-elevated text-dark-text-secondary' : 'hover:bg-gray-100 text-text-secondary'"
          >
            <n-icon :component="MenuOutline" :size="20" />
          </button>

          <!-- 面包屑 -->
          <nav class="flex items-center gap-2 text-sm">
            <button 
              @click="router.push('/dashboard')"
              class="flex items-center justify-center w-7 h-7 rounded-lg transition-colors"
              :class="isDark ? 'text-dark-text-secondary hover:text-dark-text-primary hover:bg-dark-surface-elevated' : 'text-text-secondary hover:text-text-primary hover:bg-gray-100'"
            >
              <n-icon :component="HomeOutline" :size="16" />
            </button>
            <span :class="isDark ? 'text-dark-text-tertiary' : 'text-text-tertiary'">/</span>
            <span :class="isDark ? 'text-dark-text-primary' : 'text-text-primary'">
              {{ currentBreadcrumb }}
            </span>
          </nav>
        </div>

        <!-- 右侧：操作按钮 -->
        <div class="flex items-center gap-2">
          <!-- 搜索按钮 -->
          <button
            @click="openSearch"
            class="p-2 rounded-lg transition-colors"
            :class="isDark ? 'hover:bg-dark-surface-elevated text-dark-text-secondary hover:text-dark-text-primary' : 'hover:bg-gray-100 text-text-secondary hover:text-text-primary'"
            :title="$t('common.search')"
          >
            <n-icon :component="SearchOutline" :size="20" />
          </button>

          <!-- 通知下拉 -->
          <n-popover
            v-model:show="showNotifications"
            trigger="click"
            placement="bottom-end"
            :show-arrow="false"
            raw
            :overlap="false"
            content-class="notifications-popover-content"
          >
            <template #trigger>
              <button
                class="p-2 rounded-lg transition-colors relative"
                :class="isDark ? 'hover:bg-dark-surface-elevated text-dark-text-secondary hover:text-dark-text-primary' : 'hover:bg-gray-100 text-text-secondary hover:text-text-primary'"
                :title="$t('common.notifications')"
              >
                <n-icon :component="NotificationsOutline" :size="20" />
                <!-- 未读徽章 -->
                <span
                  v-if="unreadCount > 0"
                  class="absolute -top-0.5 -right-0.5 min-w-[1.1rem] h-5 flex items-center justify-center rounded-full text-[10px] font-bold text-white bg-secondary"
                >
                  {{ unreadCount > 99 ? '99+' : unreadCount }}
                </span>
              </button>
            </template>
            <div
              class="notifications-panel w-80 overflow-hidden rounded-xl border shadow-xl"
              :class="isDark
                ? 'bg-[#2d3748] border-[#4a5568]/80'
                : 'bg-white border-gray-200/90'"
            >
              <!-- 通知头部 -->
              <div
                class="px-4 py-3 flex items-center justify-between"
                :class="isDark ? 'border-b border-white/10' : 'border-b border-gray-100'"
              >
                <div class="flex items-center gap-2">
                  <span class="text-sm font-semibold" :class="isDark ? 'text-gray-100' : 'text-gray-900'">
                    {{ $t('common.notifications') }}
                  </span>
                  <span
                    v-if="unreadCount > 0"
                    class="px-1.5 py-0.5 rounded-full text-[10px] font-bold text-white bg-secondary"
                  >
                    {{ unreadCount }}
                  </span>
                </div>
                <button
                  v-if="unreadCount > 0"
                  @click="markAllRead"
                  class="text-xs hover:underline transition-colors"
                  :class="isDark ? 'text-gray-400 hover:text-gray-200' : 'text-gray-500 hover:text-gray-700'"
                >
                  {{ $t('common.markAllRead') }}
                </button>
              </div>

              <!-- 通知列表 -->
              <div class="max-h-80 overflow-y-auto">
                <template v-if="notifications.length > 0">
                  <div
                    v-for="item in notifications"
                    :key="item.id"
                    class="px-4 py-3 flex items-start gap-3 cursor-pointer transition-colors border-b last:border-0"
                    :class="[
                      isDark ? 'border-white/5 hover:bg-white/5' : 'border-gray-50 hover:bg-gray-50/80',
                      !item.read && (isDark ? 'bg-white/[0.03]' : 'bg-blue-50/40')
                    ]"
                    @click="handleNotificationClick(item)"
                  >
                    <!-- 图标 -->
                    <div
                      class="shrink-0 w-9 h-9 rounded-lg flex items-center justify-center mt-0.5"
                      :class="{
                        'bg-secondary/10 text-secondary': item.type === 'warning' || item.type === 'pending',
                        'bg-success/10 text-success': item.type === 'success' || item.type === 'info',
                        'bg-danger/10 text-danger': item.type === 'error' || item.type === 'danger',
                        'bg-primary/10 text-primary': item.type === 'system'
                      }"
                    >
                      <n-icon :component="getNotificationIcon(item.type)" :size="18" />
                    </div>
                    <!-- 内容 -->
                    <div class="flex-1 min-w-0">
                      <p class="text-sm font-medium leading-tight truncate" :class="isDark ? 'text-gray-100' : 'text-gray-900'">
                        {{ $t(item.titleKey) }}
                      </p>
                      <p class="text-xs mt-0.5 leading-snug" :class="isDark ? 'text-gray-400' : 'text-gray-500'">
                        {{ $t(item.contentKey) }}
                      </p>
                      <p class="text-[10px] mt-1" :class="isDark ? 'text-gray-500' : 'text-gray-400'">
                        {{ $t(item.timeKey, { n: item.timeArg }) }}
                      </p>
                    </div>
                    <!-- 未读点 -->
                    <div v-if="!item.read" class="shrink-0 w-2 h-2 rounded-full bg-secondary mt-2"></div>
                  </div>
                </template>
                <!-- 空状态 -->
                <div v-else class="py-10 flex flex-col items-center gap-2">
                  <n-icon :component="NotificationsOffOutline" :size="32" :class="isDark ? 'text-gray-600' : 'text-gray-300'" />
                  <p class="text-sm" :class="isDark ? 'text-gray-500' : 'text-gray-400'">
                    {{ $t('common.noNotifications') }}
                  </p>
                </div>
              </div>

              <!-- 查看全部 -->
              <div
                v-if="notifications.length > 0"
                class="px-4 py-2.5 text-center border-t"
                :class="isDark ? 'border-white/10' : 'border-gray-100'"
              >
                <button
                  @click="goToAuditLog"
                  class="text-xs font-medium text-secondary hover:text-secondary/80 transition-colors"
                >
                  {{ $t('common.viewAllNotifications') }}
                </button>
              </div>
            </div>
          </n-popover>

          <!-- 主题切换 -->
          <button
            @click="toggleTheme"
            class="p-2 rounded-lg transition-colors"
            :class="isDark ? 'hover:bg-dark-surface-elevated text-dark-text-secondary hover:text-dark-text-primary' : 'hover:bg-gray-100 text-text-secondary hover:text-text-primary'"
            :title="$t('common.toggleTheme')"
          >
            <n-icon :component="isDark ? SunnyOutline : MoonOutline" :size="20" />
          </button>

          <!-- 全屏（仅在浏览器支持时显示） -->
          <button
            v-if="supportsFullscreen"
            ref="fullscreenBtnRef"
            class="p-2 rounded-lg transition-colors"
            :class="isDark ? 'hover:bg-dark-surface-elevated text-dark-text-secondary hover:text-dark-text-primary' : 'hover:bg-gray-100 text-text-secondary hover:text-text-primary'"
            :title="$t('common.fullscreen')"
          >
            <n-icon :component="isFullscreen ? ContractOutline : ExpandOutline" :size="20" />
          </button>

          <!-- 分隔线 -->
          <div class="w-px h-6 mx-2" :class="isDark ? 'bg-dark-border' : 'bg-border'"></div>

          <!-- 语言切换 -->
          <n-dropdown
            :options="languageOptions"
            @select="handleLanguageChange"
            trigger="click"
          >
            <button
              class="flex items-center justify-center gap-1 px-2 h-9 rounded-lg transition-colors border"
              :class="[
                appStore.language === 'zh-CN'
                  ? isDark ? 'border-dark-border bg-dark-surface-elevated text-dark-text-primary' : 'border-gray-200 bg-gray-50 text-text-primary font-medium'
                  : isDark ? 'hover:bg-dark-surface-elevated text-dark-text-secondary hover:text-dark-text-primary border-transparent' : 'hover:bg-gray-100 text-text-secondary hover:text-text-primary border-transparent'
              ]"
              :title="$t('common.language')"
            >
              <n-icon :component="LanguageOutline" :size="16" />
              <span class="text-xs font-semibold">{{ appStore.language === 'zh-CN' ? '中' : 'EN' }}</span>
            </button>
          </n-dropdown>

          <!-- 用户菜单：自定义浮层，与顶栏图标尺度、圆角统一 -->
          <n-popover
            v-model:show="showUserMenu"
            trigger="click"
            placement="bottom-end"
            :show-arrow="false"
            display-directive="show"
            raw
            :overlap="false"
            content-class="user-menu-popover-content"
          >
            <template #trigger>
              <button
                type="button"
                class="flex items-center gap-2 px-2 py-1.5 rounded-lg transition-colors"
                :class="isDark ? 'hover:bg-dark-surface-elevated' : 'hover:bg-gray-100'"
              >
                <n-avatar
                  :size="32"
                  round
                  :src="user?.avatar || 'https://gw.alipayobjects.com/zos/rmsportal/BiazfanxmamNRoxxVxka.png'"
                />
                <div class="hidden md:block text-left">
                  <div class="text-sm font-medium" :class="isDark ? 'text-dark-text-primary' : 'text-text-primary'">
                    {{ user?.nickname || user?.username || t('common.admin') }}
                  </div>
                  <div class="text-xs" :class="isDark ? 'text-dark-text-secondary' : 'text-text-secondary'">
                    {{ user?.roleName || t('common.systemAdmin') }}
                  </div>
                </div>
                <n-icon
                  :component="ChevronDownOutline"
                  :size="16"
                  class="transition-transform duration-200 shrink-0"
                  :class="[
                    isDark ? 'text-dark-text-secondary' : 'text-text-secondary',
                    showUserMenu ? 'rotate-180' : ''
                  ]"
                />
              </button>
            </template>
            <div
              class="user-menu-panel w-[15.5rem] overflow-hidden rounded-xl border shadow-xl shadow-black/5"
              :class="isDark
                ? 'bg-[#2d3748] border-[#4a5568]/80'
                : 'bg-white border-gray-200/90'"
            >
              <!-- 面板内用户摘要 -->
              <div
                class="px-3.5 pt-3.5 pb-3"
                :class="isDark ? 'bg-white/[0.04] border-b border-white/10' : 'bg-slate-50/80 border-b border-gray-100'"
              >
                <div class="flex items-center gap-3">
                  <n-avatar
                    round
                    :size="44"
                    :src="user?.avatar || 'https://gw.alipayobjects.com/zos/rmsportal/BiazfanxmamNRoxxVxka.png'"
                  />
                  <div class="min-w-0 flex-1">
                    <p
                      class="text-sm font-semibold truncate leading-tight"
                      :class="isDark ? 'text-gray-100' : 'text-gray-900'"
                    >
                      {{ user?.nickname || user?.username || t('common.admin') }}
                    </p>
                    <p
                      class="text-xs mt-0.5 truncate"
                      :class="isDark ? 'text-gray-400' : 'text-gray-500'"
                    >
                      {{ user?.roleName || t('common.systemAdmin') }}
                    </p>
                  </div>
                </div>
              </div>
              <div class="p-1.5 pb-2">
                <button
                  type="button"
                  class="user-menu-item w-full flex items-center gap-3 px-2.5 py-2 rounded-lg text-left text-sm font-medium transition-colors"
                  :class="isDark
                    ? 'text-gray-200 hover:bg-white/10'
                    : 'text-gray-700 hover:bg-gray-100/90'"
                  @click="goProfile"
                >
                  <span
                    class="user-menu-item-icon flex items-center justify-center shrink-0 rounded-lg"
                    :class="isDark
                      ? 'bg-[#0F4C5C]/35 text-teal-200/95'
                      : 'bg-primary/10 text-primary'"
                  >
                    <n-icon :component="PersonOutline" :size="18" />
                  </span>
                  <span>{{ $t('common.profileCenter') }}</span>
                </button>
                <button
                  type="button"
                  class="user-menu-item w-full flex items-center gap-3 px-2.5 py-2 rounded-lg text-left text-sm font-medium transition-colors mt-0.5"
                  :class="isDark
                    ? 'text-gray-300 hover:bg-red-500/15 hover:text-red-300'
                    : 'text-gray-700 hover:bg-red-50 hover:text-red-600'"
                  @click="goLogout"
                >
                  <span
                    class="user-menu-item-icon flex items-center justify-center shrink-0 rounded-lg"
                    :class="isDark
                      ? 'bg-red-500/12 text-red-300'
                      : 'bg-red-50 text-red-600'"
                  >
                    <n-icon :component="LogOutOutline" :size="18" />
                  </span>
                  <span>{{ $t('common.logout') }}</span>
                </button>
              </div>
            </div>
          </n-popover>
        </div>
      </header>

      <!-- 内容区域 -->
      <main
        class="flex-1 p-6 transition-colors duration-300"
        :class="isDark ? 'bg-dark-bg' : 'bg-background'"
      >
        <router-view v-slot="{ Component }">
          <transition name="fade-slide" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>

      <!-- 全局搜索弹窗 -->
      <n-modal
        v-model:show="showSearch"
        preset="card"
        :class="isDark ? 'search-modal-dark' : 'search-modal-light'"
        :mask-closable="true"
        :bordered="false"
        content-class="search-modal-content"
        :style="{ width: '26rem', maxWidth: '95vw' }"
      >
        <template #header>
          <div></div>
        </template>
        <div class="w-full">
          <!-- 搜索输入框 -->
          <div class="relative mb-4">
            <n-icon
              :component="SearchOutline"
              :size="18"
              class="absolute left-3.5 top-1/2 -translate-y-1/2 pointer-events-none"
              :class="isDark ? 'text-gray-400' : 'text-gray-400'"
            />
            <input
              ref="searchInputRef"
              v-model="searchQuery"
              type="text"
              :placeholder="$t('common.searchPlaceholder')"
              class="w-full h-11 pl-10 pr-4 rounded-xl text-sm outline-none transition-colors border"
              :class="isDark
                ? 'bg-[#1F2937] border-[#4a5568] text-gray-100 placeholder-gray-500 focus:border-secondary'
                : 'bg-gray-50 border-gray-200 text-gray-900 placeholder-gray-400 focus:border-primary'"
              @keydown.escape="showSearch = false"
            />
          </div>

          <!-- 搜索结果 -->
          <div class="max-h-80 overflow-y-auto">
            <!-- 无搜索内容时显示快捷入口 -->
            <template v-if="!searchQuery.trim()">
              <p class="text-xs font-medium px-1 mb-2" :class="isDark ? 'text-gray-500' : 'text-gray-400'">
                {{ $t('common.quickAccess') }}
              </p>
              <div class="grid grid-cols-2 gap-2">
                <button
                  v-for="item in quickLinks"
                  :key="item.path"
                  @click="navigateFromSearch(item.path)"
                  class="flex items-center gap-3 px-3 py-2.5 rounded-xl text-left transition-colors border"
                  :class="isDark
                    ? 'bg-[#1F2937] border-[#374151] hover:border-secondary/50 hover:bg-[#1F2937]/80 text-gray-200'
                    : 'bg-gray-50 border-gray-200 hover:border-primary/40 hover:bg-primary/5 text-gray-700'"
                >
                  <n-icon :component="item.icon" :size="18" class="text-primary shrink-0" />
                  <div>
                    <p class="text-sm font-medium leading-tight">{{ $t(item.labelKey) }}</p>
                    <p class="text-xs mt-0.5" :class="isDark ? 'text-gray-500' : 'text-gray-400'">{{ $t(item.subLabelKey) }}</p>
                  </div>
                </button>
              </div>
            </template>

            <!-- 搜索结果 -->
            <template v-else>
              <template v-if="searchResults.length > 0">
                <p class="text-xs font-medium px-1 mb-2" :class="isDark ? 'text-gray-500' : 'text-gray-400'">
                  {{ $t('common.resultsFound', { n: searchResults.length }) }}
                </p>
                <div class="space-y-1">
                  <button
                    v-for="result in searchResults"
                    :key="result.id"
                    @click="navigateFromSearch(result.path)"
                    class="w-full flex items-center gap-3 px-3 py-2.5 rounded-xl text-left transition-colors"
                    :class="isDark ? 'hover:bg-[#1F2937]/80 text-gray-200' : 'hover:bg-gray-50 text-gray-700'"
                  >
                    <div
                      class="shrink-0 w-8 h-8 rounded-lg flex items-center justify-center"
                      :class="{
                        'bg-primary/10 text-primary': result.type === 'user',
                        'bg-secondary/10 text-secondary': result.type === 'company',
                        'bg-success/10 text-success': result.type === 'role',
                        'bg-purple-500/10 text-purple-500': result.type === 'menu',
                      }"
                    >
                      <n-icon :component="result.icon" :size="16" />
                    </div>
                    <div class="flex-1 min-w-0">
                      <p class="text-sm font-medium truncate">{{ $t(result.titleKey) }}</p>
                      <p class="text-xs" :class="isDark ? 'text-gray-500' : 'text-gray-400'">{{ $t(result.subTitleKey) }}</p>
                    </div>
                    <span
                      class="shrink-0 text-[10px] px-1.5 py-0.5 rounded font-medium"
                      :class="{
                        'bg-primary/10 text-primary': result.type === 'user',
                        'bg-secondary/10 text-secondary': result.type === 'company',
                        'bg-success/10 text-success': result.type === 'role',
                        'bg-purple-500/10 text-purple-500': result.type === 'menu',
                      }"
                    >
                      {{ getResultTypeLabel(result.type) }}
                    </span>
                  </button>
                </div>
              </template>
              <!-- 无结果 -->
              <div v-else class="py-8 flex flex-col items-center gap-2">
                <n-icon :component="SearchOutline" :size="32" :class="isDark ? 'text-gray-600' : 'text-gray-300'" />
                <p class="text-sm" :class="isDark ? 'text-gray-500' : 'text-gray-400'">
                  {{ $t('common.noResults') }}
                </p>
              </div>
            </template>
          </div>
        </div>
      </n-modal>
    </div>

    <!-- 移动端遮罩层 -->
    <Transition name="fade">
      <div 
        v-if="!collapsed && isMobile"
        class="fixed inset-0 bg-black/50 z-30 lg:hidden"
        @click="toggleSidebar"
      ></div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch, nextTick, h } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { 
  MenuOutline, 
  ChevronBackOutline, 
  ChevronForwardOutline, 
  ChevronDownOutline,
  HomeOutline,
  SearchOutline,
  NotificationsOutline,
  NotificationsOffOutline,
  SunnyOutline,
  MoonOutline,
  LogOutOutline,
  ExpandOutline,
  ContractOutline,
  LanguageOutline,
  GridOutline,
  PeopleOutline,
  ShieldOutline,
  BusinessOutline,
  KeyOutline,
  DocumentTextOutline,
  SettingsOutline,
  PersonOutline,
  PersonAddOutline,
  BusinessSharp,
  RibbonSharp,
  AlertCircleSharp,
  CheckmarkCircleSharp,
  InformationCircleSharp,
  TimeSharp,
  ShieldSharp,
  CreateOutline
} from '@vicons/ionicons5';
import { useAppStore } from '@/stores/app';
import { NIcon } from 'naive-ui';

const router = useRouter();
const route = useRoute();
const appStore = useAppStore();
const { t, locale } = useI18n();

// 响应式状态
const isDark = computed(() => appStore.isDark);
const collapsed = computed(() => appStore.collapsed);
const user = computed(() => appStore.user);

// 移动端检测
const isMobile = ref(window.innerWidth < 1024);
const isFullscreen = ref(false);
const fullscreenBtnRef = ref<HTMLButtonElement | null>(null);
const supportsFullscreen = ref(!!(
  document.documentElement.requestFullscreen ||
  (document.documentElement as any).webkitRequestFullscreen ||
  (document.documentElement as any).mozRequestFullScreen ||
  (document.documentElement as any).msRequestFullscreen
));
// 用户菜单浮层显隐
const showUserMenu = ref(false);

// 全局搜索
const showSearch = ref(false);
const searchQuery = ref('');
const searchInputRef = ref<HTMLInputElement | null>(null);

// 通知
const showNotifications = ref(false);
const notifications = ref([
  {
    id: 1,
    titleKey: 'common.notification.newUserRegistered',
    contentKey: 'common.notification.newUserRegisteredContent',
    timeKey: 'common.notification.minutesAgo',
    timeArg: 2,
    type: 'success',
    read: false
  },
  {
    id: 2,
    titleKey: 'common.notification.companyPendingReview',
    contentKey: 'common.notification.companyPendingReviewContent',
    timeKey: 'common.notification.minutesAgo',
    timeArg: 15,
    type: 'warning',
    read: false
  },
  {
    id: 3,
    titleKey: 'common.notification.transactionRecorded',
    contentKey: 'common.notification.transactionRecordedContent',
    timeKey: 'common.notification.hourAgo',
    timeArg: 1,
    type: 'info',
    read: false
  },
  {
    id: 4,
    titleKey: 'common.notification.permissionChanged',
    contentKey: 'common.notification.permissionChangedContent',
    timeKey: 'common.notification.hourAgo',
    timeArg: 2,
    type: 'system',
    read: true
  },
  {
    id: 5,
    titleKey: 'common.notification.systemAlert',
    contentKey: 'common.notification.systemAlertContent',
    timeKey: 'common.notification.hourAgo',
    timeArg: 3,
    type: 'error',
    read: true
  }
]);
const unreadCount = computed(() => notifications.value.filter(n => !n.read).length);

// 当前激活的菜单项
const activeKey = ref(route.name as string);

// 菜单图标映射
const menuIcons: Record<string, any> = {
  Dashboard: GridOutline,
  UserManagement: PeopleOutline,
  RoleManagement: ShieldOutline,
  CompanyManagement: BusinessOutline,
  PermissionManagement: KeyOutline,
  AuditLog: DocumentTextOutline,
  SystemSettings: SettingsOutline,
  Profile: PersonOutline,
};

// 获取菜单图标
const getMenuIcon = (key: string) => {
  return menuIcons[key] || GridOutline;
};

// 完整的菜单配置
const allMenuOptions = [
  {
    labelKey: 'common.dashboard',
    key: 'Dashboard',
    permission: null
  },
  {
    labelKey: 'common.userManagement',
    key: 'UserManagement',
    permission: 'USER_MANAGE'
  },
  {
    labelKey: 'common.roleManagement',
    key: 'RoleManagement',
    permission: 'ROLE_MANAGE'
  },
  {
    labelKey: 'common.companyManagement',
    key: 'CompanyManagement',
    permission: 'COMPANY_MANAGE'
  },
  {
    labelKey: 'common.permissionManagement',
    key: 'PermissionManagement',
    permission: 'ROLE_MANAGE'
  },
  {
    labelKey: 'common.auditLog',
    key: 'AuditLog',
    permission: 'SYSTEM_AUDIT'
  },
  {
    labelKey: 'common.systemSettings',
    key: 'SystemSettings',
    permission: null
  },
  {
    labelKey: 'common.profile',
    key: 'Profile',
    permission: null
  }
];

// 根据权限过滤菜单
const filteredMenuOptions = computed(() => {
  return allMenuOptions.filter(menu => {
    if (menu.permission) {
      return appStore.checkPermission(menu.permission);
    }
    return true;
  }).map(menu => ({
    ...menu,
    label: t(menu.labelKey)
  }));
});

// 当前面包屑
const currentBreadcrumb = computed(() => {
  const current = filteredMenuOptions.value.find(item => item.key === activeKey.value);
  return current?.label || '';
});

// 语言选项（动态计算，当前语言显示勾选）
const languageOptions = computed(() => [
  {
    label: t('common.chinese'),
    key: 'zh-CN',
    icon: appStore.language === 'zh-CN'
      ? () => h(NIcon, { component: CheckmarkCircleSharp, size: 16, class: 'text-secondary' })
      : undefined,
  },
  {
    label: t('common.english'),
    key: 'en-US',
    icon: appStore.language === 'en-US'
      ? () => h(NIcon, { component: CheckmarkCircleSharp, size: 16, class: 'text-secondary' })
      : undefined,
  }
]);

// 方法
const toggleSidebar = () => {
  if (isMobile.value) {
    appStore.setCollapsed(!collapsed.value);
  } else {
    appStore.setCollapsed(!collapsed.value);
  }
};

const toggleTheme = () => {
  appStore.setDarkTheme(!isDark.value);
};

const handleLanguageChange = (lang: string) => {
  appStore.setLanguage(lang);
  locale.value = lang;
};

const handleMenuClick = (key: string) => {
  console.log('点击菜单:', key);
  const routeMap: Record<string, string> = {
    Dashboard: '/dashboard',
    UserManagement: '/users',
    RoleManagement: '/roles',
    CompanyManagement: '/companies',
    PermissionManagement: '/permissions',
    AuditLog: '/audit-logs',
    SystemSettings: '/system',
    Profile: '/profile'
  };

  if (routeMap[key]) {
    router.push(routeMap[key]);
  }
};

/** 跳转个人中心并关闭菜单 */
const goProfile = () => {
  showUserMenu.value = false;
  router.push('/profile');
};

/** 退出登录并关闭菜单 */
const goLogout = () => {
  showUserMenu.value = false;
  appStore.logout();
  router.push('/login');
};

// 监听窗口大小变化

// 快捷链接
const quickLinks = [
  { path: '/users', labelKey: 'common.userManagement', subLabelKey: 'menu.userManagement', icon: PeopleOutline },
  { path: '/companies', labelKey: 'common.companyManagement', subLabelKey: 'menu.companyManagement', icon: BusinessOutline },
  { path: '/roles', labelKey: 'common.roleManagement', subLabelKey: 'menu.roleManagement', icon: ShieldOutline },
  { path: '/audit-logs', labelKey: 'common.auditLog', subLabelKey: 'menu.auditLog', icon: DocumentTextOutline },
  { path: '/permissions', labelKey: 'common.permissionManagement', subLabelKey: 'menu.permissionManagement', icon: KeyOutline },
  { path: '/system', labelKey: 'common.systemSettings', subLabelKey: 'menu.systemSettings', icon: SettingsOutline },
];

// 搜索结果数据
const allSearchItems = [
  { id: 'u1', type: 'user', titleKey: 'searchDemo.zhangSan', subTitleKey: 'searchDemo.zhangSanSubTitle', icon: PersonOutline, path: '/users' },
  { id: 'u2', type: 'user', titleKey: 'searchDemo.liSi', subTitleKey: 'searchDemo.liSiSubTitle', icon: PersonOutline, path: '/users' },
  { id: 'u3', type: 'user', titleKey: 'searchDemo.wangWu', subTitleKey: 'searchDemo.wangWuSubTitle', icon: PersonOutline, path: '/users' },
  { id: 'c1', type: 'company', titleKey: 'searchDemo.abcTech', subTitleKey: 'searchDemo.abcTechSubTitle', icon: BusinessSharp, path: '/companies' },
  { id: 'c2', type: 'company', titleKey: 'searchDemo.xyzIndustry', subTitleKey: 'searchDemo.xyzIndustrySubTitle', icon: BusinessSharp, path: '/companies' },
  { id: 'r1', type: 'role', titleKey: 'searchDemo.sysAdmin', subTitleKey: 'searchDemo.sysAdminSubTitle', icon: ShieldSharp, path: '/roles' },
  { id: 'r2', type: 'role', titleKey: 'searchDemo.financeManager', subTitleKey: 'searchDemo.financeManagerSubTitle', icon: ShieldSharp, path: '/roles' },
  { id: 'r3', type: 'role', titleKey: 'searchDemo.normalUser', subTitleKey: 'searchDemo.normalUserSubTitle', icon: ShieldSharp, path: '/roles' },
];

// 根据查询过滤搜索结果
const searchResults = computed(() => {
  const q = searchQuery.value.trim().toLowerCase();
  if (!q) return [];
  return allSearchItems.filter(item => {
    const title = t(item.titleKey);
    const subTitle = t(item.subTitleKey);
    return title.toLowerCase().includes(q) || subTitle.toLowerCase().includes(q);
  }).slice(0, 8);
});

// 搜索结果类型标签
const getResultTypeLabel = (type: string) => {
  const keyMap: Record<string, string> = {
    user: 'common.user',
    company: 'common.company',
    role: 'common.role',
    menu: 'common.menu',
  };
  return t(keyMap[type] || type);
};

// 打开搜索
const openSearch = () => {
  showSearch.value = true;
  nextTick(() => {
    searchInputRef.value?.focus();
  });
};

// 从搜索结果导航
const navigateFromSearch = (path: string) => {
  showSearch.value = false;
  searchQuery.value = '';
  router.push(path);
};

// 通知图标
const getNotificationIcon = (type: string) => {
  const map: Record<string, any> = {
    success: CheckmarkCircleSharp,
    info: InformationCircleSharp,
    warning: TimeSharp,
    error: AlertCircleSharp,
    system: CreateOutline,
    danger: AlertCircleSharp,
    pending: TimeSharp,
  };
  return map[type] || InformationCircleSharp;
};

// 通知点击
const handleNotificationClick = (item: any) => {
  item.read = true;
  showNotifications.value = false;
  // 根据通知类型跳转
  if (item.type === 'error' || item.type === 'system') {
    router.push('/audit-logs');
  } else if (item.type === 'warning') {
    router.push('/companies');
  } else if (item.type === 'success' || item.type === 'info') {
    router.push('/users');
  }
};

// 全部已读
const markAllRead = () => {
  notifications.value.forEach(n => { n.read = true; });
};

// 跳转审计日志
const goToAuditLog = () => {
  showNotifications.value = false;
  router.push('/audit-logs');
};

const handleResize = () => {
  isMobile.value = window.innerWidth < 1024;
  if (isMobile.value) {
    appStore.setCollapsed(true);
  }
};

// 监听全屏变化
const handleFullscreenChange = () => {
  isFullscreen.value = !!document.fullscreenElement
    || !!(document as any).webkitFullscreenElement
    || !!(document as any).mozFullScreenElement
    || !!(document as any).msFullscreenElement;
};

// 监听路由变化
watch(() => route.name, (newName) => {
  activeKey.value = newName as string;
});

// 监听主题变化，同步到DOM
watch(isDark, (newDark) => {
  const html = document.documentElement;
  if (newDark) {
    html.classList.add('dark');
  } else {
    html.classList.remove('dark');
  }
}, { immediate: true });

onMounted(() => {
  window.addEventListener('resize', handleResize);
  document.addEventListener('fullscreenchange', handleFullscreenChange);
  document.addEventListener('webkitfullscreenchange', handleFullscreenChange);
  document.addEventListener('mozfullscreenchange', handleFullscreenChange);
  document.addEventListener('MSFullscreenChange', handleFullscreenChange);

  // 全屏按钮用原生 onclick（绕过 Vue/Naive UI 事件系统）
  if (fullscreenBtnRef.value) {
    fullscreenBtnRef.value.onclick = () => {
      const el = window.document.documentElement;
      const inFs = !!(
        document.fullscreenElement ||
        (document as any).webkitFullscreenElement ||
        (document as any).mozFullScreenElement ||
        (document as any).msFullscreenElement
      );
      if (!inFs) {
        const req = el.requestFullscreen
          || (el as any).webkitRequestFullscreen
          || (el as any).mozRequestFullScreen
          || (el as any).msRequestFullscreen;
        if (req) {
          (req as Function).call(el);
        } else {
          (window as any).$message?.warning(
            t('common.fullscreenNotSupported')
          );
        }
      } else {
        const exit = document.exitFullscreen
          || (document as any).webkitExitFullscreen
          || (document as any).mozCancelFullScreen
          || (document as any).msExitFullscreen;
        if (exit) (exit as Function).call(document);
      }
    };
  }

  // 初始化检查移动端
  if (window.innerWidth < 1024) {
    appStore.setCollapsed(true);
  }
});

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
  document.removeEventListener('fullscreenchange', handleFullscreenChange);
  document.removeEventListener('webkitfullscreenchange', handleFullscreenChange);
  document.removeEventListener('mozfullscreenchange', handleFullscreenChange);
  document.removeEventListener('MSFullscreenChange', handleFullscreenChange);
  if (fullscreenBtnRef.value) {
    fullscreenBtnRef.value.onclick = null;
  }
});
</script>

<style scoped>
/* 侧边栏样式 */
.sidebar {
  background: linear-gradient(180deg, #0F4C5C 0%, #0a3644 100%);
}

.sidebar.bg-primary-dark {
  background: linear-gradient(180deg, #0a3644 0%, #051B26 100%);
}

/* 过渡动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(-10px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(10px);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 深色主题样式增强 */
:deep(.n-menu) {
  --n-item-text-color: rgba(255, 255, 255, 0.7);
  --n-item-text-color-hover: rgb(255, 255, 255);
  --n-item-text-color-active: rgb(255, 255, 255);
  --n-item-color-hover: rgba(255, 255, 255, 0.08);
  --n-item-color-active: rgba(255, 255, 255, 0.15);
}

:deep(.n-menu-item-content) {
  --n-item-text-color: rgba(255, 255, 255, 0.7);
}

:deep(.n-menu-item-content:hover) {
  --n-item-text-color: rgb(255, 255, 255);
}

/* 深色主题下滚动条样式 */
.dark ::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

.dark ::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
}

.dark ::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.15);
  border-radius: 3px;
}

.dark ::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.25);
}

/* 移动端适配 */
@media (max-width: 1024px) {
  .sidebar {
    transform: translateX(-100%);
  }
  
  .sidebar:not(.collapsed) {
    transform: translateX(0);
  }
}

/* 自定义深色主题类 */
.bg-dark-surface {
  background-color: #1F2937;
}

.bg-dark-surface\/80 {
  background-color: rgba(31, 41, 55, 0.8);
}

.bg-dark-bg {
  background-color: #111827;
}

.bg-primary-dark {
  background-color: #0a3644;
}

.text-dark-text-primary {
  color: #F9FAFB;
}

.text-dark-text-secondary {
  color: #9CA3AF;
}

.text-dark-text-tertiary {
  color: #6B7280;
}

.border-dark-border {
  border-color: #374151;
}

.hover\:bg-dark-surface-elevated:hover {
  background-color: #374151;
}

.bg-dark-surface-elevated {
  background-color: #374151;
}

/* 用户菜单 popover：单层 0.75rem 圆角 + 裁切，避免与内层 rounded 叠加导致底角视觉上更「尖」 */
:deep(.user-menu-popover-content) {
  padding: 0 !important;
  border-radius: 0.75rem;
  overflow: hidden;
}

/* 通知面板 popover */
:deep(.notifications-popover-content) {
  padding: 0 !important;
  border-radius: 0.75rem;
  overflow: hidden;
}

/* 搜索弹窗样式覆盖 */
:deep(.search-modal-light .n-card) {
  border-radius: 1rem;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  border: 1px solid #e5e7eb;
  background: #ffffff;
  max-width: 26rem !important;
  width: 26rem !important;
}

:deep(.search-modal-dark .n-card) {
  border-radius: 1rem;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.6);
  border: 1px solid #374151;
  background: #1f2937;
  max-width: 26rem !important;
  width: 26rem !important;
}

:deep(.search-modal-content) {
  padding: 0 !important;
}

/* 菜单项图标容器与顶栏 20px 图标视觉对齐 */
.user-menu-item-icon {
  width: 2.25rem;
  height: 2.25rem;
}
</style>
