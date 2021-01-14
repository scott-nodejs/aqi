package com.aqi.schduel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aqi.entity.*;
import com.aqi.global.GlobalConstant;
import com.aqi.service.AqiService;
import com.aqi.service.AreaService;
import com.aqi.service.CityService;
import com.aqi.service.KeyWordService;
import com.aqi.utils.http.HttpRequestConfig;
import com.aqi.utils.http.HttpRequestResult;
import com.aqi.utils.http.HttpUtils;
import lombok.extern.slf4j.Slf4j;
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

    private CompletableFuture future = new CompletableFuture();

    @Scheduled(cron = "0 0/2 * * * ?")
    public void keyWord(){
        List<KeyWord> keyWords = keyWordService.selectList();
        for(KeyWord kw : keyWords){
            try{
                Thread.sleep(10000);
                StringBuilder url = new StringBuilder();
                url.append(GlobalConstant.keywordUrl);
                url.append(kw.getName());
                url.append("&token=");
                url.append(GlobalConstant.token);
                System.out.println("拉取城市和区域: " + url.toString());
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
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void scanCity(){
        long current = System.currentTimeMillis() / 1000;
        List<City> citys = cityService.getCity(current);
        citys.forEach(city -> {
            try{
                Thread.sleep(10000);
                StringBuilder url = new StringBuilder();
                url.append(GlobalConstant.aqiUrl);
                url.append(city.getUrl());
                url.append("/?token=");
                url.append(GlobalConstant.token);
                System.out.println("城市拉取AQI： "+url.toString());
                HttpRequestConfig config = HttpRequestConfig.create().url(url.toString());
                HttpRequestResult result = HttpUtils.get(config);
                if(result == null){
                    log.info("拉取失败: " + city.getUrl());
                }
                AqiResult res = JSON.parseObject(result.getResponseText(), AqiResult.class);
                if(res.getStatus().equals("ok")){
                    AqiResult.Aqi data = res.getData();
                    int tmp = (Integer) data.getTime().get("v")-8*60*60;
                    if(tmp >= getHour()){
                        updateAqi(data);
                        city.setVtime((int)(getHour()+60*60));
                        city.setIsUpdate(1);
                        cityService.updateById(city);
                    }
                }
            }catch (Exception e){
                log.error("拉取失败 城市更新aqi失败：", e);
            }
        });
    }

    @Scheduled(cron = "0 0/10 * * * ?")
    public void scanArea(){
        long current = System.currentTimeMillis() / 1000;
        List<Area> areas = areaService.getArea(current);
        areas.forEach(city -> {
            try{
                Thread.sleep(10000);
                StringBuilder url = new StringBuilder();
                url.append(GlobalConstant.aqiUrl);
                url.append(city.getUrl());
                url.append("/?token=");
                url.append(GlobalConstant.token);
                System.out.println("区域拉取AQI： "+url.toString());
                HttpRequestConfig config = HttpRequestConfig.create().url(url.toString());
                HttpRequestResult result = HttpUtils.get(config);
                if(result == null){
                    log.info("拉取失败: " + city.getUrl());
                }
                AqiResult res = JSON.parseObject(result.getResponseText(), AqiResult.class);
                if("ok".equals(res.getStatus())){
                    AqiResult.Aqi data = res.getData();
                    int tmp = (Integer) data.getTime().get("v")-8*60*60;
                    if(tmp >= getHour()){
                        updateAqi(data);
                        city.setVtime((int)(getHour()+60*60));
                        city.setIsUpdate(1);
                        areaService.updateById(city);
                    }
                }
            }catch (Exception e){
                log.error("拉取失败 区域更新aqi失败：", e);
            }

        });
    }

    @Scheduled(cron = "0 0 0/1 * * ?")
    public void updateTime(){
        log.info("重置时间");
        cityService.updateTime(getHour());
        areaService.updateTime(getHour());
    }


    public void updateAqi(AqiResult.Aqi aqi){
        try{
            Aqi aqi1 = new Aqi();
            if(aqi.getAqi() instanceof String){
                aqi1.setAqi(0);
            }else if(aqi.getAqi() instanceof Integer){
                aqi1.setAqi((Integer) aqi.getAqi());
            }
            aqi1.setUid(aqi.getIdx());
            int tmp = (Integer) aqi.getTime().get("v") - 8*60*60;
            String uuid = tmp+"_"+aqi.getIdx();
            aqi1.setUuid(uuid);
            aqi1.setUrl(aqi.getCity().getUrl());

            aqi1.setCo(String.valueOf(aqi.getIaqi().getCo() == null ?"0":aqi.getIaqi().getCo().get("v")));
            aqi1.setDew(String.valueOf(aqi.getIaqi().getDew() == null ?"0":aqi.getIaqi().getDew().get("v")));
            aqi1.setH(String.valueOf(aqi.getIaqi().getH() == null ?"0":aqi.getIaqi().getH().get("v")));
            aqi1.setNo2(String.valueOf(aqi.getIaqi().getNo2() == null ?"0":aqi.getIaqi().getNo2().get("v")));
            aqi1.setO3(String.valueOf(aqi.getIaqi().getO3() == null ?"0":aqi.getIaqi().getO3().get("v")));
            aqi1.setP(String.valueOf(aqi.getIaqi().getP() == null ?"0":aqi.getIaqi().getP().get("v")));
            aqi1.setPm10(String.valueOf(aqi.getIaqi().getPm10() == null ?"0":aqi.getIaqi().getPm10().get("v")));
            aqi1.setPm25(String.valueOf(aqi.getIaqi().getPm25() == null ?"0":aqi.getIaqi().getPm25().get("v")));
            aqi1.setSo2(String.valueOf(aqi.getIaqi().getSo2() == null ?"0":aqi.getIaqi().getSo2().get("v")));
            aqi1.setT(String.valueOf(aqi.getIaqi().getT() == null ?"0":aqi.getIaqi().getT().get("v")));
            aqi1.setW(String.valueOf(aqi.getIaqi().getW() == null ?"0":aqi.getIaqi().getW().get("v")));
            aqi1.setWg(String.valueOf(aqi.getIaqi().getWg() == null ?"0":aqi.getIaqi().getWg().get("v")));

            aqi1.setVtime(tmp);
            aqi1.setFtime((String) aqi.getTime().get("s"));
            aqiService.insertAqi(aqi1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static long getHour(){
        Instant instant = Instant.ofEpochMilli(System.currentTimeMillis());
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
