package com.aqi.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AllResult{
    Object aqi;
    int time;
    List<Map<String,String>> historic;
    int ctemp;
    int cpres;
    String nameen;
    String namena;
    List<Near> nearest;
    Forecast forecast;

    @Data
    public class Forecast{
        int t;
        Map<String,String> w;
        Map<String,List<String>> a;
    }

    @Data
    public class Near{
        private String nlo;
        private String nna;
        private int t;
        private String pol;
        private String x;
        private String v;
        private String u;
        private String key;
        private float d;
        private List<Double> geo;
    }
}