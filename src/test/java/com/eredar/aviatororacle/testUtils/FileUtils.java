package com.eredar.aviatororacle.testUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtils {
    /**
     * 读取资源文件并按行返回
     *
     * @param fileName 文件名
     * @return 包含每一行内容的 List
     */
    public static List<String> readFileAsLines(String fileName) {
        // 使用 ClassLoader 获取资源流，这是读取 resources 目录下最稳妥的方法
        try (InputStream is = FileUtils.class.getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                throw new IllegalArgumentException("文件未找到: " + fileName);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                // Java 8 的 lines() 方法将其转为 Stream，最后收集为 List
                return reader.lines().collect(Collectors.toList());
            }
        } catch (IOException e) {
            throw new RuntimeException("读取文件失败: " + fileName, e);
        }
    }

    /**
     * 读取资源文件并返回整个文件的内容
     *
     * @param fileName 文件名
     * @return 整个文件的内容字符串
     */
    public static String readFileAsString(String fileName) {
        List<String> lines = readFileAsLines(fileName);

        // 使用 Java 8 的 String.join，指定以换行符连接
        return String.join(System.lineSeparator(), lines);
    }
}
