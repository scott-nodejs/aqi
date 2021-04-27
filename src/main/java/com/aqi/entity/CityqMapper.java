package com.aqi.entity;

import lombok.Data;

import java.util.List;

@Data
public class CityqMapper<T> {
    private long maxPage;
    private List<T> cityMapper;
}
