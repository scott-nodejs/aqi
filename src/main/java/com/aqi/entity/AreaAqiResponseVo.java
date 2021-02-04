package com.aqi.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AreaAqiResponseVo {
    private List<String> x;
    private List<AreaAqiVo> aqis;
}
