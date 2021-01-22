package com.aqi.entity;

import lombok.Data;

import java.util.List;

@Data
public class AqiVo {

    private String name;
    private String type;
    private List<List<Long>> data;
}
