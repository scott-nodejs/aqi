package com.aqi.entity;

import lombok.Data;

import java.util.List;

@Data
public class RandResult {

    List<OnlyPm> d;

    @Data
    public class OnlyPm{
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
