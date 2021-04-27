package com.aqi.controller.api;

import com.aqi.entity.CityqMapper;
import com.aqi.service.CityService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cityq.RaiderServiceApi;
import com.cityq.TradeServiceApi;
import com.cityq.entity.Raiders;
import com.cityq.entity.Trade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/client/api/cityQ")
public class CityQController {

    @Autowired
    TradeServiceApi tradeServiceApi;

    @Autowired
    RaiderServiceApi raiderServiceApi;

    @Autowired
    CityService cityService;

    @GetMapping(value = "/list/{type}")
    public Object getCityQList(@PathVariable int type){
        return cityService.cityQByClient(type);
    }

    @GetMapping(value = "/getCity/{uid}")
    public Object getCity(@PathVariable int uid){
        return cityService.getCityByUid(uid);
    }


    @GetMapping(value = "trade/list/{id}")
    public Object getTrades(@PathVariable int id, @RequestParam int page){
        Map<String, Object> map = new HashMap<>();
        IPage<Trade> trades = tradeServiceApi.getTrades(id, page, 10);
        CityqMapper<Trade> cityqMapper1 = new CityqMapper<>();
        cityqMapper1.setMaxPage(trades.getPages());
        cityqMapper1.setCityMapper(trades.getRecords());
        map.put("trades", cityqMapper1);
        IPage<Raiders> raiders = raiderServiceApi.getRaiders(id, page, 10);
        CityqMapper<Raiders> cityqMapper2 = new CityqMapper<>();
        cityqMapper2.setMaxPage(raiders.getPages());
        cityqMapper2.setCityMapper(raiders.getRecords());
        map.put("raiders", cityqMapper2);
        return map;
    }
}
