package com.aqi.service.imp;

import com.aqi.entity.NoResult;
import com.aqi.entity.NoResultVo;
import com.aqi.mapper.city.NoResultMapper;
import com.aqi.service.AreaService;
import com.aqi.service.NoResultService;
import com.aqi.utils.TimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NoResultServiceImpl extends ServiceImpl<NoResultMapper, NoResult> implements NoResultService {

    @Autowired
    private AreaService areaService;

    @Override
    public void insertNoResult(NoResult noResult) {
        baseMapper.insert(noResult);
    }

    @Override
    public NoResultVo selectNoResult(int cityId) {
        QueryWrapper<NoResult> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", cityId);
        queryWrapper.eq("c_type", 1);
        queryWrapper.orderByAsc("vtime");
        List<NoResult> noResults = baseMapper.selectList(queryWrapper);
        Map<Integer,Integer> map = areaService.getAreaCount();
        int count = map.get(cityId);
        List<String> x = noResults.stream().map(noResult -> TimeUtil.convertMillisToString(Long.valueOf(noResult.getVtime() + "000"))).collect(Collectors.toList());
        Map<String,Object> no = new HashMap<>();
        List<Integer> noList = noResults.stream().map(noResult -> noResult.getNoCount()).collect(Collectors.toList());
        no.put("name","没有获取到的区域");
        no.put("data", noList);
        Map<String,Object> complete = new HashMap<>();
        List<Integer> completeList = noResults.stream().map(noResult -> count - noResult.getNoCount()).collect(Collectors.toList());
        complete.put("name","已获取到的区域");
        complete.put("data", completeList);
        NoResultVo noResultVo = new NoResultVo();
        List<Map<String,Object>> data = new ArrayList<>();
        data.add(no);
        data.add(complete);
        noResultVo.setData(data);
        noResultVo.setX(x);
        return noResultVo;
    }
}
