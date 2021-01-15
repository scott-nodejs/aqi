package com.aqi.service;

import com.aqi.entity.Aqi;
import com.aqi.entity.NoResult;
import com.baomidou.mybatisplus.extension.service.IService;

public interface NoResultService extends IService<NoResult> {

    void insertNoResult(NoResult noResult);
}
