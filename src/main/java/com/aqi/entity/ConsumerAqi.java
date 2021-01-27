package com.aqi.entity;
import lombok.Data;

@Data
public class ConsumerAqi {
    private AqiResult.Aqi aqi;
    private City city;
    private Area area;
}
