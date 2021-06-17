package com.aqi.service;

import com.aqi.entity.*;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;

public interface AqiService extends IService<Aqi> {

    void insertAqi(Aqi aqi);

    void updateAqi(AqiResult.Aqi aqi);

    Object selectAqiByLocation(String location, int type);

    Object selectAqiByClient(int cityId, int type);

    Aqi selectLastestAqiByCityId(int cityId, int type);

    Object selectAqiByArea(int cityId, int type);

    List<AqiVo> selectAqiByCityId(int cityId, int type);

    CompareVo compareCity(List<Integer> ids, int type);

    Integer selectAqiByCityName(String name);

    CompareVo compareCityByName(List<String> names, int type);

    AreaAqiResponseVo selectAreaAqiByCityId(int cityId, int type, int vtime);

    HourVo selectPieChartByDay(int vtime, int cityId, int type);

    Object selectAreaByMouth(int cityId, int type, String vtime);

    int getFitAqi(int aqi, int type);

    Object getMapAqi(String start, String end);

    Object getWaqiMap();
}
