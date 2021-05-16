package com.aqi.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

public class CaptchaAuthenticationToken extends UsernamePasswordToken {

    private String loginType;

    public CaptchaAuthenticationToken(String username, String password, String loginType){
        super(username,password);
        this.loginType = loginType;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }
}
