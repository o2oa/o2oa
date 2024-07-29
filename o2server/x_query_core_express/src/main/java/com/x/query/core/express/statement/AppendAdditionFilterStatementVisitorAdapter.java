package com.x.query.core.express.statement;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.core.express.plan.FilterEntry;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.StatementVisitorAdapter;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;

public class AppendAdditionFilterStatementVisitorAdapter extends StatementVisitorAdapter {

    private static final String TEXT_AND = "AND";
    private static final String TEXT_SPACE = " ";
    private static final String TEXT_LEFTPARENTHESIS = "(";
    private static final String TEXT_RIGHTPARENTHESIS = ")";
    private static final String TEXT_COLON = ":";

    private Runtime runtime;

    AppendAdditionFilterStatementVisitorAdapter(Runtime runtime) {
        this.runtime = runtime;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(AppendAdditionFilterStatementVisitorAdapter.class);

    @Override
    public void visit(Select select) {
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        Expression where = plainSelect.getWhere();
        String fromAlias = "";
        Table table = (net.sf.jsqlparser.schema.Table) plainSelect.getFromItem();
        if ((null != table) && (null != table.getAlias())) {
            fromAlias = table.getAlias().getName();
        }
        try {
            selectAppendWhere(runtime, plainSelect, where, fromAlias);
        } catch (JSQLParserException e) {
            LOGGER.error(e);
        }
    }

    @Override
    public void visit(Delete delete) {
        Expression where = delete.getWhere();
        String fromAlias = "";
        if ((null != delete.getTable()) && (null != delete.getTable().getAlias())) {
            fromAlias = delete.getTable().getAlias().getName();
        }
        try {
            deleteAppendWhere(runtime, delete, where, fromAlias);
        } catch (JSQLParserException e) {
            LOGGER.error(e);
        }
    }

    @Override
    public void visit(Update update) {
        Expression where = update.getWhere();
        String fromAlias = "";
        if ((null != update.getTable()) && (null != update.getTable().getAlias())) {
            fromAlias = update.getTable().getAlias().getName();
        }
        try {
            updateAppendWhere(runtime, update, where, fromAlias);
        } catch (JSQLParserException e) {
            LOGGER.error(e);
        }
    }

    private void selectAppendWhere(Runtime runtime, PlainSelect plainSelect, Expression where, String fromAlias)
            throws JSQLParserException {
        plainSelect.setWhere(convertToWhere(runtime, where, fromAlias));
    }

    private void deleteAppendWhere(Runtime runtime, Delete delete, Expression where, String fromAlias)
            throws JSQLParserException {
        delete.setWhere(convertToWhere(runtime, where, fromAlias));
    }

    private void updateAppendWhere(Runtime runtime, Update update, Expression where, String fromAlias)
            throws JSQLParserException {
        update.setWhere(convertToWhere(runtime, where, fromAlias));
    }

    private Expression convertToWhere(Runtime runtime, Expression where, String fromAlias) throws JSQLParserException {
        StringBuilder builder = new StringBuilder();
        if (null != where) {
            builder.append(TEXT_LEFTPARENTHESIS).append(where.toString()).append(TEXT_RIGHTPARENTHESIS)
                    .append(TEXT_SPACE).append(TEXT_AND).append(TEXT_SPACE);
        }
        builder.append(TEXT_LEFTPARENTHESIS);
        for (int i = 0; i < runtime.getFilterList().size(); i++) {
            FilterEntry entry = runtime.getFilterList().get(i);
            if (i > 0) {
                builder.append(TEXT_SPACE).append(entry.logic).append(TEXT_SPACE);
            }
            builder.append(pathWithFromAlias(entry.path, fromAlias)).append(TEXT_SPACE).append(comparison(entry))
                    .append(TEXT_SPACE).append(TEXT_COLON).append(entry.value);
        }
        builder.append(TEXT_RIGHTPARENTHESIS);
        return CCJSqlParserUtil.parseCondExpression(builder.toString());
    }

    private String pathWithFromAlias(String path, String fromAlias) {
        return (StringUtils.isEmpty(fromAlias) || StringUtils.contains(path, ".")) ? path : (fromAlias + "." + path);
    }

    private String comparison(FilterEntry entry) {
        if (Comparison.isNotEquals(entry.comparison)) {
            return "<>";
        }
        if (Comparison.isGreaterThan(entry.comparison)) {
            return ">";
        }
        if (Comparison.isGreaterThanOrEqualTo(entry.comparison)) {
            return ">=";
        }
        if (Comparison.isLessThan(entry.comparison)) {
            return "<";
        }
        if (Comparison.isLessThanOrEqualTo(entry.comparison)) {
            return "<=";
        }
        if (Comparison.isLike(entry.comparison)) {
            return "LIKE";
        }
        if (Comparison.isNotLike(entry.comparison)) {
            return "NOT LIKE";
        }
        if (Comparison.isIn(entry.comparison)) {
            return "IN";
        }
        return "=";
    }
}
