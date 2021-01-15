package com.aqi.mapper.city;

import com.aqi.entity.Area;
import com.aqi.entity.City;
import com.aqi.entity.NoResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface AreaMapper extends BaseMapper<Area> {
    void updateTime(Long vtime);

    List<NoResult> selectByNoResult();
}
