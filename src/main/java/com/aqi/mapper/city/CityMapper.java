package com.aqi.mapper.city;

import com.aqi.entity.City;
import com.aqi.entity.NoResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface CityMapper extends BaseMapper<City> {
    void updateTime(Long vtime);

    List<NoResult> selectByNoResult();
}
