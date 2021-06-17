package com.aqi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author lucong
 * @date 2021/6/15 16:32
 */

@Data
@TableName("tb_custom_city")
public class CustomCity {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String phone;

    private String citys;

    private Integer createTime;

    private Integer updateTime;
}
