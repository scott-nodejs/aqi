package com.aqi.service;

import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

public interface RedisService {

    Object getString(String key);

    void setString(String key, Object obj);

    void zadd(String key, double score);

    Set<String> zget(int rank);

    Set<ZSetOperations.TypedTuple<Object>> zgetByScore(int rank, int type);
}
