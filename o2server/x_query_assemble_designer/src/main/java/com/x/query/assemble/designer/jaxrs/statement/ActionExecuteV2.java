package com.x.query.assemble.designer.jaxrs.statement;

import java.util.Optional;

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

    ActionResult<Object> execute(EffectivePerson effectivePerson, String flag, String mode, Integer page, Integer size,
            JsonElement jsonElement) throws Exception {

        LOGGER.debug("execute:{}, flag:{}, mode:{}, page:{}, size:{}, jsonElement:{}.",
                effectivePerson::getDistinguishedName,
                () -> flag,
                () -> mode, () -> page, () -> size, () -> jsonElement);
        ClassLoader classLoader = Business.getDynamicEntityClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);

        Statement statement = null;
        ActionResult<Object> result = new ActionResult<>();
        Runtime runtime = null;
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
        }
        Optional<Object> data = Optional.empty();
        Optional<Long> count = Optional.empty();
        if (StringUtils.equals(statement.getFormat(), Statement.FORMAT_SQL)
                || StringUtils.equals(statement.getFormat(), Statement.FORMAT_SQLSCRIPT)) {
            String sql = "";
            if (StringUtils.equals(statement.getFormat(), Statement.FORMAT_SQL)) {
                sql = statement.getSql();
            } else {
                sql = script(effectivePerson, runtime, statement.getSqlScriptText());
            }
            net.sf.jsqlparser.statement.Statement stmt = CCJSqlParserUtil.parse(sql);
            data = executeSql(statement, sql, stmt, runtime);
            count = executeSqlCount(effectivePerson, statement, stmt, runtime);
        } else {
            String jpql = "";
            if (StringUtils.equals(statement.getFormat(), Statement.FORMAT_JPQL)) {
                jpql = statement.getData();
            } else {
                jpql = script(effectivePerson, runtime, statement.getScriptText());
            }
            net.sf.jsqlparser.statement.Statement stmt = CCJSqlParserUtil.parse(jpql);
            data = executeJpql(statement, jpql, stmt, runtime);
            count = executeJpqlCount(effectivePerson, statement, stmt, runtime);
        }
        if (data.isPresent()) {
            result.setData(data.get());
        }
        if (count.isPresent()) {
            result.setCount(count.get());
        }
        return result;
    }

    private Optional<Object> executeSql(Statement statement, String sql, net.sf.jsqlparser.statement.Statement stmt,
            Runtime runtime) {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            Class<? extends JpaObject> cls = this.clazz(business, statement);
            EntityManager em;
            if (StringUtils.equalsIgnoreCase(statement.getEntityCategory(), Statement.ENTITYCATEGORY_DYNAMIC)
                    && stmt instanceof net.sf.jsqlparser.statement.select.Select) {
                em = business.entityManagerContainer().get(DynamicBaseEntity.class);
            } else {
                em = business.entityManagerContainer().get(cls);
            }
            LOGGER.debug("executeSql：{}.", sql::toString);
            Query query = em.createNativeQuery(sql);
            for (Parameter<?> p : query.getParameters()) {
                if (runtime.hasParameter(p.getName())) {
                    query.setParameter(p.getName(), runtime.getParameter(p.getName()));
                }
            }
            if (stmt instanceof net.sf.jsqlparser.statement.select.Select) {
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

    private Optional<Object> executeJpql(Statement statement, String jpql, net.sf.jsqlparser.statement.Statement stmt,
            Runtime runtime) {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            Class<? extends JpaObject> cls = this.clazz(business, statement);
            EntityManager em;
            if (StringUtils.equalsIgnoreCase(statement.getEntityCategory(), Statement.ENTITYCATEGORY_DYNAMIC)
                    && stmt instanceof net.sf.jsqlparser.statement.select.Select) {
                em = business.entityManagerContainer().get(DynamicBaseEntity.class);
            } else {
                em = business.entityManagerContainer().get(cls);
            }
            LOGGER.debug("executeJpql：{}.", jpql::toString);
            Query query = em.createQuery(jpql);
            for (Parameter<?> p : query.getParameters()) {
                if (runtime.hasParameter(p.getName())) {
                    query.setParameter(p.getName(), runtime.getParameter(p.getName()));
                }
            }
            if (stmt instanceof net.sf.jsqlparser.statement.select.Select) {
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

    private Optional<Long> executeSqlCount(EffectivePerson effectivePerson, Statement statement,
            net.sf.jsqlparser.statement.Statement stmt, Runtime runtime) throws Exception {
        if ((!(stmt instanceof net.sf.jsqlparser.statement.select.Select))
                || (StringUtils.equalsIgnoreCase(statement.getCountMethod(), Statement.COUNTMETHOD_IGNORE))) {
            return Optional.empty();
        }
        if (StringUtils.equalsIgnoreCase(statement.getCountMethod(), Statement.COUNTMETHOD_AUTO)) {
            return executeCountSqlAuto(statement, stmt, runtime);
        } else {
            return executeCountSqlAssign(effectivePerson, statement, runtime);
        }
    }

    private Optional<Long> executeCountSqlAuto(Statement statement,
            net.sf.jsqlparser.statement.Statement stmt, Runtime runtime)
            throws Exception {
        Select select = (Select) stmt;
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            EntityManager em;
            if (StringUtils.equalsIgnoreCase(statement.getEntityCategory(), Statement.ENTITYCATEGORY_DYNAMIC)) {
                em = business.entityManagerContainer().get(DynamicBaseEntity.class);
            } else {
                Class<? extends JpaObject> cls = this.clazz(business, statement);
                em = business.entityManagerContainer().get(cls);
            }
            PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
            Table table = (Table) plainSelect.getFromItem();
            String txt = "select count(*) from " + table.getName();
            String whereClause = plainSelect.getWhere().toString();
            if (StringUtils.isNotBlank(whereClause)) {
                txt += " " + whereClause;
            }
            LOGGER.debug("executeCountSqlAuto：{}.", txt::toString);
            Query query = em.createQuery(txt);
            for (Parameter<?> p : query.getParameters()) {
                if (runtime.hasParameter(p.getName())) {
                    query.setParameter(p.getName(), runtime.getParameter(p.getName()));
                }
            }
            return Optional.of((Long) query.getSingleResult());
        }
    }

    private Optional<Long> executeCountSqlAssign(EffectivePerson effectivePerson, Statement statement,
            Runtime runtime) {
        String sql = "";
        if (StringUtils.equals(statement.getFormat(), Statement.FORMAT_SQL)) {
            sql = statement.getSqlCount();
        } else {
            sql = script(effectivePerson, runtime, statement.getSqlCountScriptText());
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            EntityManager em;
            if (StringUtils.equalsIgnoreCase(statement.getEntityCategory(), Statement.ENTITYCATEGORY_DYNAMIC)) {
                em = business.entityManagerContainer().get(DynamicBaseEntity.class);
            } else {
                Class<? extends JpaObject> cls = this.clazz(business, statement);
                em = business.entityManagerContainer().get(cls);
            }
            LOGGER.debug("executeCountSqlAssign：{}.", sql::toString);
            Query query = em.createQuery(sql);
            for (Parameter<?> p : query.getParameters()) {
                if (runtime.hasParameter(p.getName())) {
                    query.setParameter(p.getName(), runtime.getParameter(p.getName()));
                }
            }
            return Optional.of((Long) query.getSingleResult());
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return Optional.empty();
    }

    private Optional<Long> executeJpqlCount(EffectivePerson effectivePerson, Statement statement,
            net.sf.jsqlparser.statement.Statement stmt, Runtime runtime) throws Exception {
        if ((!(stmt instanceof net.sf.jsqlparser.statement.select.Select))
                || (StringUtils.equalsIgnoreCase(statement.getCountMethod(), Statement.COUNTMETHOD_IGNORE))) {
            return Optional.empty();
        }
        if (StringUtils.equalsIgnoreCase(statement.getCountMethod(), Statement.COUNTMETHOD_AUTO)) {
            return executeCountJpqlAuto(statement, stmt, runtime);
        } else {
            return executeCountJpqlAssign(effectivePerson, statement, runtime);
        }
    }

    private Optional<Long> executeCountJpqlAuto(Statement statement,
            net.sf.jsqlparser.statement.Statement stmt, Runtime runtime)
            throws Exception {
        Select select = (Select) stmt;
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            EntityManager em;
            if (StringUtils.equalsIgnoreCase(statement.getEntityCategory(), Statement.ENTITYCATEGORY_DYNAMIC)) {
                em = business.entityManagerContainer().get(DynamicBaseEntity.class);
            } else {
                Class<? extends JpaObject> cls = this.clazz(business, statement);
                em = business.entityManagerContainer().get(cls);
            }
            PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
            net.sf.jsqlparser.schema.Table table = (net.sf.jsqlparser.schema.Table) plainSelect.getFromItem();
            String txt = "select count(o) from " + table.getFullyQualifiedName() + " o";
            Expression exp = plainSelect.getWhere();
            if (null != exp) {
                String whereClause = exp.toString();
                if (StringUtils.isNotBlank(whereClause)) {
                    txt += " " + whereClause;
                }
            }
            LOGGER.debug("executeCountJpqlAuto：{}.", txt::toString);
            Query query = em.createQuery(txt);
            for (Parameter<?> p : query.getParameters()) {
                if (runtime.hasParameter(p.getName())) {
                    query.setParameter(p.getName(), runtime.getParameter(p.getName()));
                }
            }
            return Optional.of((Long) query.getSingleResult());
        }
    }

    private Optional<Long> executeCountJpqlAssign(EffectivePerson effectivePerson, Statement statement,
            Runtime runtime) {
        String jpql = "";
        if (StringUtils.equals(statement.getFormat(), Statement.FORMAT_JPQL)) {
            jpql = statement.getCountData();
        } else {
            jpql = script(effectivePerson, runtime, statement.getCountScriptText());
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            EntityManager em;
            if (StringUtils.equalsIgnoreCase(statement.getEntityCategory(), Statement.ENTITYCATEGORY_DYNAMIC)) {
                em = business.entityManagerContainer().get(DynamicBaseEntity.class);
            } else {
                Class<? extends JpaObject> cls = this.clazz(business, statement);
                em = business.entityManagerContainer().get(cls);
            }
            LOGGER.debug("executeCountJpqlAssign：{}.", jpql::toString);
            Query query = em.createQuery(jpql);
            for (Parameter<?> p : query.getParameters()) {
                if (runtime.hasParameter(p.getName())) {
                    query.setParameter(p.getName(), runtime.getParameter(p.getName()));
                }
            }
            return Optional.of((Long) query.getSingleResult());
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return Optional.empty();
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
