package common.utils.jsonpath;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * JsonPathUtil 测试类（修复空JSON格式错误）
 */
public class JsonPathUtilTest {

    // 基础测试JSON（核心场景）
    private static final String BASE_JSON = "{\n" +
            "  \"user\": {\n" +
            "    \"basicInfo\": {\n" +
            "      \"name\": \"张三\",\n" +
            "      \"age\": 25,\n" +
            "      \"gender\": \"男\",\n" +
            "      \"contacts\": {\n" +
            "        \"phone\": \"13800138000\",\n" +
            "        \"friends\": [\n" +
            "          {\"name\": \"李四\", \"age\": 24, \"city\": \"北京\"},\n" +
            "          {\"name\": \"王五\", \"age\": 26, \"city\": \"上海\"},\n" +
            "          {\"name\": \"赵六\", \"age\": 28, \"city\": \"广州\"}\n" +
            "        ]\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

    // 修复：空数组测试JSON（格式合法，补充缺失的 }）
    private static final String EMPTY_JSON = "{\n" +
            "  \"user\": {\n" +
            "    \"basicInfo\": {\n" +
            "      \"contacts\": {\n" +
            "        \"friends\": []\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" + // 补充闭合 user 层级
            "}";    // 补充闭合最外层

    // 多层嵌套JSON
    private static final String NESTED_JSON = "{\n" +
            "  \"a\": {\n" +
            "    \"b\": {\n" +
            "      \"c\": {\n" +
            "        \"d\": [\n" +
            "          {\"key1\": \"value1\", \"key2\": \"value2\"},\n" +
            "          {\"key1\": \"value3\", \"key2\": \"value4\"}\n" +
            "        ]\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

    /**
     * 测试1：单路径（非数组）- 提取单个字段
     * 预期：正确提取 $.user.basicInfo.name 并保留层级
     */
    @Test
    public void testSingleNonArrayPath() {
        List<String> paths = new ArrayList<>();
        paths.add("$.user.basicInfo.name");

        String result = JsonPathUtil.retainAllParentNodes(paths, BASE_JSON);
        String expected = "{\"user\":{\"basicInfo\":{\"name\":\"张三\"}}}";

        // 忽略空格/换行，只校验JSON内容
        assertEquals(formatJson(expected), formatJson(result));
        System.out.println("测试1（单非数组路径）结果：" + result);
    }

    /**
     * 测试2：单路径（数组）- 提取数组字段并保留对象结构
     * 预期：正确提取 friends[*].name，保留数组对象格式
     */
    @Test
    public void testSingleArrayPath() {
        List<String> paths = new ArrayList<>();
        paths.add("$.user.basicInfo.contacts.friends[*].name");

        String result = JsonPathUtil.retainAllParentNodes(paths, BASE_JSON);
        String expected = "{\"user\":{\"basicInfo\":{\"contacts\":{\"friends\":[{\"name\":\"李四\"},{\"name\":\"王五\"},{\"name\":\"赵六\"}]}}}}";

        assertEquals(formatJson(expected), formatJson(result));
        System.out.println("测试2（单数组路径）结果：" + result);
    }

    /**
     * 测试3：多路径（非数组+数组）- 字段正确合并到同一层级
     * 预期：name + friends[*].name 都在 user.basicInfo 下
     */
    @Test
    public void testMultiPaths() {
        List<String> paths = new ArrayList<>();
        paths.add("$.user.basicInfo.name");
        paths.add("$.user.basicInfo.contacts.friends[*].name");
        paths.add("$.user.basicInfo.age");

        String result = JsonPathUtil.retainAllParentNodes(paths, BASE_JSON);
        String expected = "{\"user\":{\"basicInfo\":{\"name\":\"张三\",\"age\":25,\"contacts\":{\"friends\":[{\"name\":\"李四\"},{\"name\":\"王五\"},{\"name\":\"赵六\"}]}}}}";

        assertEquals(formatJson(expected), formatJson(result));
        System.out.println("测试3（多路径合并）结果：" + result);
    }

    /**
     * 测试4：数组多字段提取 - 提取 friends[*].name + friends[*].city
     * 预期：friends数组元素同时包含 name 和 city
     */
    @Test
    public void testArrayMultiFields() {
        List<String> paths = new ArrayList<>();
        paths.add("$.user.basicInfo.contacts.friends[*].name");
        paths.add("$.user.basicInfo.contacts.friends[*].city");

        String result = JsonPathUtil.retainAllParentNodes(paths, BASE_JSON);
        String expected = "{\"user\":{\"basicInfo\":{\"contacts\":{\"friends\":[{\"name\":\"李四\",\"city\":\"北京\"},{\"name\":\"王五\",\"city\":\"上海\"},{\"name\":\"赵六\",\"city\":\"广州\"}]}}}}";

        assertEquals(formatJson(expected), formatJson(result));
        System.out.println("测试4（数组多字段）结果：" + result);
    }

    /**
     * 测试5：空数组场景 - friends为空数组，不报错且返回空数组
     */
    @Test
    public void testEmptyArray() {
        List<String> paths = new ArrayList<>();
        paths.add("$.user.basicInfo.contacts.friends[*].name");

        String result = JsonPathUtil.retainAllParentNodes(paths, EMPTY_JSON);
        String expected = "{\"user\":{\"basicInfo\":{\"contacts\":{\"friends\":[]}}}}";

        assertEquals(formatJson(expected), formatJson(result));
        System.out.println("测试5（空数组）结果：" + result);
    }

    /**
     * 测试6：异常路径 - 路径不存在，忽略该路径且不影响其他路径
     */
    @Test
    public void testInvalidPath() {
        List<String> paths = new ArrayList<>();
        paths.add("$.user.basicInfo.name"); // 有效路径
        paths.add("$.user.basicInfo.nonexistent"); // 无效路径

        String result = JsonPathUtil.retainAllParentNodes(paths, BASE_JSON);
        String expected = "{\"user\":{\"basicInfo\":{\"name\":\"张三\"}}}";

        assertEquals(formatJson(expected), formatJson(result));
        System.out.println("测试6（异常路径）结果：" + result);
    }

    /**
     * 测试7：多层嵌套路径 - 提取 a.b.c.d[*].key1
     * 预期：保留所有父层级，数组结构正确
     */
    @Test
    public void testNestedPath() {
        List<String> paths = new ArrayList<>();
        paths.add("$.a.b.c.d[*].key1");

        String result = JsonPathUtil.retainAllParentNodes(paths, NESTED_JSON);
        String expected = "{\"a\":{\"b\":{\"c\":{\"d\":[{\"key1\":\"value1\"},{\"key1\":\"value3\"}]}}}}";

        assertEquals(formatJson(expected), formatJson(result));
        System.out.println("测试7（多层嵌套）结果：" + result);
    }

    /**
     * 测试8：空JSON/空路径 - 边界场景
     */
    @Test
    public void testEmptyScenarios() {
        // 空路径列表
        String result1 = JsonPathUtil.retainAllParentNodes(new ArrayList<>(), BASE_JSON);
        assertEquals(formatJson(BASE_JSON), formatJson(result1));

        // 空JSON字符串
        String result2 = JsonPathUtil.retainAllParentNodes(new ArrayList<String>() {{
            add("$.user.basicInfo.name");
        }}, "");
        assertEquals("{}", result2);

        System.out.println("测试8（边界场景）通过");
    }

    /**
     * 辅助方法：格式化JSON（去除空格、换行、制表符），便于断言对比
     */
    private String formatJson(String json) {
        if (json == null) {
            return "";
        }
        return json.replaceAll("\\s+", "").trim();
    }
}