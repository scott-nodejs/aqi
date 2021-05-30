package com.aqi.controller.api;

import com.aqi.service.AqiService;
import com.aqi.service.CityService;
import com.aqi.utils.ResultVoUtil;
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

    @GetMapping(value = "/getAqiByCity/{type}")
    public Object getAqiByLocation(@PathVariable int type, @RequestParam String location){
        return aqiService.selectAqiByLocation(location, type);
    }

    @GetMapping(value = "/getRankByCity")
    public Object getRankByCityId(@RequestParam int type, @RequestParam int page){
        return cityService.rankByClient(20, type, page);
    }

    @GetMapping(value = "/getMap")
    public Object getMap(){
//        return aqiService.getMapAqi("3.311143000000,103.073730000000","54.098060000000,126.804199000000");
        return aqiService.getWaqiMap();
    }

    @GetMapping(value = "/getCityByCityName/{name}")
    public Object getCityByCityName(@PathVariable String name){
        return cityService.getCityByName(name);
    }
}
