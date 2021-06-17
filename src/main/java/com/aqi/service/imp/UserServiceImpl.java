package com.aqi.service.imp;

import com.aliyuncs.utils.StringUtils;
import com.aqi.configer.exception.ResultEnum;
import com.aqi.configer.exception.ResultException;
import com.aqi.entity.User;
import com.aqi.entity.UserVo;
import com.aqi.mapper.city.UserMapper;
import com.aqi.service.RedisService;
import com.aqi.service.UserService;
import com.aqi.shiro.ShiroUtil;
import com.aqi.utils.JwtUtil;
import com.aqi.utils.ResultVoUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    RedisService redisService;

    @Override
    public boolean register(UserVo user) {
        if (StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getPassword()) || StringUtils.isEmpty(user.getCode())) {
            throw new ResultException(ResultEnum.USER_NAME_PWD_NULL);
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", user.getUsername());
        User user1 = baseMapper.selectOne(queryWrapper);
        if(user1 != null){
            throw new ResultException(400 ,"用户已存在");
        }

        String code = redisService.getString(user.getUsername());

        if(!user.getCode().equals(code)){
            redisService.del(user.getUsername());
            throw new ResultException(400 ,"无效的验证码");
        }

        try{
            User u = new User();
            // 对密码进行加密
            String salt = ShiroUtil.getRandomSalt();
            String encrypt = ShiroUtil.encrypt(user.getPassword(), salt);
            u.setPassword(encrypt);
            u.setSalt(salt);
            u.setUserName(user.getUsername());
            int t = Integer.parseInt(String.valueOf(System.currentTimeMillis() / 1000));
            u.setCreateTime(t);
            u.setUpdateTime(t);
            u.setType(1);
            baseMapper.insert(u);
        }catch (Exception e){
            log.error("注册失败", e);
        }
        return false;
    }
}
