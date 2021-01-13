package com.aqi.service.imp;

import com.aqi.entity.KeyWord;
import com.aqi.mapper.city.KeyWordMapper;
import com.aqi.service.KeyWordService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeyWordServiceImpl extends ServiceImpl<KeyWordMapper, KeyWord> implements KeyWordService {

    @Override
    public List<KeyWord> selectList() {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("state",0);
        List<KeyWord> list = baseMapper.selectList(queryWrapper);
        return list;
    }
}
