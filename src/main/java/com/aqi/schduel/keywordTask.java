package com.aqi.schduel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aqi.amqp.RabbitMqConfig;
import com.aqi.entity.*;
import com.aqi.global.GlobalConstant;
import com.aqi.service.*;
import com.aqi.utils.http.HttpRequestConfig;
import com.aqi.utils.http.HttpRequestResult;
import com.aqi.utils.http.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class keywordTask {

    @Autowired
    private KeyWordService keyWordService;

    @Autowired
    private CityService cityService;

    @Autowired
    private AreaService areaService;

    @Autowired
    private AqiService aqiService;

    @Autowired
    private SendService sendService;

    @Autowired
    private NoResultService noResultService;

    @Autowired
    private RedissonClient redissonClient;

    private CompletableFuture future = new CompletableFuture();

    @Scheduled(cron = "0 0/2 * * * ?")
    public void keyWord(){
        RLock lock = redissonClient.getLock(GlobalConstant.LOCK_KEYWORD);
        boolean isLock = lock.tryLock();
        if(isLock){
            log.info("get redis lock success: " + GlobalConstant.LOCK_KEYWORD);
            try {
                List<KeyWord> keyWords = keyWordService.selectList();
                for(KeyWord kw : keyWords){
                    try{
                        Thread.sleep(10000);
                        StringBuilder url = new StringBuilder();
                        url.append(GlobalConstant.keywordUrl);
                        url.append(kw.getName());
                        url.append("&token=");
                        url.append(GlobalConstant.token);
                        log.info("拉取城市和区域: " + url.toString());
                        HttpRequestConfig config = HttpRequestConfig.create().url(url.toString());
                        HttpRequestResult result = HttpUtils.get(config);
                        if(result == null){
                            log.info("拉取失败: " + kw.getName());
                        }
                        KwResult res = JSON.parseObject(result.getResponseText(), KwResult.class);
                        int perantId = 0;
                        if(res.getStatus().equals("ok")){
                            List<KwResult.CityNode> nodes = res.getData();
                            for(KwResult.CityNode node : nodes){
                                int uid = node.getUid();
                                String cityUrl = node.getStation().getUrl();
                                int vtime = (int) node.getTime().get("vtime");
                                String name = node.getStation().getName();
                                double lat = node.getStation().getGeo().get(0);
                                double lon = node.getStation().getGeo().get(1);
                                if(cityUrl.equals(kw.getName())){
                                    perantId = uid;
                                    City city = new City();
                                    city.setCity(name);
                                    city.setUid(uid);
                                    city.setLat(lat);
                                    city.setLon(lon);
                                    city.setUrl(cityUrl);
                                    city.setVtime(vtime);
                                    city.setIsUpdate(0);
                                    if(cityService.getCityByUid(uid) == null){
                                        cityService.insertCity(city);
                                    }
                                }else if(cityUrl.contains(kw.getName()) && cityUrl.contains("/")){
                                    Area city = new Area();
                                    city.setName(name);
                                    city.setUid(uid);
                                    city.setLat(lat);
                                    city.setLon(lon);
                                    city.setUrl(cityUrl);
                                    city.setVtime(vtime);
                                    city.setPerantId(perantId);
                                    city.setIsUpdate(0);
                                    if(areaService.getAreaByUid(uid) == null){
                                        areaService.insertArea(city);
                                    }
                                }
                            }
                        }
                        kw.setState(1);
                        keyWordService.updateById(kw);
                    }catch (Exception e){
                        log.error("拉取失败 插入城市和区域失败：", e);
                    }
                }
            }catch (Exception e){
                log.error("keyword error : ", e);
            }finally {
                lock.unlock();
                log.info("release redis lock: " + GlobalConstant.LOCK_KEYWORD);
            }
        }else{
            log.info("get redis lock failed: " + GlobalConstant.LOCK_KEYWORD);
        }
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void scanCity(){
        RLock lock = redissonClient.getLock(GlobalConstant.LOCK_SCANCITY);
        boolean isLock = lock.tryLock();
        if(isLock){
            log.info("get redis lock successful: " + GlobalConstant.LOCK_SCANCITY);
            try {
                long current = System.currentTimeMillis() / 1000;
                List<City> citys = cityService.getCity(current);
                citys.forEach(city -> {
                    try{
                        UrlEntity urlEntity = sendCity(city, getHour());
                        sendService.sendCity(RabbitMqConfig.ROUTINGKEY_FAIL,urlEntity, 5 * 60);
                    }catch (Exception e){
                        log.error("拉取失败 城市更新aqi失败：", e);
                    }
                });
            }catch (Exception e){
                log.error("scancity error : ", e);
            }finally {
                lock.unlock();
                log.info("release redis lock: " + GlobalConstant.LOCK_SCANCITY);
            }
        }else {
            log.info("get redis lock failed: " + GlobalConstant.LOCK_SCANCITY);
        }
    }

    public UrlEntity sendCity(City city, long time){
        StringBuilder url = new StringBuilder();
        url.append(GlobalConstant.aqiUrl);
        url.append(city.getUrl());
        url.append("/?token=");
        url.append(GlobalConstant.token);
        log.info("城市拉取AQI： "+url.toString());
        UrlEntity urlEntity = new UrlEntity();
        urlEntity.setCity(city);
        urlEntity.setUrl(url.toString());
        urlEntity.setType(0);
        urlEntity.setVtime(time);
        return urlEntity;
    }

    @Scheduled(cron = "0 30,35,40,45,50,55 * * * ?")
    public void scanArea(){
        RLock lock = redissonClient.getLock(GlobalConstant.LOCK_SCANAREA);
        boolean isLock = lock.tryLock();
        if(isLock){
            log.info("get redis lock success: " + GlobalConstant.LOCK_SCANAREA);
            try{
                long current = System.currentTimeMillis() / 1000;
                List<Area> areas = areaService.getArea(current);
                areas.forEach(city -> {
                    try{
                        UrlEntity urlEntity = sendArea(city, getHour());
                        sendService.send(urlEntity, 5*60);
                    }catch (Exception e){
                        log.error("拉取失败 区域更新aqi失败：", e);
                    }
                });
            }catch (Exception e){
                log.error("scan area error : ", e);
            }finally {
                lock.unlock();
                log.info("release redis lock: " + GlobalConstant.LOCK_SCANAREA);
            }
        }else{
            log.info("get redis lock failed: " + GlobalConstant.LOCK_SCANAREA);
        }
    }

    public UrlEntity sendArea(Area city, long time){
        StringBuilder url = new StringBuilder();
        url.append(GlobalConstant.aqiUrl);
        url.append(city.getUrl());
        url.append("/?token=");
        url.append(GlobalConstant.token);
        log.info("区域拉取AQI： "+url.toString());
        UrlEntity urlEntity = new UrlEntity();
        urlEntity.setArea(city);
        urlEntity.setUrl(url.toString());
        urlEntity.setType(1);
        urlEntity.setVtime(time);
        //long timeout = (getHour() + 60 * 90) - (System.currentTimeMillis() / 1000);
        return urlEntity;
    }

    @Scheduled(cron = "0 0 * * *  ?")
    public void updateTime(){
        RLock lock = redissonClient.getLock(GlobalConstant.LOCK_UPDATETIME);
        boolean isLock = lock.tryLock();
        if(isLock){
            log.info("get redis lock success: " + GlobalConstant.LOCK_UPDATETIME);
            try{
                log.info("重置时间");
                List<NoResult> noResults = areaService.selectByNoResult();
                long time = System.currentTimeMillis() - 30*60*1000;
                int vtime = (int) getHour(time);
                noResults.forEach(noResult -> {
                    String uuid = vtime + "_" + noResult.getUid();
                    noResult.setVtime(vtime);
                    noResult.setUuid(uuid);
                    noResultService.insertNoResult(noResult);
                });
                List<NoResult> noResult1s = cityService.selectByNoResult();
                noResult1s.forEach(noResult -> {
                    String uuid = vtime + "_" + noResult.getUid();
                    noResult.setVtime(vtime);
                    noResult.setUuid(uuid);
                    noResultService.insertNoResult(noResult);
                });
                List<City> cities = cityService.selectCityByIsUpdate();
                cities.forEach(city -> {
                    UrlEntity urlEntity = sendCity(city, vtime);
                    sendService.sendCity(RabbitMqConfig.ROUTINGKEY_HALF_HOUR, urlEntity, 30 * 60);
                });
                List<Area> areas = areaService.selectAreaByIsUpdate();
                areas.forEach(area -> {
                    UrlEntity urlEntity = sendArea(area, vtime);
                    sendService.sendCity(RabbitMqConfig.ROUTINGKEY_HALF_HOUR, urlEntity, 15 * 60);
                });

                cityService.updateTime(getHour());
                areaService.updateTime(getHour());
            }catch (Exception e){
                log.error("updatetime error: ", e);
            }finally {
                lock.unlock();
                log.info("release redis lock: " + GlobalConstant.LOCK_UPDATETIME);
            }
        }else{
            log.info("get redis lock failed: " + GlobalConstant.LOCK_UPDATETIME);
        }
    }

    public static long getHour(){
        return getHour(System.currentTimeMillis());
    }

    public static long getHour(long longtime){
        Instant instant = Instant.ofEpochMilli(longtime);
        ZoneId zone = ZoneId.systemDefault();
        String time = LocalDateTime.ofInstant(instant, zone).format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
        String hour = time.substring(8);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hour));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis()/1000;
    }
}
