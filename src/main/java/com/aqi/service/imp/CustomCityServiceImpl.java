package com.aqi.service.imp;

import com.aqi.entity.City;
import com.aqi.entity.CustomCity;
import com.aqi.entity.CustomCityVo;
import com.aqi.mapper.city.CustomCityMapper;
import com.aqi.service.CityService;
import com.aqi.service.CustomCityService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author lucong
 * @date 2021/6/15 16:49
 */
@Service
public class CustomCityServiceImpl extends ServiceImpl<CustomCityMapper, CustomCity> implements CustomCityService {

    @Autowired
    private CityService cityService;

    @Override
    public List<Map<String, Object>> selectCustomCity(String phone) {
        QueryWrapper<CustomCity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        CustomCity customCity = baseMapper.selectOne(queryWrapper);
        if(customCity != null){
            List<Map<String, Object>> list = new ArrayList<>();
            List<String> citys = Arrays.asList(customCity.getCitys().split(","));
            citys.forEach(cityId->{
                City city = cityService.getCityByUid(Integer.parseInt(cityId));
                Map<String,Object> map = new HashMap<>();
                map.put("name", city.getCity());
                map.put("lat", city.getLat());
                map.put("lon", city.getLon());
                list.add(map);
            });
            return list;
        }
        return null;
    }

    @Override
    public boolean submitCustomCity(CustomCityVo customCityVo) {
        String phone = customCityVo.getPhone();
        QueryWrapper<CustomCity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        CustomCity customCity1 = baseMapper.selectOne(queryWrapper);
        String citys = change(customCityVo.getUids());
        int t = Integer.parseInt(String.valueOf(System.currentTimeMillis() / 1000));
        if(customCity1 != null){
            customCity1.setCitys(citys);
            customCity1.setUpdateTime(t);
            return baseMapper.updateById(customCity1) > 0 ? true : false;
        }
        CustomCity customCity = new CustomCity();
        customCity.setPhone(customCityVo.getPhone());
        customCity.setCitys(citys);
        customCity.setCreateTime(t);
        customCity.setUpdateTime(t);
        return baseMapper.insert(customCity) > 0 ? true : false;
    }

    private String change(List<String> uids){
        StringBuilder sb = new StringBuilder();
        uids.forEach(uid->{
            sb.append(uid);
            sb.append(",");
        });
        return sb.toString().substring(0,sb.toString().length()-1);
    }
}
