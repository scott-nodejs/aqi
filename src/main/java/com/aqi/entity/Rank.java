package com.aqi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("tb_rank")
@Data
public class Rank {
    @TableId(value = "uuid")
    private String uuid;
    private int uid;
    private int para;
    private int vtime;
    private String ftime;
}
