# CampusHub 开发 Skill 包

本目录存放本项目开发的 3 个 AI-Skill（符合任务书附录2《开发用AI-Skill编写指南》要求），以及使用日志。

| Skill 名称 | 作用 | 适用场景 |
|---|---|---|
| `campus-project-scaffold` | 一键生成后端模块 + 前端页面的完整骨架（entity/mapper/service/controller/API/列表页） | 新增一个业务模块时 |
| `campus-module-generator` | 基于已有模块复制并定制 CRUD 代码，含状态机/唯一键等细节 | 快速复刻类似模块时 |
| `campus-arch-doc-sync` | 扫描后端 Controller 路由，自动同步架构文档中的接口清单 | 代码与架构文档一致性维护 |

---

## 使用方式

1. 将对应 Skill 的 `SKILL.md` 提供给 AI（如豆包/DeepSeek/Codex 等）。
2. 按 `SKILL.md` 中描述的步骤与脚本执行。
3. 每次实际使用后，在 `usage-log.md` 追加一条使用记录。

---

## 使用日志

见 [usage-log.md](./usage-log.md)。
