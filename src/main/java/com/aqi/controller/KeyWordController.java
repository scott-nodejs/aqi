package com.aqi.controller;

import com.aqi.entity.City;
import com.aqi.service.AreaService;
import com.aqi.service.CityService;
import com.aqi.service.KeyWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class KeyWordController {

    @Autowired
    private KeyWordService keyWordService;

    @Autowired
    CityService cityService;

    @Autowired
    AreaService areaService;

    @PostMapping(value = "/add/kw/")
    public Object addKW(@RequestBody ArrayList<String> citys){
       if(citys.size() > 0){
           citys.forEach(city->{
               keyWordService.insertKw(city);
           });
       }
       return "ok";
    }

    @PostMapping(value = "/add/Area/")
    public Object addArea(@RequestBody Map<String,Object> noCity){
        if(noCity!=null){
            String city = (String) noCity.get("city");
            City cityByName = cityService.getCityByName(city);
            List<Integer> uids = (List<Integer>) noCity.get("uids");
            areaService.addArea(uids, cityByName.getUid());
        }
        return "操作完成";
    }

    @PostMapping(value = "/add/city/")
    public Object addcity(@RequestBody ArrayList<Integer> uids){
        if(uids!=null){
            cityService.addCity(uids);
        }
        return "操作完成";
    }
}
