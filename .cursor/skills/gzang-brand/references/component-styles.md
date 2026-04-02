# G-Zang 组件样式库

## 目录

1. [按钮组件](#1-按钮组件)
2. [卡片组件](#2-卡片组件)
3. [表单组件](#3-表单组件)
4. [数据展示组件](#4-数据展示组件)
5. [导航组件](#5-导航组件)
6. [反馈组件](#6-反馈组件)
7. [布局组件](#7-布局组件)

---

## 1. 按钮组件

### 1.1 主要按钮 (Primary Button)

主要按钮用于最重要的操作，如"确认"、"提交"、"创建"。

**Tailwind CSS**：
```html
<button class="bg-secondary hover:bg-secondary-dark active:scale-[0.98] text-white font-medium text-sm px-4 py-2 rounded-lg shadow-sm transition-all duration-150 flex items-center gap-2">
  <PlusIcon class="w-4 h-4" />
  新建账本
</button>
```

**CSS 变量写法**：
```css
.btn--primary {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background-color: var(--gzang-secondary);
  color: white;
  font-weight: 500;
  font-size: 0.875rem;
  border-radius: 0.5rem;
  border: none;
  cursor: pointer;
  transition: all 150ms ease;
  box-shadow: var(--shadow-sm);
}

.btn--primary:hover {
  background-color: var(--gzang-secondary-dark);
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.btn--primary:active {
  transform: scale(0.98);
}

.btn--primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}
```

### 1.2 次要按钮 (Secondary Button)

用于次要操作，如"取消"、"返回"。

**Tailwind CSS**：
```html
<button class="border border-primary text-primary hover:bg-primary hover:text-white font-medium text-sm px-4 py-2 rounded-lg transition-all duration-150">
  取消
</button>
```

**CSS 变量写法**：
```css
.btn--secondary {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0.5rem 1rem;
  background-color: transparent;
  color: var(--gzang-primary);
  font-weight: 500;
  font-size: 0.875rem;
  border: 1px solid var(--gzang-primary);
  border-radius: 0.5rem;
  cursor: pointer;
  transition: all 150ms ease;
}

.btn--secondary:hover {
  background-color: var(--gzang-primary);
  color: white;
}
```

### 1.3 文字按钮 (Text Button)

用于内联操作，如"编辑"、"删除"。

**Tailwind CSS**：
```html
<button class="text-primary hover:text-primary-dark font-medium text-sm transition-colors">
  编辑
</button>
<button class="text-danger hover:text-danger-dark font-medium text-sm transition-colors">
  删除
</button>
```

**CSS 变量写法**：
```css
.btn--text {
  background: none;
  border: none;
  padding: 0;
  font-weight: 500;
  font-size: 0.875rem;
  cursor: pointer;
  transition: color 150ms ease;
}

.btn--text--primary {
  color: var(--gzang-primary);
}

.btn--text--primary:hover {
  color: var(--gzang-primary-dark);
}

.btn--text--danger {
  color: var(--gzang-danger);
}

.btn--text--danger:hover {
  color: var(--gzang-danger-dark);
}
```

### 1.4 图标按钮 (Icon Button)

用于工具栏、卡片操作等场景。

**Tailwind CSS**：
```html
<button class="w-8 h-8 rounded-lg hover:bg-gray-100 flex items-center justify-center transition-colors" aria-label="编辑">
  <PencilIcon class="w-4 h-4 text-gray-600" />
</button>
```

**CSS 变量写法**：
```css
.btn--icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 2rem;
  height: 2rem;
  border-radius: 0.5rem;
  background: transparent;
  border: none;
  cursor: pointer;
  transition: background-color 150ms ease;
}

.btn--icon:hover {
  background-color: var(--gzang-bg);
}

.btn--icon:focus-visible {
  outline: 2px solid var(--gzang-primary);
  outline-offset: 2px;
}
```

### 1.5 按钮尺寸

| 尺寸 | Tailwind | CSS 变量 | 用途 |
|------|----------|----------|------|
| 小 | `px-3 py-1.5 text-xs` | `padding: 0.375rem 0.75rem; font-size: 0.75rem` | 紧凑表格内 |
| 中 | `px-4 py-2 text-sm` | `padding: 0.5rem 1rem; font-size: 0.875rem` | 默认 |
| 大 | `px-6 py-3 text-base` | `padding: 0.75rem 1.5rem; font-size: 1rem` | 重要 CTA |

---

## 2. 卡片组件

### 2.1 基础卡片 (Basic Card)

**Tailwind CSS**：
```html
<div class="bg-surface rounded-xl p-6 shadow-sm hover:shadow-md transition-shadow duration-200">
  <h3 class="text-lg font-semibold text-text-primary mb-4">卡片标题</h3>
  <p class="text-text-secondary">卡片内容...</p>
</div>
```

**CSS 变量写法**：
```css
.card {
  background-color: var(--gzang-surface);
  border-radius: 0.75rem;
  padding: 1.5rem;
  box-shadow: var(--shadow-sm);
  transition: box-shadow 200ms ease;
}

.card:hover {
  box-shadow: var(--shadow-md);
}
```

### 2.2 统计卡片 (Statistic Card)

用于展示关键财务指标。

**Tailwind CSS**：
```html
<div class="bg-surface rounded-xl p-6 shadow-sm">
  <div class="flex items-start justify-between">
    <div>
      <p class="text-sm text-text-secondary mb-1">本月收入</p>
      <p class="text-2xl font-bold font-mono tabular-nums text-success">+¥12,580.00</p>
      <p class="text-xs text-text-secondary mt-1">较上月 +15.3%</p>
    </div>
    <div class="w-12 h-12 rounded-full bg-success/10 flex items-center justify-center">
      <TrendingUpIcon class="w-6 h-6 text-success" />
    </div>
  </div>
</div>
```

**变化版本**：
- 支出统计：使用 `text-danger`、`bg-danger/10`
- 转账统计：使用 `text-primary`、`bg-primary/10`

### 2.3 可操作卡片 (Action Card)

带操作按钮的卡片。

**Tailwind CSS**：
```html
<div class="bg-surface rounded-xl p-6 shadow-sm border border-gray-100">
  <div class="flex items-center justify-between mb-4">
    <div class="flex items-center gap-3">
      <div class="w-10 h-10 rounded-lg bg-primary/10 flex items-center justify-center">
        <WalletIcon class="w-5 h-5 text-primary" />
      </div>
      <div>
        <h3 class="font-semibold text-text-primary">个人账本</h3>
        <p class="text-sm text-text-secondary">12 笔交易</p>
      </div>
    </div>
    <ChevronRightIcon class="w-5 h-5 text-gray-400" />
  </div>
  <div class="flex items-center justify-between pt-4 border-t border-gray-100">
    <span class="text-sm text-text-secondary">本月结余</span>
    <span class="font-mono tabular-nums font-semibold text-success">¥3,420.00</span>
  </div>
</div>
```

---

## 3. 表单组件

### 3.1 文本输入框 (Text Input)

**Tailwind CSS**：
```html
<div class="space-y-2">
  <label for="amount" class="block text-sm font-medium text-text-primary">
    金额
  </label>
  <div class="relative">
    <span class="absolute left-3 top-1/2 -translate-y-1/2 text-text-secondary">¥</span>
    <input
      id="amount"
      type="text"
      inputmode="decimal"
      placeholder="0.00"
      class="w-full pl-8 pr-4 py-3 bg-surface border border-gray-200 rounded-lg text-base focus:outline-none focus:border-primary focus:ring-2 focus:ring-primary/10 transition-all"
    />
  </div>
</div>
```

**CSS 变量写法**：
```css
.input {
  width: 100%;
  padding: 0.75rem 1rem;
  font-family: var(--font-ui);
  font-size: 1rem;
  background-color: var(--gzang-surface);
  border: 1px solid var(--gzang-border);
  border-radius: 0.5rem;
  transition: all 150ms ease;
}

.input:focus {
  outline: none;
  border-color: var(--gzang-primary);
  box-shadow: 0 0 0 3px rgba(15, 76, 92, 0.1);
}

.input--error {
  border-color: var(--gzang-danger);
}

.input--error:focus {
  box-shadow: 0 0 0 3px rgba(239, 71, 111, 0.1);
}
```

### 3.2 错误状态

**Tailwind CSS**：
```html
<div class="space-y-2">
  <label for="amount" class="block text-sm font-medium text-text-primary">
    金额
  </label>
  <input
    id="amount"
    type="text"
    class="w-full px-4 py-3 bg-surface border border-danger rounded-lg text-base focus:outline-none focus:border-danger focus:ring-2 focus:ring-danger/10 transition-all"
    placeholder="请输入金额"
  />
  <p class="text-sm text-danger flex items-center gap-1">
    <ExclamationCircleIcon class="w-4 h-4" />
    金额不能为空
  </p>
</div>
```

### 3.3 选择器 (Select)

**Tailwind CSS**：
```html
<div class="relative">
  <select class="w-full appearance-none px-4 py-3 bg-surface border border-gray-200 rounded-lg text-base focus:outline-none focus:border-primary focus:ring-2 focus:ring-primary/10 transition-all pr-10">
    <option value="">请选择类别</option>
    <option value="food">餐饮</option>
    <option value="transport">交通</option>
    <option value="shopping">购物</option>
  </select>
  <ChevronDownIcon class="absolute right-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400 pointer-events-none" />
</div>
```

### 3.4 日期选择器

**Tailwind CSS**：
```html
<div class="relative">
  <input
    type="date"
    class="w-full px-4 py-3 bg-surface border border-gray-200 rounded-lg text-base focus:outline-none focus:border-primary focus:ring-2 focus:ring-primary/10 transition-all"
  />
</div>
```

### 3.5 表单布局

**水平表单**（企业端常用）：
```html
<form class="space-y-6">
  <div class="grid grid-cols-2 gap-6">
    <div>
      <label class="block text-sm font-medium text-text-primary mb-2">姓名</label>
      <input type="text" class="input" />
    </div>
    <div>
      <label class="block text-sm font-medium text-text-primary mb-2">邮箱</label>
      <input type="email" class="input" />
    </div>
  </div>
  <div class="flex justify-end gap-3 pt-4 border-t">
    <button class="btn--secondary">取消</button>
    <button class="btn--primary">保存</button>
  </div>
</form>
```

**垂直表单**（个人端常用）：
```html
<form class="space-y-4">
  <div>
    <label class="block text-sm font-medium text-text-primary mb-2">备注</label>
    <textarea class="input min-h-[100px] resize-none" placeholder="添加备注..."></textarea>
  </div>
  <button class="w-full btn--primary">保存</button>
</form>
```

---

## 4. 数据展示组件

### 4.1 表格 (Table)

**Tailwind CSS**：
```html
<div class="overflow-x-auto">
  <table class="w-full">
    <thead>
      <tr class="border-b border-gray-200">
        <th class="text-left py-3 px-4 text-sm font-semibold text-text-primary">日期</th>
        <th class="text-left py-3 px-4 text-sm font-semibold text-text-primary">类别</th>
        <th class="text-right py-3 px-4 text-sm font-semibold text-text-primary">金额</th>
        <th class="text-center py-3 px-4 text-sm font-semibold text-text-primary">操作</th>
      </tr>
    </thead>
    <tbody class="divide-y divide-gray-100">
      <tr class="hover:bg-gray-50 transition-colors">
        <td class="py-3 px-4 text-sm text-text-secondary">2026-04-01</td>
        <td class="py-3 px-4 text-sm text-text-primary">餐饮</td>
        <td class="py-3 px-4 text-sm font-mono tabular-nums text-danger text-right">-¥45.00</td>
        <td class="py-3 px-4 text-center">
          <button class="btn--text--primary">编辑</button>
        </td>
      </tr>
    </tbody>
  </table>
</div>
```

### 4.2 标签 (Badge)

**Tailwind CSS**：
```html
<!-- 收入标签 -->
<span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-success/10 text-success">
  已完成
</span>

<!-- 支出标签 -->
<span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-danger/10 text-danger">
  待审核
</span>

<!-- 警告标签 -->
<span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-warning/10 text-warning">
  进行中
</span>
```

### 4.3 进度条 (Progress)

**Tailwind CSS**：
```html
<div class="space-y-2">
  <div class="flex justify-between text-sm">
    <span class="text-text-secondary">预算使用</span>
    <span class="font-medium text-text-primary">75%</span>
  </div>
  <div class="h-2 bg-gray-100 rounded-full overflow-hidden">
    <div class="h-full bg-warning rounded-full" style="width: 75%"></div>
  </div>
</div>
```

### 4.4 空状态 (Empty State)

**Tailwind CSS**：
```html
<div class="flex flex-col items-center justify-center py-12 px-4 text-center">
  <div class="w-16 h-16 rounded-full bg-gray-100 flex items-center justify-center mb-4">
    <InboxIcon class="w-8 h-8 text-gray-400" />
  </div>
  <h3 class="text-lg font-semibold text-text-primary mb-2">暂无交易记录</h3>
  <p class="text-sm text-text-secondary mb-6">开始记录你的第一笔收支吧</p>
  <button class="btn--primary">添加记录</button>
</div>
```

---

## 5. 导航组件

### 5.1 侧边栏 (Sidebar)

**企业端侧边栏**（固定深色）：
```html
<aside class="w-64 bg-primary text-white h-screen fixed left-0 top-0">
  <div class="p-4 border-b border-white/10">
    <div class="flex items-center gap-2">
      <div class="w-8 h-8 rounded-lg bg-secondary flex items-center justify-center">
        <span class="text-white font-bold">归</span>
      </div>
      <span class="font-semibold">G-Zang</span>
    </div>
  </div>
  <nav class="p-4 space-y-1">
    <a href="/dashboard" class="flex items-center gap-3 px-3 py-2 rounded-lg bg-white/10 text-white">
      <ChartBarIcon class="w-5 h-5" />
      <span>仪表盘</span>
    </a>
    <a href="/transactions" class="flex items-center gap-3 px-3 py-2 rounded-lg text-white/70 hover:bg-white/10 hover:text-white transition-colors">
      <ArrowsRightLeftIcon class="w-5 h-5" />
      <span>交易记录</span>
    </a>
  </nav>
</aside>
```

### 5.2 标签栏 (Tabs)

**Tailwind CSS**：
```html
<div class="border-b border-gray-200">
  <nav class="flex gap-8">
    <button class="pb-4 text-sm font-medium text-primary border-b-2 border-primary">
      全部
    </button>
    <button class="pb-4 text-sm font-medium text-text-secondary hover:text-text-primary border-b-2 border-transparent transition-colors">
      收入
    </button>
    <button class="pb-4 text-sm font-medium text-text-secondary hover:text-text-primary border-b-2 border-transparent transition-colors">
      支出
    </button>
  </nav>
</div>
```

---

## 6. 反馈组件

### 6.1 加载状态 (Loading)

**Tailwind CSS**：
```html
<!-- 按钮加载态 -->
<button class="btn--primary" disabled>
  <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"></path>
  </svg>
  加载中...
</button>

<!-- 骨架屏 -->
<div class="animate-pulse space-y-4">
  <div class="h-4 bg-gray-200 rounded w-3/4"></div>
  <div class="h-4 bg-gray-200 rounded w-1/2"></div>
</div>
```

### 6.2 消息提示 (Toast)

**Tailwind CSS**：
```html
<!-- 成功提示 -->
<div class="fixed bottom-4 right-4 bg-success text-white px-6 py-3 rounded-lg shadow-lg flex items-center gap-3">
  <CheckCircleIcon class="w-5 h-5" />
  <span>保存成功</span>
</div>

<!-- 错误提示 -->
<div class="fixed bottom-4 right-4 bg-danger text-white px-6 py-3 rounded-lg shadow-lg flex items-center gap-3">
  <XCircleIcon class="w-5 h-5" />
  <span>操作失败，请重试</span>
</div>
```

### 6.3 模态框 (Modal)

**Tailwind CSS**：
```html
<!-- 遮罩层 -->
<div class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
  <!-- 模态框 -->
  <div class="bg-surface rounded-xl shadow-xl max-w-md w-full mx-4 overflow-hidden">
    <div class="flex items-center justify-between p-4 border-b">
      <h3 class="text-lg font-semibold text-text-primary">确认删除</h3>
      <button class="btn--icon" aria-label="关闭">
        <XIcon class="w-5 h-5" />
      </button>
    </div>
    <div class="p-4">
      <p class="text-text-secondary">确定要删除这笔交易记录吗？此操作无法撤销。</p>
    </div>
    <div class="flex justify-end gap-3 p-4 border-t bg-gray-50">
      <button class="btn--secondary">取消</button>
      <button class="btn--primary bg-danger hover:bg-danger-dark">删除</button>
    </div>
  </div>
</div>
```

---

## 7. 布局组件

### 7.1 页面容器

**Tailwind CSS**：
```html
<div class="min-h-screen bg-background">
  <!-- 企业端布局 -->
  <div class="flex">
    <aside class="w-64 fixed left-0 top-0 h-screen">...</aside>
    <main class="ml-64 flex-1 p-8">
      <div class="max-w-6xl mx-auto">
        <!-- 页面内容 -->
      </div>
    </main>
  </div>
</div>
```

### 7.2 栅格系统

**Tailwind CSS**：
```html
<!-- 统计卡片网格 -->
<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
  <div class="card">收入</div>
  <div class="card">支出</div>
  <div class="card">结余</div>
  <div class="card">预算</div>
</div>

<!-- 表单网格 -->
<div class="grid grid-cols-1 md:grid-cols-2 gap-6">
  <div>左栏</div>
  <div>右栏</div>
</div>
```

### 7.3 响应式断点

| 断点 | 宽度 | Tailwind | 用途 |
|------|------|----------|------|
| 移动端 | < 640px | `sm:` | 手机 |
| 平板 | 640px+ | `md:` | 平板 |
| 桌面 | 768px+ | `lg:` | 小桌面 |
| 大屏 | 1024px+ | `xl:` | 标准桌面 |
| 超大屏 | 1280px+ | `2xl:` | 大屏 |

---

## 附录：组件速查表

### 颜色类名

| 语义 | Tailwind | CSS 变量 |
|------|----------|----------|
| 品牌主色 | `text-primary`, `bg-primary` | `--gzang-primary` |
| 品牌强调色 | `text-secondary`, `bg-secondary` | `--gzang-secondary` |
| 成功/收入 | `text-success`, `bg-success` | `--gzang-success` |
| 错误/支出 | `text-danger`, `bg-danger` | `--gzang-danger` |
| 警告 | `text-warning`, `bg-warning` | `--gzang-warning` |
| 信息 | `text-info`, `bg-info` | `--gzang-info` |
| 主文本 | `text-text-primary` | `--gzang-text-primary` |
| 次文本 | `text-text-secondary` | `--gzang-text-secondary` |
| 背景 | `bg-background` | `--gzang-bg` |
| 卡片 | `bg-surface` | `--gzang-surface` |

### 常用工具类

| 用途 | Tailwind |
|------|----------|
| 财务数字 | `font-mono tabular-nums` |
| 卡片阴影 | `shadow-sm hover:shadow-md` |
| 圆角 | `rounded-lg` (按钮), `rounded-xl` (卡片) |
| 过渡动画 | `transition-all duration-200` |
| 焦点状态 | `focus:outline-none focus:ring-2 focus:ring-primary/10` |

---

**版本**：1.0.0
**更新**：2026-04-02
