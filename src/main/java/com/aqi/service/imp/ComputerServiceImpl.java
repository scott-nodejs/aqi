package com.aqi.service.imp;

import com.aqi.configer.exception.ResultException;
import com.aqi.entity.Aqi;
import com.aqi.entity.AqiVo;
import com.aqi.entity.Area;
import com.aqi.entity.Computer;
import com.aqi.global.GlobalConstant;
import com.aqi.mapper.city.ComputerMapper;
import com.aqi.service.AqiService;
import com.aqi.service.AreaService;
import com.aqi.service.ComputerService;
import com.aqi.utils.TimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ComputerServiceImpl extends ServiceImpl<ComputerMapper, Computer> implements ComputerService {
    @Autowired
    private AqiService aqiService;

    @Autowired
    private AreaService areaService;

    @Override
    public void compute(int uid) {

    }

    @Override
    public void computeBatch(List<Integer> uids) {

    }

    @Override
    public Computer computeByVo(AqiVo aqiVo, int type) {
        if(aqiVo == null){
            throw new ResultException(500, "计算对象为空");
        }
        List<List<Long>> data = aqiVo.getData();
        int goodcount = 0;
        int justsosocount = 0;
        int lightcount = 0;
        int zcount = 0;
        int badcount = 0;
        int badestcount = 0;
        int totalhours = 0;
        for(List<Long> d : data){
            int aqi = aqiService.getFitAqi(Integer.parseInt(String.valueOf(d.get(1))), type);
            totalhours++;
            if(aqi > 0 && aqi <= 50){
                goodcount++;
            }else if(aqi >50 && aqi <= 100){
                justsosocount++;
            }else if(aqi >100 && aqi <= 150){
                lightcount++;
            }else if(aqi >150 && aqi <= 200){
                zcount++;
            }else if(aqi >200 && aqi <= 300){
                badcount++;
            }else{
                badestcount++;
            }
        }
        Computer computer = new Computer();
        computer.setBadcount(badcount);
        computer.setBadestcount(badestcount);
        computer.setGoodcount(goodcount);
        computer.setJustsosocount(justsosocount);
        computer.setLightcount(lightcount);
        computer.setZcount(zcount);
        computer.setName(aqiVo.getName());
        computer.setTotalhours(totalhours);
        return computer;
    }

    @Override
    public Computer computeByAqis(List<Aqi> aqis, int type) {
        int goodcount = 0;
        int justsosocount = 0;
        int lightcount = 0;
        int zcount = 0;
        int badcount = 0;
        int badestcount = 0;
        int totalhours = 0;
        for(Aqi d : aqis){
            int aqi = aqiService.getFitAqi(d.getAqi(), type);
            totalhours++;
            if(aqi > 0 && aqi <= 50){
                goodcount++;
            }else if(aqi >50 && aqi <= 100){
                justsosocount++;
            }else if(aqi >100 && aqi <= 150){
                lightcount++;
            }else if(aqi >150 && aqi <= 200){
                zcount++;
            }else if(aqi >200 && aqi <= 300){
                badcount++;
            }else{
                badestcount++;
            }
        }
        Computer computer = new Computer();
        computer.setBadcount(badcount);
        computer.setBadestcount(badestcount);
        computer.setGoodcount(goodcount);
        computer.setJustsosocount(justsosocount);
        computer.setLightcount(lightcount);
        computer.setZcount(zcount);
        computer.setTotalhours(totalhours);
        return computer;
    }

    @Override
    public List<Computer> computeBatchByList(List<AqiVo> list, int type) {
        List<Computer> computers = new ArrayList<>();
        list.forEach(d->{
            computers.add(this.computeByVo(d, type));
        });
        return computers;
    }

    @Override
    public float average(List<Aqi> aqis) {
        try{
            List<Integer> values = aqis.stream().map(aqi -> aqi.getAqi()).collect(Collectors.toList());
            Integer max = Collections.max(values);
            Integer min = Collections.min(values);
            int reduce = values.stream().filter(v -> (v != max || v != min)).reduce((i, j) -> i + j).get();
            return (float) (reduce*1.0 / (aqis.size()-2));
        }catch (Exception e){
            log.error("求平均值出错了: ", e);
            return 0;
        }
    }

    @Override
    public boolean cronComputer(int cityId, int type, String tmp) {
        try{
            int start = (int) (TimeUtil.convertStringToMillis(tmp+ "-01 00:00:00") / 1000);
            int end = this.getMouthEndTime(tmp);
            List<Area> areaListByPerantId = areaService.getAreaListByPerantId(cityId);
            areaListByPerantId.forEach(area -> {
                QueryWrapper<Aqi> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("uid", area.getUid());
                queryWrapper.between("vtime", start, end);
                List<Aqi> aqis = aqiService.list(queryWrapper);
                if(aqis.size() != 0){
                    Computer computer = this.computeByAqis(aqis, type);
                    float avg = this.average(aqis);
                    String uuid = start + "_" + area.getId();
                    computer.setAvg(avg);
                    String name;
                    int s = area.getName().indexOf("(");
                    int e = area.getName().indexOf(")");
                    if(s != -1 && e != -1){
                        name = area.getName().substring(s+1,e);
                    }else{
                        name = area.getName();
                    }
                    computer.setName(name);
                    computer.setMouthTime(tmp);
                    computer.setPId(cityId);
                    computer.setUid(area.getId());
                    computer.setUuid(uuid);
                    long cur = System.currentTimeMillis() / 1000;
                    if(cur > end){
                        baseMapper.insert(computer);
                    }else{
                        Computer computer1 = baseMapper.selectById(uuid);
                        if(computer1 != null){
                            baseMapper.updateById(computer);
                        }else{
                            baseMapper.insert(computer);
                        }
                    }
                }
            });
            return true;
        }catch (Exception e){
            log.error("月份异常: ", e);
            return false;
        }
    }

    @Override
    public int computeByRank(List<Aqi> aqis, int type) {
        Computer computer = this.computeByAqis(aqis, type);
        int good = computer.getGoodcount() * GlobalConstant.GOOD_PARA;
        int justsoso = computer.getJustsosocount() * GlobalConstant.JUSTSOSO_PARA;
        int light = computer.getLightcount() * GlobalConstant.LIGHT_PARA;
        int z = computer.getZcount() * GlobalConstant.Z_PARA;
        int bad = computer.getBadestcount() * GlobalConstant.BAD_PARA;
        int badest = computer.getBadestcount() * GlobalConstant.BADEST_PARA;
        return (good + justsoso + light + z + bad + badest);
    }

    public int getMouthEndTime(String tmp){
        int end = 0;
        String[] tmps = tmp.split("-");
        if(tmps.length == 2){
            int tmp1 = Integer.parseInt(tmps[1]);
            int year = Integer.parseInt(tmps[0]);
            if(tmp1 == 2 && year % 4 == 0){
                end = (int) (TimeUtil.convertStringToMillis(tmp+ "-29 23:59:59") / 1000);
            }
            if(tmp1 == 2 && year % 4 != 0){
                end = (int) (TimeUtil.convertStringToMillis(tmp+ "-28 23:59:59") / 1000);
            }
            if("1,3,5,7,8,10,12".contains(String.valueOf(tmp1))){
                end = (int) (TimeUtil.convertStringToMillis(tmp+ "-31 23:59:59") / 1000);
            }
            if("4,6,9,11".contains(String.valueOf(tmp1))){
                end = (int) (TimeUtil.convertStringToMillis(tmp+ "-30 23:59:59") / 1000);
            }
        }
        return end;
    }
}
