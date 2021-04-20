package com.aqi.controller;

import com.alibaba.fastjson.JSONObject;
import com.aqi.entity.City;
import com.aqi.service.AqiService;
import com.aqi.service.AreaService;
import com.aqi.service.CityService;
import com.aqi.service.CronService;
import com.aqi.utils.ResultVoUtil;
import com.aqi.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class AqiController {

    @Autowired
    private AqiService aqiService;

    @Autowired
    private AreaService areaService;

    @Autowired
    private CronService cronService;

    @Autowired
    private CityService cityService;

    @GetMapping(value = "/getAqiByCity/{id}/{type}")
    public Object getAqiByCityId(@PathVariable int id, @PathVariable int type){
        return aqiService.selectAqiByCityId(id, type);
    }

//    @PostMapping(value = "/compareCity/{type}")
//    public Object compareCity(String ids, @PathVariable int type){
//        String[] idarr = ids.split(",");
//        return aqiService.compareCity(idarr, type);
//    }

    @GetMapping(value = "/getAqiByCityName/{name}")
    public Object getAqiByCityName(@PathVariable String name){
        return ResultVoUtil.success("获取成功", aqiService.selectAqiByCityName(name));
    }

    @PostMapping(value = "/compareCitys/{type}")
    public Object compareCitys(String names, @PathVariable int type){
        List<String> parse = (List<String>) JSONObject.parse(names);
        return aqiService.compareCityByName(parse, type);
    }

    @GetMapping(value = "/getPidData/{id}/{type}")
    public Object getPidData(@PathVariable int id, @PathVariable int type, String tmp){
        int l = (int) (TimeUtil.convertStringToMillis(tmp+ " 00:00:00") / 1000);
        return aqiService.selectPieChartByDay(l,id, type);
    }

    @GetMapping(value = "/getPidDataBySearch/{type}")
    public Object getPidData(@PathVariable int type, String tmp,String city){
        int l = (int) (TimeUtil.convertStringToMillis(tmp+ " 00:00:00") / 1000);
        City cityByName = cityService.getCityByName(city);
        return aqiService.selectPieChartByDay(l,cityByName.getUid(), type);
    }

    @GetMapping(value = "/getAreaAqiByCity/{id}/{type}")
    public Object getAreaAqiByCityId(@PathVariable int id, @PathVariable int type, String tmp){
        int l = (int) (TimeUtil.convertStringToMillis(tmp+ " 00:00:00") / 1000);
        return aqiService.selectAreaAqiByCityId(id, type, l);
    }

    @GetMapping(value = "/getAreaAqiByCityName/{type}")
    public Object getAreaAqiByCityName(@PathVariable int type, String tmp,String city){
        int l = (int) (TimeUtil.convertStringToMillis(tmp+ " 00:00:00") / 1000);
        City cityByName = cityService.getCityByName(city);
        return aqiService.selectAreaAqiByCityId(cityByName.getUid(), type, l);
    }

    @GetMapping(value = "/getMouthAreaAqiByCity/{id}/{type}")
    public Object getMouthAreaAqiByCity(@PathVariable int id, @PathVariable int type, String tmp){
        return aqiService.selectAreaByMouth(id, type, tmp);
    }

    @GetMapping(value = "/getMouthAreaAqiByCityName/{type}")
    public Object getMouthAreaAqiByCityName(@PathVariable int type, String tmp, String city){
        City cityByName = cityService.getCityByName(city);
        return aqiService.selectAreaByMouth(cityByName.getUid(), type, tmp);
    }

    @GetMapping(value = "/waqi/sycn")
    public Object sycn(@RequestParam(required = false) long vtime){
        cronService.cronSycnWaqi(vtime);
        return 0;
    }

    @GetMapping(value = "/mouth/computer")
    public Object computer(@RequestParam(required = false) String tmp){
        cronService.cronMouthAqi(tmp);
        return 0;
    }

    @GetMapping(value = "/mouthBycity/computer/{id}")
    public Object mouthBycity(@PathVariable int id, @RequestParam(required = false) String tmp){
        cronService.cronMouthAqi(id,tmp);
        return 0;
    }

    @GetMapping(value = "/aqi/rank")
    public Object rank(@RequestParam(required = false, defaultValue = "9") int rank, @RequestParam int type){
        return cityService.rank(rank, type);
    }

    @GetMapping(value = "/genrank")
    public Object genrank(){
        cityService.getRank();
        return 0;
    }

    @GetMapping(value = "/addPoint")
    public Object addPoint(){
        areaService.addPoint();
        return 0;
    }
}
