package common.utils.sql;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author fengxi
 * @date 2022年07月08日 16:34
 */
@Data
public class Cell {

    private Integer check;
    private Integer colSpan;
    private Integer column;
    private Integer height;
    private Integer id;
    private Integer row;
    private Integer rowSpan;
    private String text;
    private Integer width;
    private Integer x;
    private Integer y;
}
