package com.aqi.controller;

import com.aliyuncs.utils.StringUtils;
import com.aqi.configer.exception.ResultEnum;
import com.aqi.configer.exception.ResultException;
import com.aqi.configer.exception.ResultVo;
import com.aqi.entity.User;
import com.aqi.entity.UserVo;
import com.aqi.service.RedisService;
import com.aqi.service.SmsService;
import com.aqi.service.UserService;
import com.aqi.utils.JwtUtil;
import com.aqi.utils.ResultVoUtil;
import com.aqi.utils.SmsUtils;
import jodd.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/client/api")
public class SmsLoginController {

    @Autowired
    private SmsService smsService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserService userService;

    @GetMapping(value = "/send/code")
    public Object sendCode(String phone){
        if(StringUtil.isNotEmpty(phone)){
            if(!SmsUtils.isMobile(phone)){
                return ResultVoUtil.error(400,"手机号格式不正确!");
            }
            smsService.smsSend(phone);
            return ResultVoUtil.success("发送成功!");
        }
        return ResultVoUtil.error(400,"手机号不能为空!");
    }

    @PostMapping(value = "/phone/login")
    @ResponseBody
    public ResultVo login(@RequestBody UserVo user){
        // 判断账号密码是否为空
        if (StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getPassword())) {
            throw new ResultException(ResultEnum.USER_NAME_PWD_NULL);
        }

        try{
            String code = redisService.getString(user.getUsername());

            if(!code.equals(user.getPassword())){
                redisService.del(user.getUsername());
                throw new ResultException(500 ,"无效的验证码");
            }

            String authToken = JwtUtil.create(user.getUsername());

            return ResultVoUtil.success("登录成功",authToken);

        }catch (Exception e){
            return ResultVoUtil.error("用户名或密码不正确!");
        }

    }

    @PostMapping(value = "/register")
    @ResponseBody
    public ResultVo register(@RequestBody UserVo user){
        return ResultVoUtil.success(userService.register(user));
    }
}
