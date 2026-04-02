---
name: gzang-brand
description: |
  G-Zang 归藏财务管理系统品牌视觉指南。当用户提到品牌设计、视觉规范、品牌色、品牌图标、品牌字体、品牌风格，或者需要为 G-Zang 项目创建任何 UI 界面、PPT 演示文稿、文档、海报、设计稿时，必须使用此技能。
  
  触发场景包括但不限于：
  - "按照 G-Zang 品牌规范设计一个界面"
  - "帮我做一份 G-Zang 的 PPT"
  - "用归藏的品牌色创建一个图表"
  - "G-Zang 的视觉设计标准是什么"
  - 为任何 G-Zang 相关项目创建视觉内容
  
  包含完整的品牌色彩系统、字体系统、组件样式、设计隐喻等品牌资产。
---

# G-Zang 归藏品牌视觉指南

## 0. 品牌理念

### 智简·归藏 (Smart & Solid)

G-Zang 是面向 B2B & B2C 的 SaaS 财务管理系统，秉承"智简·归藏"的设计理念：

- **企业端**：强调 "Trust/Professionalism" (信任/专业性) — 为企业提供稳定、专业、可信赖的财务管理解决方案
- **个人端**：强调 "Simplicity/Fun" (简洁/有趣) — 为个人用户提供简单有趣的记账体验

### 品牌隐喻

| 隐喻 | 设计表达 | 含义 |
|------|---------|------|
| **归藏 (Container)** | 卡片式布局 | 象征财富存储容器，稳重安全 |
| **流 (Flow)** | 流畅动效 | 资金流动、财务流转的隐喻 |
| **青 (Qing)** | 归藏青主色 | 中国传统色彩，沉稳内敛 |
| **金 (Gold)** | 琉璃金辅色 | 财富、成功、价值 |

---

## 1. 品牌色彩系统

### 1.1 核心色彩 (Primary Colors)

| 色名 | 色值 | 用途 | CSS 变量 |
|------|------|------|----------|
| **归藏青** | `#0F4C5C` | 品牌主色、标题、侧边栏、主文本 | `--gzang-primary` |
| 浅青 | `#186a7d` | 悬停状态、次要强调 | `--gzang-primary-light` |
| 深青 | `#0a3644` | 深色主题主色 | `--gzang-primary-dark` |
| **琉璃金** | `#FB8B24` | 品牌辅色、CTA 按钮、高亮、VIP | `--gzang-secondary` |
| 浅金 | `#fca03d` | 悬停状态 | `--gzang-secondary-light` |
| 深金 | `#e67a1a` | 深色主题 | `--gzang-secondary-dark` |

### 1.2 功能色彩 (Functional Colors)

| 色名 | 色值 | 用途 | CSS 变量 |
|------|------|------|----------|
| **利润绿** | `#06D6A0` | 收入、成功状态、正向金额 | `--gzang-success` |
| **支出红** | `#EF476F` | 支出、错误状态、负向金额 | `--gzang-danger` |
| **警示黄** | `#FFD166` | 警告状态、待处理 | `--gzang-warning` |
| **信息蓝** | `#118AB2` | 信息提示、链接 | `--gzang-info` |

### 1.3 中性色彩 (Neutral Colors)

| 色名 | 色值 | 用途 | CSS 变量 |
|------|------|------|----------|
| 背景灰 | `#F8F9FA` | 页面背景 | `--gzang-bg` |
| 卡片白 | `#FFFFFF` | 卡片、面板背景 | `--gzang-surface` |
| 主文本 | `#1F2937` | 标题、重要文本 | `--gzang-text-primary` |
| 次文本 | `#6B7280` | 说明文字、标签 | `--gzang-text-secondary` |
| 边框灰 | `#E5E7EB` | 分割线、边框 | `--gzang-border` |

### 1.4 深色主题 (Dark Mode)

```css
[data-theme="dark"] {
  --gzang-primary: #0a3644;
  --gzang-primary-light: #0F4C5C;
  --gzang-bg: #111827;
  --gzang-surface: #1F2937;
  --gzang-text-primary: #F9FAFB;
  --gzang-text-secondary: #9CA3AF;
  --gzang-border: #374151;
}
```

### 1.5 Tailwind CSS 配置

```javascript
// tailwind.config.js
colors: {
  primary: {
    DEFAULT: '#0F4C5C',
    light: '#186a7d',
    dark: '#0a3644',
  },
  secondary: {
    DEFAULT: '#FB8B24',
    light: '#fca03d',
    dark: '#e67a1a',
  },
  success: '#06D6A0',
  danger: '#EF476F',
  warning: '#FFD166',
  info: '#118AB2',
  background: '#F8F9FA',
  surface: '#FFFFFF',
}
```

---

## 2. 字体系统

### 2.1 字体家族

```css
/* UI 界面字体 */
--font-ui: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;

/* 中文文本 */
--font-cn: 'PingFang SC', 'Microsoft YaHei', 'Hiragino Sans GB', sans-serif;

/* 数字/货币（等宽，确保垂直对齐） */
--font-mono: 'Roboto Mono', 'JetBrains Mono', 'SF Mono', monospace;
```

### 2.2 字号系统

```css
:root {
  --text-xs: 0.75rem;    /* 12px - 标签、小注释 */
  --text-sm: 0.875rem;   /* 14px - 次要文本 */
  --text-base: 1rem;     /* 16px - 正文 */
  --text-lg: 1.125rem;   /* 18px - 小标题 */
  --text-xl: 1.25rem;    /* 20px - 标题 */
  --text-2xl: 1.5rem;    /* 24px - 页面标题 */
  --text-3xl: 1.875rem;  /* 30px - 大标题 */
  --text-4xl: 2.25rem;   /* 36px - 横幅标题 */
}
```

### 2.3 字重系统

```css
:root {
  --font-normal: 400;
  --font-medium: 500;   /* 默认字重 */
  --font-semibold: 600; /* 强调 */
  --font-bold: 700;     /* 标题 */
}
```

### 2.4 财务数字规范

**重要：所有财务数字必须使用等宽字体 (`font-mono`)，确保表格中垂直对齐。**

```css
/* 财务数字样式 */
.amount {
  font-family: var(--font-mono);
  font-variant-numeric: tabular-nums; /* 数字等宽对齐 */
}

/* 收入 - 利润绿 */
.amount--income {
  color: var(--gzang-success);
}

/* 支出 - 支出红 */
.amount--expense {
  color: var(--gzang-danger);
}

/* 转账 - 归藏青 */
.amount--transfer {
  color: var(--gzang-primary);
}
```

---

## 3. 组件样式规范

### 3.1 按钮

#### 主要按钮 (CTA - 琉璃金)

```css
.btn--primary {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-4);
  background-color: var(--gzang-secondary);
  color: white;
  font-weight: var(--font-medium);
  font-size: var(--text-sm);
  border-radius: 0.5rem;
  border: none;
  cursor: pointer;
  transition: all 150ms ease;
}

.btn--primary:hover:not(:disabled) {
  background-color: var(--gzang-secondary-dark);
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.btn--primary:active {
  transform: scale(0.98);
}
```

#### 次要按钮

```css
.btn--secondary {
  background-color: transparent;
  color: var(--gzang-primary);
  border: 1px solid var(--gzang-primary);
}

.btn--secondary:hover:not(:disabled) {
  background-color: var(--gzang-primary);
  color: white;
}
```

#### Tailwind CSS 写法

```html
<!-- 主要按钮 -->
<button class="bg-secondary text-white hover:bg-secondary-dark active:scale-95 transition-all rounded-lg shadow-md">
  确认操作
</button>

<!-- 次要按钮 -->
<button class="border border-primary text-primary hover:bg-primary hover:text-white rounded-lg transition-all">
  取消
</button>
```

### 3.2 卡片

```css
.card {
  background-color: var(--gzang-surface);
  border-radius: 0.75rem;
  box-shadow: var(--shadow-sm);
  padding: var(--space-6);
  transition: all 200ms ease;
}

.card:hover {
  box-shadow: var(--shadow-md);
}
```

```html
<!-- Tailwind CSS -->
<div class="bg-surface rounded-xl shadow-[0_4px_12px_rgba(0,0,0,0.05)] p-6 hover:shadow-md transition-shadow">
  <!-- 内容 -->
</div>
```

### 3.3 表单输入

```css
.input {
  width: 100%;
  padding: var(--space-3) var(--space-4);
  font-family: var(--font-ui);
  font-size: var(--text-base);
  background-color: var(--gzang-surface);
  border: 1px solid var(--gzang-border);
  border-radius: 0.5rem;
  transition: all 150ms ease;
}

.input:focus {
  outline: none;
  border-color: var(--gzang-primary);
  box-shadow: 0 0 0 3px rgb(15 76 92 / 0.1);
}

/* 错误状态 */
.input--error {
  border-color: var(--gzang-danger);
}

.input--error:focus {
  box-shadow: 0 0 0 3px rgb(239 71 111 / 0.1);
}
```

### 3.4 统计卡片

```html
<div class="bg-surface rounded-xl p-6 shadow-sm">
  <div class="flex items-center justify-between">
    <div>
      <p class="text-text-secondary text-sm">本月收入</p>
      <p class="text-2xl font-bold text-success font-mono tabular-nums">¥12,580.00</p>
    </div>
    <div class="w-12 h-12 rounded-full bg-success/10 flex items-center justify-center">
      <TrendingUpIcon class="w-6 h-6 text-success" />
    </div>
  </div>
</div>
```

---

## 4. 间距系统

```css
:root {
  --space-1: 0.25rem;   /* 4px */
  --space-2: 0.5rem;    /* 8px */
  --space-3: 0.75rem;   /* 12px */
  --space-4: 1rem;      /* 16px */
  --space-5: 1.25rem;   /* 20px */
  --space-6: 1.5rem;    /* 24px */
  --space-8: 2rem;      /* 32px */
  --space-10: 2.5rem;   /* 40px */
  --space-12: 3rem;     /* 48px */
  --space-16: 4rem;     /* 64px */
}
```

---

## 5. 阴影系统

```css
:root {
  /* 柔和浮雕（卡片默认） */
  --shadow-sm: 0 1px 2px 0 rgb(0 0 0 / 0.05);
  
  /* 悬停状态（卡片） */
  --shadow-md: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
  
  /* 弹窗/下拉 */
  --shadow-lg: 0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1);
  
  /* 深阴影（模态框） */
  --shadow-xl: 0 20px 25px -5px rgb(0 0 0 / 0.1), 0 8px 10px -6px rgb(0 0 0 / 0.1);
}
```

---

## 6. 设计原则

### 6.1 企业端 vs 个人端

| 维度 | 企业端 (pc-admin) | 个人端 (pc-personal) |
|------|------------------|---------------------|
| 布局 | Desktop First，固定侧边栏 | Mobile First，可折叠 |
| 视觉 | 密集、专业、有序 | 空旷、友好、有趣 |
| 色彩 | 归藏青为主 | 琉璃金为亮点 |
| 交互 | 精准、高效 | 简单、快捷 |

### 6.2 无障碍标准 (WCAG AA)

- **色彩对比度**：文本与背景对比度 ≥ 4.5:1
- **焦点可见**：交互元素有清晰的焦点状态
- **键盘导航**：支持 Tab 键导航
- **ARIA 标签**：图标按钮必须有 `aria-label`

```css
/* ✅ Good: 色彩对比度 4.5:1 */
.text-primary {
  color: #1F2937;
  background: #FFFFFF;
  /* 对比度 15.9:1 ✅ */
}

.text-secondary {
  color: #6B7280;
  background: #FFFFFF;
  /* 对比度 5.74:1 ✅ */
}

/* ❌ Bad: 色彩对比度不足 */
.text-muted {
  color: #9CA3AF;
  background: #FFFFFF;
  /* 对比度 2.58:1 ❌ */
}
```

---

## 7. 品牌应用示例

### 7.1 PPT / 演示文稿

- **封面主色**：归藏青 `#0F4C5C`
- **强调色**：琉璃金 `#FB8B24`
- **字体**：中文使用思源黑体/微软雅黑，英文使用 Inter
- **图表配色**：使用功能色系统

### 7.2 文档 / 报告

- **标题色**：归藏青
- **强调色**：琉璃金（用于关键数据）
- **金额**：使用利润绿/支出红区分收支

### 7.3 营销物料

- **主视觉**：归藏青 + 琉璃金
- **图标风格**：线性图标，统一线宽
- **插图风格**：简约扁平，现代商务

---

## 8. 参考文档

了解更多设计细节：

- [品牌色彩详解](./references/brand-colors.md) - 色彩心理学、色彩搭配、应用场景
- [组件样式库](./references/component-styles.md) - 完整组件代码示例

---

**版本**：1.0.0
**更新**：2026-04-02
**维护**：G-Zang 设计团队
