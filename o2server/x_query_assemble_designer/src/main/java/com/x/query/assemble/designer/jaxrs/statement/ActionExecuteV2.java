package com.x.query.assemble.designer.jaxrs.statement;

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
import com.x.base.core.project.script.ScriptFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.webservices.WebservicesClient;
import com.x.organization.core.express.Organization;
import com.x.query.assemble.designer.Business;
import com.x.query.assemble.designer.ThisApplication;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Table;
import com.x.query.core.express.statement.Runtime;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.SimpleScriptContext;
import java.util.List;
import java.util.Objects;

class ActionExecuteV2 extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionExecuteV2.class);

	private final static String[] pageKeys = { "GROUP BY", " COUNT(" };
	private final static String JOIN_KEY = " JOIN ";
	private final static String JOIN_ON_KEY = " ON ";

	ActionResult<Object> execute(EffectivePerson effectivePerson, String flag, String mode, Integer page, Integer size,
			JsonElement jsonElement) throws Exception {

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Object> result = new ActionResult<>();
			Business business = new Business(emc);
			Statement statement = emc.flag(flag, Statement.class);
			if (null == statement) {
				throw new ExceptionEntityNotExist(flag, Statement.class);
			}
			if (!business.executable(effectivePerson, statement)) {
				throw new ExceptionAccessDenied(effectivePerson, statement);
			}

			Runtime runtime = this.runtime(effectivePerson, jsonElement, business, page, size);

			Object data = null;
			Object count = null;
			switch (mode){
				case Statement.MODE_DATA:
					switch (Objects.toString(statement.getFormat(), "")) {
						case Statement.FORMAT_SCRIPT:
							data = this.script(effectivePerson, business, statement, runtime, mode);
							break;
						default:
							data = this.jpql(effectivePerson, business, statement, runtime, mode);
							break;
					}
					result.setData(data);
					break;
				case Statement.MODE_COUNT:
					switch (Objects.toString(statement.getFormat(), "")) {
						case Statement.FORMAT_SCRIPT:
							count = this.script(effectivePerson, business, statement, runtime, mode);
							break;
						default:
							count = this.jpql(effectivePerson, business, statement, runtime, mode);
							break;
					}
					result.setData(count);
					result.setCount((Long)count);
					break;
				default:
					switch (Objects.toString(statement.getFormat(), "")) {
						case Statement.FORMAT_SCRIPT:
							data = this.script(effectivePerson, business, statement, runtime, Statement.MODE_DATA);
							count = this.script(effectivePerson, business, statement, runtime, Statement.MODE_COUNT);
							break;
						default:
							data = this.jpql(effectivePerson, business, statement, runtime, Statement.MODE_DATA);
							count = this.jpql(effectivePerson, business, statement, runtime, Statement.MODE_COUNT);
							break;
					}
					result.setData(data);
					result.setCount((Long)count);
			}
			return result;
		}
	}

	private Object script(EffectivePerson effectivePerson, Business business, Statement statement, Runtime runtime, String mode)
			throws Exception {
		Object data = null;
		ScriptContext scriptContext = this.scriptContext(effectivePerson, business, runtime);
		ScriptFactory.initialServiceScriptText().eval(scriptContext);
		String scriptText = statement.getScriptText();
		if(Statement.MODE_COUNT.equals(mode)) {
			scriptText = statement.getCountScriptText();
		}
		Object o = ScriptFactory.scriptEngine.eval(ScriptFactory.functionalization(scriptText),
				scriptContext);
		String jpql = ScriptFactory.asString(o);
		Class<? extends JpaObject> cls = this.clazz(business, statement);
		EntityManager em;
		if(StringUtils.equalsIgnoreCase(statement.getEntityCategory(), Statement.ENTITYCATEGORY_DYNAMIC)
				&& StringUtils.equalsIgnoreCase(statement.getType(), Statement.TYPE_SELECT)){
			em = business.entityManagerContainer().get(DynamicBaseEntity.class);
		}else{
			em = business.entityManagerContainer().get(cls);
		}
		jpql = this.joinSql(jpql, business);
		Query query;
		String upJpql = jpql.toUpperCase();
		if(upJpql.indexOf(JOIN_KEY) > -1 && upJpql.indexOf(JOIN_ON_KEY) > -1){
			query = em.createNativeQuery(jpql);
		}else{
			query = em.createQuery(jpql);
		}
		for (Parameter<?> p : query.getParameters()) {
			if (runtime.hasParameter(p.getName())) {
				query.setParameter(p.getName(), runtime.getParameter(p.getName()));
			}
		}
		if (StringUtils.equalsIgnoreCase(statement.getType(), Statement.TYPE_SELECT)) {
			if(Statement.MODE_COUNT.equals(mode)) {
				data = query.getSingleResult();
			}else{
				if(isPageSql(jpql)) {
					query.setFirstResult((runtime.page - 1) * runtime.size);
					query.setMaxResults(runtime.size);
				}
				data = query.getResultList();
			}
		} else {
			business.entityManagerContainer().beginTransaction(cls);
			data = query.executeUpdate();
			business.entityManagerContainer().commit();
		}
		return data;
	}

	private Object jpql(EffectivePerson effectivePerson, Business business, Statement statement, Runtime runtime, String mode)
			throws Exception {
		Object data = null;
		Class<? extends JpaObject> cls = this.clazz(business, statement);
		EntityManager em;
		if(StringUtils.equalsIgnoreCase(statement.getEntityCategory(), Statement.ENTITYCATEGORY_DYNAMIC)
				&& StringUtils.equalsIgnoreCase(statement.getType(), Statement.TYPE_SELECT)){
			em = business.entityManagerContainer().get(DynamicBaseEntity.class);
		}else{
			em = business.entityManagerContainer().get(cls);
		}
		String jpql = statement.getData();
		if(Statement.MODE_COUNT.equals(mode)) {
			jpql = statement.getCountData();
		}
		jpql = this.joinSql(jpql, business);
		Query query;
		String upJpql = jpql.toUpperCase();
		if(upJpql.indexOf(JOIN_KEY) > -1 && upJpql.indexOf(JOIN_ON_KEY) > -1){
			query = em.createNativeQuery(jpql);
		}else{
			query = em.createQuery(jpql);
		}
		for (Parameter<?> p : query.getParameters()) {
			if (runtime.hasParameter(p.getName())) {
				query.setParameter(p.getName(), runtime.getParameter(p.getName()));
			}
		}
		if (StringUtils.equalsIgnoreCase(statement.getType(), Statement.TYPE_SELECT)) {
			if(Statement.MODE_COUNT.equals(mode)) {
				data = query.getSingleResult();
			}else{
				if(isPageSql(jpql)) {
					query.setFirstResult((runtime.page - 1) * runtime.size);
					query.setMaxResults(runtime.size);
				}
				data = query.getResultList();
			}
		} else {
			business.entityManagerContainer().beginTransaction(cls);
			data = Integer.valueOf(query.executeUpdate());
			business.entityManagerContainer().commit();
		}
		return data;
	}

	private boolean isPageSql(String sql){
		sql = sql.toUpperCase().replaceAll("\\s{1,}", " ");
		for (String key : pageKeys) {
			if (sql.indexOf(key) > -1) {
				return false;
			}
		}
		return true;
	}

	private Class<? extends JpaObject> clazz(Business business, Statement statement) throws Exception {
		Class<? extends JpaObject> cls = null;
		if (StringUtils.equals(Statement.ENTITYCATEGORY_OFFICIAL, statement.getEntityCategory())
				|| StringUtils.equals(Statement.ENTITYCATEGORY_CUSTOM, statement.getEntityCategory())) {
			cls = (Class<? extends JpaObject>) Class.forName(statement.getEntityClassName());
		} else {
			Table table = business.entityManagerContainer().flag(statement.getTable(), Table.class);
			if (null == table) {
				throw new ExceptionEntityNotExist(statement.getTable(), Table.class);
			}
			DynamicEntity dynamicEntity = new DynamicEntity(table.getName());
			cls = (Class<? extends JpaObject>) Class.forName(dynamicEntity.className());
		}
		return cls;
	}

	private ScriptContext scriptContext(EffectivePerson effectivePerson, Business business, Runtime runtime)
			throws Exception {
		ScriptContext scriptContext = new SimpleScriptContext();
		Resources resources = new Resources();
		resources.setEntityManagerContainer(business.entityManagerContainer());
		resources.setContext(ThisApplication.context());
		resources.setApplications(ThisApplication.context().applications());
		resources.setWebservicesClient(new WebservicesClient());
		resources.setOrganization(new Organization(ThisApplication.context()));
		Bindings bindings = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.put(ScriptFactory.BINDING_NAME_RESOURCES, resources);
		bindings.put(ScriptFactory.BINDING_NAME_EFFECTIVEPERSON, effectivePerson);
		bindings.put(ScriptFactory.BINDING_NAME_PARAMETERS, gson.toJson(runtime.getParameters()));
		return scriptContext;
	}

	private String joinSql(String sql, Business business) throws Exception{
		String upSql = sql.toUpperCase();
		if(upSql.indexOf(JOIN_KEY) > -1 && upSql.indexOf(JOIN_ON_KEY) > -1){
			sql = sql.replaceAll("\\.", ".x");
			sql = sql.replaceAll("\\.x\\*", ".*");
			List<Table> tables = business.entityManagerContainer().fetchEqual(Table.class, ListTools.toList(Table.name_FIELDNAME), Table.status_FIELDNAME, Table.STATUS_build);
			for (Table table : tables){
				sql = sql.replaceAll(" "+table.getName()+" ", " "+DynamicEntity.TABLE_PREFIX + table.getName().toUpperCase()+" ");
			}
		}
		return sql;
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
