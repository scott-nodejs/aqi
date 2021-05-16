package com.aqi.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("tb_user")
@Data
public class User {

    private int userId;
    private String userName;
    private String password;
    private String thumb;
    private int type;
    private int createTime;
    private int updateTime;
    private String salt;
}
