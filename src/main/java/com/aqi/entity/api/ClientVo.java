package com.aqi.entity.api;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ClientVo {
    private EnvEntity envEntity;
    private List<Map<String,Object>> data;
}
