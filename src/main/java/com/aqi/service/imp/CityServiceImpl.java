package com.aqi.service.imp;

import com.aqi.entity.City;
import com.aqi.entity.NoResult;
import com.aqi.mapper.city.CityMapper;
import com.aqi.service.CityService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityServiceImpl extends ServiceImpl<CityMapper, City> implements CityService {

    @Override
    public void insertCity(City city) {
        baseMapper.insert(city);
    }

    @Override
    public City getCityByUid(int uid) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("uid",uid);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public List<City> getCity(long currentTime) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.lt("vtime", currentTime);
        queryWrapper.eq("is_update", 0);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public void updateTime(long vtime) {
        baseMapper.updateTime(vtime);
    }

    @Override
    public List<NoResult> selectByNoResult() {
        return baseMapper.selectByNoResult();
    }
}
