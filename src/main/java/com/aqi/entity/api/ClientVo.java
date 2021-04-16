package com.aqi.entity.api;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ClientVo {
    private String name;
    private String loc;
    private EnvEntity envEntity;
    private List<Map<String,Object>> data;
    private List<Map<String,Object>> react;
}
