package com.aqi.entity.api;

import lombok.Data;

@Data
public class EnvEntity {
    private String aqi;
    private String pm10;
    private String so2;
    private String co;
    private String o3;
    private String desc;
    private String level;
    private int color;
    private String aqiState;
    private String pm10State;
    private String so2State;
    private String coState;
    private String o3State;
    private String source;
    private int flag;
}
