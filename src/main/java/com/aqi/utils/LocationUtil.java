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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class LocationUtil {
    private static Logger locationLog = LoggerUtils.Logger(LogfileName.BIZ_LOCATION);

    private static String url = "http://restapi.amap.com/v3/geocode/regeo?location=%s&key=85d26eb3c42bbc873a1be277028e5298&radius=1000&extensions=all";

    public static Map<String,String> getLocationBylanlng(String location) throws IOException {
        Map<String,String> locationMap = new HashMap<>();
        HttpRequestConfig config = HttpRequestConfig.create().url(String.format(url,location));
        HttpRequestResult result = HttpUtils.get(config);
        AmapResponse res = JSON.parseObject(result.getResponseText(), AmapResponse.class);
        AmapResponse.Regeocode regeocode = res.getRegeocode();
        locationLog.info("定位的位置: " + regeocode.getFormatted_address());
        Location addressComponent = regeocode.getAddressComponent();
        Location.StreetNumber streetNumber = addressComponent.getStreetNumber();
        String district = addressComponent.getDistrict();
        String street = streetNumber.getStreet();
        String loc = district + " " + street;
        String name = addressComponent.getCity();
        if(name.equals("[]")){
            int start = regeocode.getFormatted_address().indexOf("省");
            int end = regeocode.getFormatted_address().indexOf("市");
            if(start == -1){
                name = regeocode.getFormatted_address().substring(0,end);
            }else if(start != -1 && end != -1){
                name = regeocode.getFormatted_address().substring(start+1,end);
            }
        }
        locationMap.put("city", name);
        locationMap.put("loc", loc);
        return locationMap;
    }
}
