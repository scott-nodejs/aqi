package com.aqi.service;

import com.aqi.entity.CodeDetail;
import com.aqi.entity.UserVo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SmsService extends IService<CodeDetail> {
    void smsSend(String phone);
}
