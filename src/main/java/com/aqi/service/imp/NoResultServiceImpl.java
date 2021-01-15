package com.aqi.service.imp;

import com.aqi.entity.NoResult;
import com.aqi.mapper.city.NoResultMapper;
import com.aqi.service.NoResultService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class NoResultServiceImpl extends ServiceImpl<NoResultMapper, NoResult> implements NoResultService {
    @Override
    public void insertNoResult(NoResult noResult) {
        baseMapper.insert(noResult);
    }
}
