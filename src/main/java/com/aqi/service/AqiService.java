package com.aqi.service;

import com.aqi.entity.Aqi;
import com.aqi.entity.AqiResult;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AqiService extends IService<Aqi> {

    void insertAqi(Aqi aqi);

    void updateAqi(AqiResult.Aqi aqi);
}
