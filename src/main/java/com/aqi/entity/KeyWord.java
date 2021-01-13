package com.aqi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("key_word")
public class KeyWord {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;

    private String name;

    private int state;
}
