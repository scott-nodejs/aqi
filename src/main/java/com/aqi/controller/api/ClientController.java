package com.aqi.controller.api;

import com.aqi.service.AqiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client/api")
public class ClientController {

    @Autowired
    private AqiService aqiService;

    @GetMapping(value = "/getAqiByCity/{id}/{type}")
    public Object getAqiByCityId(@PathVariable int id, @PathVariable int type){
        return aqiService.selectAqiByClient(id, type);
    }
}
