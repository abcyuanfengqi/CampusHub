# CampusHub 校园服务中心

大学生综合服务平台 - 社团招新、竞赛组队、活动报名、二手交易、失物招领一站式服务

## 技术栈

### 后端
- Java 17
- Spring Boot 3.2
- MyBatis-Plus
- MySQL 8.0
- Redis
- RabbitMQ
- Spring Security + JWT
- SpringDoc OpenAPI

### 前端
- Vue 3
- Element Plus
- Vite
- Pinia
- Vue Router

## 项目结构

```
CampusHub/
├── backend/                 # 后端工程
│   ├── src/main/java/com/campushub/
│   │   ├── common/          # 公共模块（统一响应、异常、工具）
│   │   ├── config/          # 配置类
│   │   ├── modules/         # 业务模块
│   │   │   ├── user/        # M1 用户与权限中心
│   │   │   ├── club/        # M2 社团管理中心
│   │   │   ├── team/        # M3 竞赛组队平台
│   │   │   ├── activity/    # M4 活动组织管理
│   │   │   ├── message/     # M5 信息分享与推送
│   │   │   ├── checkin/     # M6 签到与核销中心
│   │   │   ├── secondhand/ # M7 二手闲置交易
│   │   │   ├── lostfound/   # M8 失物招领中心
│   │   │   └── statistics/  # M9 数据看板与统计
│   │   ├── mq/              # 消息队列
│   │   └── task/            # 定时任务
│   └── src/main/resources/
├── frontend/                # 前端工程
├── docs/                    # 文档
└── README.md
```

## 快速启动

### 环境要求
- JDK 17+
- Maven 3.8+
- Node.js 18+
- MySQL 8.0+
- Redis 7+
- RabbitMQ 3.12+

### 后端启动
```bash
cd backend
mvn spring-boot:run
```

访问 http://localhost:8080/swagger-ui.html 查看接口文档

### 前端启动
```bash
cd frontend
npm install
npm run dev
```

## 开发计划

详见 [学习开发计划.md](../学习开发计划.md)

## 作者
研一学生 - 软件体系架构课程大作业
