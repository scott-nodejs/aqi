package com.aqi.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 *
 * @author leamo
 * @date 2016年12月19日 下午3:43:11
 */
public class JwtUtil {
    private static final String ISSUER = "aqi";
    private static final String SECRET = "202cb962ac59075b964b07152d234b70";
    private static Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    /**
     * 生成JWT
     *
     * @param username
     * @return
     * @author leamo
     * @date 2016年12月19日 下午3:54:19
     */
    public static String create(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        return create(SECRET, 6, claims);
    }

    public static String create(String secret, int maxHours, Map<String, Object> claims) {
        String jwt = null;
        try {
            Builder builder = JWT.create().withIssuer(ISSUER).withIssuedAt(new Date())
                    .withExpiresAt(DateUtil.addHour(maxHours));
            if (claims != null && !claims.isEmpty()) {
                for (Map.Entry<String, Object> entry : claims.entrySet()) {
                    builder.withClaim(entry.getKey(),(String)entry.getValue());
                }
            }
            jwt = builder.sign(Algorithm.HMAC256(secret));
        } catch (Exception e) {
            logger.error("生成jwt失败：secret={}, maxHours={}, claims={}", secret, maxHours, claims);
        }
        return jwt;
    }

    public static void main(String[] args) {
//        System.out.println(new DateTime().plus(1 * 60 * 60 * 1000).toDate());
        System.out.println(isDie("eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJDZWxMb3VkIiwiZXhwIjoxNTgxODExMjM3LCJpYXQiOjE1NDU4MTEyMzcsInVzZXJuYW1lIjoic3Vud2QifQ.n9ikdh36aYetABM27SJxv3QP0qw3UMBOQxF8zTRYwFE", "bh0nXGzcjxpV1PZxvPqFww=="));
    }

    /**
     * @param key
     * @param val
     * @param time
     * @return
     * @description 自定义key, val, 过期时间
     * @author miaoqi
     * @date 2017年6月5日 下午3:30:34
     */
    public static String create(String key, String val, Long time) {
        String jwt = null;
        try {
            jwt = JWT.create().withIssuer(ISSUER).withIssuedAt(new Date())
                    .withExpiresAt(new DateTime().plus(time).toDate()).withClaim("accessKey", key)
                    .sign(Algorithm.HMAC256(val));
        } catch (Exception e) {
            logger.error("生成JWT失败！", e);
        }
        return jwt;
    }

    /**
     * 解析JWT
     *
     * @param jwt
     * @return
     * @author leamo
     * @date 2016年12月19日 下午4:07:30
     */
    public static DecodedJWT decode(String jwt) {
        return decode(jwt, SECRET);
    }

    /**
     * 解析jwt
     *
     * @param jwt
     * @param secret
     * @return
     */
    public static DecodedJWT decode(String jwt, String secret) {
        DecodedJWT decodedJWT = null;
        try {
            decodedJWT = JWT.require(Algorithm.HMAC256(secret)).withIssuer(ISSUER).acceptLeeway(3).build().verify(jwt);
        } catch (Exception e) {
            logger.error("解析JWT失败！");
        }
        return decodedJWT;
    }

    /**
     * @param jwt
     * @return
     * @Description 获取JWT中的username
     * @author lin
     * @date 2017年4月25日 下午4:36:42
     */
    public static String getUserName(String jwt) {
        String username = null;
        try {
            username = StringUtils.isBlank(jwt) ? null : JWT.decode(jwt).getClaim("username").asString();
        } catch (Exception e) {
            logger.error("获取username失败:{}", jwt);
        }
        return username;
    }

    public static String getVal(String jwt, String key) {
        String value = null;
        try {
            value = StringUtils.isBlank(jwt) ? null : JWT.decode(jwt).getClaim(key).asString();
        } catch (Exception e) {
            logger.error("获取Claim失败:key={},jwt={}", key, jwt);
        }
        return value;
    }

    /**
     * 验证jwt是否有效
     *
     * @param jwt
     * @return
     * @author leamo
     * @date 2017年1月4日 下午12:40:03
     */
    public static boolean isDie(String jwt) {
        return isDie(jwt, SECRET);
    }

    /**
     * 验证jwt是否有效
     *
     * @param jwt
     * @return
     * @author sun8wd
     * @date 2017年7月3日下午5:25:16
     */
    public static boolean isDie(String jwt, String secret) {
        if (jwt == null)
            return true;
        DecodedJWT decodedJWT = decode(jwt, secret);
        if (decodedJWT == null)
            return true;
        Date expireDate = decodedJWT.getExpiresAt();
        return expireDate.before(new Date());
    }

    /**
     * 刷新JWT
     *
     * @param jwt
     * @return
     * @author leamo
     * @date 2016年12月19日 下午4:35:42
     */
    public static String refresh(String jwt) {
        if (!isDie(jwt)) {
            return create(decode(jwt).getClaim("username").asString());
        }
        return null;
    }


}
