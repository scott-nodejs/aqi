package com.aqi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Map;

@Data
@TableName("tb_aqi")
public class Aqi {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    private String uuid;
    private int uid;
    private int aqi;
    private String url;
    private String dew;
    private String co;
    private String h;
    private String no2;
    private String o3;
    private String p;
    private String pm10;
    private String pm25;
    private String so2;
    private String t;
    private String w;
    private String wg;
    private int vtime;
    private String ftime;
}
