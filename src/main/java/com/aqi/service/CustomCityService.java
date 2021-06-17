package com.aqi.service;

import com.aqi.entity.CustomCity;
import com.aqi.entity.CustomCityVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @author lucong
 * @date 2021/6/15 16:47
 */
public interface CustomCityService extends IService<CustomCity> {

    List<Map<String, Object>> selectCustomCity(String phone);

    boolean submitCustomCity(CustomCityVo customCityVo);
}
