package com.aqi.service.imp;

import com.aqi.entity.Aqi;
import com.aqi.entity.AqiResult;
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

    @Override
    public void updateAqi(AqiResult.Aqi aqi){
        try{
            Aqi aqi1 = new Aqi();
            if(aqi.getAqi() instanceof String){
                aqi1.setAqi(0);
            }else if(aqi.getAqi() instanceof Integer){
                aqi1.setAqi((Integer) aqi.getAqi());
            }
            aqi1.setUid(aqi.getIdx());
            int tmp = (Integer) aqi.getTime().get("v") - 8*60*60;
            String uuid = tmp+"_"+aqi.getIdx();
            aqi1.setUuid(uuid);
            aqi1.setUrl(aqi.getCity().getUrl());

            aqi1.setCo(String.valueOf(aqi.getIaqi().getCo() == null ?"0":aqi.getIaqi().getCo().get("v")));
            aqi1.setDew(String.valueOf(aqi.getIaqi().getDew() == null ?"0":aqi.getIaqi().getDew().get("v")));
            aqi1.setH(String.valueOf(aqi.getIaqi().getH() == null ?"0":aqi.getIaqi().getH().get("v")));
            aqi1.setNo2(String.valueOf(aqi.getIaqi().getNo2() == null ?"0":aqi.getIaqi().getNo2().get("v")));
            aqi1.setO3(String.valueOf(aqi.getIaqi().getO3() == null ?"0":aqi.getIaqi().getO3().get("v")));
            aqi1.setP(String.valueOf(aqi.getIaqi().getP() == null ?"0":aqi.getIaqi().getP().get("v")));
            aqi1.setPm10(String.valueOf(aqi.getIaqi().getPm10() == null ?"0":aqi.getIaqi().getPm10().get("v")));
            aqi1.setPm25(String.valueOf(aqi.getIaqi().getPm25() == null ?"0":aqi.getIaqi().getPm25().get("v")));
            aqi1.setSo2(String.valueOf(aqi.getIaqi().getSo2() == null ?"0":aqi.getIaqi().getSo2().get("v")));
            aqi1.setT(String.valueOf(aqi.getIaqi().getT() == null ?"0":aqi.getIaqi().getT().get("v")));
            aqi1.setW(String.valueOf(aqi.getIaqi().getW() == null ?"0":aqi.getIaqi().getW().get("v")));
            aqi1.setWg(String.valueOf(aqi.getIaqi().getWg() == null ?"0":aqi.getIaqi().getWg().get("v")));

            aqi1.setVtime(tmp);
            aqi1.setFtime((String) aqi.getTime().get("s"));
            this.insertAqi(aqi1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
