package com.aqi.service.imp;

import com.aqi.entity.Area;
import com.aqi.entity.NoResult;
import com.aqi.mapper.city.AreaMapper;
import com.aqi.service.AreaService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements AreaService {

    @Override
    public void insertArea(Area area) {
        baseMapper.insert(area);
    }

    @Override
    public Area getAreaByUid(int uid) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("uid",uid);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public List<Area> getArea(long currentTime) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.lt("vtime", currentTime);
        queryWrapper.eq("is_update", 0);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public void updateTime(long vtime) {
        baseMapper.updateTime(vtime);
    }

    @Override
    public List<NoResult> selectByNoResult() {
        return baseMapper.selectByNoResult();
    }
}
