package com.aqi.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aqi.amqp.RabbitMqConfig;
import com.aqi.entity.*;
import com.aqi.entity.api.RankVo;
import com.aqi.global.GlobalConstant;
import com.aqi.mapper.city.CityMapper;
import com.aqi.service.*;
import com.aqi.utils.TimeUtil;
import com.aqi.utils.http.HttpRequestConfig;
import com.aqi.utils.http.HttpRequestResult;
import com.aqi.utils.http.HttpUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.util.HashCodeHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CityServiceImpl extends ServiceImpl<CityMapper, City> implements CityService {

    @Autowired
    private AqiService aqiService;

    @Autowired
    private SendService sendService;

    @Autowired
    NoCityAreaService noCityAreaService;

    @Autowired
    ComputerService computerService;

    @Autowired
    RedisService redisService;

    ExecutorService es = Executors.newFixedThreadPool(5);


    @Override
    public void insertCity(City city) {
        baseMapper.insert(city);
    }

    @Override
    public City getCityByUid(int uid) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("uid",uid);
        City city = baseMapper.selectOne(queryWrapper);
        String name = city.getCity();
        int s = name.indexOf("(");
        int e = name.indexOf(")");
        if(s != -1 && e != -1){
            name = name.substring(s+1, e);
        }
        city.setCity(name);
        return city;
    }

    @Override
    public City getCityByName(String name) {
        String city = name.replace("市","");
        QueryWrapper<City> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("city", city);
        City c = baseMapper.selectOne(queryWrapper);
        return c;
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
                long start = System.currentTimeMillis() / 1000;
                HttpRequestConfig config = HttpRequestConfig.create().url(urlEntity.getUrl());
                HttpRequestResult result = HttpUtils.get(config);
                if (result == null) {
                    log.info("拉取失败: " + city.getUrl());
                }
                AqiResult res = JSON.parseObject(result.getResponseText(), AqiResult.class);
                if (res.getStatus().equals("ok")) {
                    AqiResult.Aqi data = res.getData();
                    int tmp = (Integer) data.getTime().get("v") - 8 * 60 * 60;
                    if (tmp >= urlEntity.getVtime()) {
                        aqiService.updateAqi(data);
                        city.setVtime((int) (TimeUtil.getHour() + 60 * 60));
                        city.setIsUpdate(1);
                        baseMapper.updateById(city);
//                        ConsumerAqi consumerAqi = new ConsumerAqi();
//                        consumerAqi.setAqi(data);
//                        consumerAqi.setCity(city);
//                        sendService.sendAqiConsumer(RabbitMqConfig.ROUTINGKEY_CONSUMER_AQI,consumerAqi);
                    }
                }
                long end = System.currentTimeMillis() / 1000;
                log.info("城市消费时间: " + (end - start));
            }
        }catch (Exception e){
            log.error("消费失败: ", e);
        }

    }

    @Override
    public void halfAqi(UrlEntity urlEntity) {
        try{
            City city = urlEntity.getCity();
            if(city != null) {
                long start = System.currentTimeMillis() / 1000;
                HttpRequestConfig config = HttpRequestConfig.create().url(urlEntity.getUrl());
                HttpRequestResult result = HttpUtils.get(config);
                if (result == null) {
                    log.info("拉取失败: " + city.getUrl());
                }
                AqiResult res = JSON.parseObject(result.getResponseText(), AqiResult.class);
                if (res.getStatus().equals("ok")) {
                    AqiResult.Aqi data = res.getData();
                    int tmp = (Integer) data.getTime().get("v") - 8 * 60 * 60;
                    if (tmp >= urlEntity.getVtime()) {
//                        ConsumerAqi consumerAqi = new ConsumerAqi();
//                        consumerAqi.setAqi(data);
//                        sendService.sendAqiConsumer(RabbitMqConfig.ROUTINGKEY_CONSUMER_AQI,consumerAqi);
                        aqiService.updateAqi(data);
                        long end = System.currentTimeMillis() / 1000;
                        log.info("超时后, 城市消费时间: " + (end - start));
                    }
                }
            }
        }catch (Exception e){
            log.error("消费失败: ", e);
        }
    }

    @PreDestroy
    public void destory(){
        es.shutdown();
    }

    @Override
    public List<City> selectCityByIsUpdate() {
        return baseMapper.selectCityByIsUpdate();
    }

    @Override
    public void addCity(List<Integer> uids) {
        QueryWrapper<NoCityArea> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("uid", uids);
        List<NoCityArea> areas = noCityAreaService.list(queryWrapper);
        areas.forEach(area->{
            City area1 = new City();
            area1.setUid(area.getUid());
            area1.setLat(area.getLat());
            area1.setLon(area.getLon());
            area1.setCity(area.getZh());
            area1.setVtime((int) TimeUtil.getHour());
            area1.setIsUpdate(0);
            baseMapper.insert(area1);
        });
        noCityAreaService.removeByIds(uids);
    }

    @Override
    public void getRank() {
        String s = TimeUtil.convertMillisToDay(System.currentTimeMillis()) + " 00:00:00";
        String e = TimeUtil.convertMillisToDay(System.currentTimeMillis()) + " 23:59:59";
        long start = TimeUtil.date2TimeStamp(s, "yyyy-MM-dd hh:mm:ss");
        long end = TimeUtil.date2TimeStamp(e, "yyyy-MM-dd hh:mm:ss");
        List<String> uids;
        Object cityAlluids = redisService.getString(GlobalConstant.CITY_UIDS);
        if(cityAlluids == null){
            uids = baseMapper.selectList(new QueryWrapper<>()).stream().map(c -> c.getUid()+"").collect(Collectors.toList());
            String uidsStr = JSONObject.toJSONString(uids);
            redisService.setString(GlobalConstant.CITY_UIDS, uidsStr);
        }else{
            uids = JSON.parseArray((String) cityAlluids, String.class);
        }
        uids.forEach(uid->{
            es.execute(()->{
                QueryWrapper<Aqi> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("uid", Integer.parseInt(uid));
                queryWrapper.orderByDesc("vtime");
                queryWrapper.between("vtime", start, end);
                List<Aqi> aqis = aqiService.list(queryWrapper);
                int score = computerService.computeByRank(aqis, 2);
                String key;
                if(aqis != null && aqis.size() > 0){
                    key = uid + ":" + aqis.get(0).getAqi();
                }else{
                    key = uid + ":0";
                }
                redisService.zadd(key, score*1.0);
            });
        });
    }

    public Map<String, Object> rank(int rank, int type){
        List<String> x = new ArrayList<>();
        List<String> uids = new ArrayList<>();
        List<Map<String,Object>> y = new ArrayList<>();
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisService.zgetByScore(rank,type);;
        Iterator<ZSetOperations.TypedTuple<Object>> iterator = typedTuples.iterator();
        while (iterator.hasNext()){
            ZSetOperations.TypedTuple<Object> next = iterator.next();
            String uid = (String) next.getValue();
            if(uid.contains(":")){
                String s = uid.split(":")[0];
                uids.add(s);
            }else {
                uids.add(uid);
            }
            double score = next.getScore();
            Map<String, Object> map = new HashMap<>();
            if(type == 0){
                map.put("color", "#009966");
            }else{
                map.put("color", "#7E0023");
            }
            map.put("y", Math.abs((int) score));
            y.add(map);
        }
        QueryWrapper<City> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("uid", uids);
        List<City> citys = baseMapper.selectList(queryWrapper);
        Map<Integer, String> uidNameMap = citys.stream().collect(Collectors.toMap(City::getUid, City::getCity));
        uids.forEach(uid -> {
            String city = uidNameMap.get(Integer.parseInt(uid));
            int start = city.indexOf("(");
            int end = city.indexOf(")");
            String name = city;
            if(start != -1 && end != -1){
                name = city.substring(start+1,end);
            }
            x.add(name);
        });
        List obj = new ArrayList();
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> v = new HashMap<>();
        v.put("name","排行系数");
        v.put("data", y);
        obj.add(v);
        map.put("x", x);
        map.put("y", obj);
        return map;
    }

    @Override
    public RankVo rankByClient(int rank, int type, int page) {
        List<City> citys = new ArrayList<>();
        List<Map<String,Object>> y = new ArrayList<>();
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisService.zgetByScore(300, type);;
        List<ZSetOperations.TypedTuple<Object>> arrayList = new ArrayList(typedTuples);
        int total = (arrayList.size() % rank) + 1;
        boolean more = false;
        if(page < total){
            more = true;
            arrayList = arrayList.subList((page-1)*rank, page*rank);

        }else if(page == total){
            more = false;
            arrayList = arrayList.subList((page-1)*rank,arrayList.size());
        }else{
            more = false;
            RankVo rankVo = new RankVo();
            rankVo.setCitys(new ArrayList<>());
            rankVo.setRanks(new ArrayList<>());
            rankVo.setType(type);
            rankVo.setMore(more);
            return rankVo;
        }
        Iterator<ZSetOperations.TypedTuple<Object>> iterator = arrayList.iterator();
        while (iterator.hasNext()){
            ZSetOperations.TypedTuple<Object> next = iterator.next();
            String uid = null;
            String value = (String) next.getValue();
            String aqi = null;
            if(value.contains(":")){
                uid = value.split(":")[0];
                aqi = value.split(":")[1];
            }else{
                uid = value;
                aqi = "0";
            }
            QueryWrapper<City> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("uid", uid);
            City city = baseMapper.selectOne(queryWrapper);
            int start = city.getCity().indexOf("(");
            int end = city.getCity().indexOf(")");
            String name = city.getCity();
            if(start != -1 && end != -1){
                name = city.getCity().substring(start+1,end);
            }
            city.setCity(name);
            citys.add(city);
            double score = next.getScore();
            Map<String, Object> map = new HashMap<>();
            map.put("name", name);
            map.put("score", Math.abs((int) score));
            map.put("aqi", Integer.parseInt(aqi));
            y.add(map);
        }
        RankVo rankVo = new RankVo();
        rankVo.setCitys(citys);
        rankVo.setRanks(y);
        rankVo.setType(type);
        rankVo.setMore(more);
        return rankVo;
    }

    @Override
    public Object cityQByClient(int type) {
        QueryWrapper<City> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("city_type", type);
        List<City> cities = baseMapper.selectList(queryWrapper);
        Map<String,List<City>> map = new HashMap<>();
        map.put("citys", cities);
        return map;
    }
}
