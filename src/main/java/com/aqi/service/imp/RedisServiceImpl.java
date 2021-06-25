package com.aqi.service.imp;

import com.aqi.global.GlobalConstant;
import com.aqi.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.BoundGeoOperations;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate redisTemplate;

    final String GEO_KEY = "cities:locs";

    @Override
    public String getString(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    @Override
    public void setString(String key, String obj) {
        redisTemplate.opsForValue().set(key, obj,7, TimeUnit.DAYS);
    }

    @Override
    public void hset(String key, String k, String v) {
        redisTemplate.opsForHash().put(key,k,v);
        redisTemplate.expire(key, 1, TimeUnit.DAYS);
    }

    @Override
    public String hget(String key, String k) {
        return (String) redisTemplate.opsForHash().get(key, k);
    }

    @Override
    public void zadd(String key, double score) {
        redisTemplate.opsForZSet().add(GlobalConstant.RANK, key, score);
    }

    @Override
    public Set<String> zget(int rank) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(GlobalConstant.RANK, 0, rank);
    }

    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    @Override
    public Set<ZSetOperations.TypedTuple<Object>> zgetByScore(int rank, int type) {
        if(type == 0)
            return redisTemplate.opsForZSet().reverseRangeWithScores(GlobalConstant.RANK, 0, rank);
        else
            return redisTemplate.opsForZSet().rangeWithScores(GlobalConstant.RANK, 0, rank);
    }

    @Override
    public void addGeo(Map<String, Point> points) {
        BoundGeoOperations<String, String> geoOps = redisTemplate.boundGeoOps(GEO_KEY);
        geoOps.add(points);
    }

    @Override
    public String getLastestPoint(double lng, double lat) {
        Point point = new Point(lng, lat);
        Metric metric = RedisGeoCommands.DistanceUnit.KILOMETERS;
        Distance distance = new Distance(10, metric);
        Circle circle = new Circle(point, distance);
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands
                .GeoRadiusCommandArgs
                .newGeoRadiusArgs()
                .includeDistance()
                .includeCoordinates()
                .sortAscending()
                .limit(1);
        GeoResults<RedisGeoCommands.GeoLocation<String>> radius = redisTemplate.opsForGeo()
                .radius(GEO_KEY, circle, args);
        if (radius != null) {
            return radius.getContent().get(0).getContent().getName();
        }
        return "";
    }

    @Override
    public List<String> hgetAll(String key) {
        return redisTemplate.opsForHash().values(key);
    }
}
