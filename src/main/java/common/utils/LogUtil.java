package common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import common.annotations.HideAnn;
import common.annotations.HideCollection;
import common.annotations.HideImg;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.Map.Entry;

/**
 * 描述: 日志打印
 *
 * @author chenchaoyun
 * @create 2018/11/27
 */
public class LogUtil {

    private static final String HIDE_PWD = "*******";
    private static final String UNKNOWN = "unknown";
    private static final String SERIAL_VERSION_UID = "serialVersionUID";
    private static final String[] LOG_ARRAYS = new String[]{"log", "logger"};

    private static GsonBuilder gsonBuilder = new GsonBuilder();

    private static PropertyFilter serializeFilter = (source, name, value) -> {
        if (SERIAL_VERSION_UID.equals(name)) {
            return false;
        }
        return true;
    };

    /**
     * 美化json字符串
     *
     * @param uglyJSONString json 字符串
     */
    public static String prettyJson(String uglyJSONString) {
        try {
            Gson gson = gsonBuilder.setPrettyPrinting().create();
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(uglyJSONString);
            return gson.toJson(je);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uglyJSONString;
    }

    /**
     * 将一个json字符串中，属性值（String),如果长度超过measure，则会进行截取， 保留measure长度，并且在结尾拼接上*****
     *
     * @param originalJsonString
     * @param measure
     * @return
     */
    public static String reduceJson(String originalJsonString, int measure) {
        Object parsedObject = JSON.parse(originalJsonString);
        Object reducedObject = reduceJsonObject(parsedObject, measure);
        return JSON.toJSONString(reducedObject);
    }

    private static Object reduceJsonObject(Object jsonObject, int measure) {
        if (jsonObject instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) jsonObject;
            Iterator<Object> iterator = jsonArray.iterator();
            while (iterator.hasNext()) {
                Object nextObj = iterator.next();
                reduceJsonObject(nextObj, measure);
            }
        }
        //对象json
        if (jsonObject instanceof JSONObject) {
            JSONObject objCopy = (JSONObject) (jsonObject);
            for (Entry<String, Object> entry : objCopy.entrySet()) {
                if (entry.getValue() instanceof JSONArray) {
                    reduceJsonObject(entry.getValue(), measure);
                } else if (entry.getValue() instanceof String) {
                    String objValue = (String) entry.getValue();
                    if (objValue.length() > measure && measure != 0) {
                        objValue = objValue.substring(0, measure) + "*****";
                    }
                    entry.setValue(objValue);
                }
            }
        }
        return jsonObject;
    }

    /**
     * 过滤obj中敏感属性
     */
    public static String formatLog(Object obj) {
        if (ObjectUtils.isEmpty(obj)) {
            return "null";
        }

        if (obj instanceof byte[]) {
            return String.format("%s bytes", ((byte[]) obj).length);
        }

        //隐藏注解项
        JSONObject jsonObject = handleObjectJSON(obj);
        if (jsonObject != null) {
            return jsonObject.toJSONString();
        }
        //else
        return JSONObject.toJSONString(obj);

    }

    /**
     * 处理object 返回json对象
     */
    private static JSONObject handleObjectJSON(Object target) {
        try {
            Object jsObj = JSONObject.toJSON(target);
            //数组json
            if (jsObj instanceof JSONArray) {
                ObjectBean objectBean = new ObjectBean(target);
                return handleObjectJSON(objectBean);
            }
            //对象json
            if (jsObj instanceof JSONObject) {
                JSONObject objCopy = (JSONObject) (jsObj);
                List<Field> objFields = getFieldListByClass(target.getClass());
                for (Field field : objFields) {

                    /**
                     * 过滤 static final
                     */
                    if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                        continue;
                    }

                    field.setAccessible(true);
                    Object fieldVal = field.get(target);
                    hideProperties(objCopy, field, fieldVal);//敏感信息隐藏
                }
                return objCopy;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //else
        return null;
    }


    /**
     * 敏感信息隐藏
     */
    private static void hideProperties(JSONObject objCopy, Field field, Object fieldVal) {
        if (isBasicType(fieldVal)) {
            //基本类型
            handleBasicType(objCopy, field, fieldVal);
        } else if (fieldVal instanceof Date) {
            //date 类型
            handleDateType(objCopy, field, fieldVal);
        } else if (fieldVal instanceof Collection) {
            //Collection类型
            handleCollectionType(objCopy, field, fieldVal);
        } else if (fieldVal instanceof Map) {
            //Map类型
            handleMapType(objCopy, field, fieldVal);
        } else if (ObjectUtils.isArray(fieldVal)) {
            //数组类型
            handleArrayType(objCopy, field, fieldVal);
        } else {
            String fieldName = null;
            if (hasJSONField(field)) {
                fieldName = getJSONFieldName(field);
            } else {
                fieldName = field.getName();
            }

            if (Logger.class.isAssignableFrom(field.getType())) {
                return;
            }

            if (ArrayUtils.contains(LOG_ARRAYS, field.getName()) && Modifier.isStatic(field.getModifiers())) {
                return;
            }

            //对象类型
            JSONObject jsonObject = handleObjectJSON(fieldVal);
            objCopy.put(fieldName, jsonObject);
        }
    }


    /**
     * 处理数组类型
     */
    private static void handleArrayType(JSONObject objCopy, Field field, Object fieldVal) {
        Object[] objects = (Object[]) fieldVal;
        String fieldName = null;
        if (hasJSONField(field)) {
            fieldName = getJSONFieldName(field);
        } else {
            fieldName = field.getName();
        }
        if (hasHideCollection(field)) {
            JSONArray array = new JSONArray();
            int length = objects.length;
            if (length > 0) {
                //取一个
                array.add(handleType(objects[0]));
                array.add(hideArrays(length));
            }
            objCopy.put(fieldName, array);
        } else {
            JSONArray array = new JSONArray();
            for (Object item : objects) {
                array.add(handleType(item));
            }
            objCopy.put(fieldName, array);
        }
    }


    /**
     * 处理Map 类型
     */
    private static void handleMapType(JSONObject objCopy, Field field, Object fieldVal) {
        Map map = (Map) fieldVal;
        String fieldName = null;
        if (hasJSONField(field)) {
            fieldName = getJSONFieldName(field);
        } else {
            fieldName = field.getName();
        }
        if (hasHideCollection(field)) {
            int size = map.size();
            JSONObject jsonObj = hideArrays(size);
            if (size > 0) {
                //取一个
                Entry<Object, Object> next = (Entry<Object, Object>) map.entrySet().iterator().next();
                Object key = next.getKey();
                Object item = next.getValue();
                jsonObj.put(key.toString(), handleType(item));
            }
            objCopy.put(fieldName, jsonObj);
        } else {
            JSONObject jsonObj = new JSONObject();
            for (Entry<Object, Object> objectEntry : (Set<Entry<Object, Object>>) (map.entrySet())) {
                Object key = objectEntry.getKey();
                Object item = objectEntry.getValue();
                jsonObj.put(key.toString(), handleType(item));
            }
            objCopy.put(fieldName, jsonObj);
        }
    }

    /**
     * 处理date类型
     */
    private static void handleDateType(JSONObject objCopy, Field field, Object fieldVal) {
        Date date = (Date) fieldVal;
        String fieldName = null;
        if (hasJSONField(field)) {
            fieldName = getJSONFieldName(field);
        } else {
            fieldName = field.getName();
        }
        objCopy.put(fieldName, DateConvertUtils.format(date, DateConvertUtils.DATE_TIME_SSSS));
    }

    /**
     * 处理集合类型
     */
    private static void handleCollectionType(JSONObject objCopy, Field field, Object fieldVal) {
        Collection objCollection = (Collection) fieldVal;
        String fieldName = null;
        if (hasJSONField(field)) {
            fieldName = getJSONFieldName(field);
        } else {
            fieldName = field.getName();
        }

        if (hasHideCollection(field)) {
            int size = objCollection.size();
            JSONArray array = new JSONArray();
            if (size > 0) {
                //取一个
                Object someone = objCollection.iterator().next();
                Object next = handleType(someone);
                int length = next.toString().length();
                if (length > 24) {
                    String front = StringUtils.substring((next.toString()), 0, 23);
                    String after = StringUtils.substring((fieldVal.toString()), length - 11, length - 1);
                    array.add(front + HIDE_PWD + after);
                } else {
                    array.add(next);
                }
                array.add(hideArrays(size));
            }
            objCopy.put(fieldName, array);
        } else {
            JSONArray array = new JSONArray();
            for (Object item : objCollection) {
                array.add(handleType(item));
            }
            objCopy.put(fieldName, array);
        }
    }


    /**
     * 处理基本类型
     */
    private static void handleBasicType(JSONObject objCopy, Field field, Object fieldVal) {

        //如果是序列化ID
        if (field.getName().equals(SERIAL_VERSION_UID) && Modifier.isStatic(field.getModifiers()) &&
            Modifier.isFinal(field.getModifiers()) && field.getType().getTypeName().equalsIgnoreCase("long")) {
            return;
        }

        //基本类型
        int length = fieldVal.toString().length();
        String fieldName = null;
        if (hasJSONField(field)) {
            fieldName = getJSONFieldName(field);
        } else {
            fieldName = field.getName();
        }
        if ((hasHideImg(field))) {
            String front = StringUtils.substring((fieldVal.toString()), 0, 23);
            String after = StringUtils.substring((fieldVal.toString()), length - 11, length - 1);
            objCopy.put(fieldName, front + HIDE_PWD + after);
        } else if ((hasHideAn(field))) {
            objCopy.put(fieldName, HIDE_PWD);
        } else {
            objCopy.put(fieldName, fieldVal);
        }
    }


    /**
     * 隐藏集合对象，只显示第一条内容
     */
    private static JSONObject hideArrays(int size) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "collection total size is " + size);
        return jsonObject;
    }

    /**
     * 非基本类型继续format
     */
    private static Object handleType(Object target) {
        if (!isBasicType(target)) {
            return handleObjectJSON(target);
        }
        return target;
    }

    /**
     * 获取类以及父类属性
     */
    private static List<Field> getFieldListByClass(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fieldList;
    }

    private static boolean hasHideImg(Field field) {
        return field.getAnnotation(HideImg.class) != null;
    }

    private static boolean hasJSONField(Field field) {
        return field.getAnnotation(JSONField.class) != null;
    }

    private static String getJSONFieldName(Field field) {
        JSONField annotation = field.getAnnotation(JSONField.class);
        if (annotation != null) {
            return annotation.name();
        }
        return UNKNOWN;
    }

    private static boolean hasHideAn(Field field) {
        return field.getAnnotation(HideAnn.class) != null;
    }

    private static boolean hasHideCollection(Field field) {
        return field.getAnnotation(HideCollection.class) != null;
    }

    /**
     * 判断是否为基本类型
     */
    private static boolean isBasicType(Object obj) {
        return (obj instanceof CharSequence || obj instanceof Boolean || obj instanceof Number ||
                obj instanceof Character || obj instanceof char[]);
    }

    public static void main(String[] args) {
        String json1 = "{\n" + "    \"age\":18,\n" + "    \"name\":\"张三\",\n" + "    \"score\":[\n" + "        {\n" +
                       "            \"yuwen\":80,\n" + "            \"shuxue\":90\n" + "        },\n" + "        {\n" +
                       "            \"yuwen\":81,\n" + "            \"shuxue\":91\n" + "        }\n" + "    ]\n" + "}";

        String json2 = "       \n" + "[\n" + "    {\n" + "        \"age\":19,\n" + "        \"name\":\"李四\",\n" +
                       "        \"en\":\"tom123456789012345678901234567890\",\n" + "        \"score\":[\n" +
                       "            {\n" + "                \"yuwen\":80,\n" + "                \"shuxue\":90,\n" +
                       "                \"code\":\"1abcdefgabcdefgabcdefgabcdefgabcdefg\"\n" + "            },\n" +
                       "            {\n" + "                \"yuwen\":81,\n" + "                \"shuxue\":91,\n" +
                       "                \"code\":\"2abcdefgabcdefgabcdefgabcdefgabcdefg\"\n" + "            }\n" +
                       "        ]\n" + "    },\n" + "    {\n" + "        \"age\":18,\n" + "        \"name\":\"张三\",\n" +
                       "        \"en\":\"kate123456789012345678901234567890\",\n" + "        \"score\":[\n" +
                       "            {\n" + "                \"yuwen\":80,\n" + "                \"shuxue\":90,\n" +
                       "                \"code\":\"3abcdefgabcdefgabcdefgabcdefgabcdefg\"\n" + "            },\n" +
                       "            {\n" + "                \"yuwen\":81,\n" + "                \"shuxue\":91,\n" +
                       "                \"code\":\"4abcdefgabcdefgabcdefgabcdefgabcdefg\"\n" + "            }\n" +
                       "        ]\n" + "    }\n" + "]";

        System.out.println(LogUtil.reduceJson(json2, 10));
    }

}
