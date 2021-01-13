package com.aqi.service;

import com.aqi.entity.City;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CityService extends IService<City> {

    void insertCity(City city);

    City getCityByUid(int uid);

    List<City> getCity(long currentTime);

    void updateTime(long vtime);
}
