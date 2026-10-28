---
name: campus-project-scaffold
description: 为 CampusHub 后端（Spring Boot 3 + MyBatis-Plus）新增一个业务模块时使用。自动生成 entity / mapper / service / serviceImpl / controller 五个文件，并同步生成前端 api 文件与列表页骨架。适用于"新增社团/活动/二手/失物等模块"场景。
---

# campus-project-scaffold

## 输入

- 模块名（小写，如 `activity`）
- 实体类名（大驼峰，如 `Activity`）
- 表名（如 `activity`）
- 业务状态机（可选，如 `DRAFT -> PUBLISHED -> REGISTERING -> IN_PROGRESS -> FINISHED`）

## 步骤

1. 在 `backend/src/main/java/com/campushub/modules/<module>/` 下创建：
   - `entity/<Entity>.java`：Lombok `@Data`，字段与表列一一对应，主键 `@TableId(type = IdType.AUTO)`
   - `mapper/<Entity>Mapper.java`：继承 `BaseMapper<Entity>`
   - `service/<Entity>Service.java`：接口
   - `service/impl/<Entity>ServiceImpl.java`：实现类（状态机校验 + 业务规则）
   - `controller/<Entity>Controller.java`：REST 接口，`/api/<module>/**`，类上 `@RestController @RequestMapping("/<module>")`
2. 在前端 `frontend/src/api/<module>.js` 生成 axios 封装（基于 `utils/request.js`）。
3. 在前端 `frontend/src/views/<Module>List.vue` 生成列表页（Element Plus 表格/卡片 + 分页 + 新增弹窗）。
4. 在 `frontend/src/router/index.js` 注册路由；在 `MainLayout.vue` 增加菜单项。

## 约定

- 所有后端接口统一返回 `Result` 结构（`{code, message, data}`）。
- 鉴权：公开接口放入 `WebConfig` 白名单，其余由 `JwtAuthInterceptor` 拦截；管理员接口加 `@RequireRole("ADMIN")`。
- 唯一键冲突、非法状态流转在 Service 层抛 `BusinessException`。
- 写完后执行 `mvn compile` 验证编译。

## 输出

- 后端 5 个文件 + 前端 2 个文件 + 路由/菜单修改点清单。
