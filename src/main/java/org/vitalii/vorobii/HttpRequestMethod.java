package org.vitalii.vorobii;

public enum HttpRequestMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    HEAD("HEAD"),
    OPTIONS("OPTIONS");

    private final String code;

    HttpRequestMethod(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
