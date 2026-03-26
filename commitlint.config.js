module.exports = {
  extends: ["@commitlint/config-conventional"],
  rules: {
    // 类型枚举
    "type-enum": [
      2,
      "always",
      [
        "feat", // 新功能
        "fix", // 修复 bug
        "docs", // 文档更新
        "style", // 代码格式（不影响功能）
        "refactor", // 重构
        "perf", // 性能优化
        "test", // 测试相关
        "build", // 构建相关
        "ci", // CI/CD 相关
        "chore", // 其他杂项
        "revert", // 回滚
        "wip", // 工作进行中
        "types", // 类型定义更新
      ],
    ],
    // 允许 subject 使用各种大小写格式
    "subject-case": [0],
    // 允许 type 使用各种大小写格式
    "type-case": [0],
    // 允许 type 空值
    "type-empty": [2, "never"],
    // 允许 subject 空值
    "subject-empty": [2, "never"],
    // 头部最大长度
    "header-max-length": [2, "always", 100],
    // 主体最大长度
    "body-max-length": [2, "always", 200],
    // Footer 最大长度
    "footer-max-length": [2, "always", 200],
    // 空 body 分隔符
    "body-empty": [0],
    // Footer 空行
    "footer-empty": [0],
    // Scope 枚举（可自定义项目 scope）
    "scope-enum": [
      2,
      "always",
      [
        "core", // 核心模块
        "apps", // 应用目录
        "packages", // 包目录
        "server", // 服务端
        "admin", // 管理端
        "mobile", // 移动端
        "pc", // PC端
        "shared", // 共享模块
        "config", // 配置相关
        "deps", // 依赖更新
        "release", // 发布相关
        "workflow", // 工作流
        "docs", // 文档
        "i18n", // 国际化
        "api", // API 相关
        "db", // 数据库相关
        "auth", // 认证授权
        "ui", // UI 组件
        "utils", // 工具函数
        "types", // 类型定义
        "lint", // 代码规范
        "test", // 测试
        "security", // 安全相关
        "performance", // 性能相关
        "migrate", // 数据迁移
        "deploy", // 部署相关
        "monitor", // 监控相关
        "analytics", // 分析相关
        "notification", // 通知相关
        "storage", // 存储相关
        "cache", // 缓存相关
        "gateway", // 网关相关
        "microservice", // 微服务相关
        "websocket", // WebSocket 相关
        "mail", // 邮件相关
        "sms", // 短信相关
        "payment", // 支付相关
        "file", // 文件相关
        "image", // 图片相关
        "video", // 视频相关
        "audio", // 音频相关
        "export", // 导出功能
        "import", // 导入功能
        "report", // 报表相关
        "dashboard", // 仪表盘相关
        "plugin", // 插件相关
        "theme", // 主题相关
        "template", // 模板相关
        "workflow", // 工作流相关
        "rule", // 规则引擎
        "audit", // 审计日志
        "backup", // 备份相关
        "sync", // 同步相关
        "integration", // 第三方集成
        "sdk", // SDK 相关
        "cli", // CLI 工具
        "migration", // 数据库迁移
        "schema", // 数据库 schema
        "seed", // 数据种子
        "mock", // 模拟数据
        "debug", // 调试相关
        "hotfix", // 热修复
        "patch", // 补丁
        "minor", // 小版本
        "major", // 主版本
      ],
    ],
    // 允许 scope 空值
    "scope-empty": [2, "never"],
    // 允许 footer 空值
    "footer-empty": [2, "never"],
  },
  prompt: {
    // 自定义提示信息（需要 @commitlint/prompt-cli 支持）
    messages: {
      type: "选择提交类型:",
      scope: "选择影响范围 (可选):",
      customScope: "请输入自定义范围:",
      subject: "简短描述 (必填):",
      body: "详细描述 (可选, 使用 | 换行):",
      breaking: "列出 BREAKING CHANGES (可选):",
      footerPrefixesSelect: "选择关联的 issue 类型 (可选):",
      footerPrefixes: "输入 issue 前缀 (如 #, GH-):",
      footer: "关联的 issue (可选):",
      generatingByAI: "由 AI 生成... (可选):",
      validateCommit: "确认提交信息:",
    },
    types: [
      { value: "feat", name: "feat:     新功能", emoji: "✨" },
      { value: "fix", name: "fix:      修复 bug", emoji: "🐛" },
      { value: "docs", name: "docs:     文档更新", emoji: "📝" },
      { value: "style", name: "style:    代码格式", emoji: "💄" },
      { value: "refactor", name: "refactor: 重构", emoji: "🔄" },
      { value: "perf", name: "perf:     性能优化", emoji: "⚡️" },
      { value: "test", name: "test:     测试相关", emoji: "✅" },
      { value: "build", name: "build:    构建相关", emoji: "📦️" },
      { value: "ci", name: "ci:       CI/CD", emoji: "🎡" },
      { value: "chore", name: "chore:    杂项", emoji: "♻️" },
      { value: "revert", name: "revert:   回滚", emoji: "⏪" },
      { value: "wip", name: "wip:      工作进行中", emoji: "🚧" },
      { value: "types", name: "types:    类型定义", emoji: "🏷️" },
    ],
  },
};
