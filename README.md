# CampusHub 校园服务中心

大学生综合服务平台 —— 社团管理、竞赛组队、活动组织、二手闲置、失物招领、消息通知、数据看板一站式服务。

**软件体系架构课程大作业**：采用五视图方法（逻辑/开发/运行/物理/场景）设计，含 8 条 ADR 架构决策记录。

## 技术栈

| 层 | 技术 |
|---|---|
| 后端 | Java 17、Spring Boot 3.2、MyBatis-Plus 3.5、Spring Security + JWT |
| 存储 | MySQL 8.0（19 张表）、Redis 7（活动名额原子扣减） |
| 消息 | RabbitMQ 3.12（社团审批/活动报名/二手成交异步通知 + 死信队列） |
| 前端 | Vue 3、Element Plus、Vite、Pinia、Vue Router |
| 文档 | SpringDoc OpenAPI（Swagger UI） |
| 部署 | Docker Compose（MySQL/Redis/RabbitMQ），本地进程部署应用 |

## 项目结构

```
CampusHub/
├── backend/                     # 后端工程（Spring Boot 3）
│   └── src/main/java/com/campushub/
│       ├── common/              # 公共层：Result统一响应、BusinessException、
│       │                        #   UserContext(ThreadLocal)、JwtAuthInterceptor、
│       │                        #   RequireRole注解+AOP切面、DomainEvent+EventPublisher
│       ├── config/              # WebConfig(拦截器注册)、RabbitConfig(交换机/队列/DLX)、
│       │                        #   MybatisPlusConfig(分页)、ScheduledTasks(定时)
│       └── modules/             # 9 个业务模块
│           ├── user/            # M1 用户与权限（登录注册/JWT/个人资料/管理员用户管理）
│           ├── club/            # M2 社团（创建/审批/成员/动态，CLUB_APPROVED_EVENT）
│           ├── competition/     # M3 竞赛组队（竞赛/队伍/成员，uk防重复）
│           ├── activity/        # M4 活动（状态机+Redis防超卖报名+签到+定时推进）
│           ├── notify/          # M5 消息通知（站内信/公告/RabbitMQ消费者）
│           ├── secondhand/      # M6 二手闲置（ON_SALE→RESERVED→SOLD，TRADE_EVENT）
│           ├── lostfound/       # M7 失物招领（PENDING→CLAIMED/RESOLVED）
│           ├── file/            # M8 文件管理（上传到本地目录+元数据落库）
│           └── stat/            # M9 数据看板（每日统计+管理员聚合看板）
├── frontend/                    # 前端工程（Vue 3 + Element Plus）
│   └── src/
│       ├── api/                 # 接口封装（user/club/activity/competition/modules）
│       ├── views/               # 页面（登录/注册/首页/社团/竞赛/活动/二手/失物/消息/看板/个人）
│       ├── layouts/             # 主布局（侧边栏菜单+顶栏）
│       ├── router/              # 路由（含登录守卫）
│       └── utils/request.js     # axios 封装（JWT Bearer + 401跳转）
├── docs/                        # 课程文档
│   ├── architecture/            # 五视图架构文档（12份md + 11张PNG + 8条ADR）
│   ├── skills/                  # 开发用AI-Skill包（3个 + usage-log 6次）
│   ├── sql/init.sql             # 建库建表SQL（19张表）
│   └── 需求规格说明书.md
├── docker-compose.yml           # 中间件编排（MySQL/Redis/RabbitMQ）
└── README.md
```

## 环境要求

- JDK 17+（本项目使用 D:\software\jdk17）
- Maven 3.8+
- Node.js 18+（npm）
- MySQL 8.0（端口 3306，库 campus_hub）
- Redis（端口 6379）
- RabbitMQ（端口 5672，默认 guest/guest）

## 快速启动

### 1. 初始化数据库

用 MySQL 客户端执行 `docs/sql/init.sql`（创建 campus_hub 库与 19 张表）。
默认连接信息：`localhost:3306 / root / 123456`（可在 `backend/src/main/resources/application.yml` 修改）。

### 2. 启动中间件（可选，已装本地服务则跳过）

```bash
docker compose up -d
```

### 3. 启动后端

```bash
cd backend
mvn spring-boot:run
```

- API 前缀：`http://localhost:8080/api`
- Swagger 文档：`http://localhost:8080/swagger-ui/index.html`
- 测试账号：`admin/123456`（管理员）、`student/123456`（学生），密码 MD5 存储

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

- 访问 `http://localhost:5173`，Vite 已配置代理转发 `/api` 到 `http://localhost:8080`

### 5. 运行单元测试

```bash
cd backend
mvn test
```

17 个测试用例（JWT/社团/活动/失物），覆盖状态机与 Redis 并发控制。

## 架构设计

详见 [docs/architecture/README.md](docs/architecture/README.md)：

- **五视图**：逻辑视图（L1-L7）、开发视图（V1-V8）、运行视图（R1-R7）、物理视图（P1/P4）、场景视图（需求用例）
- **8 条 ADR**：技术栈、前后端分离、MySQL 存储、Redis 缓存、JWT 认证授权、RabbitMQ 异步、部署形态、模块划分
- **11 张架构图**：分层图、ER 图、时序图、部署图等（PIL 绘制）

## 作者

研一学生 · 软件体系架构课程大作业 · 长春理工大学
