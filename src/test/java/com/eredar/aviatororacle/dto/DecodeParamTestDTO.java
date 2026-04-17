package com.eredar.aviatororacle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DecodeParamTestDTO {

    // 表达式
    private String expression;
    // 参数集合
    private HashMap<String, Object> env;
    // 期望值
    private String expected;
}
