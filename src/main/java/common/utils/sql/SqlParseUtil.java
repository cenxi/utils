package common.utils.sql;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveCreateTableStatement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fengxi
 * @date 2022年07月29日 12:43
 */
public class SqlParseUtil {

    public Table sqlToHeads(String createTableSql){
        String tableName = "";
        List<Head> heads = new ArrayList<>();
        List<SQLStatement> statements = SQLUtils.parseStatements(createTableSql, "hive", true);
        if (CollUtil.isEmpty(statements)){
            throw new RuntimeException("语法错误");
        }
        SQLStatement statement = statements.get(0);
        if (! (statement instanceof HiveCreateTableStatement)){
            throw new RuntimeException("语法错误");
        }
        HiveCreateTableStatement s = (HiveCreateTableStatement) statement;
        tableName = s.getTableSource().getName().getSimpleName();
//        s.setStoredAs(new SQLIdentifierExpr("textfile"));
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
                    String name = SqlStrUtil.trimSpecialChar(columnDefinition.getName().getSimpleName());
                    String comment = SqlStrUtil.trimSpecialChar(columnDefinition.getComment().toString());
                    String dataType = columnDefinition.getDataType().getName();
                    List<SQLExpr> arguments = columnDefinition.getDataType().getArguments();
                    head.setFieldComment(comment);
                    head.setFieldName(name);
                    head.setFieldType(DbColType.valueOf(dataType));
                    if (CollUtil.isEmpty(arguments)){
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

        Table table = new Table();
        table.setHeads(heads);
        table.setTableName(tableName);
        return table;
    }

    public static String headsToSql(List<Head> heads,String tableName){
        String createTableSql = "";
        HiveCreateTableStatement statement = new HiveCreateTableStatement();
//        statement.setStoredAs(new SQLIdentifierExpr("textfile"));
        SQLExprTableSource tableSource = new SQLExprTableSource();
        tableSource.setExpr(SqlStrUtil.trimSpecialChar(tableName));
        statement.setTableSource(tableSource);
        if (CollUtil.isEmpty(heads)){
            throw new RuntimeException("语法错误: 字段不可为空");
        }

        SQLAlterTableAddColumn alterTableAddColumn = new SQLAlterTableAddColumn();
        alterTableAddColumn.setFirst(true);
        heads.stream()
                .map(h -> {
                    SQLColumnDefinition columnDefinition = new SQLColumnDefinition();
                    if (DbColType.INT.equals(h.getFieldType())) {
                        SQLDataTypeImpl dataType = new SQLDataTypeImpl();
                        dataType.setName(h.getFieldType().name());
                        dataType.addArgument(new SQLIntegerExpr(h.getFieldLength()));
                        dataType.addArgument(new SQLIntegerExpr(h.getFieldPrecision()));
                        columnDefinition.setDataType(dataType);
                    } else {
                        SQLCharacterDataType dataType = new SQLCharacterDataType(h.getFieldType().name());
                        dataType.addArgument(new SQLIntegerExpr(h.getFieldLength()));
                        columnDefinition.setDataType(dataType);
                    }
                    columnDefinition.setName(SqlStrUtil.trimSpecialChar(h.getFieldName()));
                    columnDefinition.setComment(SqlStrUtil.trimSpecialChar(h.getFieldComment()));
                    return columnDefinition;
                }).forEach(alterTableAddColumn::addColumn);
        SQLAlterTableStatement alterTableStatement = new SQLAlterTableStatement();
        alterTableStatement.addItem(alterTableAddColumn);
        alterTableStatement.setName(new SQLIdentifierExpr(tableName));
        statement.apply(alterTableStatement);
        createTableSql = SQLUtils.toSQLString(statement, "hive");
        return createTableSql;
    }
}
