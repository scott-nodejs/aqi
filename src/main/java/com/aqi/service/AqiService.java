package com.aqi.service;

import com.aqi.entity.*;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface AqiService extends IService<Aqi> {

    void insertAqi(Aqi aqi);

    void updateAqi(AqiResult.Aqi aqi);

    List<AqiVo> selectAqiByCityId(int cityId);

    HourVo selectPieChartByDay(int vtime, int cityId);
}
