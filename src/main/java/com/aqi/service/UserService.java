package com.aqi.service;

import com.aqi.entity.User;
import com.aqi.entity.UserVo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<User> {

    /**
     * 注册
     */
    boolean register(UserVo userVo);
}
