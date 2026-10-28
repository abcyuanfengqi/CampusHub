---
name: campus-module-generator
description: 当需要"快速复刻一个类似模块"或"给现有模块增加一个子表"时使用。读取指定参考模块（如 club）的完整代码，按差异点（表名/字段/状态机/接口路径）批量替换生成新模块代码，保持分层与命名风格完全一致。避免手写重复的 CRUD 样板代码。
---

# campus-module-generator

## 输入

- 参考模块名（如 `club`）
- 目标模块名（如 `competition`）
- 差异清单（表名字段、实体字段、状态机、接口路径、是否需要 Redis/MQ）

## 步骤

1. 用 `Glob`/`Read` 完整读取参考模块 5 个文件的真实内容。
2. 复制到 `modules/<target>/` 对应路径，按差异清单执行替换：
   - `entity`：替换类名、表名、字段；`@TableName` 对应新表
   - `mapper`：仅替换泛型与类名
   - `service` / `serviceImpl`：保留状态机模板与事务注解，替换业务方法签名与规则
   - `controller`：替换 `@RequestMapping` 前缀与 DTO 字段
3. 校验唯一键逻辑：如参考模块有 `uk_` 唯一键防重复，新模块如有同样约束需在 service 层保留 selectCount 校验。
4. 运行 `mvn compile`，修复编译错误后再 `mvn test -Dtest=<Target>ServiceImplTest`（如写了测试）。
5. 输出改动文件清单与"与参考模块的差异点对照表"。

## 约定

- 禁止在 Service 层使用 `LambdaUpdateWrapper`（纯单测会触发 MyBatis-Plus lambda 缓存缺失），一律用字符串 `UpdateWrapper`。
- 状态机流转必须集中在一个 `transition` 风格方法中，便于加校验与审计。
