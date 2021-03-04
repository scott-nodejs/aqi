package com.aqi.service.imp;

import com.aqi.entity.NoCityArea;
import com.aqi.mapper.city.NoCityAreaMapper;
import com.aqi.service.NoCityAreaService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class NoCityAreaServiceImpl extends ServiceImpl<NoCityAreaMapper, NoCityArea> implements NoCityAreaService {
}
