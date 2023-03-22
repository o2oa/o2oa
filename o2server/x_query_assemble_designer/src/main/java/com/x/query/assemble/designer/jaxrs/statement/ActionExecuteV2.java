package com.x.query.assemble.designer.jaxrs.statement;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.Parameter;
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
import com.x.query.core.express.statement.Runtime;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

class ActionExecuteV2 extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionExecuteV2.class);

    public static final Pattern QM_PARAMETER_REGEX = Pattern.compile("(\\?\\d+)");
    public static final Pattern NAMED_PARAMETER_REGEX = Pattern.compile("(:(\\w+))");

    private static final String KEY_SELECT = "SELECT";
    private static final String KEY_COUNT = "COUNT";
    private static final String KEY_LEFT_PARENTHESIS = "(";
    private static final String KEY_RIGHT_PARENTHESIS = ")";
    private static final String KEY_COUNTSQL = "COUNT(*)";
    private static final String KEY_FROM = "FROM";
    private static final String KEY_WHERE = "WHERE";
    private static final String KEY_SPACE = " ";

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
        Pair<Optional<ExecuteTarget>, Optional<ExecuteTarget>> executeTargetPair;
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            statement = emc.flag(flag, Statement.class);
            if (null == statement) {
                throw new ExceptionEntityNotExist(flag, Statement.class);
            }
            if (!business.executable(effectivePerson, statement)) {
                throw new ExceptionAccessDenied(effectivePerson, statement);
            }
            runtime = this.runtime(effectivePerson, jsonElement, business, page, size);
            executeTargetPair = concreteExecuteTarget(effectivePerson, business, statement, runtime);
        }
        Optional<ExecuteTarget> data = executeTargetPair.first();
        Optional<ExecuteTarget> count = executeTargetPair.second();
        if (data.isPresent()) {
            result.setData(executeData(statement, runtime, data.get()));
        }
        if (count.isPresent()) {
            result.setCount(executeCount(statement, count.get()));
        }
        return result;
    }

    // 创建运行对象
    private Pair<Optional<ExecuteTarget>, Optional<ExecuteTarget>> concreteExecuteTarget(
            EffectivePerson effectivePerson, Business business, Statement statement, Runtime runtime) throws Exception {
        if (StringUtils.equalsAnyIgnoreCase(statement.getFormat(), Statement.FORMAT_SQL,
                Statement.FORMAT_SQLSCRIPT)) {
            return concreteExecuteTargetSql(effectivePerson, business, statement, runtime);
        } else {
            return concreteExecuteTargetJpql(effectivePerson, business, statement, runtime);
        }
    }

    // 创建 SQL 运行对象
    private Pair<Optional<ExecuteTarget>, Optional<ExecuteTarget>> concreteExecuteTargetSql(
            EffectivePerson effectivePerson, Business business, Statement statement,
            Runtime runtime) throws Exception {
        Optional<ExecuteTarget> data;
        Optional<ExecuteTarget> count = Optional.empty();
        String sql = "";
        if (StringUtils.equals(statement.getFormat(), Statement.FORMAT_SQL)) {
            sql = statement.getSql();
        } else {
            sql = script(effectivePerson, runtime, statement.getSqlScriptText());
        }
        data = Optional.of(new ExecuteTarget(effectivePerson, business, sql, runtime));
        if (data.get().getParsedStatement() instanceof net.sf.jsqlparser.statement.select.Select) {
            if (StringUtils.equalsIgnoreCase(statement.getCountMethod(), Statement.COUNTMETHOD_IGNORE)) {
                count = Optional.empty();
            } else if (StringUtils.equalsIgnoreCase(statement.getCountMethod(), Statement.COUNTMETHOD_AUTO)) {
                count = Optional.of(concreteExecuteTargetSqlCountAuto(effectivePerson, business,
                        runtime, data.get()));
            } else {
                count = Optional
                        .of(concreteExecuteTargetSqlCountAssign(effectivePerson, business, statement, runtime));
            }
        }
        return Pair.of(data, count);
    }

    // 创建 JPQL 运行对象
    private Pair<Optional<ExecuteTarget>, Optional<ExecuteTarget>> concreteExecuteTargetJpql(
            EffectivePerson effectivePerson, Business business, Statement statement,
            Runtime runtime) throws Exception {
        Optional<ExecuteTarget> data;
        Optional<ExecuteTarget> count = Optional.empty();
        String jpql = "";
        if (StringUtils.equals(statement.getFormat(), Statement.FORMAT_JPQL)) {
            jpql = statement.getData();
        } else {
            jpql = script(effectivePerson, runtime, statement.getScriptText());
        }
        data = Optional.of(new ExecuteTarget(effectivePerson, business, jpql, runtime));
        if (data.get().getParsedStatement() instanceof net.sf.jsqlparser.statement.select.Select) {
            if (StringUtils.equalsIgnoreCase(statement.getCountMethod(), Statement.COUNTMETHOD_IGNORE)) {
                count = Optional.empty();
            } else if (StringUtils.equalsIgnoreCase(statement.getCountMethod(), Statement.COUNTMETHOD_AUTO)) {
                count = Optional
                        .of(concreteExecuteTargetJpqlCountAuto(effectivePerson, business, runtime, data.get()));
            } else {
                count = Optional
                        .of(concreteExecuteTargetJpqlCountAssign(effectivePerson, business, statement, runtime));
            }
        }
        return Pair.of(data, count);
    }

    private ExecuteTarget concreteExecuteTargetSqlCountAssign(EffectivePerson effectivePerson, Business business,
            Statement statement,
            Runtime runtime) throws Exception {
        if (StringUtils.equalsAnyIgnoreCase(statement.getFormat(), Statement.FORMAT_SQL,
                Statement.FORMAT_SQLSCRIPT)) {
            String sql = "";
            if (StringUtils.equals(statement.getFormat(), Statement.FORMAT_SQL)) {
                sql = statement.getSqlCount();
            } else {
                sql = script(effectivePerson, runtime, statement.getSqlCountScriptText());
            }
            return new ExecuteTarget(effectivePerson, business, sql, runtime);
        } else {
            String jpql = "";
            if (StringUtils.equals(statement.getFormat(), Statement.FORMAT_JPQL)) {
                jpql = statement.getCountData();
            } else {
                jpql = script(effectivePerson, runtime, statement.getCountScriptText());
            }
            return new ExecuteTarget(effectivePerson, business, jpql, runtime);
        }
    }

    private ExecuteTarget concreteExecuteTargetSqlCountAuto(EffectivePerson effectivePerson, Business business,
            Runtime runtime, ExecuteTarget dataExecuteTarget)
            throws Exception {
        Select select = (Select) dataExecuteTarget.getParsedStatement();
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        net.sf.jsqlparser.schema.Table table = (net.sf.jsqlparser.schema.Table) plainSelect.getFromItem();
        StringBuilder builder = new StringBuilder();
        builder.append(KEY_SELECT).append(KEY_SPACE).append(KEY_COUNTSQL).append(KEY_SPACE)
                .append(KEY_FROM).append(KEY_SPACE).append(table.getFullyQualifiedName());
        String whereClause = plainSelect.getWhere().toString();
        if (StringUtils.isNotBlank(whereClause)) {
            builder.append(KEY_SPACE).append(KEY_WHERE).append(KEY_SPACE).append(whereClause);
        }
        return new ExecuteTarget(effectivePerson, business, builder.toString(), runtime);
    }

    private ExecuteTarget concreteExecuteTargetJpqlCountAssign(EffectivePerson effectivePerson, Business business,
            Statement statement,
            Runtime runtime) throws Exception {
        String jpql = "";
        if (StringUtils.equals(statement.getFormat(), Statement.FORMAT_JPQL)) {
            jpql = statement.getCountData();
        } else {
            jpql = script(effectivePerson, runtime, statement.getCountScriptText());
        }
        return new ExecuteTarget(effectivePerson, business, jpql, runtime);
    }

    private ExecuteTarget concreteExecuteTargetJpqlCountAuto(EffectivePerson effectivePerson, Business business,
            Runtime runtime, ExecuteTarget dataExecuteTarget)
            throws Exception {
        Select select = (Select) dataExecuteTarget.getParsedStatement();
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
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
        return new ExecuteTarget(effectivePerson, business, builder.toString(), runtime);
    }

    private Optional<Object> executeData(Statement statement,
            Runtime runtime,
            ExecuteTarget executeTarget) {
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
            LOGGER.debug("executeSql：{}.", executeTarget::getSql);
            Query query = em.createNativeQuery(executeTarget.getSql());
            for (Map.Entry<String, Object> entry : executeTarget.param.entrySet()) {
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
            LOGGER.debug("executeJpql：{}.", executeTarget::getSql);
            Query query = em.createQuery(executeTarget.getSql());
            for (Parameter<?> p : query.getParameters()) {
                query.setParameter(p.getName(), executeTarget.getParam().get(p.getName()));
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
            LOGGER.debug("executeSql：{}.", executeTarget::getSql);
            Query query = em.createNativeQuery(executeTarget.getSql());
            for (Map.Entry<String, Object> entry : executeTarget.param.entrySet()) {
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
            LOGGER.debug("executeJpql：{}.", executeTarget::getSql);
            Query query = em.createQuery(executeTarget.getSql());
            for (Parameter<?> p : query.getParameters()) {
                query.setParameter(p.getName(), executeTarget.getParam().get(p.getName()));
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

    public class ExecuteTarget {
        private String sql;
        private Map<String, Object> param = new LinkedHashMap<>();
        private net.sf.jsqlparser.statement.Statement parsedStatement;

        public String getSql() {
            return sql;
        }

        public Map<String, Object> getParam() {
            return param;
        }

        public net.sf.jsqlparser.statement.Statement getParsedStatement() {
            return parsedStatement;
        }

        // DATA 运行后已经生成了 PARAM 作为 PREVPARAM 传入,可以减少运行次数
        public ExecuteTarget(EffectivePerson effectivePerson, Business business, String sql,
                Runtime runtime, Map<String, Object> prevParam) throws Exception {
            init(effectivePerson, business, sql, runtime, prevParam);
        }

        public ExecuteTarget(EffectivePerson effectivePerson, Business business, String sql,
                Runtime runtime) throws Exception {
            init(effectivePerson, business, sql, runtime, null);
        }

        private void init(EffectivePerson effectivePerson, Business business, String sql,
                Runtime runtime, Map<String, Object> prevParam) throws Exception {
            questionMarkParameterChange(effectivePerson, business, runtime, sql, prevParam);
            this.sql = namedParameterChange(effectivePerson, business, runtime, sql, prevParam);
            parsedStatement = CCJSqlParserUtil.parse(this.sql);
        }

        // 读取 QUESTION MARK 的值
        private void questionMarkParameterChange(EffectivePerson effectivePerson, Business business,
                Runtime runtime, String sql,
                Map<String, Object> prevParam) throws Exception {
            Matcher matcher = QM_PARAMETER_REGEX.matcher(sql);
            while (matcher.find()) {
                String p = matcher.group(1);
                if ((null != prevParam) && prevParam.containsKey(p)) {
                    param.put(p, prevParam.get(p));
                } else {
                    Optional<Object> optional = getParameter(effectivePerson, business.organization(), matcher.group(2),
                            runtime);
                    if (optional.isPresent()) {
                        param.put(p, optional.get());
                    } else {
                        throw new ExceptionRequiredParameterNotPassed(p);
                    }
                }
            }
        }

        private String namedParameterChange(EffectivePerson effectivePerson, Business business, Runtime runtime,
                String sql, Map<String, Object> prevParam) throws Exception {
            Matcher matcher = NAMED_PARAMETER_REGEX.matcher(sql);
            while (matcher.find()) {
                String p = usableQuestionMark(param);
                namedParameterChangeWithPrevParam(prevParam, matcher, p);
                namedParameterChangeWithRuntime(effectivePerson, business, runtime, matcher, p);
                sql = StringUtils.replaceOnce(sql, matcher.group(1), p);
            }
            return sql;
        }

        private void namedParameterChangeWithPrevParam(Map<String, Object> prevParam, Matcher matcher, String p) {
            if (null != prevParam) {
                if (prevParam.containsKey(p)) {
                    param.put(p, prevParam.get(p));
                }
                if (prevParam.containsKey(matcher.group(2))) {
                    param.put(p, prevParam.get(matcher.group(2)));
                }
            }
        }

        private void namedParameterChangeWithRuntime(EffectivePerson effectivePerson, Business business,
                Runtime runtime, Matcher matcher, String p) throws Exception {
            if (!param.containsKey(p)) {
                Optional<Object> optional = getParameter(effectivePerson, business.organization(), p,
                        runtime);
                if (optional.isEmpty()) {
                    optional = getParameter(effectivePerson, business.organization(), matcher.group(2),
                            runtime);
                }
                if (optional.isPresent()) {
                    param.put(p, optional.get());
                } else {
                    throw new ExceptionRequiredParameterNotPassed(p);
                }
            }
        }

        private Optional<Object> getParameter(EffectivePerson effectivePerson, Organization organization, String name,
                Runtime runtime) throws Exception {
            if (StringUtils.equalsIgnoreCase(name, Runtime.PARAMETER_PERSON)) {
                return Optional.of(effectivePerson.getDistinguishedName());
            }
            if (StringUtils.equalsIgnoreCase(name, Runtime.PARAMETER_IDENTITYLIST)) {
                return Optional.of(organization.identity().listWithPerson(effectivePerson));
            }
            if (StringUtils.equalsIgnoreCase(name, Runtime.PARAMETER_UNITLIST)) {
                return Optional.of(organization.unit().listWithPerson(effectivePerson));
            }
            if (StringUtils.equalsIgnoreCase(name, Runtime.PARAMETER_UNITALLLIST)) {
                return Optional.of(organization.unit().listWithPersonSupNested(effectivePerson));
            }
            if (StringUtils.equalsIgnoreCase(name, Runtime.PARAMETER_GROUPLIST)) {
                return Optional.of(organization.group().listWithPerson(effectivePerson));
            }
            if (StringUtils.equalsIgnoreCase(name, Runtime.PARAMETER_ROLELIST)) {
                return Optional.of(organization.role().listWithPerson(effectivePerson));
            }
            if (runtime.hasParameter(name)) {
                return Optional.of(runtime.get(name));
            } else {
                return Optional.empty();
            }
        }

        private String usableQuestionMark(Map<String, Object> map) {
            int p = 1;
            while (map.keySet().contains("?" + p)) {
                p++;
            }
            return "?" + p;
        }
    }

}
