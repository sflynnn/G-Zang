# G-Zang PC端平台详细功能设计

> **文档版本**：1.0.0
> **最后更新**：2026-03-27
> **维护人员**：前端开发工程师
> **关联规则**：`pc-main.mdc`、`pc-personal.mdc`、`pc-business.mdc`

## 1. 平台概述

### 1.1 平台架构
PC端采用微前端架构，分为两个独立子应用：
- **pc-personal**：个人记账子应用
- **pc-main**：主应用框架（负责路由、认证、子应用加载）

### 1.2 目标用户
- **个人用户**：日常记账、消费分析、预算管理
- **企业用户**：公司财务管理、业务关联记账

### 1.3 技术栈
- **前端框架**：Vue 3 + TypeScript + Naive UI
- **微前端**：qiankun
- **状态管理**：Pinia
- **路由**：Vue Router 4
- **HTTP客户端**：Axios

---

## 2. 主应用框架 (pc-main)

### 2.1 应用结构
```
pc-main/
├── src/
│   ├── layouts/          # 布局组件
│   ├── router/           # 路由配置
│   ├── stores/           # 状态管理
│   ├── micro-apps/       # 子应用配置
│   └── utils/            # 工具函数
├── micro-apps.json       # 子应用注册配置
└── index.html
```

### 2.2 主应用职责
- 用户认证和权限验证
- 子应用注册和加载
- 全局状态管理（用户信息、公司信息）
- 主框架布局和导航
- 路由分发和权限控制

### 2.3 子应用配置
```javascript
// micro-apps.json
[
  {
    "name": "pc-personal",
    "entry": "//localhost:8081",
    "container": "#subapp-container",
    "activeRule": "/personal",
    "props": {
      "routerBase": "/personal",
      "userInfo": "${userInfo}",
      "companyInfo": "${companyInfo}"
    }
  },
  {
    "name": "pc-business",
    "entry": "//localhost:8082",
    "container": "#subapp-container",
    "activeRule": "/business",
    "props": {
      "routerBase": "/business",
      "userInfo": "${userInfo}",
      "companyInfo": "${companyInfo}"
    }
  }
]
```

---

## 3. 个人记账子应用 (pc-personal)

### 3.1 应用概述
个人记账子应用专注于个人用户的记账需求，提供便捷的记账体验和详细的数据分析。

### 3.2 主页面布局
```
┌─────────────────────────────────────────────────┐
│                    顶部工具栏                      │
├─────────────────────────────────────────────────┤
│  ┌─────────────┬─────────────────────────────┐   │
│  │             │                             │   │
│  │   左侧菜单    │          主内容区域            │   │
│  │             │                             │   │
│  └─────────────┴─────────────────────────────┘   │
├─────────────────────────────────────────────────┤
│                    底部状态栏                      │
└─────────────────────────────────────────────────┘
```

### 3.3 记账中心模块

#### 3.3.1 快速记账页面
**页面路径**：`/personal/accounting/quick`

##### 页面布局
- **左侧面板**：记账表单
- **右侧面板**：快捷操作区
- **底部**：操作按钮区

##### 记账表单设计
```vue
<template>
  <n-card title="快速记账">
    <!-- 交易类型选择 -->
    <n-radio-group v-model:value="form.type" class="mb-4">
      <n-radio value="1" button-style>收入</n-radio>
      <n-radio value="2" button-style>支出</n-radio>
      <n-radio value="3" button-style>转账</n-radio>
    </n-radio-group>

    <!-- 金额输入 -->
    <n-form-item label="金额" required>
      <n-input-number
        v-model:value="form.amount"
        :precision="2"
        :min="0.01"
        placeholder="请输入金额"
        class="w-full"
      />
    </n-form-item>

    <!-- 分类选择 -->
    <n-form-item label="分类" required>
      <n-cascader
        v-model:value="form.categoryId"
        :options="categoryOptions"
        placeholder="请选择分类"
        class="w-full"
      />
    </n-form-item>

    <!-- 账户选择 -->
    <n-form-item label="账户" required>
      <n-select
        v-model:value="form.accountId"
        :options="accountOptions"
        placeholder="请选择账户"
        class="w-full"
      />
    </n-form-item>

    <!-- 交易时间 -->
    <n-form-item label="交易时间">
      <n-date-picker
        v-model:value="form.transactionTime"
        type="datetime"
        format="yyyy-MM-dd HH:mm:ss"
        class="w-full"
      />
    </n-form-item>

    <!-- 备注 -->
    <n-form-item label="备注">
      <n-input
        v-model:value="form.remark"
        type="textarea"
        placeholder="请输入备注信息"
        :maxlength="500"
        show-count
        class="w-full"
      />
    </n-form-item>

    <!-- 操作按钮 -->
    <n-space justify="end" class="mt-6">
      <n-button @click="resetForm">重置</n-button>
      <n-button type="primary" @click="submitForm" :loading="submitting">
        确认记账
      </n-button>
    </n-space>
  </n-card>
</template>
```

##### 前端功能
- **智能表单验证**：
  - 金额必填且大于0
  - 分类和账户必选
  - 备注长度限制

- **快捷操作**：
  - 常用金额快速选择
  - 最近使用的分类记忆
  - 分类搜索功能

- **数据提交**：
  - 表单数据校验
  - API调用和错误处理
  - 成功提示和页面跳转

##### 后端API
```javascript
POST /api/v1/accounting/transaction
Request:
{
  "amount": 25.50,
  "type": 2,
  "categoryId": 101,
  "accountId": 201,
  "transactionTime": "2024-01-15 12:30:00",
  "remark": "午餐"
}

Response:
{
  "code": 0,
  "message": "记账成功",
  "data": { "transactionId": 12345 }
}
```

#### 3.3.2 交易记录页面
**页面路径**：`/personal/accounting/transactions`

##### 页面功能
- **列表展示**：
  - 分页表格显示交易记录
  - 列：时间、类型、分类、账户、金额、备注、操作
  - 支持列排序和隐藏

- **筛选功能**：
  - 时间范围选择器
  - 交易类型筛选
  - 分类筛选
  - 账户筛选
  - 金额范围筛选
  - 关键词搜索

- **批量操作**：
  - 批量删除
  - 批量修改分类
  - 批量导出

##### 表格组件设计
```vue
<template>
  <n-data-table
    :columns="columns"
    :data="data"
    :pagination="pagination"
    :loading="loading"
    :row-key="row => row.id"
    :single-line="false"
    max-height="600"
  />
</template>

<script setup>
const columns = [
  {
    type: 'selection'
  },
  {
    title: '交易时间',
    key: 'transactionTime',
    sorter: true,
    width: 160
  },
  {
    title: '类型',
    key: 'type',
    width: 80,
    render: (row) => {
      const typeMap = { 1: '收入', 2: '支出', 3: '转账' }
      return h('n-tag', { type: row.type === 1 ? 'success' : 'error' }, typeMap[row.type])
    }
  },
  {
    title: '分类',
    key: 'categoryName',
    width: 120
  },
  {
    title: '账户',
    key: 'accountName',
    width: 120
  },
  {
    title: '金额',
    key: 'amount',
    width: 100,
    render: (row) => `¥${row.amount.toFixed(2)}`
  },
  {
    title: '备注',
    key: 'remark',
    ellipsis: true,
    width: 200
  },
  {
    title: '操作',
    key: 'actions',
    width: 150,
    render: (row) => h('n-space', {}, [
      h('n-button', { size: 'small', onClick: () => editTransaction(row) }, '编辑'),
      h('n-button', { size: 'small', type: 'error', onClick: () => deleteTransaction(row) }, '删除')
    ])
  }
]
</script>
```

##### 后端API
```javascript
GET /api/v1/accounting/transactions?page=1&size=20&startDate=2024-01-01&endDate=2024-01-31&type=2&categoryId=101&keyword=午餐

Response:
{
  "code": 0,
  "data": {
    "total": 156,
    "records": [
      {
        "id": 12345,
        "amount": 25.50,
        "type": 2,
        "categoryName": "餐饮",
        "accountName": "支付宝",
        "transactionTime": "2024-01-15 12:30:00",
        "remark": "午餐"
      }
    ]
  }
}
```

### 3.4 账户管理模块

#### 3.4.1 账户列表页面
**页面路径**：`/personal/accounts`

##### 页面功能
- **账户卡片展示**：
  - 账户名称、类型、余额
  - 账户状态指示
  - 快捷操作按钮

- **账户统计**：
  - 总资产
  - 各类型账户分布
  - 账户余额变化趋势

##### 账户卡片设计
```vue
<template>
  <n-card class="account-card" hoverable>
    <n-space vertical size="small">
      <n-space justify="space-between">
        <n-text strong>{{ account.accountName }}</n-text>
        <n-tag :type="account.accountType === 1 ? 'default' : 'info'">
          {{ accountTypeMap[account.accountType] }}
        </n-tag>
      </n-space>

      <n-text depth="3" style="font-size: 24px; font-weight: bold;">
        ¥{{ account.balance.toFixed(2) }}
      </n-text>

      <n-space>
        <n-button size="small" @click="editAccount(account)">编辑</n-button>
        <n-button size="small" type="error" @click="deleteAccount(account)">删除</n-button>
      </n-space>
    </n-space>
  </n-card>
</template>
```

#### 3.4.2 添加账户页面
**页面路径**：`/personal/accounts/add`

##### 表单字段
- **账户名称**：必填，唯一性校验
- **账户类型**：现金、银行卡、电子支付
- **初始余额**：可选，默认0
- **备注**：可选

### 3.5 分类管理模块

#### 3.5.1 分类设置页面
**页面路径**：`/personal/categories`

##### 页面布局
- **标签页**：收入分类 / 支出分类
- **树形结构**：支持无限级分类
- **操作区域**：添加、编辑、删除分类

##### 分类树组件
```vue
<template>
  <n-tree
    :data="categoryTree"
    :render-prefix="renderPrefix"
    :render-suffix="renderSuffix"
    :on-load="handleLoad"
    block-line
  />
</template>

<script setup>
const renderSuffix = ({ option }) => {
  return h('n-space', {}, [
    h('n-button', {
      size: 'tiny',
      onClick: () => addSubCategory(option)
    }, '添加子类'),
    h('n-button', {
      size: 'tiny',
      onClick: () => editCategory(option)
    }, '编辑'),
    h('n-button', {
      size: 'tiny',
      type: 'error',
      onClick: () => deleteCategory(option)
    }, '删除')
  ])
}
</script>
```

### 3.6 报表中心模块

#### 3.6.1 收支概况页面
**页面路径**：`/personal/reports/overview`

##### 图表展示
- **收支趋势图**：月度收支对比柱状图
- **分类占比饼图**：支出分类占比
- **账户余额图**：各账户余额分布

##### 数据卡片
```vue
<template>
  <n-space>
    <n-card class="summary-card">
      <n-statistic title="本月收入" :value="summary.monthIncome">
        <template #prefix>
          <n-icon><TrendingUpIcon /></n-icon>
        </template>
      </n-statistic>
    </n-card>

    <n-card class="summary-card">
      <n-statistic title="本月支出" :value="summary.monthExpense">
        <template #prefix>
          <n-icon><TrendingDownIcon /></n-icon>
        </template>
      </n-statistic>
    </n-card>

    <n-card class="summary-card">
      <n-statistic title="结余" :value="summary.balance">
        <template #prefix>
          <n-icon><WalletIcon /></n-icon>
        </template>
      </n-statistic>
    </n-card>
  </n-space>
</template>
```

##### 后端API
```javascript
GET /api/v1/report/summary?startDate=2024-01-01&endDate=2024-01-31

Response:
{
  "code": 0,
  "data": {
    "totalIncome": 8000.00,
    "totalExpense": 4500.00,
    "netIncome": 3500.00,
    "incomeByMonth": [
      { "month": "2024-01", "amount": 8000.00 }
    ],
    "expenseByMonth": [
      { "month": "2024-01", "amount": 4500.00 }
    ]
  }
}
```

#### 3.6.2 分类统计页面
**页面路径**：`/personal/reports/categories`

##### 图表类型
- **饼图**：分类占比
- **柱状图**：月度对比
- **趋势图**：分类变化趋势

##### 交互功能
- 点击图例筛选
- 时间范围切换
- 数据导出

### 3.7 个人设置模块

#### 3.7.1 个人信息页面
**页面路径**：`/personal/settings/profile`

##### 设置项
- **基本信息**：昵称、头像、性别、生日
- **联系方式**：手机号、邮箱
- **偏好设置**：默认账户、默认分类

#### 3.7.2 安全设置页面
**页面路径**：`/personal/settings/security`

##### 功能
- **修改密码**：原密码、新密码、确认密码
- **登录设备管理**：查看登录设备、强制下线

---

## 4. 企业业务子应用 (pc-business)

### 4.1 应用概述
企业业务子应用专注于企业级财务管理，提供业务关联记账和企业报表功能。

### 4.2 企业记账模块

#### 4.2.1 企业记账页面
**页面路径**：`/business/accounting`

##### 特色功能
- **业务关联**：支持关联维修单、采购单等业务
- **多用户协作**：支持多人记账和审核
- **成本中心**：支持成本归属和分析

##### 记账表单扩展
```vue
<template>
  <!-- 基础记账字段 -->
  <!-- ... 金额、分类、账户等 ... -->

  <!-- 业务关联字段 -->
  <n-form-item label="关联业务">
    <n-select
      v-model:value="form.businessType"
      :options="businessOptions"
      placeholder="选择业务类型"
      @update:value="onBusinessTypeChange"
    />
  </n-form-item>

  <n-form-item v-if="form.businessType" label="业务单号">
    <n-input
      v-model:value="form.businessId"
      placeholder="请输入业务单号"
      @blur="validateBusinessId"
    />
  </n-form-item>

  <!-- 成本中心 -->
  <n-form-item label="成本中心">
    <n-tree-select
      v-model:value="form.costCenterId"
      :options="costCenterOptions"
      placeholder="选择成本中心"
    />
  </n-form-item>
</template>
```

### 4.3 财务管理模块

#### 4.3.1 预算管理页面
**页面路径**：`/business/budgets`

##### 预算设置
- **预算类型**：年度预算、月度预算、分类预算
- **预算金额**：设置预算额度
- **预警设置**：预算使用百分比预警

##### 预算监控
- **预算执行进度条**
- **预算 vs 实际对比图表**
- **预算预警提醒**

### 4.4 业务关联模块

#### 4.4.1 维修业务页面
**页面路径**：`/business/maintenance`

##### 功能
- **维修单列表**：查看所有维修单
- **成本录入**：为维修单录入配件成本、人工成本
- **财务关联**：自动生成相关财务记录

##### 维修单详情页面
```vue
<template>
  <n-card>
    <!-- 维修单基本信息 -->
    <n-descriptions :column="2" title="维修单信息">
      <n-descriptions-item label="单号">{{ repairOrder.orderNo }}</n-descriptions-item>
      <n-descriptions-item label="客户">{{ repairOrder.customerName }}</n-descriptions-item>
      <n-descriptions-item label="车辆">{{ repairOrder.vehicleInfo }}</n-descriptions-item>
      <n-descriptions-item label="维修类型">{{ repairOrder.repairType }}</n-descriptions-item>
    </n-descriptions>

    <!-- 成本明细 -->
    <n-table :columns="costColumns" :data="repairOrder.costs" class="mt-4" />

    <!-- 录入成本 -->
    <n-card title="录入成本" class="mt-4">
      <n-form :model="costForm" @submit.prevent="submitCost">
        <n-form-item label="成本类型">
          <n-radio-group v-model:value="costForm.type">
            <n-radio value="parts">配件成本</n-radio>
            <n-radio value="labor">人工成本</n-radio>
          </n-radio-group>
        </n-form-item>

        <n-form-item label="金额" required>
          <n-input-number v-model:value="costForm.amount" :precision="2" />
        </n-form-item>

        <n-form-item label="备注">
          <n-input v-model:value="costForm.remark" type="textarea" />
        </n-form-item>

        <n-form-item>
          <n-space>
            <n-button type="primary" :loading="submitting" @click="submitCost">
              录入成本
            </n-button>
          </n-space>
        </n-form-item>
      </n-form>
    </n-card>
  </n-card>
</template>
```

---

## 5. 前后端技术实现要点

### 5.1 前端实现要点

#### 5.1.1 微前端架构
- **主应用**：负责路由分发和子应用加载
- **子应用**：独立开发、部署和运行
- **通信机制**：props传递全局状态

#### 5.1.2 组件设计
- **表单组件**：统一的表单验证和提交
- **表格组件**：支持排序、筛选、分页
- **图表组件**：ECharts封装和配置
- **弹窗组件**：统一的弹窗交互

#### 5.1.3 状态管理
```javascript
// stores/user.js
export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: null,
    companyInfo: null,
    permissions: []
  }),

  actions: {
    async login(credentials) {
      const response = await api.login(credentials)
      this.userInfo = response.data.user
      this.permissions = response.data.permissions
    },

    async loadCompanyInfo() {
      const response = await api.getCompanyInfo()
      this.companyInfo = response.data
    }
  }
})
```

### 5.2 后端实现要点

#### 5.2.1 多租户处理
- **个人用户**：user_id过滤
- **企业用户**：company_id过滤
- **数据隔离**：MyBatis Plus拦截器自动注入

#### 5.2.2 业务逻辑
- **记账处理**：事务保证数据一致性
- **余额计算**：实时更新账户余额
- **权限校验**：方法级权限控制

#### 5.2.3 性能优化
- **查询优化**：索引和分页
- **缓存策略**：Redis缓存热点数据
- **异步处理**：批量操作异步执行

---

## 6. 用户体验设计

### 6.1 响应式布局
- **桌面端**：完整功能展示
- **平板端**：自适应布局调整
- **快捷键支持**：常用操作快捷键

### 6.2 交互优化
- **加载状态**：骨架屏和加载动画
- **操作反馈**：成功/失败提示
- **数据刷新**：实时数据更新
- **离线支持**：网络异常处理

### 6.3 无障碍支持
- **键盘导航**：Tab键导航
- **屏幕阅读器**：语义化标签
- **高对比度**：主题切换

---

**文档版本**：1.0.0
**最后更新**：2026-01-14
**维护人员**：G-Zang开发团队
