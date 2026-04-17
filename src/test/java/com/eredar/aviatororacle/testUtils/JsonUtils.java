package com.eredar.aviatororacle.testUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;

import java.io.File;
import java.util.TimeZone;

public class JsonUtils {

    private static final ObjectMapper ob;

    static {
        ob = new ObjectMapper();
        // 支持 java.time 包下的日期类型
        ob.registerModule(new JavaTimeModule());
        // 显式设为 UTC 时区
        ob.setTimeZone(TimeZone.getTimeZone("UTC"));
        // 禁止将带时区的时间调整为 ObjectMapper 的时区
        ob.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        // 禁用时间戳
        ob.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 忽略为 null 的字段
        ob.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 金融场景最好开启，增强浮点类型精度
        ob.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
    }

    @SneakyThrows
    public static <T> T readValue(String jsonString, TypeReference<T> type) {
        return ob.readValue(jsonString, type);
    }

    @SneakyThrows
    public static <T> T readValue(String jsonString, Class<T> type) {
        return ob.readValue(jsonString, type);
    }

    @SneakyThrows
    public static <T> T readValue(File file, TypeReference<T> type) {
        return ob.readValue(file, type);
    }

    @SneakyThrows
    public static <T> T treeToValue(JsonNode jsonNode, Class<T> valueType) {
        return ob.treeToValue(jsonNode, valueType);
    }

    @SneakyThrows
    public static String writeValueAsString(Object object) {
        return ob.writeValueAsString(object);
    }

    @SneakyThrows
    public static void writeValue(File file, Object value) {
        ob.writeValue(file, value);
    }

    public static <T> T writeThenRead(Object object, TypeReference<T> type) {
        String jsonString = writeValueAsString(object);
        return readValue(jsonString, type);
    }

    public static <T> T writeThenRead(Object object, Class<T> type) {
        String jsonString = writeValueAsString(object);
        return readValue(jsonString, type);
    }
}
