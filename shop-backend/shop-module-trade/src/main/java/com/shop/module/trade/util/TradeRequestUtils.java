package com.shop.module.trade.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public final class TradeRequestUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private TradeRequestUtils() {
    }

    public static Map<String, Object> parse(String rawBody, Map<String, Object> params) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (params != null) {
            result.putAll(params);
        }
        if (rawBody == null || rawBody.isBlank()) {
            return result;
        }
        String body = rawBody.trim();
        try {
            if (body.startsWith("{")) {
                result.putAll(OBJECT_MAPPER.readValue(body, new TypeReference<Map<String, Object>>() {
                }));
            } else {
                for (String pair : body.split("&")) {
                    if (pair.isBlank()) {
                        continue;
                    }
                    String[] parts = pair.split("=", 2);
                    String key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
                    String value = parts.length > 1
                            ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8)
                            : "";
                    result.put(key, value);
                }
            }
        } catch (Exception ignored) {
            return result;
        }
        return result;
    }

    public static Long getLong(Map<String, Object> body, String key, Long defaultValue) {
        Object value = body == null ? null : body.get(key);
        if (value == null || value.toString().isBlank()) {
            return defaultValue;
        }
        return Long.parseLong(value.toString());
    }

    public static Integer getInt(Map<String, Object> body, String key, Integer defaultValue) {
        Object value = body == null ? null : body.get(key);
        if (value == null || value.toString().isBlank()) {
            return defaultValue;
        }
        return Integer.parseInt(value.toString());
    }

    public static String getString(Map<String, Object> body, String key, String defaultValue) {
        Object value = body == null ? null : body.get(key);
        if (value == null) {
            return defaultValue;
        }
        return value.toString();
    }
}
