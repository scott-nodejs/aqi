package com.aqi.service.imp;

import com.alibaba.fastjson.JSON;
import com.aqi.entity.*;
import com.aqi.mapper.aqi.WaqiMapper;
import com.aqi.service.AreaService;
import com.aqi.service.CityService;
import com.aqi.service.NoCityAreaService;
import com.aqi.service.WaqiService;
import com.aqi.utils.TimeUtil;
import com.aqi.utils.http.HttpRequestConfig;
import com.aqi.utils.http.HttpRequestResult;
import com.aqi.utils.http.HttpUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class WaqiServiceImpl extends ServiceImpl<WaqiMapper, Waqi> implements WaqiService {

    @Autowired
    private AreaService areaService;

    @Autowired
    private NoCityAreaService noCityAreaService;

    @Autowired
    private CityService cityService;

    @Override
    public void consumerRand(UrlEntity urlEntity) {
        try {
            long start = System.currentTimeMillis() / 1000;
            HttpRequestConfig config = HttpRequestConfig.create().url(urlEntity.getUrl());
            HttpRequestResult result = HttpUtils.get(config);
            if (result == null) {
                log.info("拉取失败: ");
            }
            RandResult res = JSON.parseObject(result.getResponseText(), RandResult.class);
            List<RandResult.OnlyPm> data = res.getD();
            data.forEach(onlyPm -> {
                Waqi aqi = new Waqi();
                aqi.setUid(Integer.parseInt(onlyPm.getX()));
                String uuid = onlyPm.getT()+"_"+onlyPm.getX();
                aqi.setUuid(uuid);
                aqi.setAqi(Integer.parseInt(onlyPm.getV()));
                aqi.setAqi(Integer.parseInt(onlyPm.getV()));
                aqi.setVtime(onlyPm.getT());
                try{
                    baseMapper.insert(aqi);
                }catch (Exception e){

                }

                if(onlyPm.getU().contains("/")){
                    QueryWrapper<Area> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("uid", onlyPm.getX());
                    Area area = areaService.getOne(queryWrapper);
                    if(area == null){
                        NoCityArea noCityArea = new NoCityArea();
                        noCityArea.setAddr(onlyPm.getU());
                        noCityArea.setEn(onlyPm.getNlo());
                        noCityArea.setZh(onlyPm.getNna());
                        noCityArea.setD(onlyPm.getD());
                        noCityArea.setLat(onlyPm.getGeo().get(0));
                        noCityArea.setLon(onlyPm.getGeo().get(1));
                        noCityArea.setUid(Integer.parseInt(onlyPm.getX()));
                        noCityArea.setType(1);
                        NoCityArea byId = noCityAreaService.getById(Integer.parseInt(onlyPm.getX()));
                        if(byId == null){
                            noCityAreaService.save(noCityArea);
                        }
                    }else{
                        area.setUniKey(onlyPm.getKey());
                        areaService.updateById(area);
                    }
                }else{
                    QueryWrapper<City> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("uid", onlyPm.getX());
                    City city = cityService.getOne(queryWrapper);
                    if(city == null){
                        NoCityArea noCityArea = new NoCityArea();
                        noCityArea.setAddr(onlyPm.getU());
                        noCityArea.setEn(onlyPm.getNlo());
                        noCityArea.setZh(onlyPm.getNna());
                        noCityArea.setD(onlyPm.getD());
                        noCityArea.setLat(onlyPm.getGeo().get(0));
                        noCityArea.setLon(onlyPm.getGeo().get(1));
                        noCityArea.setUid(Integer.parseInt(onlyPm.getX()));
                        noCityArea.setType(0);
                        NoCityArea byId = noCityAreaService.getById(Integer.parseInt(onlyPm.getX()));
                        if(byId == null){
                            noCityAreaService.save(noCityArea);
                        }
                    }
                }
            });
            long end = System.currentTimeMillis() / 1000;
            log.info("补充爬取: " + (end - start));
        }catch (Exception e){
            log.error("补充爬取: 可能出现了", e);
        }
    }
}
