package com.aqi.controller.api;

import com.aqi.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client/api/cityQ")
public class CityQController {

    @Autowired
    CityService cityService;

    @GetMapping(value = "/list/{type}")
    public Object getCityQList(@PathVariable int type){
        return cityService.cityQByClient(type);
    }
}
