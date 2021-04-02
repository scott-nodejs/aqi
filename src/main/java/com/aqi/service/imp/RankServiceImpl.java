package com.aqi.service.imp;

import com.aqi.entity.Rank;
import com.aqi.mapper.city.RankMapper;
import com.aqi.service.RankService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class RankServiceImpl extends ServiceImpl<RankMapper, Rank> implements RankService {
}
