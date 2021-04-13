package com.aqi.configer;

import com.alibaba.fastjson.JSONObject;
import com.aqi.utils.IPUtils;
import com.aqi.utils.IpParse;
import com.aqi.utils.LogfileName;
import com.aqi.utils.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
@Slf4j
public class IPInterceptor implements HandlerInterceptor {

    Logger iplogger = LoggerUtils.Logger(LogfileName.BIZ_IP);
    
    @Value("${ip.path}")
    String ipPath;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        IpParse.load(ipPath);
        String ipAddress= IPUtils.getRealIP(request);
        Map<String, String[]> parameterMap = request.getParameterMap();
        String para = JSONObject.toJSONString(parameterMap);
        String requestURI = request.getRequestURI();
        String[] info = IpParse.find(ipAddress);
        StringBuilder sb = new StringBuilder();
        for(String c : info){
            sb.append(c);
        }
        String str = sb.toString();
        String reg = "[^\u4e00-\u9fa5]";
        str = str.replaceAll(reg, "");
        iplogger.info("USER IP ADDRESS IS =>ip地址："+ipAddress+",来源: "+str+"=>"+requestURI+"=>"+para);
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
