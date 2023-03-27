package com.x.query.assemble.designer.jaxrs.statement;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptContext;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.dynamic.DynamicBaseEntity;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.script.AbstractResources;
import com.x.base.core.project.scripting.JsonScriptingExecutor;
import com.x.base.core.project.scripting.ScriptingFactory;
import com.x.base.core.project.tools.NumberTools;
import com.x.base.core.project.webservices.WebservicesClient;
import com.x.organization.core.express.Organization;
import com.x.query.assemble.designer.Business;
import com.x.query.assemble.designer.ThisApplication;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Table;
import com.x.query.core.express.statement.ExecuteTarget;
import com.x.query.core.express.statement.Runtime;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;

/**
 * DATA,COUNT分别执行
 * <p>
 * sql: select xid, xjob FROM PP_C_TASK WHERE xtitle =?1 and xperson=:person and
 * xcreateTime > ?2
 *
 * <p>
 * jqpl: select o.id, o.job FROM TASK WHERE o.title =:title and o.person=:person
 * and o.createTime > ?1
 * 
 * @author ray
 *
 */
class ActionExecuteV2 extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionExecuteV2.class);

    private static final String KEY_SELECT = "SELECT";
    private static final String KEY_COUNT = "COUNT";
    private static final String KEY_LEFT_PARENTHESIS = "(";
    private static final String KEY_RIGHT_PARENTHESIS = ")";
    private static final String KEY_COUNTSQL = "COUNT(*)";
    private static final String KEY_FROM = "FROM";
    private static final String KEY_WHERE = "WHERE";
    private static final String KEY_SPACE = " ";

    private static final String[] AGGREGATE_FUNCTION_STARTS = new String[] { "AVG(", "COUNT(", "DISTINCT(", "MAX(",
            "MIN(", "SUM(" };

    ActionResult<Object> execute(EffectivePerson effectivePerson, String flag, String mode, Integer page, Integer size,
            JsonElement jsonElement) throws Exception {

        LOGGER.debug("execute:{}, flag:{}, mode:{}, page:{}, size:{}, jsonElement:{}.",
                effectivePerson::getDistinguishedName, () -> flag, () -> mode, () -> page, () -> size,
                () -> jsonElement);
        ClassLoader classLoader = Business.getDynamicEntityClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);
        ActionResult<Object> result = new ActionResult<>();
        Statement statement;
        Runtime runtime;
        Pair<ExecuteTarget, Optional<ExecuteTarget>> executeTargetPair;
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            statement = emc.flag(flag, Statement.class);
            if (null == statement) {
                throw new ExceptionEntityNotExist(flag, Statement.class);
            }
            if (!business.executable(effectivePerson, statement)) {
                throw new ExceptionAccessDenied(effectivePerson, statement);
            }
            runtime = this.concreteRuntime(effectivePerson, jsonElement, business.organization(), page, size);
            executeTargetPair = concreteExecuteTarget(effectivePerson, business.organization(), statement, runtime);
        }
        ExecuteTarget data = executeTargetPair.first();
        Optional<ExecuteTarget> optionalCount = executeTargetPair.second();
        result.setData(executeData(statement, runtime, data));
        if (optionalCount.isPresent()) {
            result.setCount(executeCount(statement, optionalCount.get()));
        }
        return result;
    }

    // 创建运行对象
    private Pair<ExecuteTarget, Optional<ExecuteTarget>> concreteExecuteTarget(
            EffectivePerson effectivePerson, Organization organization, Statement statement, Runtime runtime)
            throws Exception {
        if (StringUtils.equalsAnyIgnoreCase(statement.getFormat(), Statement.FORMAT_SQL,
                Statement.FORMAT_SQLSCRIPT)) {
            return concreteExecuteTargetSql(effectivePerson, organization, statement, runtime);
        } else {
            return concreteExecuteTargetJpql(effectivePerson, organization, statement, runtime);
        }
    }

    // 创建 SQL 运行对象
    private Pair<ExecuteTarget, Optional<ExecuteTarget>> concreteExecuteTargetSql(
            EffectivePerson effectivePerson, Organization organization, Statement statement,
            Runtime runtime) throws Exception {
        ExecuteTarget data;
        Optional<ExecuteTarget> optionalCount = Optional.empty();
        String sql = "";
        if (StringUtils.equals(statement.getFormat(), Statement.FORMAT_SQL)) {
            sql = statement.getSql();
        } else {
            sql = script(effectivePerson, runtime, statement.getSqlScriptText());
        }
        data = new ExecuteTarget(effectivePerson, organization, sql, runtime, null);
        if (data.getParsedStatement() instanceof net.sf.jsqlparser.statement.select.Select) {
            if (StringUtils.equalsIgnoreCase(statement.getCountMethod(), Statement.COUNTMETHOD_IGNORE)) {
                optionalCount = Optional.empty();
            } else if (StringUtils.equalsIgnoreCase(statement.getCountMethod(), Statement.COUNTMETHOD_AUTO)) {
                optionalCount = concreteExecuteTargetSqlCountAuto(effectivePerson, organization,
                        runtime, sql, data.getNamedParam());
            } else {
                optionalCount = Optional
                        .of(concreteExecuteTargetSqlCountAssign(effectivePerson, organization, statement, runtime,
                                data.getNamedParam()));
            }
        }
        return Pair.of(data, optionalCount);
    }

    // 创建 JPQL 运行对象
    private Pair<ExecuteTarget, Optional<ExecuteTarget>> concreteExecuteTargetJpql(
            EffectivePerson effectivePerson, Organization organization, Statement statement,
            Runtime runtime) throws Exception {
        ExecuteTarget data;
        Optional<ExecuteTarget> optionalCount = Optional.empty();
        String jpql = "";
        if (StringUtils.equals(statement.getFormat(), Statement.FORMAT_JPQL)) {
            jpql = statement.getData();
        } else {
            jpql = script(effectivePerson, runtime, statement.getScriptText());
        }
        data = new ExecuteTarget(effectivePerson, organization, jpql, runtime, null);
        if (data.getParsedStatement() instanceof net.sf.jsqlparser.statement.select.Select) {
            if (StringUtils.equalsIgnoreCase(statement.getCountMethod(), Statement.COUNTMETHOD_IGNORE)) {
                optionalCount = Optional.empty();
            } else if (StringUtils.equalsIgnoreCase(statement.getCountMethod(), Statement.COUNTMETHOD_AUTO)) {
                optionalCount = concreteExecuteTargetJpqlCountAuto(effectivePerson, organization, runtime, jpql,
                        data.getNamedParam());
            } else {
                optionalCount = Optional
                        .of(concreteExecuteTargetJpqlCountAssign(effectivePerson, organization, statement, runtime,
                                data.getNamedParam()));
            }
        }
        return Pair.of(data, optionalCount);
    }

    // 创建 SQL COUNT ASSIGN
    private ExecuteTarget concreteExecuteTargetSqlCountAssign(EffectivePerson effectivePerson,
            Organization organization,
            Statement statement, Runtime runtime, Map<String, Object> prevNamedParam) throws Exception {
        String sql = "";
        if (StringUtils.equals(statement.getFormat(), Statement.FORMAT_SQL)) {
            sql = statement.getSqlCount();
        } else {
            sql = script(effectivePerson, runtime, statement.getSqlCountScriptText());
        }
        return new ExecuteTarget(effectivePerson, organization, sql, runtime, prevNamedParam);
    }

    // 创建 SQL COUNT AUTO
    private Optional<ExecuteTarget> concreteExecuteTargetSqlCountAuto(EffectivePerson effectivePerson,
            Organization organization, Runtime runtime, String sql, Map<String, Object> prevNamedParam)
            throws Exception {
        Select select = (Select) CCJSqlParserUtil.parse(sql);
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        if (onlyAggregateFunction(plainSelect.getSelectItems())) {
            return Optional.empty();
        }
        net.sf.jsqlparser.schema.Table table = (net.sf.jsqlparser.schema.Table) plainSelect.getFromItem();
        StringBuilder builder = new StringBuilder();
        builder.append(KEY_SELECT).append(KEY_SPACE).append(KEY_COUNTSQL).append(KEY_SPACE)
                .append(KEY_FROM).append(KEY_SPACE).append(table.getFullyQualifiedName());
        String whereClause = plainSelect.getWhere().toString();
        if (StringUtils.isNotBlank(whereClause)) {
            builder.append(KEY_SPACE).append(KEY_WHERE).append(KEY_SPACE).append(whereClause);
        }
        // 将在生成DATA语句中的参数对象PARAM传入避免重复计算
        return Optional
                .of(new ExecuteTarget(effectivePerson, organization, builder.toString(), runtime, prevNamedParam));
    }

    private boolean onlyAggregateFunction(List<SelectItem> selectItems) {
        Optional<SelectItem> optional = selectItems.stream()
                .filter(o -> !StringUtils.startsWithAny(o.toString(), AGGREGATE_FUNCTION_STARTS)).findAny();
        return optional.isEmpty();
    }

    private ExecuteTarget concreteExecuteTargetJpqlCountAssign(EffectivePerson effectivePerson,
            Organization organization,
            Statement statement, Runtime runtime, Map<String, Object> prevNamedParam) throws Exception {
        String jpql = "";
        if (StringUtils.equals(statement.getFormat(), Statement.FORMAT_JPQL)) {
            jpql = statement.getCountData();
        } else {
            jpql = script(effectivePerson, runtime, statement.getCountScriptText());
        }
        return new ExecuteTarget(effectivePerson, organization, jpql, runtime, prevNamedParam);
    }

    private Optional<ExecuteTarget> concreteExecuteTargetJpqlCountAuto(EffectivePerson effectivePerson,
            Organization organization,
            Runtime runtime, String jpql, Map<String, Object> prevNamedParam)
            throws Exception {
        Select select = (Select) CCJSqlParserUtil.parse(jpql);
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        if (onlyAggregateFunction(plainSelect.getSelectItems())) {
            return Optional.empty();
        }
        net.sf.jsqlparser.schema.Table table = (net.sf.jsqlparser.schema.Table) plainSelect.getFromItem();
        StringBuilder builder = new StringBuilder();
        builder.append(KEY_SELECT).append(KEY_SPACE).append(KEY_COUNT).append(KEY_LEFT_PARENTHESIS)
                .append(table.getAlias())
                .append(KEY_RIGHT_PARENTHESIS).append(KEY_SPACE).append(KEY_FROM).append(KEY_SPACE)
                .append(table.getFullyQualifiedName()).append(KEY_SPACE).append(table.getAlias());
        Expression exp = plainSelect.getWhere();
        if (null != exp) {
            String whereClause = exp.toString();
            if (StringUtils.isNotBlank(whereClause)) {
                builder.append(KEY_SPACE).append(KEY_WHERE).append(KEY_SPACE).append(whereClause);
            }
        }
        return Optional
                .of(new ExecuteTarget(effectivePerson, organization, builder.toString(), runtime, prevNamedParam));
    }

    private Optional<Object> executeData(Statement statement, Runtime runtime, ExecuteTarget executeTarget) {
        Optional<Object> data;
        if (StringUtils.equalsAnyIgnoreCase(statement.getFormat(), Statement.FORMAT_SQL, Statement.FORMAT_SQLSCRIPT)) {
            data = executeDataSql(statement, runtime, executeTarget);
        } else {
            data = executeDataJpql(statement, runtime, executeTarget);
        }
        return data;
    }

    private Optional<Object> executeDataSql(Statement statement, Runtime runtime, ExecuteTarget executeTarget) {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            Class<? extends JpaObject> cls = this.clazz(business, statement);
            EntityManager em;
            if (StringUtils.equalsIgnoreCase(statement.getEntityCategory(), Statement.ENTITYCATEGORY_DYNAMIC)
                    && executeTarget.getParsedStatement() instanceof net.sf.jsqlparser.statement.select.Select) {
                em = business.entityManagerContainer().get(DynamicBaseEntity.class);
            } else {
                em = business.entityManagerContainer().get(cls);
            }
            LOGGER.debug("executeDataSql:{}, param:{}.", executeTarget::getSql, executeTarget::getQuestionMarkParam);
            Query query = em.createNativeQuery(executeTarget.getSql());
            for (Map.Entry<String, Object> entry : executeTarget.getQuestionMarkParam().entrySet()) {
                int idx = Integer.parseInt(entry.getKey().substring(1));
                query.setParameter(idx, entry.getValue());
            }
            if (executeTarget.getParsedStatement() instanceof net.sf.jsqlparser.statement.select.Select) {
                if (NumberTools.greaterThan(runtime.page, 0) && NumberTools.greaterThan(runtime.size, 0)) {
                    query.setFirstResult((runtime.page - 1) * runtime.size);
                    query.setMaxResults(runtime.size);
                }
                return Optional.of(query.getResultList());
            } else {
                business.entityManagerContainer().beginTransaction(cls);
                Object data = Integer.valueOf(query.executeUpdate());
                business.entityManagerContainer().commit();
                return Optional.of(data);
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return Optional.empty();
    }

    private Optional<Object> executeDataJpql(Statement statement, Runtime runtime, ExecuteTarget executeTarget) {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            Class<? extends JpaObject> cls = this.clazz(business, statement);
            EntityManager em;
            if (StringUtils.equalsIgnoreCase(statement.getEntityCategory(), Statement.ENTITYCATEGORY_DYNAMIC)
                    && executeTarget.getParsedStatement() instanceof net.sf.jsqlparser.statement.select.Select) {
                em = business.entityManagerContainer().get(DynamicBaseEntity.class);
            } else {
                em = business.entityManagerContainer().get(cls);
            }
            LOGGER.debug("executeDataJpql:{}, param:{}.", executeTarget::getSql, executeTarget::getQuestionMarkParam);
            Query query = em.createQuery(executeTarget.getSql());
            for (Map.Entry<String, Object> entry : executeTarget.getQuestionMarkParam().entrySet()) {
                int idx = Integer.parseInt(entry.getKey().substring(1));
                query.setParameter(idx, entry.getValue());
            }
            if (executeTarget.getParsedStatement() instanceof net.sf.jsqlparser.statement.select.Select) {
                if (NumberTools.greaterThan(runtime.page, 0) && NumberTools.greaterThan(runtime.size, 0)) {
                    query.setFirstResult((runtime.page - 1) * runtime.size);
                    query.setMaxResults(runtime.size);
                }
                return Optional.of(query.getResultList());
            } else {
                business.entityManagerContainer().beginTransaction(cls);
                Object data = Integer.valueOf(query.executeUpdate());
                business.entityManagerContainer().commit();
                return Optional.of(data);
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return Optional.empty();
    }

    private Long executeCount(Statement statement, ExecuteTarget executeTarget) {
        if (StringUtils.equalsAnyIgnoreCase(statement.getFormat(), Statement.FORMAT_SQL, Statement.FORMAT_SQLSCRIPT)) {
            return executeCountSql(statement, executeTarget);
        } else {
            return executeCountJpql(statement, executeTarget);
        }
    }

    private Long executeCountSql(Statement statement, ExecuteTarget executeTarget) {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            Class<? extends JpaObject> cls = this.clazz(business, statement);
            EntityManager em;
            if (StringUtils.equalsIgnoreCase(statement.getEntityCategory(), Statement.ENTITYCATEGORY_DYNAMIC)
                    && executeTarget.getParsedStatement() instanceof net.sf.jsqlparser.statement.select.Select) {
                em = business.entityManagerContainer().get(DynamicBaseEntity.class);
            } else {
                em = business.entityManagerContainer().get(cls);
            }
            LOGGER.debug("executeCountSql:{}, param:{}.", executeTarget::getSql, executeTarget::getQuestionMarkParam);
            Query query = em.createNativeQuery(executeTarget.getSql());
            for (Map.Entry<String, Object> entry : executeTarget.getQuestionMarkParam().entrySet()) {
                int idx = Integer.parseInt(entry.getKey().substring(1));
                query.setParameter(idx, entry.getValue());
            }
            return (Long) query.getSingleResult();
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }

    private Long executeCountJpql(Statement statement, ExecuteTarget executeTarget) {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            Class<? extends JpaObject> cls = this.clazz(business, statement);
            EntityManager em;
            if (StringUtils.equalsIgnoreCase(statement.getEntityCategory(), Statement.ENTITYCATEGORY_DYNAMIC)
                    && executeTarget.getParsedStatement() instanceof net.sf.jsqlparser.statement.select.Select) {
                em = business.entityManagerContainer().get(DynamicBaseEntity.class);
            } else {
                em = business.entityManagerContainer().get(cls);
            }
            LOGGER.debug("executeCountJpql:{}, param:{}.", executeTarget::getSql, executeTarget::getQuestionMarkParam);
            Query query = em.createQuery(executeTarget.getSql());
            for (Map.Entry<String, Object> entry : executeTarget.getQuestionMarkParam().entrySet()) {
                int idx = Integer.parseInt(entry.getKey().substring(1));
                query.setParameter(idx, entry.getValue());
            }
            return (Long) query.getSingleResult();
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }

    private String script(EffectivePerson effectivePerson, Runtime runtime, String scriptText) {
        String text = "";
        try {
            ScriptContext scriptContext = this.scriptContext(effectivePerson, runtime);
            CompiledScript cs = ScriptingFactory.functionalizationCompile(scriptText);
            text = JsonScriptingExecutor.evalString(cs, scriptContext);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return text;
    }

    @SuppressWarnings("unchecked")
    private Class<? extends JpaObject> clazz(Business business, Statement statement) throws Exception {
        Class<? extends JpaObject> cls = null;
        if (StringUtils.equals(Statement.ENTITYCATEGORY_OFFICIAL, statement.getEntityCategory())
                || StringUtils.equals(Statement.ENTITYCATEGORY_CUSTOM, statement.getEntityCategory())) {
            cls = (Class<? extends JpaObject>) Thread.currentThread().getContextClassLoader()
                    .loadClass(statement.getEntityClassName());
        } else {
            Table table = business.entityManagerContainer().flag(statement.getTable(), Table.class);
            if (null == table) {
                throw new ExceptionEntityNotExist(statement.getTable(), Table.class);
            }
            DynamicEntity dynamicEntity = new DynamicEntity(table.getName());
            cls = (Class<? extends JpaObject>) Thread.currentThread().getContextClassLoader()
                    .loadClass(dynamicEntity.className());
        }
        return cls;
    }

    private ScriptContext scriptContext(EffectivePerson effectivePerson, Runtime runtime)
            throws Exception {
        ScriptContext scriptContext = ScriptingFactory.scriptContextEvalInitialServiceScript();
        Resources resources = new Resources();
        resources.setContext(ThisApplication.context());
        resources.setApplications(ThisApplication.context().applications());
        resources.setWebservicesClient(new WebservicesClient());
        resources.setOrganization(new Organization(ThisApplication.context()));
        Bindings bindings = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put(ScriptingFactory.BINDING_NAME_SERVICE_RESOURCES, resources);
        bindings.put(ScriptingFactory.BINDING_NAME_SERVICE_EFFECTIVEPERSON, effectivePerson);
        bindings.put(ScriptingFactory.BINDING_NAME_SERVICE_PARAMETERS, gson.toJson(runtime.getParameters()));
        return scriptContext;
    }

    public static class Resources extends AbstractResources {

        private Organization organization;

        public Organization getOrganization() {
            return organization;
        }

        public void setOrganization(Organization organization) {
            this.organization = organization;
        }

    }

}
