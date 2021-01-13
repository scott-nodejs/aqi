package com.aqi.utils.http;


import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * HttpEntity 类型
 *
 * @author wubaoxin
 */
@Getter
@AllArgsConstructor
public enum HttpEntityType {

    ENTITY_STRING(0, "StringEntity"),

    //ENTITY_FILE(1,"FileEntity"),

    ENTITY_BYTES(2, "ByteArrayEntity"),

    // ENTITY_INPUT_STREAM(3,"ENTITY_INPUT_STREAM"),

    // ENTITY_SERIALIZABLE(4,"SerializableEntity"),

    // ENTITY_MULTIPART(5,"MultipartEntity"),

    ENTITY_FORM(6, "UrlEncodedFormEntity");

    private int code;

    private String name;

}
