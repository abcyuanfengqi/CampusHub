# -*- coding: utf-8 -*-
"""生成CampusHub R1运行进程视图 / R4状态机图 / P1物理部署拓扑 / V3开发模块依赖图"""
import os
from PIL import Image, ImageDraw, ImageFont

OUT = r"C:\Users\19465\Doubao\chats\2026-09-21\new-chat\CampusHub\docs\architecture\images"
os.makedirs(OUT, exist_ok=True)

FONT_PATH = r"C:\Windows\Fonts\msyh.ttc"
FONT_BOLD = r"C:\Windows\Fonts\msyhbd.ttc"

def font(size, bold=False):
    return ImageFont.truetype(FONT_BOLD if bold else FONT_PATH, size)

C_TEXT = "#2d3748"
C_GRAY = "#718096"
C_PRIMARY = "#667eea"
C_GREEN = "#48bb78"
C_ORANGE = "#ed8936"
C_RED = "#e53e3e"

def center(d, xy, text, fnt, fill):
    bbox = d.textbbox((0, 0), text, font=fnt)
    w, h = bbox[2]-bbox[0], bbox[3]-bbox[1]
    d.text((xy[0]-w/2, xy[1]-h/2), text, font=fnt, fill=fill)

def box(d, x1, y1, x2, y2, fill, outline, title, subtitle, title_font=15, sub_font=11):
    d.rounded_rectangle((x1, y1, x2, y2), radius=10, fill=fill, outline=outline, width=2)
    center(d, ((x1+x2)/2, (y1+y2)/2-12), title, font(title_font, True), outline)
    center(d, ((x1+x2)/2, (y1+y2)/2+16), subtitle, font(sub_font), C_GRAY)

# ============ R1 运行视图·进程服务视图 ============
def draw_r1():
    W, H = 1450, 820
    img = Image.new("RGB", (W, H), "#ffffff")
    d = ImageDraw.Draw(img)
    center(d, (W/2, 36), "R1 运行视图：进程服务视图（单机部署形态）", font(22, True), C_PRIMARY)

    # 请求来源
    box(d, 60, 80, 420, 190, "#eef2ff", C_PRIMARY, "客户端进程", "浏览器 / 手机 ｜ HTTPS(生产) / HTTP(开发)")
    d.line([(240, 190), (700, 220)], fill=C_GRAY, width=3)
    d.polygon([(700, 220), (690, 212), (690, 228)], fill=C_GRAY)
    center(d, (460, 200), "请求", font(11), C_GRAY)

    # JVM应用进程（大框）
    d.rounded_rectangle((60, 230, 1060, 720), radius=15, fill="#f7fafc", outline=C_GREEN, width=3)
    center(d, (560, 258), "JVM 应用进程 — campushub-backend（Spring Boot 3，端口8080）", font(16, True), C_GREEN)

    # 应用内组件
    box(d, 90, 285, 300, 470, "#ffffff", C_PRIMARY, "Web容器", "Tomcat 线程池\nHTTP请求处理(默认200)")
    box(d, 330, 285, 540, 470, "#ffffff", C_PRIMARY, "业务线程", "9个Service 事务边界\n(Spring @Transactional)")
    box(d, 90, 500, 300, 690, "#ffffff", C_PRIMARY, "MQ 消费者线程", "NotificationConsumer\n活动通知/审批通知")
    box(d, 330, 500, 540, 690, "#ffffff", C_ORANGE, "定时任务线程", "@Scheduled\n每日统计stat_daily")
    box(d, 570, 285, 1030, 430, "#ffffff", C_ORANGE, "公共组件", "Security过滤器链 / JwtUtils / 全局异常\nResult / AOP日志 / MyBatis-Plus Mapper")
    box(d, 570, 460, 1030, 690, "#ffffff", C_ORANGE, "Redis 客户端 / AMQP 客户端", "Spring Data Redis(缓存/计数)\nSpring AMQP(消息收发)")

    # 基础设施进程
    box(d, 1090, 230, 1410, 370, "#fffaf0", C_ORANGE, "MySQL 8.0", "端口3306\ncampus_hub库")
    box(d, 1090, 400, 1410, 540, "#fff5f5", C_RED, "Redis 5.0", "端口6379\n缓存/计数器")
    box(d, 1090, 570, 1410, 710, "#f0fff4", C_GREEN, "RabbitMQ 3.12", "端口5672\n队列/交换机")

    # 连接线
    for y in [(350, 350), (470, 470), (620, 620)]:
        pass
    d.line([(1030, 380), (1090, 310)], fill=C_GRAY, width=3)
    d.line([(1030, 570), (1090, 470)], fill=C_GRAY, width=3)
    d.line([(1030, 570), (1090, 640)], fill=C_GRAY, width=3)
    center(d, (1060, 340), "JDBC", font(10), C_GRAY)
    center(d, (1060, 520), "Redis协议", font(10), C_GRAY)
    center(d, (1060, 640), "AMQP", font(10), C_GRAY)

    img.save(os.path.join(OUT, "R1-进程服务视图.png"))
    print("✅ R1-进程服务视图.png")

# ============ R4 运行视图·状态机图（5个状态机） ============
def draw_r4():
    W, H = 1750, 1050
    img = Image.new("RGB", (W, H), "#ffffff")
    d = ImageDraw.Draw(img)
    center(d, (W/2, 36), "R4 运行视图：核心业务状态机图（5个，满足C2复杂度）", font(22, True), C_PRIMARY)

    states = [
        ("活动状态机 Activity", ["DRAFT", "PUBLISHED", "REGISTERING", "IN_PROGRESS", "FINISHED"], ["发布", "开放报名", "开始", "结束", "取消(CANCELLED)"], 5, "#667eea", 90),
        ("社团状态机 Club", ["PENDING", "APPROVED", "REJECTED", "DISBANDED"], ["审批通过", "驳回", "解散"], 4, "#48bb78", 280),
        ("二手状态机 Secondhand", ["ON_SALE", "RESERVED", "SOLD", "OFF_SHELF"], ["被预约", "成交", "下架"], 4, "#ed8936", 470),
        ("失物状态机 LostFound", ["PENDING", "CLAIMED", "RESOLVED"], ["被认领", "核实结案"], 3, "#e53e3e", 660),
        ("竞赛状态机 Competition", ["RECRUITING", "IN_PROGRESS", "FINISHED"], ["开始比赛", "完赛"], 3, "#38b2ac", 850),
    ]

    for (name, ss, actions, n, color, y) in states:
        center(d, (W/2, y-32), name, font(17, True), color)
        sw, gap = 250, 90
        total = n*sw + (n-1)*gap
        x0 = (W-total)/2
        centers = []
        for i in range(n):
            cx = x0 + i*(sw+gap) + sw/2
            centers.append(cx)
            d.rounded_rectangle((cx-sw/2, y, cx+sw/2, y+70), radius=12, fill="#ffffff", outline=color, width=3)
            center(d, (cx, y+35), ss[i], font(13, True), color)
        for i in range(n-1):
            x1 = centers[i] + sw/2
            x2 = centers[i+1] - sw/2
            my = y+35
            d.line([(x1, my), (x2, my)], fill=color, width=2)
            d.polygon([(x2-10, my-5), (x2-10, my+5), (x2, my)], fill=color)
            mx = (x1+x2)/2
            d.text((mx-25, my-26), actions[i], font=font(10), fill=C_GRAY)
        # 取消弧线（对活动/二手/失物：从起始到结束的例外路径）
        if n == 5:  # 活动：任意态可取消
            d.line([(centers[0], y-10), (centers[-1]-sw/2, y-10)], fill=C_RED, width=2, )
            d.text((W/2-90, y-28), "任意态 → CANCELLED（取消）", font=font(10, True), fill=C_RED)
        if n == 4 and name.startswith("二手"):  # 二手可下架
            d.text((centers[-2]+(centers[-1]-centers[-2])/2-70, y+80), "ON_SALE/RESERVED → OFF_SHELF（下架）", font=font(10, True), fill=C_RED)
        if n == 3 and name.startswith("失物"):
            d.text((centers[0]+30, y+80), "PENDING → RESOLVED（未找到直接结案）", font=font(10, True), fill=C_RED)

    center(d, (W/2, 1005), "状态流转均在 Service 层通过枚举+校验器控制，非法流转抛业务异常（结合ADR-006）", font(12), C_GRAY)

    img.save(os.path.join(OUT, "R4-核心状态机图.png"))
    print("✅ R4-核心状态机图.png")

# ============ P1 物理视图·部署拓扑图 ============
def draw_p1():
    W, H = 1500, 860
    img = Image.new("RGB", (W, H), "#ffffff")
    d = ImageDraw.Draw(img)
    center(d, (W/2, 36), "P1 物理视图：部署拓扑图（单机 Docker Compose）", font(22, True), C_PRIMARY)

    # 主机边界
    d.rounded_rectangle((60, 250, 1120, 760), radius=15, fill="#f7fafc", outline=C_ORANGE, width=3)
    center(d, (590, 276), "物理主机（本机/云服务器）— Docker Compose 编排", font(15, True), C_ORANGE)

    # 外部
    box(d, 320, 80, 820, 200, "#eef2ff", C_PRIMARY, "客户端", "浏览器 / 手机App ｜ HTTPS")
    d.line([(570, 200), (570, 250)], fill=C_GRAY, width=3)

    # 容器
    box(d, 150, 320, 560, 480, "#ffffff", C_PRIMARY, "nginx 容器", "端口80→前端静态资源\n/ → 静态SPA")
    box(d, 150, 520, 560, 680, "#ffffff", C_PRIMARY, "campushub-backend 容器", "端口8080:8080\nJVM 应用(Spring Boot 3)")
    box(d, 640, 320, 1030, 430, "#ffffff", C_ORANGE, "mysql 容器", "端口3306:3306\ncampus_hub库+数据卷")
    box(d, 640, 460, 1030, 570, "#ffffff", C_RED, "redis 容器", "端口6379:6379\n缓存/计数器")
    box(d, 640, 600, 1030, 710, "#ffffff", C_GREEN, "rabbitmq 容器", "端口5672:5672\n管理页15672")

    # 连接
    d.line([(560, 380), (640, 380)], fill=C_GRAY, width=3)
    d.line([(560, 520), (640, 400)], fill=C_GRAY, width=3)
    d.line([(560, 600), (640, 490)], fill=C_GRAY, width=3)
    d.line([(560, 600), (640, 650)], fill=C_GRAY, width=3)
    center(d, (600, 360), "代理", font(10), C_GRAY)
    center(d, (600, 480), "JDBC", font(10), C_GRAY)
    center(d, (600, 560), "Redis", font(10), C_GRAY)
    center(d, (600, 660), "AMQP", font(10), C_GRAY)

    # 数据卷说明
    box(d, 1120, 320, 1450, 430, "#fffaf0", C_ORANGE, "命名卷", "mysql_data(持久化)\nredis_data")
    box(d, 1120, 460, 1450, 570, "#fffaf0", C_ORANGE, "网络", "campushub_net\n(自定义bridge)")
    box(d, 1120, 600, 1450, 710, "#fffaf0", C_ORANGE, "生产扩展", "同镜像多副本\n+Nginx负载均衡")

    img.save(os.path.join(OUT, "P1-部署拓扑图.png"))
    print("✅ P1-部署拓扑图.png")

# ============ V3 开发视图·模块依赖图 ============
def draw_v3():
    W, H = 1400, 860
    img = Image.new("RGB", (W, H), "#ffffff")
    d = ImageDraw.Draw(img)
    center(d, (W/2, 36), "V3 开发视图：工程内模块依赖图（无循环依赖）", font(22, True), C_PRIMARY)

    # 公共层
    box(d, 440, 90, 960, 210, "#eef2ff", C_PRIMARY, "common 公共层（被所有模块依赖）", "Result / 全局异常 / JwtUtils / Security配置\nBaseEntity / 工具类")
    # 9个业务模块
    modules = [
        ("auth 认证", 90, 300, "用户注册/登录"),
        ("user 用户", 280, 300, "个人信息/角色"),
        ("club 社团", 470, 300, "社团/成员/动态"),
        ("competition 竞赛", 660, 300, "竞赛/队伍"),
        ("activity 活动", 850, 300, "活动/报名/签到"),
        ("secondhand 二手", 1040, 300, "商品/交易"),
        ("lostfound 失物", 1230, 300, "失物/招领"),
        ("stat 看板", 660, 620, "统计聚合"),
        ("notify 通知", 940, 620, "消息/公告"),
    ]
    for (name, cx, cy, desc) in modules:
        box(d, cx-140, cy, cx+140, cy+130, "#ffffff", C_GREEN, name, desc, 13, 10)

    # 公共层 → 模块 依赖线
    for (name, cx, cy, desc) in modules:
        d.line([(700, 210), (cx, cy)], fill="#cbd5e0", width=2)

    # 业务模块间：活动/二手/社团 → 通知（异步消息，不直接依赖）
    for (x1, y1, x2, y2) in [(850, 430, 940, 620), (1040, 430, 940, 620), (470, 430, 940, 620)]:
        d.line([(x1, y1), (x2, y2)], fill=C_ORANGE, width=2)
    center(d, (950, 530), "异步消息(AMQP事件) →", font(10, True), C_ORANGE)
    center(d, (750, 730), "stat 看板：仅查库聚合，不依赖业务模块（数据经DB流转）", font(11), C_GRAY)

    img.save(os.path.join(OUT, "V3-模块依赖图.png"))
    print("✅ V3-模块依赖图.png")

draw_r1()
draw_r4()
draw_p1()
draw_v3()
print("全部生成完成！")
