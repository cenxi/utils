package common.utils.sql;

import cn.hutool.core.collection.CollUtil;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fengxi
 * @date 2022年07月29日 12:43
 */
public class SqlParseUtil {

    public static Table sqlToHeads(String createTableSql){
        Table table = new Table();
        List<Head> heads = null;
        String tableName = "";

        List<SQLStatement> statements = SQLUtils.parseStatements(createTableSql, "hive", true);
        if (CollUtil.isEmpty(statements)){
            throw new RuntimeException("语法错误: "+createTableSql);
        }
        SQLStatement statement = statements.get(0);
        if (! (statement instanceof HiveCreateTableStatement)){
            throw new RuntimeException("语法错误: 表" + tableName);
        }
        HiveCreateTableStatement s = (HiveCreateTableStatement) statement;
        tableName = s.getTableSource().getName().getSimpleName();
        s.setStoredAs(new SQLIdentifierExpr("textfile"));
        List<SQLTableElement> tableElementList = s.getTableElementList();
        if (CollUtil.isEmpty(tableElementList)){
            heads = Collections.emptyList();
        }
        heads = tableElementList.stream()
                .map(e -> {
                    if (!(e instanceof SQLColumnDefinition)) {
                        throw new RuntimeException("语法错误:" + e);
                    }
                    Head head = new Head();
                    SQLColumnDefinition columnDefinition = (SQLColumnDefinition) e;
                    String name = columnDefinition.getName().getSimpleName();
                    String comment = columnDefinition.getComment().toString();
                    String dataType = columnDefinition.getDataType().getName();
                    List<SQLExpr> arguments = columnDefinition.getDataType().getArguments();
                    head.setFieldComment(comment);
                    head.setFieldName(name);
                    head.setFieldType(DbColType.valueOf(dataType));
                    if (CollUtil.isEmpty(arguments)) {
                        return head;
                    }
                    //长度
                    SQLExpr sqlExpr0 = arguments.get(0);
                    if (!(sqlExpr0 instanceof SQLIntegerExpr)) {
                        throw new RuntimeException("语法错误:" + e);
                    }
                    int length = ((SQLIntegerExpr) sqlExpr0).getNumber().intValue();
                    head.setFieldLength(length);
                    //精度
                    if (arguments.size() >= 2) {
                        SQLExpr sqlExpr1 = arguments.get(1);
                        if (!(sqlExpr1 instanceof SQLIntegerExpr)) {
                            throw new RuntimeException("语法错误:" + e);
                        }
                        int precision = ((SQLIntegerExpr) sqlExpr1).getNumber().intValue();
                        head.setFieldPrecision(precision);
                    }
                    return head;
                }).collect(Collectors.toList());
        table.setTableName(tableName);
        table.setHeads(heads);
        return table;
    }
}
