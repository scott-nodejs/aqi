package com.aqi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class IndexController {

    @RequestMapping("/index")
    public String hello(Model model) {
        return "index";
    }

    @RequestMapping("/noResult")
    public String noResult(Model model) {
        return "noresult";
    }

    @RequestMapping("/area")
    public String area(Model model) {
        return "area";
    }

    @RequestMapping("/compare")
    public String compare(Model model) {
        return "compare";
    }

    @RequestMapping("/rank")
    public String rank(Model model) {
        return "rank";
    }
}
