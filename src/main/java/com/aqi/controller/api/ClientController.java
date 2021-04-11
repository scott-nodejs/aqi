package com.aqi.controller.api;

import com.aqi.service.AqiService;
import com.aqi.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client/api")
public class ClientController {

    @Autowired
    private AqiService aqiService;

    @Autowired
    private CityService cityService;

    @GetMapping(value = "/getAqiByCity/{id}/{type}")
    public Object getAqiByCityId(@PathVariable int id, @PathVariable int type){
        return aqiService.selectAqiByClient(id, type);
    }

    @GetMapping(value = "/getRankByCity")
    public Object getRankByCityId(@RequestParam int type){
        return cityService.rankByClient(9, type);
    }
}
