package com.aqi.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AqiResult {
    private String status;
    private Aqi data;

    @Data
    public class Aqi{
        private Object aqi;
        private int idx;
        private Iaqi iaqi;
        private Map<String,Object> time;
        private City city;

        @Data
        public class City{
            private String name;
            private List<Double> geo;
            private String url;
        }

        @Data
        public class Iaqi{
            private Map<String,Double> co;
            private Map<String,Double> dew;
            private Map<String,Double> h;
            private Map<String,Double> no2;
            private Map<String,Double> o3;
            private Map<String,Double> p;
            private Map<String,Double> pm10;
            private Map<String,Double> pm25;
            private Map<String,Double> so2;
            private Map<String,Double> t;
            private Map<String,Double> w;
            private Map<String,Double> wg;
        }
    }
}
