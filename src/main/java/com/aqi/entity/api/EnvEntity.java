package com.aqi.entity.api;

import lombok.Data;

@Data
public class EnvEntity {
    private int aqi;
    private int pm10;
    private int so2;
    private int co;
    private int o3;
}
