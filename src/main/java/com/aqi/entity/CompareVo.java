package com.aqi.entity;

import lombok.Data;

import java.util.List;

@Data
public class CompareVo {
    List<AqiVo> aqiVos;
    List<Computer> computers;
}
