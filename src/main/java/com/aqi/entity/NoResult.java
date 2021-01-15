package com.aqi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("no_result")
public class NoResult {

    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    private String uuid;
    private int vtime;
    private int uid;
    private String city;
    private int cType;
    private int noCount;
}
