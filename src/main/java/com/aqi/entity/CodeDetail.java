package com.aqi.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "tb_code_detail")
public class CodeDetail {
    @TableId(value = "id")
    private int id;
    private String phone;

    @TableField(value = "requestId")
    private String RequestId;

    @TableField(value = "bizId")
    private String BizId;

    @TableField(value = "status")
    private String Code;

    @TableField(value = "message")
    private String Message;

    @TableField(value = "createTimeStamp")
    private long createTimeStamp;

    @TableField(value = "code")
    private String yzCode;
}
