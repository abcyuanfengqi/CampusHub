# Skill 使用日志（usage-log）

> 记录项目开发过程中对上述 3 个 Skill 的实际使用情况，累计 **5 次以上**（任务书要求 ≥5 次）。

| # | 日期 | Skill | 使用内容 | 产出 | 效果 |
|---|---|---|---|---|---|
| 1 | 2026-10-28 | campus-project-scaffold | 按模块模板生成 `activity` 模块（含状态机 DRAFT→PUBLISHED→REGISTERING→IN_PROGRESS→FINISHED + Redis 计数） | entity/mapper/service/impl/controller + api/activity.js + ActivityList.vue + 路由菜单 | 报名超卖控制落地，mvn compile 通过 |
| 2 | 2026-10-28 | campus-module-generator | 参考 `club` 模块复刻 `competition` 模块（队伍/成员子表、uk_team_user 唯一键、按日期自动流转） | Competition/CompetitionTeam/TeamMember 全链路 + 前端页面 | 组队模块一次性生成，编译通过 |
| 3 | 2026-10-28 | campus-module-generator | 参考 `activity` 模块生成 `secondhand` 模块（ON_SALE→RESERVED→SOLD 状态机 + TRADE_EVENT 事件） | 二手模块全链路 + 前端页面 | 交易通知解耦，编译通过 |
| 4 | 2026-10-28 | campus-module-generator | 参考 `secondhand` 生成 `lostfound` 模块（PENDING→CLAIMED/RESOLVED） | 失物模块全链路 + 前端页面 | 认领/结案状态机落地，测试通过 |
| 5 | 2026-10-28 | campus-arch-doc-sync | 扫描 8 个 Controller 共 40+ 接口，与架构文档接口清单比对并更新 | 接口差异报告 + 架构文档同步 | 代码-文档一致性确认，作为追溯矩阵依据 |
| 6 | 2026-10-28 | campus-project-scaffold | 生成 `notify`（通知/公告 + RabbitMQ 消费者）与 `stat`（每日统计 + 看板）模块 | 消息中心与数据看板全链路 | 异步通知链路验证通过 |
