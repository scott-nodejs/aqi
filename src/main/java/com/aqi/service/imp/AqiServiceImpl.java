package com.aqi.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.aqi.configer.exception.ResultException;
import com.aqi.entity.*;
import com.aqi.entity.api.ClientVo;
import com.aqi.entity.api.EnvEntity;
import com.aqi.global.GlobalConstant;
import com.aqi.mapper.aqi.AqiMapper;
import com.aqi.service.*;
import com.aqi.utils.LocationUtil;
import com.aqi.utils.TimeUtil;
import com.aqi.utils.http.HttpRequestConfig;
import com.aqi.utils.http.HttpRequestResult;
import com.aqi.utils.http.HttpUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AqiServiceImpl extends ServiceImpl<AqiMapper, Aqi> implements AqiService {

    @Autowired
    CityService cityService;

    @Autowired
    AreaService areaService;

    @Autowired
    WaqiService waqiService;

    @Autowired
    ComputerService computerService;

    @Autowired
    RedisService redisService;

    @Override
    public void insertAqi(Aqi aqi) {
        try{
            QueryWrapper<Waqi> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("uuid", aqi.getUuid());
            baseMapper.insert(aqi);
            Waqi aqi1 = waqiService.getOne(queryWrapper);
            if(aqi1 != null){
                waqiService.removeById(aqi1.getUuid());
            }
        }catch (Exception e){
            log.error("主键冲突: " + aqi.getUuid());
        }

    }

    @Override
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
            this.insertAqi(aqi1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public Object selectAqiByLocation(String location, int type) {
        try{
            Map<String,String> locationMap = LocationUtil.getLocationBylanlng(location);
            String cityName = locationMap.get("city");
            String loc = locationMap.get("loc");
            String[] locArr = location.split(",");
            String point = redisService.getLastestPoint(Double.parseDouble(locArr[0]), Double.parseDouble(locArr[1]));
            ClientVo clientVo;
            if(point.equals("")){
                Integer cityId = this.selectAqiByCityName(cityName);
                clientVo = (ClientVo) this.selectAqiByClient(cityId, type);
            }else{
                Integer cityId = Integer.parseInt(point);
                clientVo = (ClientVo) this.selectAqiByArea(cityId, type);
            }
            clientVo.setName(cityName);
            clientVo.setLoc(loc);
            return clientVo;
        }catch (Exception e){
            e.printStackTrace();
            log.error("定位异常: " + location);
            return this.selectAqiByClient(1451, type);
        }
    }

    public List<Aqi> getAqisByCondition(int cityId, int type, int hours){
        QueryWrapper<Aqi> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid",cityId);
        long endtime = TimeUtil.getHour();
        long starttime = endtime - hours*60*60;
        queryWrapper.between("vtime", starttime, endtime);
        queryWrapper.orderByAsc("vtime");
        List<Aqi> aqis = baseMapper.selectList(queryWrapper);
        return aqis;
    }

    public List<Map<String, Object>> getMap(List<Aqi> aqis, int type, String key){
        List<Map<String, Object>> aqiList = aqis.stream().map(aqi -> {
            Map<String,Object> node = new HashMap<>();
            int time = aqi.getVtime() + 8 * 60 * 60;
            if(key.equals("date")){
                String t = time + "000";
                node.put(key,Long.parseLong(t));
            }else{
                String hour = aqi.getFtime().split(" ")[1];
                String s = hour.split(":")[0];
                node.put(key,s+":00");
            }

            if(aqi.getAqi() == 0){
                if(type == 1){
                    int china = getChina(Integer.valueOf(aqi.getPm25().replace(".0","")));
                    node.put("aqi",Long.valueOf(china));
                }
                node.put("aqi",Long.valueOf(aqi.getPm25().replace(".0","")));
            }else{
                if(type == 1){
                    int china = getChina(aqi.getAqi());
                    node.put("aqi",Long.valueOf(china));
                }else{
                    node.put("aqi", Long.valueOf(aqi.getAqi()));
                }
            }
            return node;}).collect(Collectors.toList());
        return aqiList;
    }

    @Override
    public Object selectAqiByClient(int cityId, int type) {
        City city = cityService.getCityByUid(cityId);
        int start = city.getCity().indexOf("(");
        int end = city.getCity().indexOf(")");
        String name = city.getCity();
        if(start != -1 && end != -1){
            name = city.getCity().substring(start+1,end);
        }
        List<Aqi> aqis = getAqisByCondition(cityId,type,7*24);
        List<Map<String,Object>> aqiList = getMap(aqis, type, "date");
        ClientVo aqiVo = new ClientVo();
        EnvEntity envEntity = new EnvEntity();
        Aqi aqi = aqis.get(aqis.size() - 1);
        List<Aqi> aqis1 = aqis.subList(aqis.size() - 8, aqis.size());
        List<Map<String, Object>> react = getMap(aqis1, type, "hour");
        envEntity.setAqi(aqi.getPm25());
        envEntity.setPm10(aqi.getPm10());
        envEntity.setCo(aqi.getNo2());
        envEntity.setO3(aqi.getO3());
        envEntity.setSo2(aqi.getSo2());
        aqiVo.setEnvEntity(envEntity);
        getColorInt(aqi.getAqi(),envEntity);
        setState(aqi, envEntity);
        envEntity.setDesc("  更新于"+aqi.getFtime());
        if(aqi.getAqi() >= 100){
            envEntity.setFlag(1);
        }else{
            envEntity.setFlag(0);
        }
        aqiVo.setName(name);
        aqiVo.setData(aqiList);
        aqiVo.setReact(react);
        return aqiVo;
    }

    @Override
    public Object selectAqiByArea(int cityId, int type) {
        Area city = areaService.getAreaByUid(cityId);
        int start = city.getName().indexOf("(");
        int end = city.getName().indexOf(")");
        String name = city.getName();
        if(start != -1 && end != -1){
            name = city.getName().substring(start+1,end);
        }
        List<Aqi> aqis = getAqisByCondition(cityId,type,7*24);
        List<Map<String,Object>> aqiList = getMap(aqis, type, "date");
        ClientVo aqiVo = new ClientVo();
        EnvEntity envEntity = new EnvEntity();
        Aqi aqi = aqis.get(aqis.size() - 1);
        List<Aqi> aqis1 = aqis.subList(aqis.size() - 8, aqis.size());
        List<Map<String, Object>> react = getMap(aqis1, type, "hour");
        envEntity.setAqi(aqi.getPm25());
        envEntity.setPm10(aqi.getPm10());
        envEntity.setCo(aqi.getNo2());
        envEntity.setO3(aqi.getO3());
        envEntity.setSo2(aqi.getSo2());
        aqiVo.setEnvEntity(envEntity);
        getColorInt(aqi.getAqi(),envEntity);
        setState(aqi, envEntity);
        envEntity.setDesc("  更新于"+aqi.getFtime());
        if(aqi.getAqi() >= 100){
            envEntity.setFlag(1);
        }else{
            envEntity.setFlag(0);
        }
        aqiVo.setName(name);
        aqiVo.setData(aqiList);
        aqiVo.setReact(react);
        return aqiVo;
    }

    public void getColorInt(int aqi, EnvEntity envEntity){
        if(aqi > 0 && aqi <= 50){
            envEntity.setColor(0xff009966);
            envEntity.setLevel("空气质量优");
        }else if(aqi > 50 && aqi <= 100){
            envEntity.setColor(0xffFFDE33);
            envEntity.setLevel("空气质量良");
        }else if(aqi > 100 && aqi <= 150){
            envEntity.setColor(0xffFF9933);
            envEntity.setLevel("轻度污染");
        }else if(aqi > 150 && aqi <= 200){
            envEntity.setColor(0xffCC0033);
            envEntity.setLevel("中度污染");
        }else if(aqi > 200 && aqi <= 300){
            envEntity.setColor(0xff660099);
            envEntity.setLevel("重度污染");
        }else{
            envEntity.setColor(0xff7E0023);
            envEntity.setLevel("超级污染");
        }
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

    public void setState(Aqi aqi, EnvEntity envEntity){
        if(aqi.getPm25() != null){
            double v = Double.parseDouble(aqi.getPm25());
            String state = getState(v);
            envEntity.setAqiState(state);
            envEntity.setSource("pm2.5");
        }
        if(aqi.getPm10() != null){
            double v = Double.parseDouble(aqi.getPm10());
            double v1 = Double.parseDouble(aqi.getPm25());
            String state = getState(v);
            envEntity.setPm10State(state);
            if(v1 < v){
                envEntity.setSource("pm10");
            }
        }
        if(aqi.getSo2() != null){
            double v = Double.parseDouble(aqi.getSo2());
            String state = getState(v);
            envEntity.setSo2State(state);
        }
        if(aqi.getCo() != null){
            double v = Double.parseDouble(aqi.getCo());
            String state = getState(v);
            envEntity.setCoState(state);
        }
        if(aqi.getO3() != null){
            double v = Double.parseDouble(aqi.getO3());
            String state = getState(v);
            envEntity.setO3State(state);
        }

    }

    public String getState(double aqi){
        if(aqi > 0 && aqi <= 50){
            return "good";
        }else if(aqi > 50 && aqi <= 100){
            return "face";
        }else if(aqi > 100 && aqi <= 150){
            return "just";
        }else if(aqi > 150 && aqi <= 200){
            return "bad";
        }else if(aqi > 200 && aqi <= 300){
            return "sobad";
        }
        return "badest";
    }

    @Override
    public List<AqiVo> selectAqiByCityId(int cityId, int type) {
        City city = cityService.getCityByUid(cityId);
        int start = city.getCity().indexOf("(");
        int end = city.getCity().indexOf(")");
        String name = city.getCity();
        if(start != -1 && end != -1){
            name = city.getCity().substring(start+1,end);
        }
        List<Aqi> aqis = getAqisByCondition(cityId,type,30*24);
        List<List<Long>> aqiList = aqis.stream().map(aqi -> {
            List<Long> node = new ArrayList<>();
//            String[] split = aqi.getUuid().split("_");
            int time = aqi.getVtime() + 8 * 60 * 60;
//            int time = 0;
//            if(split.length == 2){
//                time = Integer.parseInt(split[0]) + 8 * 60 * 60;
//            }
            String t = time + "000";
            node.add(Long.parseLong(t));
            if(aqi.getAqi() == 0){
                if(type == 1){
                    int china = getChina(Integer.valueOf(aqi.getPm25().replace(".0","")));
                    node.add(Long.valueOf(china));
                }
                node.add(Long.valueOf(aqi.getPm25().replace(".0","")));
            }else{
                if(type == 1){
                    int china = getChina(aqi.getAqi());
                    node.add(Long.valueOf(china));
                }else{
                    node.add(Long.valueOf(aqi.getAqi()));
                }
            }
            return node;}).collect(Collectors.toList());
        List<AqiVo> aqiVos = new ArrayList<>();
        AqiVo aqiVo = new AqiVo();
        aqiVo.setName(name);
        aqiVo.setData(aqiList);
        aqiVo.setType("area");
        aqiVos.add(aqiVo);
        return aqiVos;
    }

    @Override
    public CompareVo compareCity(List<Integer> ids, int type) {
        List<AqiVo> aqiVos = new ArrayList<>();
        ids.forEach(id->{
            List<AqiVo> aqiVos1 = selectAqiByCityId(id, type);
            aqiVos.addAll(aqiVos1);
        });
        List<AqiVo> aqivos = aqiVos.stream().map(aqiVo -> {
            aqiVo.setType("");
            return aqiVo;
        }).collect(Collectors.toList());
        CompareVo compareVo = new CompareVo();
        compareVo.setAqiVos(aqivos);
        compareVo.setComputers(computerService.computeBatchByList(aqivos,type));
        return compareVo;
    }


    @Override
    public Integer selectAqiByCityName(String name) {
        City cityByName = cityService.getCityByName(name);
        if(cityByName == null){
            throw new ResultException(400,"很抱歉,该城市暂时没有收录");
        }
        return cityByName.getUid();
    }

    @Override
    public CompareVo compareCityByName(List<String> names, int type) {
        List<Integer> ids = new ArrayList<>();
        names.forEach(name->{
            ids.add(selectAqiByCityName(name));
        });
        return compareCity(ids, type);
    }

    @Override
    public AreaAqiResponseVo selectAreaAqiByCityId(int cityId, int type, int vtime) {
        List<Area> areas = areaService.getAreaListByPerantId(cityId);
        List<String> x = areas.stream().map(area -> {
            int start = area.getName().indexOf("(");
            int end = area.getName().indexOf(")");
            if(start != -1 && end != -1){
                return area.getName().substring(start+1,end);
            }else{
                return area.getName();
            }
        }).collect(Collectors.toList());
        List<List<Integer>> totalAqi = new ArrayList<>();
        List<Integer> hours = gen24Hours(vtime);

        List<Integer> uids = areas.stream().map(area -> area.getUid()).collect(Collectors.toList());

        int start = vtime;
        int end = vtime + 24*60*60;
        QueryWrapper<Aqi> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("uid", uids);
        queryWrapper.between("vtime", start, end);
        queryWrapper.orderByAsc("vtime");
        List<Aqi> aqilist = baseMapper.selectList(queryWrapper);
        Map<Integer, List<Aqi>> aqiMap = aqilist.stream().collect(Collectors.groupingBy(Aqi::getUid));
        areas.forEach(area -> {
            List<Aqi> aqis = aqiMap.getOrDefault(area.getUid(), null);
            QueryWrapper<Waqi> waqiQuery = new QueryWrapper<>();
            waqiQuery.eq("uid", area.getUid());
            waqiQuery.between("vtime", start, end);
            waqiQuery.orderByAsc("vtime");
            List<Waqi> waqiList = waqiService.list(waqiQuery);
            List<Integer> collect = hours.stream().map(hour -> {
                return 0;
            }).collect(Collectors.toList());
            if((aqis != null && aqis.size() > 0) || (waqiList != null && waqiList.size() > 0)){
                for(int i = 0; i < collect.size() ; i++){
                    for(int j = 0; j < aqis.size(); j++){
                        if(hours.get(i) == aqis.get(j).getVtime()){
                            Aqi aqi = aqis.get(j);
                            int v;
                            if (aqi.getAqi() == 0) {
                                if (type == 1) {
                                    v = getChina(Integer.valueOf(aqi.getPm25().replace(".0", "")));
                                }else{
                                    v = Integer.parseInt(aqi.getPm25().replace(".0", ""));
                                }
                            } else {
                                if (type == 1) {
                                    v = getChina(aqi.getAqi());
                                } else {
                                    v = aqi.getAqi();
                                }
                            }
                            collect.set(i, v);
                            break;
                        }
                    }
                    if(collect.get(i) == 0){
                        for(int j = 0; j < waqiList.size(); j++){
                            if(hours.get(i) == waqiList.get(j).getVtime()){
                                Waqi aqi = waqiList.get(j);
                                int v;
                                if (type == 1) {
                                    v = getChina(aqi.getAqi());
                                } else {
                                        v = aqi.getAqi();
                                }
                                collect.set(i, v);
                                break;
                            }
                        }
                    }
                }
            }
            totalAqi.add(collect);
        });

        //将全部是0的区域给过滤掉
        filter(x,totalAqi);

        List<AreaAqiVo> areaAqiVos = new ArrayList<>();
        for(int i = 0; i < hours.size(); i++){
            AreaAqiVo areaAqiVo = new AreaAqiVo();
            areaAqiVo.setName(TimeUtil.convertMillisToString(((long)hours.get(i)) * 1000));
            List<Map<String,Object>> data = new ArrayList<>();
            for(int j = 0; j < totalAqi.size(); j++){
                Map<String, Object> map = new HashMap<>();
                int aqi = totalAqi.get(j).get(i);
                String color = getColor(aqi);
                map.put("y", aqi);
                map.put("color", color);
                data.add(map);
            }
            areaAqiVo.setData(data);
            areaAqiVos.add(areaAqiVo);
        }

        AreaAqiResponseVo areaAqiResponseVo = new AreaAqiResponseVo();
        areaAqiResponseVo.setAqis(areaAqiVos);
        areaAqiResponseVo.setX(x);
        return areaAqiResponseVo;
    }

    private void filter(List<String> x, List<List<Integer>> totalAqi) {
        List<Integer> f = totalAqi.stream().map(arr ->
             arr.stream().reduce((i, j) -> i + j).get() == 0 ? 0 : 1
        ).collect(Collectors.toList());
        List<String> x1 = new ArrayList<>();
        List<List<Integer>> totalAqi1 = new ArrayList<>();
        for(int i = 0; i < x.size(); i++){
            if(f.get(i) == 1){
                x1.add(x.get(i));
                totalAqi1.add(totalAqi.get(i));
            }
        }
        x.clear();
        totalAqi.clear();
        x.addAll(x1);
        totalAqi.addAll(totalAqi1);
    }

    @Override
    public HourVo selectPieChartByDay(int vtime, int cityId, int type) {
        int start = vtime;
        int end = vtime + 24*60*60;
        QueryWrapper<Aqi> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", cityId);
        queryWrapper.between("vtime", start, end);
        queryWrapper.orderByAsc("vtime");
        List<Aqi> aqis = baseMapper.selectList(queryWrapper);
        List<Map<String, Object>> react = new ArrayList<>();
        List<String> reactColors = new ArrayList<>();
        for(Aqi aqi : aqis){
            Map<String, Object> node = new HashMap<>();
            String hour = aqi.getFtime().split(" ")[1];
            node.put("name",hour);
            if(type == 1){
                int china = getChina(aqi.getAqi());
                node.put("y",china);
                node.put("color", getColor(china));
            }else{
                node.put("y", aqi.getAqi());
                node.put("color", getColor(aqi.getAqi()));
            }
            react.add(node);
        }
        double good = getCondition(aqis, 0, 50, type);
        double so = getCondition(aqis, 50, 100, type);
        double light = getCondition(aqis, 100, 150, type);
        double bad = getCondition(aqis, 150, 200, type);
        double tbad = getCondition(aqis, 200, 300, type);
        double ttbad = getCondition(aqis, 300, 1000, type);
        List<Map<String, Object>> sort = new ArrayList<>();
        Map<String,Object> gmap = new HashMap<>();
        gmap.put("name","优");
        gmap.put("y", good);
        gmap.put("type", 25);
        sort.add(gmap);
        Map<String,Object> smap = new HashMap<>();
        smap.put("name","良");
        smap.put("y", so);
        smap.put("type", 75);
        sort.add(smap);
        Map<String,Object> lmap = new HashMap<>();
        lmap.put("name","轻度污染");
        lmap.put("y", light);
        lmap.put("type", 125);
        sort.add(lmap);
        Map<String,Object> zmap = new HashMap<>();
        zmap.put("name","中度污染");
        zmap.put("y", bad);
        zmap.put("type", 175);
        sort.add(zmap);
        Map<String,Object> tmap = new HashMap<>();
        tmap.put("name","重度污染");
        tmap.put("y", tbad);
        tmap.put("type", 225);
        sort.add(tmap);
        Map<String,Object> ttmap = new HashMap<>();
        ttmap.put("name","爆表");
        ttmap.put("y", ttbad);
        ttmap.put("type", 345);
        sort.add(ttmap);
        Collections.sort(sort, new Comparator<Map<String,Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    double d1 = (double) o1.get("y");
                    double d2 = (double) o2.get("y");
                    if(d1 < d2){
                        return 1;
                    }else if(d1 == d2){
                        return 0;
                    }else{
                        return -1;
                    }
            }
        });
        List<Map<String,Object>> data = new ArrayList<>();
        List<String> pieColor = new ArrayList<>();
        int count = 0;
        for(Map<String, Object> i : sort){
            double d = (double) i.get("y");
            if(d != 0){
                count = count + 1;
                pieColor.add(getColor((Integer) i.get("type")));
                if(count == 1){
                    i.put("sliced",true);
                    i.put("selected", true);
                    data.add(i);
                }else{
                    data.add(i);
                }
            }
        }
        DayTotalVo dayTotalVo = new DayTotalVo();
        dayTotalVo.setColorByPoint(true);
        dayTotalVo.setName("Brands");
        dayTotalVo.setData(data);
        List<DayTotalVo> dayTotalVos = new ArrayList<>();
        dayTotalVos.add(dayTotalVo);
        HourVo.ReactNode reactNode = new HourVo.ReactNode();
        reactNode.setColors(reactColors);
        reactNode.setReact(react);
        HourVo.PieNode pieNode = new HourVo.PieNode();
        pieNode.setPie(dayTotalVos);
        pieNode.setColors(pieColor);
        HourVo hourVo = new HourVo();
        hourVo.setPie(pieNode);
        hourVo.setReact(reactNode);
        return hourVo;
    }

    @Override
    public Object selectAreaByMouth(int cityId, int type, String tmp) {
        QueryWrapper<Computer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("p_id", cityId);
        queryWrapper.eq("mouth_time", tmp);
        List<Computer> computers = computerService.list(queryWrapper);
        if(computers == null || computers.size() == 0){
            throw new ResultException(500, "不存在相应的数据");
        }
        List<String> x = computers.stream().map(computer -> computer.getName()).collect(Collectors.toList());
        List<Float> avgs = computers.stream().map(computer -> computer.getAvg()).collect(Collectors.toList());
        List<Integer> goods = computers.stream().map(computer -> computer.getGoodcount()).collect(Collectors.toList());
        List<Integer> justsosos = computers.stream().map(computer -> computer.getJustsosocount()).collect(Collectors.toList());
        List<Integer> lights = computers.stream().map(computer -> computer.getLightcount()).collect(Collectors.toList());
        List<Integer> zs = computers.stream().map(computer -> computer.getZcount()).collect(Collectors.toList());
        List<Integer> bads = computers.stream().map(computer -> computer.getBadcount()).collect(Collectors.toList());
        List<Integer> badests = computers.stream().map(computer -> computer.getBadestcount()).collect(Collectors.toList());
        List<Map<String,Object>> total = new ArrayList<>();
        Map<String,Object> avgmap = new HashMap<>();
        avgmap.put("name","月平均值");
        avgmap.put("data",avgs);
        Map<String,Object> goodmap = new HashMap<>();
        goodmap.put("name","月优的小时总数");
        goodmap.put("data",goods);
        Map<String,Object> justmap = new HashMap<>();
        justmap.put("name","月良的小时总数");
        justmap.put("data",justsosos);
        Map<String,Object> lightmap = new HashMap<>();
        lightmap.put("name","月轻度污染的小时总数");
        lightmap.put("data",lights);
        Map<String,Object> zmap = new HashMap<>();
        zmap.put("name","月中度污染的小时总数");
        zmap.put("data",zs);
        Map<String,Object> badmap = new HashMap<>();
        badmap.put("name","月重度污染的小时总数");
        badmap.put("data",bads);
        Map<String,Object> badestmap = new HashMap<>();
        badestmap.put("name","月爆表的小时总数");
        badestmap.put("data",badests);
        total.add(avgmap);
        total.add(goodmap);
        total.add(justmap);
        total.add(lightmap);
        total.add(zmap);
        total.add(badmap);
        total.add(badestmap);
        MouthVo mouthVo = new MouthVo();
        mouthVo.setData(total);
        mouthVo.setX(x);
        return mouthVo;
    }

    @Override
    public int getFitAqi(int aqi, int type) {
        if(type == 1){
            return this.getChina(aqi);
        }
        else{
            return aqi;
        }
    }

    @Override
    public Object getMapAqi() {
        try{
            String url = "http://mapi3.aqicn.org/mapi/?bounds=(3.311143000000,103.073730000000),(54.098060000000,126.804199000000))&zoom=11.0&v=2&sid=174234829&lang=zh&package=Asia&appv=132&appn=3.5&tz=28800000&metrics=1080,2211,3.0&wifi=&devid=6fb268749236975d";
            HttpRequestConfig config = new HttpRequestConfig();
            config.url(url);
            HttpRequestResult result = HttpUtils.get(config);
            MapResult mapResult = JSON.parseObject(result.getResponseText(), MapResult.class);
            List<MapResult.Geo> m = mapResult.getM();
            m.forEach(g->{
                if(g.getA().equals("-")){
                    g.setColor(0xff808080);
                    g.setA("0");
                }else{
                    int color = getColorInt(Integer.parseInt(g.getA()));
                    g.setColor(color);
                }
            });
            Map<String,List<MapResult.Geo>> map = new HashMap<>();
            map.put("items", m);
            return map;
        }catch (Exception e){
            log.error("获取地图aqi失败: "+ e);
        }
        return null;
    }

    @Override
    public Object getWaqiMap() {
        Map<Integer,Area> areas;
        String allArea = redisService.getString(GlobalConstant.ALL_AREA);
        if(allArea == null){
            areas = areaService.list().stream().collect(Collectors.toMap(Area::getUid,area->area));
            redisService.setString(GlobalConstant.ALL_AREA, JSONObject.toJSONString(areas));
        }else{
            areas = JSONObject.parseObject(allArea,  new TypeReference<Map<Integer, Area>>(){});
        }

        QueryWrapper<Waqi> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("vtime", TimeUtil.getHour());
        List<Waqi>   list = waqiService.list(queryWrapper);

        List<Waqi> list1;
        String before = redisService.hget(GlobalConstant.MER_HOUR_AQI, (TimeUtil.getHour() - 60 * 60)+"");
        if(before == null){
            QueryWrapper<Waqi> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("vtime", TimeUtil.getHour()- 60*60);
            list1 = waqiService.list(queryWrapper1);
            redisService.hset(GlobalConstant.MER_HOUR_AQI, (TimeUtil.getHour() - 60 * 60)+"", JSONObject.toJSONString(list1));
        }else{
            list1 = JSON.parseArray(before, Waqi.class);
        }
        list.addAll(list1);
        Map<String, Waqi> map = new HashMap<>();
        list.forEach(waqi -> {
            if(map.containsKey(waqi.getUid())){
                Waqi waqi1 = map.get(waqi.getUid());
                if(waqi.getVtime() < waqi1.getVtime()){
                    map.put(waqi.getUid()+"",waqi);
                }
            }else{
                map.put(waqi.getUid()+"",waqi);
            }
        });
        Iterator<Map.Entry<String, Waqi>> iterator = map.entrySet().iterator();
        List<MapResult.Geo> m = new ArrayList<>();
        while (iterator.hasNext()){
            Map.Entry<String, Waqi> next = iterator.next();
            MapResult.Geo geo = new MapResult.Geo();
            Area area = areas.get(Integer.parseInt(next.getKey()));
            if(area == null){
                continue;
            }
            List<Double> g = new ArrayList<>();
            g.add(area.getLat());
            g.add(area.getLon());
            geo.setG(g);
            geo.setA(String.valueOf(next.getValue().getAqi()));
            geo.setX(next.getKey());
            String name = area.getName();
            int start = area.getName().indexOf("(");
            int end = area.getName().indexOf(")");
            if(start != -1 && end != -1){
                name = area.getName().substring(start+1,end);
            }
            geo.setName(name);
            geo.setUpdateTime(TimeUtil.convertMillisToString(Long.valueOf(next.getValue().getVtime()+"000")));
            int color = getColorInt(next.getValue().getAqi());
            geo.setColor(color);
            m.add(geo);
        }
        Map<String,List<MapResult.Geo>> map1 = new HashMap<>();
        map1.put("items", m);
        return map1;
    }

    public List<Integer> gen24Hours(int vtime){
        int current = (int)(System.currentTimeMillis() / 1000);
        List<Integer> hours = new ArrayList<>();
        int c = current - vtime;
        if(c > 0 && c < 24*60*60){
            int count = c / 3600;
            for(int i = 0; i < count; i++){
                hours.add(vtime + i*60*60);
            }
        }else{
            for(int i = 0; i < 24; i++){
                hours.add(vtime + i*60*60);
            }
        }
        return hours;
    }

    public double getCondition(List<Aqi> aqis, int start, int end, int type){
        List<Integer> collect = aqis.stream().map(aqi -> {
            if (aqi.getAqi() == 0) {
                if(type == 1){
                    return getChina(Integer.valueOf(aqi.getPm25().replace(".0", "")));
                }
                return Integer.valueOf(aqi.getPm25().replace(".0", ""));
            }
            if(type == 1){
                return getChina(aqi.getAqi());
            }
            return aqi.getAqi();
        }).filter(v -> v > start && v <= end).collect(Collectors.toList());
        return collect.size() * 1.0 / aqis.size();
    }

    public String getColor(int aqi){
        if(aqi > 0 && aqi <= 50){
            return "#009966";
        }else if(aqi > 50 && aqi <= 100){
            return "#FFDE33";
        }else if(aqi > 100 && aqi <= 150){
            return "#FF9933";
        }else if(aqi > 150 && aqi <= 200){
            return "#CC0033";
        }else if(aqi > 200 && aqi <= 300){
            return "#660099";
        }
        return "#7E0023";
    }

    public int getChina(int aqi){
        if(aqi > 0 && aqi <= 50){
            float degree = getdegree(trafloat(GlobalConstant.a[0][3]),
                    trafloat(GlobalConstant.a[0][2]),
                    trafloat(GlobalConstant.a[0][1]),
                    trafloat(GlobalConstant.a[0][0]), aqi);
            float chinadegree = getChinaDegree(trafloat(GlobalConstant.c[0][3]),
                    trafloat(GlobalConstant.c[0][2]),
                    trafloat(GlobalConstant.c[0][1]),
                    trafloat(GlobalConstant.c[0][0]),degree);
            return (int) chinadegree;
        }else if(aqi > 50 && aqi <= 100){
            float degree = getdegree(trafloat(GlobalConstant.a[1][3]),
                    trafloat(GlobalConstant.a[1][2]),
                    trafloat(GlobalConstant.a[1][1]),
                    trafloat(GlobalConstant.a[1][0]), aqi);
            float chinadegree = getChinaDegree(trafloat(GlobalConstant.c[1][3]),
                    trafloat(GlobalConstant.c[1][2]),
                    trafloat(GlobalConstant.c[1][1]),
                    trafloat(GlobalConstant.c[1][0]),degree);
            return (int) chinadegree;
        }else if(aqi > 100 && aqi <= 150){
            float degree = getdegree(trafloat(GlobalConstant.a[2][3]),
                    trafloat(GlobalConstant.a[2][2]),
                    trafloat(GlobalConstant.a[2][1]),
                    trafloat(GlobalConstant.a[2][0]), aqi);
            float chinadegree = getChinaDegree(trafloat(GlobalConstant.c[2][3]),
                    trafloat(GlobalConstant.c[2][2]),
                    trafloat(GlobalConstant.c[2][1]),
                    trafloat(GlobalConstant.c[2][0]),degree);
            return (int) chinadegree;
        }else if(aqi > 150 && aqi <= 200){
            float degree = getdegree(trafloat(GlobalConstant.a[3][3]),
                    trafloat(GlobalConstant.a[3][2]),
                    trafloat(GlobalConstant.a[3][1]),
                    trafloat(GlobalConstant.a[3][0]), aqi);
            float chinadegree = getChinaDegree(trafloat(GlobalConstant.c[3][3]),
                    trafloat(GlobalConstant.c[3][2]),
                    trafloat(GlobalConstant.c[3][1]),
                    trafloat(GlobalConstant.c[3][0]),degree);
            return (int) chinadegree;
        }else if(aqi > 200 && aqi <= 300){
            float degree = getdegree(trafloat(GlobalConstant.a[4][3]),
                    trafloat(GlobalConstant.a[4][2]),
                    trafloat(GlobalConstant.a[4][1]),
                    trafloat(GlobalConstant.a[4][0]), aqi);
            float chinadegree = getChinaDegree(trafloat(GlobalConstant.c[4][3]),
                    trafloat(GlobalConstant.c[4][2]),
                    trafloat(GlobalConstant.c[4][1]),
                    trafloat(GlobalConstant.c[4][0]),degree);
            return (int) chinadegree;
        }
        float degree = getdegree(trafloat(GlobalConstant.a[5][3]),
                trafloat(GlobalConstant.a[5][2]),
                trafloat(GlobalConstant.a[5][1]),
                trafloat(GlobalConstant.a[5][0]), aqi);
        float chinadegree = getChinaDegree(trafloat(GlobalConstant.c[5][3]),
                trafloat(GlobalConstant.c[5][2]),
                trafloat(GlobalConstant.c[5][1]),
                trafloat(GlobalConstant.c[5][0]),degree);
        return (int) chinadegree;
    }

    public float trafloat(int v){
        return (float) (v * 1.0 / 10);
    }

    public float getdegree(float ih, float il, float ch, float cl, int aqi){
        return  (aqi - il)*((ch - cl)/(ih - il)) + cl;
    }

    public float getChinaDegree(float ih, float il, float ch, float cl, float degree){
        return ((ih - il) / (ch - cl)) * (degree - cl) + il;
    }
}
