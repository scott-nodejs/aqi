package com.aqi.service.imp;

import com.aliyuncs.utils.StringUtils;
import com.aqi.configer.exception.ResultEnum;
import com.aqi.configer.exception.ResultException;
import com.aqi.entity.User;
import com.aqi.entity.UserVo;
import com.aqi.mapper.city.UserMapper;
import com.aqi.service.RedisService;
import com.aqi.service.UserService;
import com.aqi.utils.JwtUtil;
import com.aqi.utils.ResultVoUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    RedisService redisService;

    @Override
    public boolean register(UserVo user) {
        if (StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getPassword()) || StringUtils.isEmpty(user.getCode())) {
            throw new ResultException(ResultEnum.USER_NAME_PWD_NULL);
        }

        try{
            String code = redisService.getString(user.getUsername());

            if(!code.equals(user.getCode())){
                redisService.del(user.getUsername());
                throw new ResultException(500 ,"无效的验证码");
            }

            User u = new User();
            u.setPassword(user.getPassword());
            u.setUserName(user.getUsername());
            int t = Integer.parseInt(String.valueOf(System.currentTimeMillis() / 1000));
            u.setCreateTime(t);
            u.setUpdateTime(t);
            u.setType(1);
            baseMapper.insert(u);

        }catch (Exception e){
        }
        return false;
    }
}
