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

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate redisTemplate;

    final String GEO_KEY = "cities:locs";

    @Override
    public Object getString(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void setString(String key, Object obj) {
        redisTemplate.opsForValue().set(key, obj);
    }

    @Override
    public void zadd(String key, double score) {
        redisTemplate.opsForZSet().add(GlobalConstant.RANK, key, score);
    }

    @Override
    public Set<String> zget(int rank) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(GlobalConstant.RANK, 0, rank);
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
}
