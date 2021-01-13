package com.aqi.utils.http;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wubaoxin
 * http 请求动词
 */
@AllArgsConstructor
@Getter
public enum HttpRequestMethod {

    GET(0, "GET"),

    POST(1, "POST"),

    HEAD(2, "HEAD"),

    PUT(3, "PUT"),

    DELETE(4, "DELETE"),

    TRACE(5, "TRACE"),

    PATCH(6, "PATCH"),

    OPTIONS(7, "OPTIONS");

    private int code;

    private String name;

}
