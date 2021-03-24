package com.aqi.configer;

import com.aqi.utils.IPUtils;
import com.aqi.utils.IpParse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class IPInterceptor implements HandlerInterceptor {
    
    @Value("${ip.path}")
    String ipPath;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        IpParse.load(ipPath);
        String ipAddress= IPUtils.getRealIP(request);
        String para = request.getParameterMap().toString();
        String requestURI = request.getRequestURI();
        String[] info = IpParse.find(ipAddress);
        StringBuilder sb = new StringBuilder();
        for(String c : info){
            sb.append(c);
        }
        log.info("USER IP ADDRESS IS =>"+ipAddress+"==>"+sb.toString()+"==>"+requestURI);
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
