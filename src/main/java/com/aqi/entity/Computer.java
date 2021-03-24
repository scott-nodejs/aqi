package com.aqi.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_mouth")
public class Computer {
    @TableId(value = "uuid")
    String uuid;
    int uid;
    String mouthTime;
    String name;
    int goodcount = 0;
    int justsosocount = 0;
    int lightcount = 0;
    int zcount = 0;
    int badcount = 0;
    int badestcount = 0;
    int totalhours = 0;
    float avg;
    int pId;
}
