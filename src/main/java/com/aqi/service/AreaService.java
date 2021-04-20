package com.aqi.service;

import com.aqi.entity.*;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface AreaService extends IService<Area> {

    void insertArea(Area area);

    Area getAreaByUid(int uid);

    List<Area> getArea(long currentTime);

    void updateTime(long vtime);

    List<NoResult> selectByNoResult();

    void insertAqi(UrlEntity urlEntity);

    List<Area> selectAreaByIsUpdate();

    Map<Integer,Integer> getAreaCount();

    void halfAqi(UrlEntity urlEntity);

    List<Area> getAreaListByPerantId(int parentId);

    List<Area> getRandAreaList();

    void addArea(List<Integer> uids, Integer pId);

    void addAreaAqiAll(UrlEntity urlEntity);

    void addPoint();
}
