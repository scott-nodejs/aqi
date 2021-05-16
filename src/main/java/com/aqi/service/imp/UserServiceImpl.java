package com.aqi.service.imp;

import com.aqi.entity.User;
import com.aqi.mapper.city.UserMapper;
import com.aqi.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
