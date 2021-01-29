package com.aqi.service.imp;

import com.aqi.configer.exception.ResultException;
import com.aqi.entity.*;
import com.aqi.mapper.aqi.AqiMapper;
import com.aqi.service.AqiService;
import com.aqi.service.CityService;
import com.aqi.utils.TimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.attoparser.trace.MarkupTraceEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AqiServiceImpl extends ServiceImpl<AqiMapper, Aqi> implements AqiService {

    @Autowired
    CityService cityService;

    @Override
    public void insertAqi(Aqi aqi) {
        baseMapper.insert(aqi);
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
    public AqiResponseVo selectAqiByCityId(int cityId, int type) {
        City city = cityService.getCityByUid(cityId);
        int start = city.getCity().indexOf("(");
        int end = city.getCity().indexOf(")");
        String name = city.getCity().substring(start+1,end);
        QueryWrapper<Aqi> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid",cityId);
        List<Aqi> aqis = baseMapper.selectList(queryWrapper);
        List<List<Long>> aqiList = aqis.stream().map(aqi -> {
            List<Long> node = new ArrayList<>();
            int time = aqi.getVtime() + 8 * 60 * 60;
            String t = time + "000";
            node.add(Long.parseLong(t));
            if(aqi.getAqi() == 0){
                node.add(Long.valueOf(aqi.getPm25().replace(".0","")));
            }else{
                node.add(Long.valueOf(aqi.getAqi()));
            }
            return node;}).collect(Collectors.toList());
        List<AqiVo> aqiVos = new ArrayList<>();
        AqiVo aqiVo = new AqiVo();
        aqiVo.setName(name);
        aqiVo.setData(aqiList);
        aqiVo.setType("area");
        aqiVos.add(aqiVo);
        AqiResponseVo aqiResponseVo = new AqiResponseVo();
        aqiResponseVo.setAqis(aqiVos);
        return aqiResponseVo;
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
    public HourVo selectPieChartByDay(int vtime, int cityId) {
        int start = vtime;
        int end = vtime + 24*60*60;
        QueryWrapper<Aqi> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", cityId);
        queryWrapper.between("vtime", start, end);
        queryWrapper.orderByAsc("vtime");
        List<Aqi> aqis = baseMapper.selectList(queryWrapper);
        List<List<Object>> react = new ArrayList<>();
        List<String> reactColors = new ArrayList<>();
        for(Aqi aqi : aqis){
            List<Object> node = new ArrayList<>();
            String hour = aqi.getFtime().split(" ")[1];
            node.add(hour);
            node.add(aqi.getAqi());
            node.add(50);
            react.add(node);
            reactColors.add(getColor(aqi.getAqi()));
        }
        double good = getCondition(aqis, 0, 50);
        double so = getCondition(aqis, 50, 100);
        double light = getCondition(aqis, 100, 150);
        double bad = getCondition(aqis, 150, 200);
        double tbad = getCondition(aqis, 200, 300);
        double ttbad = getCondition(aqis, 300, 1000);
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

    public double getCondition(List<Aqi> aqis, int start, int end){
        List<Integer> collect = aqis.stream().map(aqi -> {
            if (aqi.getAqi() == 0) {
                return Integer.valueOf(aqi.getPm25().replace(".0", ""));
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
}
