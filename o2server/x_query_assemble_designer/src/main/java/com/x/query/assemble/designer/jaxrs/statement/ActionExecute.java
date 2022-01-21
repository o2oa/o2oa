package com.x.query.assemble.designer.jaxrs.statement;

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
import com.x.base.core.project.script.AbstractResources;
import com.x.base.core.project.scripting.JsonScriptingExecutor;
import com.x.base.core.project.scripting.ScriptingFactory;
import com.x.base.core.project.webservices.WebservicesClient;
import com.x.organization.core.express.Organization;
import com.x.query.assemble.designer.Business;
import com.x.query.assemble.designer.ThisApplication;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Table;
import com.x.query.core.express.statement.Runtime;

class ActionExecute extends BaseAction {

	private static final String[] pageKeys = { "GROUP BY", " COUNT(" };

	ActionResult<Object> execute(EffectivePerson effectivePerson, String flag, Integer page, Integer size,
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

			if (StringUtils.equalsIgnoreCase(statement.getFormat(), Statement.FORMAT_SCRIPT)) {
				data = this.script(effectivePerson, business, statement, runtime);
			} else {
				data = this.jpql(business, statement, runtime);
			}

			result.setData(data);
			return result;
		}
	}

	private Object script(EffectivePerson effectivePerson, Business business, Statement statement, Runtime runtime)
			throws Exception {
		Object data = null;
		ScriptContext scriptContext = this.scriptContext(effectivePerson, runtime);
		CompiledScript cs = ScriptingFactory.functionalizationCompile(statement.getScriptText());
		String text = JsonScriptingExecutor.evalString(cs, scriptContext);
		Class<? extends JpaObject> cls = this.clazz(business, statement);
		EntityManager em;
		if (StringUtils.equalsIgnoreCase(statement.getEntityCategory(), Statement.ENTITYCATEGORY_DYNAMIC)
				&& StringUtils.equalsIgnoreCase(statement.getType(), Statement.TYPE_SELECT)) {
			em = business.entityManagerContainer().get(DynamicBaseEntity.class);
		} else {
			em = business.entityManagerContainer().get(cls);
		}
		Query query = em.createQuery(text);
		for (Parameter<?> p : query.getParameters()) {
			if (runtime.hasParameter(p.getName())) {
				query.setParameter(p.getName(), runtime.getParameter(p.getName()));
			}
		}
		if (StringUtils.equalsIgnoreCase(statement.getType(), Statement.TYPE_SELECT)) {
			if (isPageSql(text)) {
				query.setFirstResult((runtime.page - 1) * runtime.size);
				query.setMaxResults(runtime.size);
			}
			data = query.getResultList();
		} else {
			business.entityManagerContainer().beginTransaction(cls);
			data = query.executeUpdate();
			business.entityManagerContainer().commit();
		}
		return data;
	}

	private Object jpql(Business business, Statement statement, Runtime runtime) throws Exception {
		Object data = null;
		Class<? extends JpaObject> cls = this.clazz(business, statement);
		EntityManager em;
		if (StringUtils.equalsIgnoreCase(statement.getEntityCategory(), Statement.ENTITYCATEGORY_DYNAMIC)
				&& StringUtils.equalsIgnoreCase(statement.getType(), Statement.TYPE_SELECT)) {
			em = business.entityManagerContainer().get(DynamicBaseEntity.class);
		} else {
			em = business.entityManagerContainer().get(cls);
		}
		Query query = em.createQuery(statement.getData());
		for (Parameter<?> p : query.getParameters()) {
			if (runtime.hasParameter(p.getName())) {
				query.setParameter(p.getName(), runtime.getParameter(p.getName()));
			}
		}
		if (StringUtils.equalsIgnoreCase(statement.getType(), Statement.TYPE_SELECT)) {
			if (isPageSql(statement.getData())) {
				query.setFirstResult((runtime.page - 1) * runtime.size);
				query.setMaxResults(runtime.size);
			}
			data = query.getResultList();
		} else {
			business.entityManagerContainer().beginTransaction(cls);
			data = Integer.valueOf(query.executeUpdate());
			business.entityManagerContainer().commit();
		}
		return data;
	}

	private boolean isPageSql(String sql) {
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

	private ScriptContext scriptContext(EffectivePerson effectivePerson, Runtime runtime) throws Exception {
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
