package com.aqi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_waqi")
public class Waqi {
    @TableId(value = "uuid")
    private String uuid;
    private int uid;
    private int aqi;
    private int vtime;
}
