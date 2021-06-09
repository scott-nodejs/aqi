package com.aqi.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aqi.configer.exception.ResultException;
import com.aqi.entity.CodeDetail;
import com.aqi.entity.UserVo;
import com.aqi.mapper.aqi.CodeMapper;
import com.aqi.service.RedisService;
import com.aqi.service.SmsService;
import com.aqi.utils.SmsUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class SmsServiceImpl extends ServiceImpl<CodeMapper, CodeDetail> implements SmsService {

    @Autowired
    private RedisService redisService;

    @Override
    public void smsSend(String phone) {
        String code = genSms();
        Map<String,String> map = new HashMap<>();
        map.put("code", code);
        String param = JSONObject.toJSONString(map);
        //String common = SmsUtils.common(phone, param);
//        if(common == null){
//            throw new ResultException(405,"发送失败，请联系工作人员");
//        }
//        CodeDetail codeDetail = JSON.parseObject(common, CodeDetail.class);
//        codeDetail.setYzCode(code);
//        codeDetail.setPhone(phone);
//        codeDetail.setCreateTimeStamp(System.currentTimeMillis() / 1000);
//        baseMapper.insert(codeDetail);
//        if(!"OK".equals(codeDetail.getCode())){
//            throw new ResultException(500, "验证码发送失败");
//        }
        redisService.setString(phone, code);
        redisService.expire(phone,30*60);
    }

    public String genSms(){
        long timeTmp = System.currentTimeMillis();
        String prefix = String.valueOf(timeTmp).substring(String.valueOf(timeTmp).length()-5);
        Random random = new Random();
        int i = random.nextInt(10);
        return prefix + i;
    }
}
