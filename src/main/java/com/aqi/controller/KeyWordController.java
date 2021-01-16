package com.aqi.controller;

import com.aqi.service.KeyWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class KeyWordController {

    @Autowired
    private KeyWordService keyWordService;

    @PostMapping(value = "/add/kw/")
    public Object addKW(@RequestBody ArrayList<String> citys){
       if(citys.size() > 0){
           citys.forEach(city->{
               keyWordService.insertKw(city);
           });
       }
       return "ok";
    }
}
