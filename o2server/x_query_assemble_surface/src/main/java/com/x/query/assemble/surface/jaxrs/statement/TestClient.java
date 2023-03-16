package com.x.query.assemble.surface.jaxrs.statement;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;

public class TestClient {
    public static void main(String[] args) throws JSQLParserException {
        String sql = "SELECT 1 FROM dual WHERE a = b and c=:c or d >:d group by a";
        Statement statement = CCJSqlParserUtil.parse(sql);
        if (statement instanceof Select) {
            Select select = (Select) statement;
            PlainSelect plainSelect = (PlainSelect) select.getSelectBody();

            SelectExpressionItem selectExpressionItem = (SelectExpressionItem) plainSelect.getSelectItems().get(0);

            Table table = (Table) plainSelect.getFromItem();
            System.out.println(table);

            Expression exp = plainSelect.getWhere();
            System.out.println(exp.toString());

        }
    }
}
