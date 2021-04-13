package com.aqi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("aqi_city")
public class City {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    private int uid;
    private String city;
    private double lat;
    private double lon;
    private String url;
    private int vtime;
    private int isUpdate;
    private String province;
    private String feature;
}
