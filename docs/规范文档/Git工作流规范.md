# G-Zang Git 工作流规范

> **文档版本**：1.0.0
> **最后更新**：2026-03-27
> **维护人员**：Git 工作流管理员
> **关联规则**：`git-workflow.mdc`、`.cursor/rules/git-workflow.mdc`

---

## 1. 分支策略

### 采用 Trunk-Based Development

```
main ─────●────●────●────●────●────●─── (始终可部署)
          \  /          \  /          \  /
           ●             ●             ●     (短期特性分支)
```

### 分支类型

| 分支类型 | 命名格式 | 生命周期 | 合并方式 |
|---------|---------|---------|---------|
| 特性分支 | `feat/xxx-123` | 1天-1周 | Squash Merge |
| 修复分支 | `fix/xxx-123` | 几小时-1天 | Squash Merge |
| 热修复分支 | `hotfix/xxx` | 几小时 | Merge |
| 发布分支 | `release/x.y.z` | 发布周期 | Merge |

---

## 2. 提交信息规范（Conventional Commits）

```
<类型>(<范围>): <简短描述>

[可选正文]

[可选脚注]
```

| 类型 | 描述 | 示例 |
|------|------|------|
| `feat` | 新功能 | `feat(transaction): add voice input for quick recording` |
| `fix` | Bug 修复 | `fix(auth): prevent token refresh race condition` |
| `docs` | 文档更新 | `docs: update API documentation` |
| `refactor` | 重构 | `refactor(report): extract chart components` |
| `perf` | 性能优化 | `perf(transaction): add index for date range query` |
| `test` | 测试相关 | `test: add unit tests for CategoryService` |
| `chore` | 构建/工具/依赖 | `chore: upgrade Spring Boot to 3.2.0` |
| `ci` | CI/CD 配置 | `ci: add GitHub Actions for backend deployment` |

### 提交信息示例

```bash
# ✅ Good
feat(transaction): add voice recording for quick transaction entry
- Integrate browser MediaRecorder API
- Add Whisper AI transcription endpoint
- Auto-classify based on voice content
Closes #456

# ❌ Bad
fix bug
WIP
asdf
changes
```

---

## 3. Pull Request 规范

### PR 标题格式

```
[feat/fix/docs/refactor/perf/chore]: <简短描述> (#工单号)
```

### PR 描述模板

```markdown
## Summary
<!-- 简述改动的核心内容 -->

## Motivation
<!-- 为什么需要这个改动？ -->

## Changes
- [x] Backend: ...
- [x] Frontend: ...
- [x] Tests: ...

## Test Plan
- [x] 单元测试覆盖新代码
- [x] 手动测试场景 A、B、C
```

---

## 4. 代码审查规范

详见 `code-review-guide.md`。

**审查优先级：**

| 标记 | 含义 | 行动 |
|------|------|------|
| 🔴 Blocker | 阻止合并，必须修复 | PR 不能合并 |
| 🟡 Suggestion | 建议修复，下个迭代处理 | 鼓励修复 |
| 💭 Nit | 可选优化，不阻碍合并 | 随意 |
| ✅ Praise | 值得学习 | 鼓励继续 |

---

**文档版本**：1.0.0
**最后更新**：2026-03-27
**维护人员**：Git 工作流管理员
