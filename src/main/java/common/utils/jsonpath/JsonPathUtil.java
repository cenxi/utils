package common.utils.jsonpath;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * JsonPath工具类：提取多个目标节点并保留原始层级结构（Java 8兼容）
 * 包名：com.cx.demo
 * 类名：JsonPathUtil
 */
@Slf4j
public class JsonPathUtil {

    /**
     * 提取多个目标节点并保留原始层级结构，保证字段正确合并到对应父层级
     *
     * @param targetPaths 目标节点的JsonPath路径列表（如 [$.user.basicInfo.name, $.user.basicInfo.contacts.friends[*].name]）
     * @param json        原始JSON字符串
     * @return 包含所有目标节点、层级正确的JSON字符串
     */
    public static String retainAllParentNodes(List<String> targetPaths, String json) {
        // 1. 入参校验
        if (targetPaths == null || targetPaths.isEmpty()) {
            return json;
        }
        if (json == null || json.trim().isEmpty()) {
            return "{}";
        }

        try {
            // 提前解析一次，校验JSON格式
            JsonPath.parse(json);
        } catch (InvalidJsonException e) {
            log.error("JSON格式不合法",e);
            return json; // 或抛出自定义异常
        }

        // 正则：匹配数组操作符（[*]/[0]/[?()]）
        Pattern arrayPattern = Pattern.compile("\\[.*?\\]");
        // 正则：拆分路径为「父路径」和「目标字段」
        Pattern fieldPattern = Pattern.compile("(.*?)\\.([^.]+)$");

        // 2. 构建根Map（存储最终结果）
        Map<String, Object> rootMap = new HashMap<>();
        DocumentContext doc = JsonPath.using(Configuration.defaultConfiguration()).parse(json);

        // 3. 遍历每个目标路径处理
        for (String targetPath : targetPaths) {
            if (targetPath == null || targetPath.trim().isEmpty()) {
                continue;
            }

            // ===== 步骤1：解析路径，区分数组/非数组场景 =====
            java.util.regex.Matcher fieldMatcher = fieldPattern.matcher(targetPath);
            String pureFullPath = arrayPattern.matcher(targetPath).replaceAll(""); // 纯路径（无数组操作符）
            String parentPathForBuild = ""; // 用于构建层级的父路径
            String targetField = "";
            boolean isArrayPath = targetPath.contains("[");

            if (fieldMatcher.find()) {
                targetField = fieldMatcher.group(2); // 目标字段（如 name）
                // 提取用于构建层级的父路径（去掉数组操作符）
                parentPathForBuild = arrayPattern.matcher(fieldMatcher.group(1)).replaceAll("");
            } else {
                // 无字段后缀（如 $.user），父路径为空，目标字段为最后一级
                parentPathForBuild = "";
                targetField = pureFullPath.replace("$.", "");
            }

            // ===== 步骤2：提取目标值 =====
            Object finalValue;
            try {
                if (isArrayPath && !targetField.isEmpty()) {
                    // 数组路径：提取数组并过滤元素（只保留目标字段）
                    final String finalTargetField = targetField;
                    List<Map<String, Object>> originalArray = doc.read(fieldMatcher.group(1));
                    finalValue = originalArray.stream()
                            .map(item -> {
                                Map<String, Object> filteredItem = new HashMap<>();
                                if (item.containsKey(finalTargetField)) {
                                    filteredItem.put(finalTargetField, item.get(finalTargetField));
                                }
                                return filteredItem;
                            })
                            .collect(Collectors.toList());
                } else {
                    // 非数组路径：直接提取值
                    finalValue = doc.read(targetPath);
                }
            } catch (Exception e) {
                System.err.println("路径[" + targetPath + "]解析失败：" + e.getMessage());
                continue;
            }

            // ===== 步骤3：构建完整的层级结构（核心：保证层级正确）=====
            Map<String, Object> currentLevel = new HashMap<>();
            if (isArrayPath) {
                // 数组路径：内层键是数组节点（如 friends），值是过滤后的数组
                String[] arrayPathParts = parentPathForBuild.replace("$.", "").split("\\.");
                if (arrayPathParts.length > 0) {
                    String arrayNodeKey = arrayPathParts[arrayPathParts.length - 1];
                    currentLevel.put(arrayNodeKey, finalValue);
                    // 从倒数第二层构建父层级
                    for (int i = arrayPathParts.length - 2; i >= 0; i--) {
                        Map<String, Object> temp = new HashMap<>();
                        temp.put(arrayPathParts[i], currentLevel);
                        currentLevel = temp;
                    }
                }
            } else {
                // 非数组路径：内层键是目标字段（如 name），值是提取的值
                currentLevel.put(targetField, finalValue);
                // 构建父层级（如 user.basicInfo）
                if (!parentPathForBuild.isEmpty()) {
                    String[] nonArrayPathParts = parentPathForBuild.replace("$.", "").split("\\.");
                    for (int i = nonArrayPathParts.length - 1; i >= 0; i--) {
                        Map<String, Object> temp = new HashMap<>();
                        temp.put(nonArrayPathParts[i], currentLevel);
                        currentLevel = temp;
                    }
                }
            }

            // ===== 步骤4：递归合并到根Map（保证字段不丢失、层级不覆盖）=====
            mergeMap(rootMap, currentLevel);
        }

        // 4. 转为标准JSON字符串返回
        return mapToJson(rootMap);
    }

    /**
     * 递归合并两个Map：保证子层级正确合并，已有字段不被覆盖
     *
     * @param target 目标Map（最终结果）
     * @param source 源Map（当前路径的层级结构）
     */
    @SuppressWarnings("unchecked")
    private static void mergeMap(Map<String, Object> target, Map<String, Object> source) {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = entry.getKey();
            Object sourceValue = entry.getValue();
            Object targetValue = target.get(key);

            if (targetValue == null) {
                // 目标Map无此键，直接放入
                target.put(key, sourceValue);
            } else if (targetValue instanceof Map && sourceValue instanceof Map) {
                // 都是Map，递归合并子层级
                mergeMap((Map<String, Object>) targetValue, (Map<String, Object>) sourceValue);
            } else if (targetValue instanceof List && sourceValue instanceof List) {
                // 都是List，合并元素（泛型安全转换）
                List<Object> targetList = (List<Object>) targetValue;
                List<Object> sourceList = (List<Object>) sourceValue;
                targetList.addAll(sourceList);
            }
            // 非Map/List：保留原有值，避免覆盖
        }
    }

    /**
     * Map转标准JSON字符串（Java 8兼容，支持Jackson降级）
     *
     * @param map 待转换的Map
     * @return 标准JSON字符串
     */
    private static String mapToJson(Map<String, Object> map) {
        try {
            // 优先使用Jackson（需引入依赖），格式更标准
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            // 降级：手动拼接JSON（无依赖）
            StringBuilder sb = new StringBuilder("{");
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                sb.append("\"").append(entry.getKey()).append("\":");
                if (entry.getValue() instanceof Map) {
                    sb.append(mapToJson((Map<String, Object>) entry.getValue()));
                } else if (entry.getValue() instanceof List) {
                    sb.append(listToJson((List<?>) entry.getValue()));
                } else if (entry.getValue() instanceof String) {
                    sb.append("\"").append(entry.getValue()).append("\"");
                } else if (entry.getValue() instanceof Number || entry.getValue() instanceof Boolean) {
                    sb.append(entry.getValue());
                } else {
                    sb.append("\"").append(entry.getValue() == null ? "" : entry.getValue().toString()).append("\"");
                }
                sb.append(",");
            }
            if (sb.length() > 1) {
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.append("}");
            return sb.toString();
        }
    }

    /**
     * List转JSON数组字符串（适配各种数据类型）
     *
     * @param list 待转换的List
     * @return JSON数组字符串
     */
    private static String listToJson(List<?> list) {
        StringBuilder sb = new StringBuilder("[");
        for (Object obj : list) {
            if (obj instanceof Map) {
                sb.append(mapToJson((Map<String, Object>) obj));
            } else if (obj instanceof String) {
                sb.append("\"").append(obj).append("\"");
            } else if (obj instanceof Number || obj instanceof Boolean) {
                sb.append(obj);
            } else {
                sb.append("\"").append(obj == null ? "" : obj.toString()).append("\"");
            }
            sb.append(",");
        }
        if (sb.length() > 1) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("]");
        return sb.toString();
    }

    // 测试方法（可删除或保留）
    public static void main(String[] args) {
        String json = "{\n" +
                "  \"user\": {\n" +
                "    \"basicInfo\": {\n" +
                "      \"name\": \"张三\",\n" +
                "      \"age\": 25,\n" +
                "      \"contacts\": {\n" +
                "        \"friends\": [\n" +
                "          {\"name\": \"李四\", \"age\": 24},\n" +
                "          {\"name\": \"王五\", \"age\": 26}\n" +
                "        ]\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        // 测试路径列表
        List<String> targetPaths = new java.util.ArrayList<>();
        targetPaths.add("$.user.basicInfo.name");
        targetPaths.add("$.user.basicInfo.contacts.friends[*].name");

        // 调用工具方法
        String result = JsonPathUtil.retainAllParentNodes(targetPaths, json);
        System.out.println("最终结果：");
        System.out.println(result);
        // 预期输出：{"user":{"basicInfo":{"name":"张三","contacts":{"friends":[{"name":"李四"},{"name":"王五"}]}}}}
    }
}