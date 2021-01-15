package com.aqi.service;

import com.aqi.entity.Area;
import com.aqi.entity.City;
import com.aqi.entity.NoResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface AreaService extends IService<Area> {

    void insertArea(Area area);

    Area getAreaByUid(int uid);

    List<Area> getArea(long currentTime);

    void updateTime(long vtime);

    List<NoResult> selectByNoResult();
}
