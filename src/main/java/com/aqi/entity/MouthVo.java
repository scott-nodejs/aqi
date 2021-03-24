package com.aqi.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class MouthVo {
    private List<String> x;
    private List<Map<String,Object>> data;
}
