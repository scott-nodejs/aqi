package com.aqi.service;

import com.aqi.entity.*;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;

public interface AqiService extends IService<Aqi> {

    void insertAqi(Aqi aqi, int type);

    void updateAqi(AqiResult.Aqi aqi);

    List<AqiVo> selectAqiByCityId(int cityId, int type);

    List<AqiVo> compareCity(List<Integer> ids, int type);

    Integer selectAqiByCityName(String name);

    List<AqiVo> compareCityByName(List<String> names, int type);

    AreaAqiResponseVo selectAreaAqiByCityId(int cityId, int type, int vtime);

    HourVo selectPieChartByDay(int vtime, int cityId, int type);
}
