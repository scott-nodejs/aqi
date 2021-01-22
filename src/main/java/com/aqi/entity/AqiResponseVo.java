package com.aqi.entity;

import lombok.Data;

import java.util.List;

@Data
public class AqiResponseVo {
    private List<Integer> x;
    private List<AqiVo> aqis;
}
