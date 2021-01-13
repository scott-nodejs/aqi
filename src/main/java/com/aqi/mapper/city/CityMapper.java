package com.aqi.mapper.city;

import com.aqi.entity.City;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

public interface CityMapper extends BaseMapper<City> {
    void updateTime(Long vtime);
}
