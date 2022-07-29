package common.utils.sql;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 模板表头信息
 * @author renchao
 */
@Data
public class Head {

    @ApiModelProperty("字段英文名")
    private String fieldName;

    @ApiModelProperty("字段中文名")
    private String fieldComment;

    @ApiModelProperty("字段类型:VARCHAR...")
    private DbColType fieldType = DbColType.VARCHAR;

    @ApiModelProperty("字段长度")
    private Integer fieldLength = 255;

    @ApiModelProperty("字段精度")
    private Integer fieldPrecision = 2;

}
