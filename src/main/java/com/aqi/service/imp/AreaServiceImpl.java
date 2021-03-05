package com.aqi.service.imp;

import com.alibaba.fastjson.JSON;
import com.aqi.amqp.RabbitMqConfig;
import com.aqi.entity.*;
import com.aqi.mapper.city.AreaMapper;
import com.aqi.service.*;
import com.aqi.utils.TimeUtil;
import com.aqi.utils.http.HttpRequestConfig;
import com.aqi.utils.http.HttpRequestResult;
import com.aqi.utils.http.HttpUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements AreaService {

    @Autowired
    private AqiService aqiService;

    @Autowired
    private SendService sendService;
    
    @Autowired
    private CityService cityService;

    @Autowired
    private NoCityAreaService noCityAreaService;

    @Override
    public void insertArea(Area area) {
        baseMapper.insert(area);
    }

    @Override
    public Area getAreaByUid(int uid) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("uid",uid);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public List<Area> getArea(long currentTime) {
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
        try {
            long start = System.currentTimeMillis() / 1000;
            Area city = urlEntity.getArea();
            if(city != null){
                HttpRequestConfig config = HttpRequestConfig.create().url(urlEntity.getUrl());
                HttpRequestResult result = HttpUtils.get(config);
                if (result == null) {
                    log.info("拉取失败: " + city.getUrl());
                }
                AqiResult res = JSON.parseObject(result.getResponseText(), AqiResult.class);
                if ("ok".equals(res.getStatus())) {
                    AqiResult.Aqi data = res.getData();
                    int tmp = (Integer) data.getTime().get("v") - 8 * 60 * 60;
                    if (tmp >= urlEntity.getVtime()) {
                        aqiService.updateAqi(data);
                        city.setVtime((int) (TimeUtil.getHour() + 60 * 60));
                        city.setIsUpdate(1);
                        this.updateById(city);
//                        ConsumerAqi consumerAqi = new ConsumerAqi();
//                        consumerAqi.setAqi(data);
//                        consumerAqi.setArea(city);
//                        sendService.sendAqiConsumer(RabbitMqConfig.ROUTINGKEY_CONSUMER_AQI,consumerAqi);
//                        long end = System.currentTimeMillis() / 1000;
//                        log.info("区域消费时间: " + (end - start));
                    }
                }
            }
        }catch (Exception e){
            log.error("区域消费失败", e);
        }
    }

    @Override
    public List<Area> selectAreaByIsUpdate() {
        return baseMapper.selectAreaByIsUpdate();
    }

    @Override
    public Map<Integer, Integer> getAreaCount() {
        QueryWrapper<Area> queryWrapper = new QueryWrapper();
        queryWrapper.groupBy("perant_id");
        queryWrapper.select("perant_id, count(*) as vtime");
        List<Area> areas = baseMapper.selectList(queryWrapper);
        Map<Integer,Integer> map = areas.stream().collect(Collectors.toMap(Area::getPerantId, Area::getVtime));
        return map;
    }

    @Override
    public void halfAqi(UrlEntity urlEntity) {
        try {
            long start = System.currentTimeMillis() / 1000;
            Area city = urlEntity.getArea();
            if(city != null){
                HttpRequestConfig config = HttpRequestConfig.create().url(urlEntity.getUrl());
                HttpRequestResult result = HttpUtils.get(config);
                if (result == null) {
                    log.info("拉取失败: " + city.getUrl());
                }
                AqiResult res = JSON.parseObject(result.getResponseText(), AqiResult.class);
                if ("ok".equals(res.getStatus())) {
                    AqiResult.Aqi data = res.getData();
                    int tmp = (Integer) data.getTime().get("v") - 8 * 60 * 60;
                    if (tmp >= urlEntity.getVtime()) {
                        aqiService.updateAqi(data);
//                        ConsumerAqi consumerAqi = new ConsumerAqi();
//                        consumerAqi.setAqi(data);
//                        sendService.sendAqiConsumer(RabbitMqConfig.ROUTINGKEY_CONSUMER_AQI,consumerAqi);
                        long end = System.currentTimeMillis() / 1000;
                        log.info("超时后, 区域消费时间: " + (end - start));
                    }
                }
            }
        }catch (Exception e){
            log.error("区域消费失败", e);
        }
    }

    @Override
    public List<Area> getAreaListByPerantId(int parentId) {
        QueryWrapper<Area> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("perant_id", parentId);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<Area> getRandAreaList() {
        QueryWrapper<Area> Nullquery = new QueryWrapper<>();
        List<Area> areas = baseMapper.selectList(Nullquery);
        Map<Integer, List<Area>> areaMap = areas.stream().collect(Collectors.groupingBy(Area::getPerantId));
        List<Area> randList = new ArrayList<>();
        areaMap.values().forEach(as->{
            Random random=new Random();
            int i = random.nextInt(as.size());
            randList.add(as.get(i));
        });
        return randList;
    }

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
                Aqi aqi = new Aqi();
                aqi.setUid(Integer.parseInt(onlyPm.getX()));
                String uuid = onlyPm.getT()+"_"+onlyPm.getX();
                aqi.setUuid(uuid);
                aqi.setPm25(onlyPm.getV());
                aqi.setAqi(Integer.parseInt(onlyPm.getV()));
                aqi.setVtime(onlyPm.getT());
                long t = Long.valueOf(onlyPm.getT()+"000");
                aqi.setFtime(TimeUtil.convertMillisToString(t));
                aqiService.insertAqi(aqi, 1);
                
                if(onlyPm.getU().contains("/")){
                    QueryWrapper<Area> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("uid", onlyPm.getX());
                    Area area = baseMapper.selectOne(queryWrapper);
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
                        noCityAreaService.save(noCityArea);
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
                        noCityAreaService.save(noCityArea);
                    }
                }
            });
            long end = System.currentTimeMillis() / 1000;
            log.info("补充爬取: " + (end - start));
        }catch (Exception e){
            log.error("补充爬取: {}",e);
        }
    }

    @Data
    class Count{
        private int cityId;
        private int count;
    }
}
