# -*- coding: utf-8 -*-
"""生成CampusHub逻辑视图 L4分层架构图 / L5领域类图 / L7时序图"""
import os
from PIL import Image, ImageDraw, ImageFont

OUT = r"C:\Users\19465\Doubao\chats\2026-09-21\new-chat\CampusHub\docs\architecture\images"
os.makedirs(OUT, exist_ok=True)

FONT_PATH = r"C:\Windows\Fonts\msyh.ttc"
FONT_BOLD = r"C:\Windows\Fonts\msyhbd.ttc"

def font(size, bold=False):
    return ImageFont.truetype(FONT_BOLD if bold else FONT_PATH, size)

C_BG = "#ffffff"
C_TEXT = "#2d3748"
C_GRAY = "#718096"
C_PRIMARY = "#667eea"
C_GREEN = "#48bb78"
C_ORANGE = "#ed8936"
C_RED = "#e53e3e"
C_BORDER = "#a0aec0"

def center(d, xy, text, fnt, fill):
    bbox = d.textbbox((0, 0), text, font=fnt)
    w, h = bbox[2]-bbox[0], bbox[3]-bbox[1]
    d.text((xy[0]-w/2, xy[1]-h/2), text, font=fnt, fill=fill)

# ================= L4 分层架构图 =================
def draw_l4():
    W, H = 1700, 1050
    img = Image.new("RGB", (W, H), C_BG)
    d = ImageDraw.Draw(img)

    center(d, (W/2, 40), "L4 逻辑视图：分层架构图", font(24, True), C_PRIMARY)

    # 外部请求
    d.rounded_rectangle((60, 80, 420, 190), radius=12, fill="#f3e8ff", outline=C_PRIMARY, width=2)
    center(d, (240, 118), "客户端（外部）", font(16, True), "#5f3f8a")
    center(d, (240, 155), "浏览器 / 手机 ｜ HTTPS", font(12), C_GRAY)

    d.rounded_rectangle((470, 80, 820, 190), radius=12, fill="#e6fffa", outline=C_GREEN, width=2)
    center(d, (645, 118), "Nginx（生产）", font(16, True), "#276749")
    center(d, (645, 155), "反向代理 / 静态资源 / TLS", font(12), C_GRAY)

    d.rounded_rectangle((870, 80, 1270, 190), radius=12, fill="#fffaf0", outline=C_ORANGE, width=2)
    center(d, (1070, 118), "外部服务（可选）", font(16, True), "#9c4221")
    center(d, (1070, 155), "短信服务 / 校园统一认证", font(12), C_GRAY)

    # 四个层
    layers = [
        ("接入层（Presentation）", "Vue3 前端 SPA ｜ REST API (/api/**) ｜ JWT 过滤器 ｜ 参数校验 ｜ Swagger 文档", 220, 90, C_PRIMARY),
        ("应用层（Application）", "AuthService ｜ ClubService ｜ ActivityService ｜ CompetitionService ｜ SecondhandService ｜ LostFoundService ｜ NotificationService ｜ StatService ｜ FileService", 360, 140, C_GREEN),
        ("领域层（Domain）", "业务实体（19张表对应） ｜ 状态机枚举（活动5态/社团4态/二手4态/失物3态/竞赛4态） ｜ 领域校验规则（名额/状态流转） ｜ 事件定义（REGISTRATION_EVENT 等）", 540, 140, C_ORANGE),
        ("基础设施层（Infrastructure）", "MySQL 8.0（事务） ｜ Redis 5.0（缓存/计数） ｜ RabbitMQ 3.12（异步消息） ｜ 文件存储（本地/OSS） ｜ MyBatis-Plus Mapper ｜ Spring Data Redis/AMQP", 720, 160, C_RED),
    ]
    for (name, desc, y, h, color) in layers:
        d.rounded_rectangle((60, y, W-60, y+h), radius=15, fill="#f7fafc", outline=color, width=3)
        center(d, (150, y+25), name, font(16, True), color)
        # 描述区域
        d.rounded_rectangle((70, y+50, W-70, y+h-15), radius=10, fill="#ffffff", outline="#e2e8f0", width=1)
        # 简单折行
        words = desc
        fnt = font(13)
        lines = []
        cur = ""
        for ch in words:
            test = cur + ch
            bbox = d.textbbox((0, 0), test, font=fnt)
            if bbox[2]-bbox[0] > (W-140) and cur:
                lines.append(cur)
                cur = ch
            else:
                cur = test
        lines.append(cur)
        for i, line in enumerate(lines[:2]):
            center(d, (W/2, y+65 + i*24), line, fnt, C_TEXT)

    # 层间箭头
    for y1, y2 in [(190, 220), (330, 360), (510, 540)]:
        arrow_y1, arrow_y2 = y1, y2
        d.line([(W/2, arrow_y1), (W/2, arrow_y2)], fill=C_GRAY, width=3)
        # 箭头
        d.polygon([(W/2-8, arrow_y2-14), (W/2+8, arrow_y2-14), (W/2, arrow_y2)], fill=C_GRAY)

    # 标注"调用方向"与"依赖方向"
    center(d, (W/2+40, 205), "请求↓", font(11, True), C_GRAY)
    center(d, (W/2+40, 348), "业务调用↓", font(11, True), C_GRAY)
    center(d, (W/2+40, 528), "数据访问↓", font(11, True), C_GRAY)

    img.save(os.path.join(OUT, "L4-分层架构图.png"))
    print("✅ L4-分层架构图.png")

# ================= L5 领域模型类图 =================
def draw_l5():
    W, H = 1900, 1180
    img = Image.new("RGB", (W, H), C_BG)
    d = ImageDraw.Draw(img)

    center(d, (W/2, 36), "L5 逻辑视图：领域模型类图（核心实体）", font(24, True), C_PRIMARY)

    # 类: (类名, 属性列表, 方法列表, cx, cy)
    classes = [
        ("User", ["id: Long", "username: String", "role: String", "status: Integer"], ["login()", "register()", "updateProfile()"], 950, 90),
        ("Club", ["id: Long", "name: String", "category: String", "status: String"], ["create()", "approve()", "listMembers()"], 300, 320),
        ("ClubMember", ["id: Long", "clubId: Long", "userId: Long", "role: String"], ["join()", "approve()", "remove()"], 700, 320),
        ("Activity", ["id: Long", "title: String", "status: String", "currentParticipants: Int"], ["publish()", "register()", "checkCapacity()", "checkIn()"], 1200, 320),
        ("ActivityRegistration", ["id: Long", "activityId: Long", "userId: Long", "status: String"], ["register()", "cancel()", "checkIn()"], 1600, 320),
        ("Competition", ["id: Long", "name: String", "level: String", "status: String"], ["create()", "list()"], 400, 680),
        ("CompetitionTeam", ["id: Long", "competitionId: Long", "leaderId: Long", "memberCount: Int"], ["createTeam()", "joinTeam()", "approveMember()"], 800, 680),
        ("SecondhandItem", ["id: Long", "title: String", "price: Decimal", "status: String"], ["publish()", "reserve()", "sell()"], 1400, 680),
        ("LostFound", ["id: Long", "type: String", "location: String", "status: String"], ["publish()", "claim()", "resolve()"], 1750, 680),
        ("Notification", ["id: Long", "userId: Long", "type: String", "isRead: Int"], ["send()", "markRead()", "countUnread()"], 1100, 1020),
    ]

    # 先画关系线
    rels = [
        ("User", "Club", (950, 170), (300, 240), "1", "0..*"),
        ("User", "ClubMember", (950, 170), (700, 240), "1", "0..*"),
        ("Club", "ClubMember", (300, 400), (700, 240), "1", "0..*"),
        ("User", "Activity", (950, 170), (1200, 240), "1", "0..*"),
        ("Activity", "ActivityRegistration", (1200, 400), (1600, 240), "1", "0..*"),
        ("User", "ActivityRegistration", (950, 170), (1600, 240), "1", "0..*"),
        ("User", "CompetitionTeam", (950, 170), (800, 600), "1", "0..*"),
        ("Competition", "CompetitionTeam", (400, 760), (800, 600), "1", "0..*"),
        ("User", "SecondhandItem", (950, 170), (1400, 600), "1", "0..*"),
        ("User", "LostFound", (950, 170), (1750, 600), "1", "0..*"),
        ("User", "Notification", (950, 170), (1100, 940), "1", "0..*"),
    ]
    for src, dst, p1, p2, card1, card2 in rels:
        d.line([p1, p2], fill="#a0aec0", width=2)
        # 基数标注
        mid = ((p1[0]+p2[0])/2, (p1[1]+p2[1])/2)
        off = 12
        d.text((p1[0]-off, p1[1]-off-8), card1, font=font(11, True), fill="#276749")
        d.text((p2[0]-off, p2[1]-off+6), card2, font=font(11, True), fill="#c53030")

    # 类框
    for (name, attrs, methods, cx, cy) in classes:
        w = 300
        h = 45 + len(attrs)*22 + len(methods)*22
        x1, y1 = cx-w/2, cy-h/2
        # 类名
        d.rounded_rectangle((x1, y1, x1+w, y1+42), radius=8, fill="#667eea", outline="#5a67d8", width=2)
        center(d, (cx, y1+21), name, font(16, True), "#ffffff")
        # 属性区
        y = y1+45
        d.line([(x1, y), (x1+w, y)], fill="#cbd5e0", width=1)
        for a in attrs:
            d.text((x1+12, y+2), a, font=font(11), fill=C_TEXT)
            y += 22
        # 方法区
        d.line([(x1, y), (x1+w, y)], fill="#cbd5e0", width=1)
        y += 2
        for m in methods:
            d.text((x1+12, y), m, font=font(11), fill="#276749")
            y += 22
        d.rounded_rectangle((x1, y1, x1+w, y1+45+len(attrs)*22+len(methods)*22), radius=8, outline="#a0aec0", width=2)

    # 图例
    d.rounded_rectangle((1630, 60, 1870, 200), radius=10, fill="#f7fafc", outline="#e2e8f0", width=1)
    d.text((1645, 72), "图例", font=font(13), fill=C_TEXT)
    d.line([(1655, 110), (1700, 110)], fill="#a0aec0", width=2)
    d.text((1708, 100), "1 : 0..* 关联", font=font(10), fill=C_TEXT)
    d.rounded_rectangle((1655, 130, 1700, 155), radius=6, fill="#667eea", outline="#5a67d8", width=2)
    d.text((1708, 132), "实体类", font=font(10), fill=C_TEXT)
    d.rounded_rectangle((1655, 165, 1700, 185), radius=4, fill="#ffffff", outline="#cbd5e0", width=1)
    d.text((1708, 162), "属性/方法区", font=font(10), fill=C_TEXT)

    img.save(os.path.join(OUT, "L5-领域模型类图.png"))
    print("✅ L5-领域模型类图.png")

# ================= L7 时序图（活动报名） =================
def draw_l7():
    W, H = 1500, 900
    img = Image.new("RGB", (W, H), C_BG)
    d = ImageDraw.Draw(img)

    center(d, (W/2, 34), "L7 逻辑视图：关键时序图 — 活动报名流程（跨模块+异步）", font(22, True), C_PRIMARY)

    # 参与者
    actors = [
        ("学生", 100, "#e53e3e"),
        ("前端页面", 300, C_PRIMARY),
        ("ActivityController", 560, C_PRIMARY),
        ("ActivityService", 820, C_GREEN),
        ("DB/Redis", 1060, C_ORANGE),
        ("RabbitMQ/通知", 1320, C_RED),
    ]
    lifeline_y1 = 100
    lifeline_y2 = 830
    for name, x, color in actors:
        d.rounded_rectangle((x-65, 60, x+65, 110), radius=8, fill="#f7fafc", outline=color, width=2)
        center(d, (x, 85), name, font(13, True), color)
        # 生命线
        d.line([(x, 110), (x, lifeline_y2)], fill="#e2e8f0", width=2)

    # 消息序列: (序号, 从x, 到x, 标签, 颜色, 虚线)
    msgs = [
        (1, 100, 300, "点击报名活动", C_TEXT, False),
        (2, 300, 560, "POST /activity/{id}/register (JWT)", C_TEXT, False),
        (3, 560, 820, "register(activityId, userId)", C_TEXT, False),
        (4, 820, 1060, "SELECT activity 校验状态/名额", C_TEXT, False),
        (5, 820, 1060, "Redis INCR 原子扣减名额", C_ORANGE, False),
        (6, 820, 1060, "UPDATE 乐观锁回写(version)", C_TEXT, False),
        (7, 820, 1320, "发布 REGISTRATION_EVENT", C_RED, False),
        (8, 1320, 1060, "消费事件→写notification", C_RED, True),
        (9, 560, 300, "返回 code:200 报名成功", C_GREEN, False),
        (10, 300, 100, "显示'报名成功'+站内消息", C_GREEN, False),
    ]
    y = 140
    for (no, x1, x2, label, color, dash) in msgs:
        arrow_y = y + no*58
        d.line([(x1, arrow_y), (x2, arrow_y)], fill=color, width=2)
        # 箭头
        if not dash:
            d.polygon([(x2-10, arrow_y-6), (x2-10, arrow_y+6), (x2, arrow_y)], fill=color)
        else:
            d.polygon([(x2-10, arrow_y-6), (x2-10, arrow_y+6), (x2, arrow_y)], outline=color, width=1)
        # 序号
        d.ellipse((x1+5, arrow_y-11, x1+25, arrow_y+11), fill=color)
        center(d, (x1+15, arrow_y), str(no), font(10, True), "#ffffff")
        # 标签
        lx = (x1+x2)/2
        bbox = d.textbbox((0, 0), label, font=font(11))
        tw = bbox[2]-bbox[0]
        lx = min(max(lx, 150), W-300)
        d.text((lx-tw/2, arrow_y-22), label, font=font(11), fill=color if color != C_TEXT else C_TEXT)

    # 底部说明
    center(d, (W/2, 862), "异常分支：名额不足→Redis计数达上限→返回500'名额已满'；状态非REGISTERING→拒绝报名", font(12), C_GRAY)

    img.save(os.path.join(OUT, "L7-活动报名时序图.png"))
    print("✅ L7-活动报名时序图.png")

draw_l4()
draw_l5()
draw_l7()
print("全部生成完成！")
