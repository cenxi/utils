package common.utils.mybatisWrapper;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author fengxi
 * @Description TODO
 * @CreateTime 2021/8/26
 */
public class WrapperUtils {

    private static HashMap<Class, List<QueryField>> cache = new HashMap<>();

    public static <T> QueryWrapper<T> buildQueryWrapper(Object obj, Class<T> clazz) {

        QueryWrapper<T> queryWrapper = Wrappers.query();

        TableInfo tableInfo = TableInfoHelper.getTableInfo(clazz);
        if (tableInfo == null) {
            throw new RuntimeException(clazz.toString() + "->不存在对应的表信息");
        }
        List<TableFieldInfo> tableFields = tableInfo.getFieldList();
        if (CollectionUtils.isEmpty(tableFields)) {
            throw new RuntimeException(clazz.toString() + "->对应的表不存在任何数据库字段");
        }

        List<QueryField> queryFields = cache.get(obj.getClass());
        // 若字体对应表信息为空,则利用反射获取相关信息，并加入缓存
        if (CollectionUtils.isEmpty(queryFields)) {
            queryFields = new ArrayList<>();
            // 反射获取类相关信息，并加到缓存当中
            Field[] fields = ReflectUtil.getFields(obj.getClass());
            if (ArrayUtil.isEmpty(fields)) {
                return queryWrapper;
            }
            for (Field field : fields) {
                Query query = field.getAnnotation(Query.class);
                if (query == null) {
                    continue;
                }
                String propName = query.propName();
                QueryField queryField = new QueryField();
                queryField.setKey(query.value());
                queryField.setField(field);

                String columnName = isBlank(propName) ? field.getName() : propName;
                for (TableFieldInfo tableField : tableFields) {
                    if ("id".equalsIgnoreCase(columnName)) {
                        queryField.setTableFieldName("id");
                    }
                    if (tableField.getProperty().equalsIgnoreCase(columnName)) {
                        queryField.setTableFieldName(tableField.getColumn());
                    }
                }
                queryFields.add(queryField);
            }

            cache.put(obj.getClass(), queryFields);
        }

        for (QueryField queryField : queryFields) {
            Object fieldVal = ReflectUtil.getFieldValue(obj, queryField.getField());
            if (queryField.key == SqlKeyword.ASC
                    || queryField.key == SqlKeyword.DESC
                    || queryField.key == SqlKeyword.ORDER_BY) {
                buildQueryWrapper(queryWrapper, fieldVal
                        , queryField.getKey(), queryField.getTableFieldName());
            }
            if (fieldVal == null || StringUtils.isEmpty(fieldVal.toString())) {
                // 传入的字段为空，则不做处理
                continue;
            }
            buildQueryWrapper(queryWrapper, fieldVal
                    , queryField.getKey(), queryField.getTableFieldName());
        }

        return queryWrapper;
    }

    private static void buildQueryWrapper(QueryWrapper queryWrapper, Object fieldVal
            , SqlKeyword key, String tableFieldName) {
        switch (key) {
            case DESC:
                queryWrapper.orderByDesc(tableFieldName);
                break;
            case ASC:
                queryWrapper.orderByAsc(tableFieldName);
                break;
            case ORDER_BY:
                if (!(fieldVal instanceof Boolean)) {
                    throw new RuntimeException("使用ORDER_BY类型,属性只能是boolean类型(true代表降序,false代表升序)");
                }
                // 为true则降序
                if ((Boolean) fieldVal) {
                    queryWrapper.orderByDesc(tableFieldName);
                } else {
                    queryWrapper.orderByAsc(tableFieldName);
                }
                break;
            case EQ:
                queryWrapper.eq(tableFieldName, fieldVal);
                break;
            case LIKE:
                queryWrapper.like(tableFieldName, fieldVal);
                break;
            case NOT_LIKE:
                queryWrapper.notLike(tableFieldName, fieldVal);
                break;
            case NE:
                queryWrapper.ne(tableFieldName, fieldVal);
                break;
            case IS_NOT_NULL:
                queryWrapper.isNotNull(tableFieldName);
                break;
            case IS_NULL:
                queryWrapper.isNull(tableFieldName);
                break;
            default:
        }
    }

    @Data
    public static class QueryField {

        private SqlKeyword key;

        private Field field;

        private String tableFieldName;

    }

    private static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}