package com.aqi.mapper.aqi;

import com.aqi.entity.Aqi;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface AqiMapper extends BaseMapper<Aqi> {
    void updateSelectById(Aqi aqi);
}
