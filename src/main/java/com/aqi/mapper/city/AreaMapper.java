package com.aqi.mapper.city;

import com.aqi.entity.Area;
import com.aqi.entity.City;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface AreaMapper extends BaseMapper<Area> {
    void updateTime(Long vtime);
}
