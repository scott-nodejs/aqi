package com.aqi.mapper.aqi;

import com.aqi.entity.Waqi;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface WaqiMapper extends BaseMapper<Waqi> {

    List<Waqi> selectByLastest();
}
