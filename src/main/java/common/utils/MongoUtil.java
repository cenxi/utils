package common.utils;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Function;

/**
 * @author :fengxi
 * @description：
 * @modified By:
 */
@Slf4j
public class MongoUtil {

    /**
     * 遍历处理
     */
    public static void iteratorDeal(MongoTemplate mongoTemplate, Criteria filter,
                             Sort sort, int size, Class<?> clazz, Function func) {
        Query query = new Query(filter).limit(size).with(sort);

        List datas = mongoTemplate.find(query, clazz);
        while (!CollectionUtils.isEmpty(datas)) {

            for (Object data : datas) {
                func.apply(data);
            }
            String id = getId(datas.get(datas.size() - 1), clazz);
            Criteria criteria = Criteria.where("_id").gt(new ObjectId(id)).andOperator(filter);
            query = new Query(criteria)
                    .limit(size)
                    .with(sort);
            datas = mongoTemplate.find(query, clazz);
        }
    }

    private static String getId(Object data, Class<?> clazz) {
        try {
            Field field = clazz.getDeclaredField("id");
            field.setAccessible(true);
            return field.get(data).toString();
        } catch (Exception e) {
            log.error("无id字段", e);
            throw new RuntimeException("无id字段");
        }
    }
}
