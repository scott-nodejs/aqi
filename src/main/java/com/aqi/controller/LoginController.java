package com.aqi.controller;

import com.aqi.configer.exception.ResultEnum;
import com.aqi.configer.exception.ResultException;
import com.aqi.configer.exception.ResultVo;
import com.aqi.entity.UserVo;
import com.aqi.service.UserService;
import com.aqi.shiro.CaptchaAuthenticationToken;
import com.aqi.utils.JwtUtil;
import com.aqi.utils.ResultVoUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;


/**
 * @author lucong
 * @date 2020/01/23
 */
@RestController
@RequestMapping(value = "/client/api")
public class LoginController {

    /**
     * 借助shiro实现登录功能
     */
    @PostMapping(value = "/login")
    @ResponseBody
    public ResultVo login(@RequestBody UserVo user){
        // 判断账号密码是否为空
        if (StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getPassword())) {
            throw new ResultException(ResultEnum.USER_NAME_PWD_NULL);
        }

        try{
            // 1.获取Subject主体对象
            Subject subject = SecurityUtils.getSubject();

            // 2.封装用户数据
            UsernamePasswordToken token = new CaptchaAuthenticationToken(user.getUsername(),user.getPassword(),"1");

//        3.实现登录操作
            subject.login(token);

            String authToken = JwtUtil.create(token.getUsername());

            return ResultVoUtil.success("登录成功",authToken);

        }catch (AuthenticationException e){
            return ResultVoUtil.error("用户名或密码不正确!");
        }

    }
}
