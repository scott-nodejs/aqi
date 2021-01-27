package com.aqi.service;

import com.aqi.entity.Aqi;
import com.aqi.entity.City;
import com.aqi.entity.NoResult;
import com.aqi.entity.UrlEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CityService extends IService<City> {

    void insertCity(City city);

    City getCityByUid(int uid);

    City getCityByName(String name);

    List<City> getCity(long currentTime);

    void updateTime(long vtime);

    List<NoResult> selectByNoResult();

    void insertAqi(UrlEntity urlEntity);

    void halfAqi(UrlEntity urlEntity);

    List<City> selectCityByIsUpdate();
}
