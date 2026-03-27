# G-Zang 开发规范文档

> **文档版本**：1.0.0
> **最后更新**：2026-03-27
> **维护人员**：开发团队
> **关联规则**：`code-review.mdc`、`git-workflow.mdc`、`.cursor/rules/code-review.mdc`、`.cursor/rules/git-workflow.mdc`

---

## 1. 规范文档列表

| 文档 | 描述 | 关联规则 |
|------|------|---------|
| `OpenAPI规范.md` | OpenAPI 3.0 规范索引 | `technical-writing.mdc` |
| `Git工作流规范.md` | Git 工作流规范 — 分支策略、提交规范、协作流程 | `git-workflow.mdc` |
| `代码审查规范.md` | 代码审查规范 — 审查标准、评论格式、质量关卡 | `code-review.mdc` |
| `命名规范.md` | 命名规范 — 前端/后端/数据库命名标准 | `backend.mdc`、`frontend` 规范 |

---

## 2. OpenAPI 规范

> 完整规范定义请参考 `OpenAPI规范.md`。OpenAPI YAML 文件作为 CI/CD 的一部分由规范自动生成。

## 3. Git 工作流规范

详见 `Git工作流规范.md` 文档。

**快速参考：**

| 类型 | 格式 | 示例 |
|------|------|------|
| 特性 | `feat/xxx-123` | `feat/voice-input-456` |
| 修复 | `fix/xxx-123` | `fix/auth-timeout-789` |
| 提交格式 | `type(scope): message` | `feat(transaction): add voice input` |

---

## 4. 代码审查规范

详见 `代码审查规范.md` 文档。

**审查优先级：**

| 标记 | 含义 | 行动 |
|------|------|------|
| Blocker | 阻止合并，必须修复 | PR 不能合并 |
| Suggestion | 建议修复，下个迭代处理 | 鼓励修复 |
| Nit | 可选优化，不阻碍合并 | 随意 |
| Praise | 值得学习 | 鼓励继续 |

---

**维护人员**：开发团队
**最后更新**：2026-03-27
