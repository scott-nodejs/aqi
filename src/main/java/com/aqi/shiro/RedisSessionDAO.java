package com.aqi.shiro;

import com.aqi.service.RedisService;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;

@Component
public class RedisSessionDAO extends EnterpriseCacheSessionDAO {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisService redisService;

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = super.doCreate(session);
        redisTemplate.opsForValue().set(getSessionKey(sessionId), session.toString());

        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        Session session = super.doReadSession(sessionId);
        try{
            if (session == null) {
                String sessionKey = getSessionKey(sessionId);
                session = (Session) redisTemplate.opsForValue().get(getSessionKey(sessionId));
            }
        }catch (Exception e){

        }


        return session;
    }

    @Override
    protected void doUpdate(Session session) {
        super.doUpdate(session);
        redisTemplate.opsForValue().set(getSessionKey(session.getId()), session.toString());
    }

    @Override
    protected void doDelete(Session session) {
        super.doDelete(session);
        redisTemplate.delete(getSessionKey(session.getId().toString()));
    }

    private String getSessionKey(Serializable sessionId) {
        return "aqi:session:"+sessionId.toString();
    }

}
