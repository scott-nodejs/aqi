package com.aqi.controller;

import com.aqi.service.AqiService;
import com.aqi.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AqiController {

    @Autowired
    private AqiService aqiService;

    @GetMapping(value = "/getAqiByCity/{id}")
    public Object getAqiByCityId(@PathVariable int id){
        return aqiService.selectAqiByCityId(id);
    }

    @GetMapping(value = "/getPidData/{id}")
    public Object getPidData(@PathVariable int id, String tmp){
        int l = (int) (TimeUtil.convertStringToMillis(tmp+ " 00:00:00") / 1000);
        return aqiService.selectPieChartByDay(l,id);
    }
}
