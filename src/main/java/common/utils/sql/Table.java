package common.utils.sql;

import lombok.Data;

import java.util.List;

/**
 * @author fengxi
 * @date 2022年07月29日 12:54
 */
@Data
public class Table {

    private String tableName;

    private List<Head> heads;
}
