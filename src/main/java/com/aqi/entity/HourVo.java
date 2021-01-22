package com.aqi.entity;

import lombok.Data;

import java.util.List;

@Data
public class HourVo {
    private PieNode pie;
    private ReactNode react;

    @Data
    public static class ReactNode{
        private List<String> colors;
        private List<List<Object>> react;
    }

    @Data
    public static class PieNode{
        private List<String> colors;
        private List<DayTotalVo> pie;
    }
}


