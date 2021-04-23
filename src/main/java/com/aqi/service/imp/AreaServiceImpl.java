package com.aqi.service.imp;

import com.alibaba.fastjson.JSON;
import com.aqi.amqp.RabbitMqConfig;
import com.aqi.configer.exception.ResultException;
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
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
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
    private RedisService redisService;

    @Autowired
    private NoCityAreaService noCityAreaService;

    Executor ex = Executors.newFixedThreadPool(10);

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
    public void addArea(List<Integer> uids, Integer pId) {
        if(pId == null){
            throw new ResultException(500, "Pid不能为空");
        }
        QueryWrapper<NoCityArea> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("uid", uids);
        List<NoCityArea> areas = noCityAreaService.list(queryWrapper);
        areas.forEach(area->{
            Area area1 = new Area();
            area1.setUid(area.getUid());
            area1.setPerantId(pId);
            area1.setLat(area.getLat());
            area1.setLon(area.getLon());
            area1.setName(area.getZh());
            area1.setVtime((int) TimeUtil.getHour());
            area1.setIsUpdate(0);
            baseMapper.insert(area1);
        });
        noCityAreaService.removeByIds(uids);
    }

    @Override
    public void addAreaAqiAll(UrlEntity urlEntity) {
        ex.execute(()->{
            try{
                long start = System.currentTimeMillis();
                Area area = urlEntity.getArea();
                if(area == null){
                    return;
                }
                String url = urlEntity.getUrl();
                List<Integer> list = new ArrayList<>();
                List<Integer> pm10s = new ArrayList<>();
                List<Integer> so2s = new ArrayList<>();
                List<Integer> no2s = new ArrayList<>();
                List<Integer> o3s = new ArrayList<>();
                gen(d,x);
                HttpRequestConfig httpRequestConfig = new HttpRequestConfig();
                httpRequestConfig.url(url);
                HttpRequestResult result = HttpUtils.get(httpRequestConfig);
                AllResult allResult = JSON.parseObject(result.getResponseText(), AllResult.class);
                List<Map<String, String>> historic = allResult.getHistoric();
                historic.forEach(m->{
                    if(m.get("n").equals("pm25")){
                        String v = m.get("d");
                        handle(v,list);
                    }
                    if(m.get("n").equals("pm10")){
                        String v = m.get("d");
                        handle(v,pm10s);
                    }
                    if(m.get("n").equals("so2")){
                        String v = m.get("d");
                        handle(v,so2s);
                    }
                    if(m.get("n").equals("no2")){
                        String v = m.get("d");
                        handle(v,no2s);
                    }
                    if(m.get("n").equals("o3")){
                        String v = m.get("d");
                        handle(v,o3s);
                    }
                });
                int time = allResult.getTime() + 60 * 60;
                Aqi aqi = new Aqi();
                aqi.setVtime(time);
                aqi.setFtime(TimeUtil.convertMillisToString(Long.valueOf(time+"")));
                String uuid = time + "_" + area.getUid();
                int aqi1 = allResult.getAqi().equals("-") ? 0: (int) allResult.getAqi();
                if(TimeUtil.getHour() <= time){
                    aqi.setUid(area.getUid());
                    aqi.setUuid(uuid);
                    aqi.setAqi(aqi1);
                    aqi.setPm25(list.size() > 0 ? String.valueOf(list.get(0)): "0");
                    aqi.setPm10(pm10s.size() > 0 ? String.valueOf(pm10s.get(0)): "0");
                    aqi.setSo2(so2s.size() > 0 ? String.valueOf(so2s.get(0)) : "0");
                    aqi.setNo2(no2s.size() > 0? String.valueOf(no2s.get(0)): "0");
                    aqi.setO3(o3s.size() > 0 ? String.valueOf(o3s.get(0)): "0");
                    aqiService.insertAqi(aqi);

                    area.setVtime((int) (TimeUtil.getHour() + 60 * 60));
                    area.setIsUpdate(1);
                    this.updateById(area);
                    log.info("命中了===>: " + (System.currentTimeMillis() - start) / 1000);
                }else{
                    log.info("没有命中==> 地区解析全部信息的花费时间: " + (System.currentTimeMillis() - start) / 1000);
                }
            }catch (Exception e){
                log.error("解析全部的aqi失败: ",e);
            }
        });
    }

    @Override
    public void addPoint() {
        List<Area> areas = baseMapper.selectList(new QueryWrapper<>());
        Map<String, Point> pointMap = new HashMap<>();
        areas.forEach(area -> {
            pointMap.put(area.getUid()+"",new Point(area.getLon(), area.getLat()));
        });
        redisService.addGeo(pointMap);
    }


    Map<String,Integer> d = new HashMap<>();
    Map<String,Integer> x = new HashMap<>();


    private void gen(Map<String, Integer> d, Map<String, Integer> x) {
        String dstr = "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z";
        String xstr = "a b c d e f g h i j k l m n o p q r s t u v w x y z";
        char[] dchars = dstr.toCharArray();
        char[] xchars = xstr.toCharArray();
        for(char c : dchars){
            if(c >= 'A' && c <= 'Z'){
                d.put(String.valueOf(c),(int)c - 64);
            }
        }
        for(char c : xchars){
            if(c >= 'a' && c <= 'z'){
                x.put(String.valueOf(c),(int)c - 97);
            }
        }
    }


    @Data
    class Count{
        private int cityId;
        private int count;
    }

    private void handle(String v, List<Integer> list) {
        int c = 0;
        char[] chars = v.toCharArray();
        if(chars[0] >= '0' && chars[0] <= '9'){
            for(int k = 1; k < ((int)chars[0] - 1); k++){
                list.add(0);
            }
        }
        for(int i=0; i < chars.length; i++){
            char ch = chars[i];
            char f;
            if(ch >= '0' && ch <= '9'){
                continue;
            }
            if(ch >= 'A' && ch <= 'Z'){
                f = '-';
                Integer data = d.get(String.valueOf(ch));
                c = compute(c,data,f);
                list.add(c);
                StringBuilder sb1 = new StringBuilder();
                if(i < chars.length - 1){
                    sb1.append(chars[i + 1]);
                }
                if(i < chars.length - 2){
                    sb1.append(chars[i + 2]);
                }
                if(i < chars.length - 3){
                    sb1.append(chars[i + 3]);
                }
                if(sb1.length() > 0){
                    char[] chars1 = sb1.toString().toCharArray();
                    StringBuilder sb = new StringBuilder();
                    for(char cha : chars1){
                        if(cha >= '0' && cha <= '9'){
                            sb.append(cha);
                        }else{
                            break;
                        }
                    }
                    if(sb.length() > 0 && Integer.parseInt(sb.toString()) > 0){
                        for(int j=0; j < Integer.parseInt(sb.toString()) -1; j++){
                            list.add(0);
                        }
                    }
                }
            }
            if(ch >= 'a' && ch <= 'z'){
                f = '+';
                Integer data = x.get(String.valueOf(ch));
                c = compute(c,data,f);
                list.add(c);
                StringBuilder sb1 = new StringBuilder();
                if(i < chars.length - 1){
                    sb1.append(chars[i + 1]);
                }
                if(i < chars.length - 2){
                    sb1.append(chars[i + 2]);
                }
                if(i < chars.length - 3){
                    sb1.append(chars[i + 3]);
                }
                if(sb1.length() > 0){
                    char[] chars1 = sb1.toString().toCharArray();
                    StringBuilder sb = new StringBuilder();
                    for(char cha : chars1){
                        if(cha >= '0' && cha <= '9'){
                            sb.append(cha);
                        }else{
                            break;
                        }
                    }
                    if(sb.length() > 0 && Integer.parseInt(sb.toString()) > 0){
                        for(int j=0; j < Integer.parseInt(sb.toString()) -1; j++){
                            list.add(0);
                        }
                    }
                }
            }
            if(ch == '!'){
                f = '+';
                StringBuilder sb = new StringBuilder();
                if(i < chars.length - 1){
                    merge(chars[i + 1], sb);
                }
                if(i < chars.length - 2){
                    merge(chars[i + 2], sb);
                }
                if(i < chars.length - 3){
                    merge(chars[i + 3], sb);
                }
                c = compute(c, Integer.parseInt(sb.toString()), f);
                list.add(c);
            }
            if(ch == '$'){
                f = '-';
                StringBuilder sb = new StringBuilder();
                if(i < chars.length - 1){
                    merge(chars[i + 1], sb);
                }
                if(i < chars.length - 2){
                    merge(chars[i + 2], sb);
                }
                if(i < chars.length - 3){
                    merge(chars[i + 3], sb);
                }
                c = compute(c, Integer.parseInt(sb.toString()), f);
                list.add(c);
            }
        }
    }

    private void merge(char ar, StringBuilder sb){
        if(ar >= '0' && ar <= '9'){
            sb.append(ar);
        }
    }

    private int compute(int a, int b, char f){
        if(f == '-'){
            return a - b;
        }else if(f == '+'){
            return a + b;
        }
        return 0;
    }
}
