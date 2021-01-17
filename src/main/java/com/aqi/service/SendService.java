package com.aqi.service;

import com.aqi.entity.UrlEntity;

public interface SendService {

    boolean send(UrlEntity urlEntity, long times);

    boolean sendCity(UrlEntity urlEntity, long times);
}
