package com.aqi.service;

import com.aqi.entity.KeyWord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface KeyWordService extends IService<KeyWord> {
    List<KeyWord> selectList();
}
