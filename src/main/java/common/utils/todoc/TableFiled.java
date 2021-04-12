package common.utils.todoc;

import lombok.Data;

/**
 * <p> 数据库表字段信息 </p>
 *
 * @author : zhengqing
 * @description :
 * @date : 2019/11/8 16:28
 */
@Data
public class TableFiled {

    private String catagory;
    /**
     * 字段名
     */
    private String field;
    /**
     * 类型
     */
    private String type;
    /**
     * 字段说明
     */
    private String comment;

}
