# -*- coding: utf-8 -*-
"""生成CampusHub数据视图D1 ER图（19实体，主外键+基数）"""
import os
from PIL import Image, ImageDraw, ImageFont

OUT = r"C:\Users\19465\Doubao\chats\2026-09-21\new-chat\CampusHub\docs\architecture\images"
os.makedirs(OUT, exist_ok=True)

FONT_PATH = r"C:\Windows\Fonts\msyh.ttc"
FONT_BOLD = r"C:\Windows\Fonts\msyhbd.ttc"

def font(size, bold=False):
    return ImageFont.truetype(FONT_BOLD if bold else FONT_PATH, size)

# 配色
C_BG = "#ffffff"
C_PRIMARY = "#667eea"
C_TEXT = "#2d3748"
C_GRAY = "#718096"

# 域颜色
DOMAINS = {
    "组织域": "#d6f5e3",
    "活动域": "#e6f0ff",
    "学业域": "#fff0e0",
    "交易生活域": "#ffe6e6",
    "支撑域": "#f2f2f2",
}

# 实体: (表名, 主键, 业务域)
ENTITIES = [
    ("sys_user", "PK: id", "核心"),
    ("club", "PK: id", "组织域"),
    ("club_member", "PK: id", "组织域"),
    ("club_post", "PK: id", "组织域"),
    ("activity", "PK: id", "活动域"),
    ("activity_registration", "PK: id", "活动域"),
    ("activity_checkin", "PK: id", "活动域"),
    ("competition", "PK: id", "学业域"),
    ("competition_team", "PK: id", "学业域"),
    ("team_member", "PK: id", "学业域"),
    ("secondhand_item", "PK: id", "交易生活域"),
    ("lost_found", "PK: id", "交易生活域"),
    ("notification", "PK: id", "支撑域"),
    ("announcement", "PK: id", "支撑域"),
    ("favorite", "PK: id", "支撑域"),
    ("comment", "PK: id", "支撑域"),
    ("file_info", "PK: id", "支撑域"),
    ("operation_log", "PK: id", "支撑域"),
    ("stat_daily", "PK: id", "支撑域"),
]

# 布局: 表名 -> (cx, cy) 中心坐标
POS = {
    "sys_user": (1080, 90),
    "club": (180, 330), "club_member": (430, 330), "club_post": (180, 540),
    "activity": (780, 330), "activity_registration": (1030, 330), "activity_checkin": (780, 540),
    "competition": (1500, 330), "competition_team": (1750, 330), "team_member": (1500, 540),
    "secondhand_item": (2020, 330), "lost_found": (2020, 540),
    "notification": (180, 830), "announcement": (430, 830), "favorite": (680, 830),
    "comment": (930, 830), "file_info": (1180, 830), "operation_log": (1430, 830), "stat_daily": (1680, 830),
}

W, H = 2240, 980

def main():
    img = Image.new("RGB", (W, H), C_BG)
    d = ImageDraw.Draw(img)

    # 标题
    t = font(24, True)
    bbox = d.textbbox((0, 0), "D1 数据实体关系图（ER）— 19张表 · 主外键 · 基数", font=t)
    d.text(((W-bbox[2]+bbox[0])/2, 15), "D1 数据实体关系图（ER）— 19张表 · 主外键 · 基数", font=t, fill=C_PRIMARY)

    # 域背景
    domains_boxes = {
        "组织域": (90, 250, 610, 660),
        "活动域": (700, 250, 1220, 660),
        "学业域": (1420, 250, 1950, 660),
        "交易生活域": (1940, 250, 2150, 660),
        "支撑域": (90, 720, 1850, 950),
    }
    for name, (x1, y1, x2, y2) in domains_boxes.items():
        d.rounded_rectangle((x1, y1, x2, y2), radius=15, fill=DOMAINS[name], outline="#e0e0e0", width=1)
        d.text((x1+15, y1+8), name, font=font(13, True), fill=C_GRAY)

    # 关系线（先画线，实体框盖在上面）
    # (起点表, 终点表, 起点侧, 终点侧)
    RELS = [
        ("sys_user", "club", "bottom", "top"),
        ("sys_user", "club_member", "bottom", "top"),
        ("club", "club_member", "right", "left"),
        ("sys_user", "club_post", "bottom", "top"),
        ("club", "club_post", "right", "top"),
        ("sys_user", "activity", "bottom", "top"),
        ("club", "activity", "bottom", "left"),
        ("sys_user", "activity_registration", "bottom", "top"),
        ("activity", "activity_registration", "right", "left"),
        ("activity", "activity_checkin", "right", "top"),
        ("sys_user", "activity_checkin", "bottom", "top"),
        ("sys_user", "competition_team", "bottom", "top"),
        ("competition", "competition_team", "right", "left"),
        ("competition_team", "team_member", "right", "top"),
        ("sys_user", "team_member", "bottom", "top"),
        ("sys_user", "secondhand_item", "bottom", "top"),
        ("sys_user", "lost_found", "bottom", "top"),
        ("sys_user", "notification", "bottom", "top"),
        ("sys_user", "announcement", "bottom", "top"),
        ("sys_user", "favorite", "bottom", "top"),
        ("sys_user", "comment", "bottom", "top"),
        ("sys_user", "file_info", "bottom", "top"),
        ("sys_user", "operation_log", "bottom", "top"),
    ]

    SIDE_OFF = {"top": (0, -33), "bottom": (0, 33), "left": (-80, 0), "right": (80, 0)}

    for src, dst, ss, ds in RELS:
        if src not in POS or dst not in POS:
            continue
        p1 = (POS[src][0] + SIDE_OFF[ss][0], POS[src][1] + SIDE_OFF[ss][1])
        p2 = (POS[dst][0] + SIDE_OFF[ds][0], POS[dst][1] + SIDE_OFF[ds][1])
        # 弯线
        mx, my = (p1[0]+p2[0])/2, (p1[1]+p2[1])/2
        d.line([p1, (mx, my), p2], fill="#a0aec0", width=2, joint="curve")
        # 基数标注
        mid = (p1[0]*0.3+p2[0]*0.7, p1[1]*0.3+p2[1]*0.7)
        # 1:N 标注放在靠近终点一侧
        lx, ly = p2[0] + (0 if p2[0]==p1[0] else 10), p2[1] + (10 if p2[1] != p1[1] else 0)
        d.text((lx-8, ly-12), "N", font=font(11, True), fill="#c53030")
        lx1, ly1 = p1[0] - (0 if p2[0]==p1[0] else 12), p1[1] - (12 if p2[1] != p1[1] else 0)
        d.text((lx1-8, ly1-8), "1", font=font(11, True), fill="#276749")

    # 实体框
    for name, pk, domain in ENTITIES:
        cx, cy = POS[name]
        w, h = 150, 55
        x1, y1 = cx-w/2, cy-h/2
        if domain == "核心":
            fill, outline, tcol = "#667eea", "#5a67d8", "#ffffff"
        else:
            fill, outline, tcol = "#ffffff", "#a0aec0", C_TEXT
        d.rounded_rectangle((x1, y1, x1+w, y1+h), radius=10, fill=fill, outline=outline, width=2)
        tb = d.textbbox((0,0), name, font=font(15, True))
        d.text((cx-(tb[2]-tb[0])/2, cy-22), name, font=font(15, True), fill=tcol if domain=="核心" else C_TEXT)
        pkb = d.textbbox((0,0), pk, font=font(10))
        d.text((cx-(pkb[2]-pkb[0])/2, cy+4), pk, font=font(10), fill="#e2e8f0" if domain=="核心" else C_GRAY)

    # 图例
    d.rounded_rectangle((1940, 40, 2210, 190), radius=10, fill="#f7fafc", outline="#e2e8f0", width=1)
    d.text((1955, 52), "图例", font=font(13, True), fill=C_TEXT)
    d.line([(1965, 90), (2010, 90)], fill="#a0aec0", width=2)
    d.text((2018, 78), "1:N 关系", font=font(11), fill=C_TEXT)
    d.rounded_rectangle((1965, 110, 2010, 135), radius=6, fill="#ffffff", outline="#a0aec0", width=2)
    d.text((2018, 112), "实体表(PK)", font=font(11), fill=C_TEXT)
    d.rounded_rectangle((1965, 145, 2010, 170), radius=6, fill="#667eea", outline="#5a67d8", width=2)
    d.text((2018, 147), "核心用户表", font=font(11), fill=C_TEXT)

    img.save(os.path.join(OUT, "D1-数据实体关系图.png"))
    print("✅ D1-数据实体关系图.png 生成成功")

if __name__ == "__main__":
    main()
