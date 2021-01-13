package com.aqi.service.imp;

import com.aqi.entity.Aqi;
import com.aqi.mapper.aqi.AqiMapper;
import com.aqi.service.AqiService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AqiServiceImpl extends ServiceImpl<AqiMapper, Aqi> implements AqiService {
    @Override
    public void insertAqi(Aqi aqi) {
        baseMapper.insert(aqi);
    }
}
