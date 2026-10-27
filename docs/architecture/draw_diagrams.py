# -*- coding: utf-8 -*-
"""生成CampusHub逻辑视图三张架构图（PNG）"""
import os
from PIL import Image, ImageDraw, ImageFont

OUT = r"C:\Users\19465\Doubao\chats\2026-09-21\new-chat\CampusHub\docs\architecture\images"
os.makedirs(OUT, exist_ok=True)

# 字体
FONT_PATH = r"C:\Windows\Fonts\msyh.ttc"
FONT_BOLD_PATH = r"C:\Windows\Fonts\msyhbd.ttc"

def font(size, bold=False):
    path = FONT_BOLD_PATH if bold and os.path.exists(FONT_BOLD_PATH) else FONT_PATH
    return ImageFont.truetype(path, size)

# 配色
C_PRIMARY = "#667eea"      # 主蓝紫
C_PRIMARY_D = "#5a67d8"
C_PURPLE = "#764ba2"
C_GREEN = "#48bb78"
C_ORANGE = "#ed8936"
C_RED = "#e53e3e"
C_GRAY = "#718096"
C_LIGHT = "#eef2ff"
C_BG = "#ffffff"
C_BORDER = "#cbd5e0"
C_TEXT = "#2d3748"

def rounded_box(draw, xy, fill, outline, width=2, r=12):
    draw.rounded_rectangle(xy, radius=r, fill=fill, outline=outline, width=width)

def center_text(draw, xy, text, fnt, fill=C_TEXT):
    bbox = draw.textbbox((0, 0), text, font=fnt)
    w, h = bbox[2]-bbox[0], bbox[3]-bbox[1]
    draw.text((xy[0]-w/2, xy[1]-h/2), text, font=fnt, fill=fill)

def arrow(draw, p1, p2, color=C_GRAY, width=2):
    draw.line([p1, p2], fill=color, width=width)
    # 箭头三角
    import math
    dx, dy = p2[0]-p1[0], p2[1]-p1[1]
    ang = math.atan2(dy, dx)
    L = 12
    a1 = (p2[0]-L*math.cos(ang-0.4), p2[1]-L*math.sin(ang-0.4))
    a2 = (p2[0]-L*math.cos(ang+0.4), p2[1]-L*math.sin(ang+0.4))
    draw.polygon([p2, a1, a2], fill=color)

# ============ L1 系统上下文图 ============
def draw_l1():
    W, H = 1000, 620
    img = Image.new("RGB", (W, H), C_BG)
    d = ImageDraw.Draw(img)

    # 标题
    center_text(d, (W/2, 34), "L1 系统上下文图（C4-Context）— CampusHub 校园服务中心", font(22, True), C_PRIMARY_D)

    # 中间系统框
    cx, cy, cw, ch = 500, 300, 400, 140
    rounded_box(d, (cx-cw/2, cy-ch/2, cx+cw/2, cy+ch/2), C_LIGHT, C_PRIMARY, 3)
    center_text(d, (cx, cy-25), "CampusHub", font(24, True), C_PRIMARY_D)
    center_text(d, (cx, cy+12), "校园服务中心", font(20, True), C_PRIMARY_D)
    center_text(d, (cx, cy+42), "9大模块 · 3类角色 · 状态机 · 异步消息", font(13), C_GRAY)

    # 左侧角色（3个）
    roles = [("普通学生", "找活动/组队/买二手/寻失物"), ("社团负责人", "管成员/发活动/做统计"), ("系统管理员", "审内容/管用户/看数据")]
    ry = [150, 300, 450]
    for (name, desc), y in zip(roles, ry):
        rounded_box(d, (40, y-45, 270, y+45), "#f0fff4", C_GREEN, 2)
        center_text(d, (155, y-20), name, font(17, True), "#276749")
        center_text(d, (155, y+15), desc, font(12), C_GRAY)
        arrow(d, (270, y), (cx-cw/2, y), C_GREEN, 2)

    # 右侧外部系统（3个）
    exts = [("对象存储 OSS", "图片/文件上传"), ("短信服务", "验证码/通知"), ("校园统一认证", "学号对接（预留）")]
    for (name, desc), y in zip(exts, ry):
        rounded_box(d, (730, y-45, 960, y+45), "#fff5f5", C_RED, 2)
        center_text(d, (845, y-20), name, font(17, True), "#9b2c2c")
        center_text(d, (845, y+15), desc, font(12), C_GRAY)
        arrow(d, (cx+cw/2, y), (730, y), C_RED, 2)

    # 系统边界虚线框
    d.rectangle((290, 80, 710, 520), outline=C_PRIMARY, width=1)
    center_text(d, (500, 92), "系统边界", font(13, True), C_PRIMARY)

    img.save(os.path.join(OUT, "L1-系统上下文图.png"))
    print("✅ L1-系统上下文图.png")

# ============ L2 功能结构图 ============
def draw_l2():
    W, H = 1560, 1060
    img = Image.new("RGB", (W, H), C_BG)
    d = ImageDraw.Draw(img)

    center_text(d, (W/2, 36), "L2 功能结构图（模块树）— 9大模块 × 4子功能", font(22, True), C_PRIMARY_D)

    # 根节点
    rx, ry, rw, rh = W/2, 100, 320, 60
    rounded_box(d, (rx-rw/2, ry-rh/2, rx+rw/2, ry+rh/2), C_PRIMARY, C_PRIMARY_D, 3)
    center_text(d, (rx, ry), "CampusHub 校园服务中心", font(20, True), "#ffffff")

    modules = [
        ("M1 用户权限", ["注册（学号/手机号）", "登录 + JWT认证", "个人信息管理", "角色权限控制"]),
        ("M2 社团管理", ["社团创建与审核", "成员管理（审批/移除）", "社团列表与搜索", "社团动态发布"]),
        ("M3 竞赛组队", ["竞赛信息发布", "队伍创建与招募", "加入队伍申请/审批", "队伍成员管理"]),
        ("M4 活动组织", ["活动创建与发布", "在线报名（名额递减）", "签到核销（二维码）", "状态流转5状态"]),
        ("M5 信息推送", ["系统公告发布", "站内消息通知", "已读/未读管理", "消息分类筛选"]),
        ("M6 二手闲置", ["商品发布", "浏览与搜索", "状态流转3状态", "收藏与评论"]),
        ("M7 失物招领", ["失物/招领发布", "认领申请与审核", "状态流转3状态", "按地点/时间筛选"]),
        ("M8 数据看板", ["用户统计/日活", "活动数据/参与率", "交易数据/分类", "可视化图表"]),
        ("M9 文件管理", ["图片上传", "文件存储与URL", "类型/大小校验", "文件列表管理"]),
    ]

    bw, bh = 460, 250
    positions = []
    start_x, start_y = 80, 200
    for i in range(9):
        col = i % 3
        row = i // 3
        x = start_x + col * (bw + 40)
        y = start_y + row * (bh + 40)
        positions.append((x, y))

    for (name, subs), (x, y) in zip(modules, positions):
        rounded_box(d, (x, y, x+bw, y+bh), C_LIGHT, C_PRIMARY, 2)
        # 模块名
        rounded_box(d, (x, y, x+bw, y+44), C_PRIMARY, C_PRIMARY_D, 2, r=8)
        center_text(d, (x+bw/2, y+22), name + " ★", font(17, True), "#ffffff")
        # 子功能
        fnt = font(13)
        for j, sub in enumerate(subs):
            center_text(d, (x+bw/2, y+70 + j*44), sub, fnt, C_TEXT)
        # 根连线
        cx = x + bw/2
        d.line([(rx, ry+rh/2), (cx, y)], fill=C_PRIMARY, width=2)

    img.save(os.path.join(OUT, "L2-功能结构图.png"))
    print("✅ L2-功能结构图.png")

# ============ L3 容器图 ============
def draw_l3():
    W, H = 1300, 640
    img = Image.new("RGB", (W, H), C_BG)
    d = ImageDraw.Draw(img)

    center_text(d, (W/2, 34), "L3 容器图（C4-Container）— 前后端 + 数据中间件", font(22, True), C_PRIMARY_D)

    # 用户浏览器
    rounded_box(d, (30, 260, 210, 380), "#f3e8ff", C_PURPLE, 2)
    center_text(d, (120, 300), "🌐 用户浏览器", font(17, True), C_PURPLE)
    center_text(d, (120, 335), "Chrome / Edge", font(12), C_GRAY)

    # 前端容器
    rounded_box(d, (290, 220, 560, 420), "#eef2ff", C_PRIMARY, 3)
    center_text(d, (425, 250), "容器：前端 SPA", font(16, True), C_PRIMARY_D)
    rounded_box(d, (310, 280, 540, 390), "#ffffff", C_BORDER, 1)
    center_text(d, (425, 305), "Vue 3 + Vite 5", font(15, True), C_TEXT)
    center_text(d, (425, 330), "Element Plus + Pinia", font(12), C_GRAY)
    center_text(d, (425, 353), "端口 5173（开发）/ Nginx（生产）", font(11), C_GRAY)
    center_text(d, (425, 376), "API代理 /api → 8080", font(11), C_GRAY)

    # 后端容器
    rounded_box(d, (650, 220, 950, 420), "#eef2ff", C_PRIMARY, 3)
    center_text(d, (800, 250), "容器：后端服务", font(16, True), C_PRIMARY_D)
    rounded_box(d, (670, 280, 930, 390), "#ffffff", C_BORDER, 1)
    center_text(d, (800, 305), "Spring Boot 3.2", font(15, True), C_TEXT)
    center_text(d, (800, 330), "MyBatis-Plus + Security + JWT", font(12), C_GRAY)
    center_text(d, (800, 353), "端口 8080 ｜ Java 17", font(11), C_GRAY)
    center_text(d, (800, 376), "9大模块 modules/ 分包", font(11), C_GRAY)

    # 数据中间件
    dbs = [
        (("MySQL 8.0", "端口 3306", "19张业务表"), 1060, 190, C_GREEN),
        (("Redis 5.0", "端口 6379", "缓存/验证码"), 1060, 320, C_ORANGE),
        (("RabbitMQ 3.12", "端口 5672", "异步消息"), 1060, 450, C_RED),
    ]
    for (name, port, desc), x, y, c in dbs:
        rounded_box(d, (x, y-50, x+220, y+50), "#f0fff4" if c==C_GREEN else ("#fffaf0" if c==C_ORANGE else "#fff5f5"), c, 2)
        center_text(d, (x+110, y-25), name, font(16, True), "#276749" if c==C_GREEN else ("#9c4221" if c==C_ORANGE else "#9b2c2c"))
        center_text(d, (x+110, y+3), port, font(12), C_GRAY)
        center_text(d, (x+110, y+25), desc, font(11), C_GRAY)

    # 连线
    arrow(d, (210, 320), (290, 320), C_PURPLE, 3)          # 浏览器→前端
    arrow(d, (560, 320), (650, 320), C_PRIMARY, 3)         # 前端→后端
    arrow(d, (950, 300), (1060, 210), C_GREEN, 2)          # 后端→MySQL
    arrow(d, (950, 340), (1060, 330), C_ORANGE, 2)         # 后端→Redis
    arrow(d, (950, 380), (1060, 450), C_RED, 2)            # 后端→RabbitMQ

    # 连线标签
    center_text(d, (425, 200), "HTTPS/REST+JSON（JWT）", font(12, True), C_PURPLE)
    center_text(d, (605, 300), "REST API /api/**", font(12, True), C_PRIMARY)
    center_text(d, (1000, 255), "SQL事务", font(11, True), C_GREEN)
    center_text(d, (1000, 310), "缓存读写", font(11, True), C_ORANGE)
    center_text(d, (1010, 415), "AMQP 消息", font(11, True), C_RED)

    img.save(os.path.join(OUT, "L3-容器图.png"))
    print("✅ L3-容器图.png")

draw_l1()
draw_l2()
draw_l3()
print("全部生成完成！")
