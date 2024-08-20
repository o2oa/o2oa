package com.x.query.core.express.statement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.dynamic.DynamicBaseEntity;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.NumberTools;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Table;

import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

public class Executor {

    private static final String JOIN_KEY = " JOIN ";
    private static final String JOIN_ON_KEY = " ON ";

    private Executor() {
        // nothing
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Executor.class);

    public static Object executeData(Statement statement, Runtime runtime, ExecuteTarget executeTarget)
            throws Exception {
        String sql = executeTarget.getSql().toUpperCase();
        if (StringUtils.equalsAnyIgnoreCase(statement.getFormat(), Statement.FORMAT_SQL, Statement.FORMAT_SQLSCRIPT)) {
            return executeDataSql(runtime, executeTarget, false);
        } else if(sql.indexOf(JOIN_KEY) > -1 && sql.indexOf(JOIN_ON_KEY) > -1){
            return executeDataSql(runtime, executeTarget, true);
        } else {
            return executeDataJpql(statement, runtime, executeTarget);
        }
    }

    private static Object executeDataSql(Runtime runtime, ExecuteTarget executeTarget, boolean isOld) throws Exception {
        checkDeleteInsertUpdateDml(executeTarget.getParsedStatement());
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            EntityManager em = emc.get(DynamicBaseEntity.class);
            LOGGER.debug("executeDataSql:{}, param:{}.", executeTarget::getSql, executeTarget::getQuestionMarkParam);
            Query query;
            if(executeTarget.getParsedStatement() instanceof net.sf.jsqlparser.statement.select.Select) {
                if (isOld) {
                    query = em.createNativeQuery(joinSql(executeTarget.getSql()));
                } else {
                    query = em.createNativeQuery(executeTarget.getSql(), LinkedHashMap.class);
                }
            }else{
                query = em.createNativeQuery(executeTarget.getSql());
            }
            for (Map.Entry<String, Object> entry : executeTarget.getQuestionMarkParam().entrySet()) {
                int idx = Integer.parseInt(entry.getKey().substring(1));
                query.setParameter(idx, entry.getValue());
            }
            if (executeTarget.getParsedStatement() instanceof net.sf.jsqlparser.statement.select.Select) {
                appendSelectRange(runtime, query);
                if(isOld){
                    return query.getResultList();
                }else{
                    List<LinkedHashMap<String, Object>> list = query.getResultList();
                    for (LinkedHashMap<String, Object> map : list) {
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            if (entry.getValue() instanceof Clob) {
                                map.put(entry.getKey(), convertClobToString((Clob) entry.getValue()));
                            }
                        }
                    }
                    return list;
                }
            } else {
                emc.beginTransaction(DynamicBaseEntity.class);
                Object data = query.executeUpdate();
                emc.commit();
                return data;
            }
        }
    }

    private static String convertClobToString(Clob clob) throws SQLException,IOException {
        StringBuilder sb = new StringBuilder();
        try (Reader reader = clob.getCharacterStream();
                BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    private static void appendSelectRange(Runtime runtime, Query query) {
        if (NumberTools.greaterThan(runtime.page, 0) && NumberTools.greaterThan(runtime.size, 0)) {
            query.setFirstResult((runtime.page - 1) * runtime.size);
            query.setMaxResults(runtime.size);
        }
    }

    private static Object executeDataJpql(Statement statement, Runtime runtime, ExecuteTarget executeTarget)
            throws Exception {
        checkJpqlDeleteInsertUpdateDml(executeTarget.getParsedStatement());
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Class<? extends JpaObject> cls = clazz(emc, statement);
            EntityManager em;
            if (StringUtils.equalsIgnoreCase(statement.getEntityCategory(), Statement.ENTITYCATEGORY_DYNAMIC)
                    && executeTarget.getParsedStatement() instanceof net.sf.jsqlparser.statement.select.Select) {
                em = emc.get(DynamicBaseEntity.class);
            } else {

                em = emc.get(cls);
            }
            LOGGER.debug("executeDataJpql:{}, param:{}.", executeTarget::getSql, executeTarget::getQuestionMarkParam);
            Query query = em.createQuery(executeTarget.getSql());
            for (Map.Entry<String, Object> entry : executeTarget.getQuestionMarkParam().entrySet()) {
                int idx = Integer.parseInt(entry.getKey().substring(1));
                query.setParameter(idx, entry.getValue());
            }
            if (executeTarget.getParsedStatement() instanceof net.sf.jsqlparser.statement.select.Select) {
                appendSelectRange(runtime, query);
                List<?> os = query.getResultList();
                Float fv = statement.getFv();
                if ((null != fv) && (fv >= Statement.VALUE_FV_8_0)) {
                    return jpqlResultToMap(
                            (net.sf.jsqlparser.statement.select.Select) executeTarget.getParsedStatement(),
                            os);
                }
                return os;
            } else {
                emc.beginTransaction(cls);
                Object data = Integer.valueOf(query.executeUpdate());
                emc.commit();
                return data;
            }
        }
    }

    /**
     * 在8.0.0以上版本jpql的输出值通过jsqlparser转换成字段属性该方法通过fv字段进行判断
     *
     * @param select
     * @param list
     * @return
     */
    private static Object jpqlResultToMap(net.sf.jsqlparser.statement.select.Select select,
            List<?> list) {
        if (ListTools.isEmpty(list)) {
            return list;
        }
        Object o = list.get(0);
        if ((null != o) && (JpaObject.class.isAssignableFrom(o.getClass()))) {
            return list;
        }
        final Map<Integer, String> itemMapping = selectItemMapping(select);
        final List<Map<String, Object>> result = new ArrayList<>();
        list.stream().forEach(obj -> {
            Map<String, Object> target = new LinkedHashMap<>();
            if (obj.getClass().isArray()) {
                Object[] from = (Object[]) obj;
                itemMapping.entrySet().forEach(m -> target.put(m.getValue(), from[m.getKey()]));
            } else {
                target.put(itemMapping.get(0), obj);
            }
            result.add(target);
        });
        return result;
    }

    private static Map<Integer, String> selectItemMapping(net.sf.jsqlparser.statement.select.Select select) {
        Map<Integer, String> mapping = new LinkedHashMap<>();
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        List<SelectItem> items = plainSelect.getSelectItems();
        if (null != items) {
            for (int i = 0; i < plainSelect.getSelectItems().size(); i++) {
                SelectExpressionItem selectExpressionItem = (SelectExpressionItem) items.get(i);
                Alias alias = selectExpressionItem.getAlias();
                if (null != alias) {
                    mapping.put(i, alias.getName());
                } else {
                    mapping.put(i, simplifyName(selectExpressionItem.toString()));
                }

            }
        }
        return mapping;
    }

    /**
     * 简化名称,考虑一下情况<br>
     * x.id
     * (x.id)
     * ((x.id))
     *
     * @param name
     * @return
     */
    private static String simplifyName(String name) {
        name = StringUtils.trimToEmpty(name);
        while (name.startsWith("(") && name.endsWith(")")) {
            name = name.substring(1, name.length() - 1);
        }
        if (StringUtils.containsAny(name, "(", ")")) {
            return name;
        }
        return StringUtils.contains(name, ".") ? StringUtils.substringAfterLast(name, ".") : name;
    }

    public static Long executeCount(Statement statement, ExecuteTarget executeTarget) throws Exception {
        String sql = executeTarget.getSql().toUpperCase();
        if (StringUtils.equalsAnyIgnoreCase(statement.getFormat(), Statement.FORMAT_SQL, Statement.FORMAT_SQLSCRIPT)) {
            return executeCountSql(executeTarget, false);
        } else if(sql.indexOf(JOIN_KEY) > -1 && sql.indexOf(JOIN_ON_KEY) > -1){
            return executeCountSql(executeTarget, true);
        } else {
            return executeCountJpql(statement, executeTarget);
        }
    }

    private static Long executeCountSql(ExecuteTarget executeTarget, boolean isOld) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            EntityManager em = emc.get(DynamicBaseEntity.class);
            LOGGER.debug("executeCountSql:{}, param:{}.", executeTarget::getSql, executeTarget::getQuestionMarkParam);
            String sql = isOld ? joinSql(executeTarget.getSql()) : executeTarget.getSql();
            Query query = em.createNativeQuery(sql);
            for (Map.Entry<String, Object> entry : executeTarget.getQuestionMarkParam().entrySet()) {
                int idx = Integer.parseInt(entry.getKey().substring(1));
                query.setParameter(idx, entry.getValue());
            }
            return ((Number) query.getSingleResult()).longValue();
        }
    }

    private static Long executeCountJpql(Statement statement, ExecuteTarget executeTarget) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Class<? extends JpaObject> cls = clazz(emc, statement);
            EntityManager em;
            if (StringUtils.equalsIgnoreCase(statement.getEntityCategory(), Statement.ENTITYCATEGORY_DYNAMIC)
                    && executeTarget.getParsedStatement() instanceof net.sf.jsqlparser.statement.select.Select) {
                em = emc.get(DynamicBaseEntity.class);
            } else {
                em = emc.get(cls);
            }
            LOGGER.debug("executeCountJpql:{}, param:{}.", executeTarget::getSql, executeTarget::getQuestionMarkParam);
            Query query = em.createQuery(executeTarget.getSql());
            for (Map.Entry<String, Object> entry : executeTarget.getQuestionMarkParam().entrySet()) {
                int idx = Integer.parseInt(entry.getKey().substring(1));
                query.setParameter(idx, entry.getValue());
            }
            return ((Number) query.getSingleResult()).longValue();
        }
    }

    /**
     * jpql不支持insert,进行单独判断,然后判断checkDeleteInsertUpdateDml
     *
     * @param statement
     * @throws Exception
     */
    private static void checkJpqlDeleteInsertUpdateDml(net.sf.jsqlparser.statement.Statement statement)
            throws Exception {
        if (statement instanceof net.sf.jsqlparser.statement.insert.Insert) {
            throw new ExceptionJpqlInsertNotSupported();
        } else {
            checkDeleteInsertUpdateDml(statement);
        }
    }

    /**
     * 检查配置文件是否允许执行 delete,insert,update语句
     *
     * @param statement
     * @throws Exception
     */
    private static void checkDeleteInsertUpdateDml(net.sf.jsqlparser.statement.Statement statement)
            throws Exception {
        if (statement instanceof net.sf.jsqlparser.statement.delete.Delete) {
            if (BooleanUtils.isNotTrue(Config.query().getStatementDeleteEnable())) {
                throw new ExceptionDisableDelete();
            }
        } else if (statement instanceof net.sf.jsqlparser.statement.update.Update) {
            if (BooleanUtils.isNotTrue(Config.query().getStatementUpdateEnable())) {
                throw new ExceptionDisableUpdate();
            }
        } else if (statement instanceof net.sf.jsqlparser.statement.insert.Insert) {
            if (BooleanUtils.isNotTrue(Config.query().getStatementInsertEnable())) {
                throw new ExceptionDisableInsert();
            }
        } else if (!(statement instanceof net.sf.jsqlparser.statement.select.Select)) {
            throw new ExceptionDmlNotAllowed();
        }
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends JpaObject> clazz(EntityManagerContainer entityManagerContainer, Statement statement)
            throws Exception {
        Class<? extends JpaObject> cls = null;
        if (StringUtils.equals(Statement.ENTITYCATEGORY_OFFICIAL, statement.getEntityCategory())
                || StringUtils.equals(Statement.ENTITYCATEGORY_CUSTOM, statement.getEntityCategory())) {
            cls = (Class<? extends JpaObject>) Thread.currentThread().getContextClassLoader()
                    .loadClass(statement.getEntityClassName());
        } else {
            Table table = entityManagerContainer.flag(statement.getTable(), Table.class);
            if (null == table) {
                throw new ExceptionEntityNotExist(statement.getTable(), Table.class);
            }
            DynamicEntity dynamicEntity = new DynamicEntity(table.getName());
            cls = (Class<? extends JpaObject>) Thread.currentThread().getContextClassLoader()
                    .loadClass(dynamicEntity.className());
        }
        return cls;
    }

    private static String joinSql(String sql) throws Exception{
        String upSql = sql.toUpperCase();
        if (upSql.indexOf(JOIN_KEY) > -1 && upSql.indexOf(JOIN_ON_KEY) > -1) {
            sql = sql.replaceAll("\\.", ".x");
            sql = sql.replaceAll("\\.x\\*", ".*");
            List<Table> tables;
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                tables = emc.fetchEqual(Table.class,
                        ListTools.toList(Table.NAME_FIELDNAME), Table.STATUS_FIELDNAME, Table.STATUS_BUILD);
            }
            for (Table table : tables) {
                sql = sql.replaceAll(" " + table.getName() + " ",
                        " " + DynamicEntity.TABLE_PREFIX + table.getName().toUpperCase() + " ");
            }
        }
        return sql;
    }
}
