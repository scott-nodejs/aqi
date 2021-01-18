package com.aqi.service;

import com.aqi.entity.UrlEntity;

public interface SendService {

    boolean send(UrlEntity urlEntity, long times);

    boolean sendCity(String key, UrlEntity urlEntity, long times);
}
