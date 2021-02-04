package com.aqi.controller;

import com.aqi.service.AqiService;
import com.aqi.utils.ResultVoUtil;
import com.aqi.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AqiController {

    @Autowired
    private AqiService aqiService;

    @GetMapping(value = "/getAqiByCity/{id}/{type}")
    public Object getAqiByCityId(@PathVariable int id, @PathVariable int type){
        return aqiService.selectAqiByCityId(id, type);
    }

    @GetMapping(value = "/getAqiByCityName/{name}")
    public Object getAqiByCityName(@PathVariable String name){
        return ResultVoUtil.success("获取成功", aqiService.selectAqiByCityName(name));
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
}
