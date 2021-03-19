package com.aqi.controller;

import com.alibaba.fastjson.JSONObject;
import com.aqi.service.AqiService;
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
    private CronService cronService;

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

    @GetMapping(value = "/getAreaAqiByCity/{id}/{type}")
    public Object getAreaAqiByCityId(@PathVariable int id, @PathVariable int type, String tmp){
        int l = (int) (TimeUtil.convertStringToMillis(tmp+ " 00:00:00") / 1000);
        return aqiService.selectAreaAqiByCityId(id, type, l);
    }

    @GetMapping(value = "/waqi/sycn")
    public Object sycn(@RequestParam(required = false) long vtime){
        cronService.cronSycnWaqi(vtime);
        return 0;
    }
}
