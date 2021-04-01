package com.aqi.service;

import com.aqi.entity.Aqi;
import com.aqi.entity.AqiVo;
import com.aqi.entity.Computer;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ComputerService extends IService<Computer> {
    void compute(int uid);

    void computeBatch(List<Integer> uids);

    Computer computeByVo(AqiVo aqiVo, int type);

    Computer computeByAqis(List<Aqi> aqis, int type);

    List<Computer> computeBatchByList(List<AqiVo> list, int type);

    float average(List<Aqi> aqis);

    boolean cronComputer(int cityId, int type, String tmp);

    int computeByRank(List<Aqi> aqis, int type);
}
