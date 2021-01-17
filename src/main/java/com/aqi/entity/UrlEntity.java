package com.aqi.entity;

import lombok.Data;

@Data
public class UrlEntity {

    private String url;

    private City city;

    private Area area;

    private long vtime;

    private int type;
}
