package common.utils.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author fengxi
 * @date 2022年09月06日 11:17
 */
@Data
@Configuration
public class ConfSample {


    /**
     * 对应propeties配置aifill.hadoop.conf.map={"fs.defaultFS":"hdfs://hadoopcluster"}
     * 对应yml文件配置
     * hadoop:
     *     conf:
     *       map: '{"fs.defaultFS":"hdfs://hadoopcluster"}'
     * 说明
     *   $是取字符串的值，#是el表达式符号
     */
    @Value("#{${hadoop.conf.map:{\"aa\":\"bb\"}}}")
    private Map<String,String> confMap;
}
