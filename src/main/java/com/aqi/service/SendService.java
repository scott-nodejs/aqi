package com.aqi.service;

import com.aqi.entity.UrlEntity;

public interface SendService {

    boolean send(UrlEntity urlEntity);

    boolean sendCity(UrlEntity urlEntity);
}
