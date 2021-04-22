package com.aqi.service;

import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Map;
import java.util.Set;

public interface RedisService {

    String getString(String key);

    void setString(String key, String obj);

    void hset(String key, String k, String v);

    String hget(String key, String k);

    void zadd(String key, double score);

    Set<String> zget(int rank);

    Set<ZSetOperations.TypedTuple<Object>> zgetByScore(int rank, int type);

    void addGeo(Map<String, Point> points);

    String getLastestPoint(double lng, double lat);
}
