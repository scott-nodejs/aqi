package com.aqi.entity;

import lombok.Data;

@Data
public class Location {
    private String city;
    private String province;
    private String adcode;
    private String district;
    private String towncode;
    private StreetNumber streetNumber;
    private String country;
    private String township;

    @Data
    class StreetNumber{
        private String number;
        private String location;
        private String direction;
        private String distance;
        private String street;
    }
}
