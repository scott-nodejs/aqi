package com.aqi.controller.api;

import com.aqi.configer.exception.ResultException;
import com.aqi.entity.CustomCity;
import com.aqi.entity.CustomCityVo;
import com.aqi.service.AqiService;
import com.aqi.service.CityService;
import com.aqi.service.CustomCityService;
import com.aqi.utils.ResultVoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/client/api")
public class ClientController {

    @Autowired
    private AqiService aqiService;

    @Autowired
    private CityService cityService;

    @Autowired
    private CustomCityService customCityService;

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

    @PostMapping(value = "/custom/city")
    public Object submitCustomCity(@RequestBody CustomCityVo customCityVo){
        if(customCityVo.getPhone() == null){
            throw new ResultException(400, "电话号码不能为空");
        }
        return ResultVoUtil.success(customCityService.submitCustomCity(customCityVo));
    }

    @GetMapping(value = "/get/customCity")
    public Object getCustomCity(String phone){
        if(phone == null){
            throw new ResultException(400, "电话号码不能为空");
        }
        return ResultVoUtil.success(customCityService.selectCustomCity(phone));
    }
}
