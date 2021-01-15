package com.aqi.controller;

import com.aqi.service.AreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NoResultController {

    @Autowired
    private AreaService areaService;

    @GetMapping("/no/result")
    public Object getNoResult(){
        return areaService.selectByNoResult();
    }
}
