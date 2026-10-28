package com.campushub.modules.secondhand.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 二手商品实体（M6 二手闲置）
 * 状态机: ON_SALE → RESERVED → SOLD；ON_SALE/RESERVED → OFF_SHELF
 */
@Data
@TableName("secondhand_item")
public class SecondhandItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private String category;
    private String images;
    private Long sellerId;
    private Long buyerId;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
