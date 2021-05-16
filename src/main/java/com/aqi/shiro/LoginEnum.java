package com.aqi.shiro;

public enum LoginEnum {
    CUSTOMER("1"),ADMIN("2");

    private String type;

    private LoginEnum(String type){
        this.type = type;
    }
    @Override
    public  String toString(){
        return this.type.toString();
    }
}
