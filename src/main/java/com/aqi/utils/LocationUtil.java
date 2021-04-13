package com.aqi.utils;

import com.alibaba.fastjson.JSON;
import com.aqi.entity.AmapResponse;
import com.aqi.entity.AqiResult;
import com.aqi.entity.Location;
import com.aqi.utils.http.HttpRequestConfig;
import com.aqi.utils.http.HttpRequestResult;
import com.aqi.utils.http.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LocationUtil {
    private static Logger locationLog = LoggerUtils.Logger(LogfileName.BIZ_LOCATION);

    private static String url = "http://restapi.amap.com/v3/geocode/regeo?location=%s&key=85d26eb3c42bbc873a1be277028e5298&radius=1000&extensions=all";

    public static String getLocationBylanlng(String location) throws IOException {
        HttpRequestConfig config = HttpRequestConfig.create().url(String.format(url,location));
        HttpRequestResult result = HttpUtils.get(config);
        AmapResponse res = JSON.parseObject(result.getResponseText(), AmapResponse.class);
        AmapResponse.Regeocode regeocode = res.getRegeocode();
        locationLog.info("定位的位置: " + regeocode.getFormatted_address());
        Location addressComponent = regeocode.getAddressComponent();
        return addressComponent.getCity();
    }
}
