package com.aqi.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aqi.entity.*;
import com.aqi.global.GlobalConstant;
import com.aqi.mapper.aqi.WaqiMapper;
import com.aqi.service.*;
import com.aqi.utils.TimeUtil;
import com.aqi.utils.http.HttpRequestConfig;
import com.aqi.utils.http.HttpRequestResult;
import com.aqi.utils.http.HttpUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WaqiServiceImpl extends ServiceImpl<WaqiMapper, Waqi> implements WaqiService {

    @Autowired
    private AreaService areaService;

    @Autowired
    private NoCityAreaService noCityAreaService;

    @Autowired
    private CityService cityService;

    @Autowired
    RedisService redisService;

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
                } catch (Exception e){

                }

                mapToRedis(onlyPm);

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

    @Override
    public List<Waqi> selectWaqiByLastest() {
        return baseMapper.selectByLastest();
    }

    private void mapToRedis(RandResult.OnlyPm onlyPm){
        MapResult.Geo geo = new MapResult.Geo();
        Area area = areaService.getAreaByUid(Integer.parseInt(onlyPm.getX()));
        List<Double> g = new ArrayList<>();
        g.add(area.getLat());
        g.add(area.getLon());
        geo.setG(g);
        geo.setA(onlyPm.getV());
        geo.setX(onlyPm.getX());
        String name = area.getName();
        int start = area.getName().indexOf("(");
        int end = area.getName().indexOf(")");
        if(start != -1 && end != -1){
            name = area.getName().substring(start+1,end);
        }
        geo.setName(name);
        geo.setUpdateTime(TimeUtil.convertMillisToString(Long.valueOf(onlyPm.getT()+"000")));
        int color = getColorInt(Integer.parseInt(onlyPm.getV()));
        geo.setColor(color);
        redisService.hset(GlobalConstant.WAQI_UID, onlyPm.getX(), JSONObject.toJSONString(geo));
    }

    public int getColorInt(int aqi){
        if(aqi > 0 && aqi <= 50){
            return 0xff009966;
        }else if(aqi > 50 && aqi <= 100){
            return 0xffFFDE33;
        }else if(aqi > 100 && aqi <= 150){
            return 0xffFF9933;
        }else if(aqi > 150 && aqi <= 200){
            return 0xffCC0033;
        }else if(aqi > 200 && aqi <= 300){
            return 0xff660099;
        }else{
            return 0xff7E0023;
        }
    }
}
