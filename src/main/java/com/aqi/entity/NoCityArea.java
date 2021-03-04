package com.aqi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("no_city_area")
public class NoCityArea {
    @TableId(value = "uid")
    private int uid;
    private int type;
    private String en;
    private String zh;
    private String addr;
    private float d;
    private double lat;
    private double lon;
}
