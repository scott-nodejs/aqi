package com.aqi.entity.api;

import lombok.Data;

@Data
public class EnvEntity {
    private int aqi;
    private String pm10;
    private String so2;
    private String co;
    private String o3;
}
