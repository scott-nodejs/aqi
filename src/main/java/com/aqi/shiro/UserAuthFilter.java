package com.aqi.shiro;

import com.aqi.utils.JwtUtil;
import org.apache.shiro.session.Session;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 处理session超时问题拦截器
 * @author lucong
 * @date 2020/01/23
 */
public class UserAuthFilter extends AccessControlFilter {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    public UserAuthFilter(RedisTemplate<String,Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = httpRequest.getHeader("accessToken");
        if(token == null){
            Cookie[] cookies = httpRequest.getCookies();
            Session session = null;
            String username = null;
            if (null != cookies) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("accessToken")) {
//                        session = (Session) redisTemplate.opsForValue().get(RedisKeyPrefix.SYS_USER_SESSION.getPrefix()+":"+cookie.getValue());
                        username = JwtUtil.getUserName(cookie.getValue());
                        break;
                    }
                }
            }
            if(username != null){
                return true;
            }
        }else{
            return true;
        }

        if (isLoginRequest(request, response)) {
            return true;
        }
//        else {
//            Subject subject = getSubject(request, response);
//            return subject.getPrincipal() != null;
//        }
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        HttpServletResponse httpResponse = WebUtils.toHttp(response);

        if (httpRequest.getHeader("X-Requested-With") != null
                && httpRequest.getHeader("X-Requested-With").equalsIgnoreCase("XMLHttpRequest")) {
            httpResponse.sendError(HttpStatus.UNAUTHORIZED.value());
        } else {
            redirectToLogin(request, response);
        }
        return false;
    }
}
