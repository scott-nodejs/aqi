package com.aqi.entity;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class AreaAqiVo {
    private boolean showInLegend = false;
    private String name;
    private List<Map<String, Object>> data;
}
