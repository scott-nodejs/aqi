package com.aqi.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class NoResultVo {
    private List<String> x;
    private List<Map<String, Object>> data;
}
