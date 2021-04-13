package com.aqi.entity;

import lombok.Data;

@Data
public class AmapResponse {
    private String status;
    private Regeocode regeocode;
    private String info;
    private String infocode;

    @Data
    public static class Regeocode{
        Location addressComponent;
        String formatted_address;
    }
}
