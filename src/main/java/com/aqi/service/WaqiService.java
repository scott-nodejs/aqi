package com.aqi.service;

import com.aqi.entity.UrlEntity;
import com.aqi.entity.Waqi;
import com.baomidou.mybatisplus.extension.service.IService;

public interface WaqiService extends IService<Waqi> {
    void consumerRand(UrlEntity urlEntity);
}
