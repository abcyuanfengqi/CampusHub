---
name: campus-arch-doc-sync
description: 维护架构文档（docs/architecture/）与后端代码一致性时使用。扫描 backend 全部 Controller 的 @RequestMapping/@GetMapping/@PostMapping/@PutMapping/@DeleteMapping，生成接口清单，与架构文档中的"接口一览"章节对比，输出新增/变更/删除差异，并同步更新文档。适用于每次代码迭代后同步架构文档。
---

# campus-arch-doc-sync

## 步骤

1. 递归扫描 `backend/src/main/java/` 下所有 `*Controller.java`，用正则提取：
   - 类级 `@RequestMapping("/xxx")`
   - 方法级 `@GetMapping/@PostMapping/@PutMapping/@DeleteMapping("...")`
   - 方法上的 `@RequireRole` 注解（标注权限）
2. 汇总为接口清单（模块 / 方法 / 路径 / 权限 / 说明），输出 JSON。
3. 与 `docs/architecture/` 中对应文档的接口章节比对，输出 diff：`新增 / 变更 / 缺失（文档有代码无）`。
4. 更新架构文档接口表格：`docs/architecture/04-逻辑视图-L3组件.md` 或运行视图对应章节。
5. 若接口行为影响状态机描述，同步更新对应业务模块的状态机小节（如 `08-逻辑视图-L7关键时序.md`）。

## 输出

- 接口清单 JSON + 差异报告 + 已更新的文档段落清单。

## 约定

- 只更新接口事实（路径/方法/权限），不修改设计意图类描述。
- 每次代码提交前建议运行一次，保证"代码-文档"一致，作为架构设计说明书的追溯依据。
