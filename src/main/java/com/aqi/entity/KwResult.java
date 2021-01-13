package com.aqi.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class KwResult {
    private String status;
    private List<CityNode> data;

    @Data
    public class CityNode{
        private int uid;
        private String aqi;
        private Map<String,Object> time;
        private Station station;

        @Data
        public class Station{
            private String name;
            private List<Double> geo;
            private String url;
            private String country;
        }
    }
}



