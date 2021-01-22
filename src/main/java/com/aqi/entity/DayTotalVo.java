package com.aqi.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DayTotalVo {
    private String name;
    private boolean colorByPoint;
    private List<Map<String,Object>> data;
}
