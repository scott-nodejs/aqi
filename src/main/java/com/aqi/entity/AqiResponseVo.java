package com.aqi.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AqiResponseVo {
    private Map<String,Integer> good;
    private Map<String,Integer> so;
    private Map<String,Integer> light;
    private Map<String,Integer> bad;
    private Map<String,Integer> tbad;
    private Map<String,Integer> ttbad;
    private List<AqiVo> aqis;
}
