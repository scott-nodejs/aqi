package com.aqi.entity;

import lombok.Data;

import java.util.List;

@Data
public class MapResult {
    List<Geo> m;
    String z;
    List<Integer> t;
    String i;
    boolean reset;

    @Data
    public static class Geo{
        List<Double> g;
        String a;
        String x;
        int color;
        String name;
        String updateTime;
    }
}
