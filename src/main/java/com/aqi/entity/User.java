package com.aqi.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("tb_user")
@Data
public class User {
    @TableId(value = "user_id")
    private int userId;
    private String userName;
    private String password;
    private String thumb;
    private int type;
    private int createTime;
    private int updateTime;
    private String salt;
}
