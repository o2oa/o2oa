package com.x.query.assemble.surface.jaxrs.statement;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;

class ActionTest extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionTest.class);

    ActionResult<Object> execute(EffectivePerson effectivePerson) throws Exception {
        String sql = "SELECT xid FROM PP_C_TASK WHERE xidentity=:identity";
        Statement statement = CCJSqlParserUtil.parse(sql);
        if (statement instanceof Select) {
            Select select = (Select) statement;
            PlainSelect plainSelect = (PlainSelect) select.getSelectBody();

            SelectExpressionItem selectExpressionItem = (SelectExpressionItem) plainSelect.getSelectItems().get(0);

            Table table = (Table) plainSelect.getFromItem();
            System.out.println("!!!!!!!!!!!!!!!" + table.getFullyQualifiedName());

            Expression exp = plainSelect.getWhere();
            exp.accept(new ExpressionVisitorAdapter() {
                @Override
                public void visit(AndExpression expr) {
                    if (expr.getLeftExpression() instanceof AndExpression) {
                        expr.getLeftExpression().accept(this);
                    } else if ((expr.getLeftExpression() instanceof EqualsTo)) {
                        System.out.println(expr.getLeftExpression());
                    }
                    System.out.println(expr.getRightExpression());
                }
            });

        }
        ActionResult result = new ActionResult<>();
        return result;
    }

}
