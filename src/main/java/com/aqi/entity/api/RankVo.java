package com.aqi.entity.api;

import com.aqi.entity.City;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RankVo {
    private int type;
    private List<City> citys;
    private List<Map<String,Object>> ranks;
}
