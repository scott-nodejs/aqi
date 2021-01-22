package com.aqi.controller;

import com.aqi.entity.NoResult;
import com.aqi.service.AreaService;
import com.aqi.service.NoResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NoResultController {

    @Autowired
    private NoResultService noResultService;

    @GetMapping("/no/result/{id}")
    public Object getNoResult(@PathVariable int id){
        return noResultService.selectNoResult(id);
    }
}
