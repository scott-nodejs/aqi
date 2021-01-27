package com.aqi.service;

import com.aqi.entity.ConsumerAqi;
import com.aqi.entity.UrlEntity;

import java.util.function.Consumer;

public interface SendService {

    boolean send(UrlEntity urlEntity, long times);

    boolean sendCity(String key, UrlEntity urlEntity, long times);

    boolean sendAqiConsumer(String key, ConsumerAqi consumerAqi);
}
