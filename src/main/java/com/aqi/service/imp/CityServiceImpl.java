package com.aqi.service.imp;

import com.alibaba.fastjson.JSON;
import com.aqi.entity.*;
import com.aqi.mapper.city.CityMapper;
import com.aqi.service.AqiService;
import com.aqi.service.CityService;
import com.aqi.utils.http.HttpRequestConfig;
import com.aqi.utils.http.HttpRequestResult;
import com.aqi.utils.http.HttpUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.aqi.schduel.keywordTask.getHour;

@Service
@Slf4j
public class CityServiceImpl extends ServiceImpl<CityMapper, City> implements CityService {

    @Autowired
    private AqiService aqiService;

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

    @Override
    public void insertAqi(UrlEntity urlEntity) {
        try{
            City city = urlEntity.getCity();
            if(city != null) {
                HttpRequestConfig config = HttpRequestConfig.create().url(urlEntity.getUrl());
                HttpRequestResult result = HttpUtils.get(config);
                if (result == null) {
                    log.info("拉取失败: " + city.getUrl());
                }
                AqiResult res = JSON.parseObject(result.getResponseText(), AqiResult.class);
                if (res.getStatus().equals("ok")) {
                    AqiResult.Aqi data = res.getData();
                    int tmp = (Integer) data.getTime().get("v") - 8 * 60 * 60;
                    if (tmp >= getHour()) {
                        aqiService.updateAqi(data);
                        city.setVtime((int) (getHour() + 60 * 60));
                        city.setIsUpdate(1);
                        baseMapper.updateById(city);
                    }
                }
            }
        }catch (Exception e){
            log.error("消费失败: ", e);
        }

    }
}
