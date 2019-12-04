package com.x.query.assemble.surface.jaxrs.statement;

import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.SimpleScriptContext;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.script.AbstractResources;
import com.x.base.core.project.script.ScriptFactory;
import com.x.base.core.project.webservices.WebservicesClient;
import com.x.organization.core.express.Organization;
import com.x.query.assemble.surface.Business;
import com.x.query.assemble.surface.ThisApplication;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Table;
import com.x.query.core.express.statement.Runtime;

import net.sf.ehcache.Element;

class ActionExecute extends BaseAction {

	ActionResult<Object> execute(EffectivePerson effectivePerson, String flag, Integer page, Integer size,
			JsonElement jsonElement) throws Exception {

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Object> result = new ActionResult<>();
			Business business = new Business(emc);
			Statement statement = this.getStatement(business, flag);
			if (null == statement) {
				throw new ExceptionEntityNotExist(flag, Statement.class);
			}

			if (!business.executable(effectivePerson, statement)) {
				throw new ExceptionAccessDenied(effectivePerson, statement);
			}

			Runtime runtime = this.runtime(effectivePerson, jsonElement, business, page, size);

			Object data = null;

			switch (Objects.toString(statement.getFormat(), "")) {
			case Statement.FORMAT_SCRIPT:
				data = this.script(effectivePerson, business, statement, runtime);
				break;
			default:
				data = this.jpql(effectivePerson, business, statement, runtime);
				break;
			}
			result.setData(data);
			return result;
		}
	}

	private Statement getStatement(Business business, String flag) throws Exception {
		Statement statement = null;
		String statmentCacheKey = ApplicationCache.concreteCacheKey(this.getClass(), flag);
		Element element = this.cache.get(statmentCacheKey);
		if ((null != element) && null != element.getObjectValue()) {
			statement = (Statement) element.getObjectValue();
		} else {
			statement = business.entityManagerContainer().flag(flag, Statement.class);
			if (null != statement) {
				cache.put(new Element(statmentCacheKey, statement));
			}
		}
		return statement;
	}

	private CompiledScript getCompiledScriptOfScriptText(Statement statement) throws Exception {
		CompiledScript compiledScript = null;
		String compiledScriptCacheKey = ApplicationCache.concreteCacheKey(this.getClass(), statement.getId(),
				Statement.scriptText_FIELDNAME);
		Element element = this.cache.get(compiledScriptCacheKey);
		if ((null != element) && null != element.getObjectValue()) {
			compiledScript = (CompiledScript) element.getObjectValue();
		} else {
			compiledScript = ScriptFactory.compile(ScriptFactory.functionalization(statement.getScriptText()));
			cache.put(new Element(compiledScriptCacheKey, compiledScript));
		}
		return compiledScript;
	}

	private Object script(EffectivePerson effectivePerson, Business business, Statement statement, Runtime runtime)
			throws Exception {
		Object data = null;

		ScriptContext scriptContext = this.scriptContext(effectivePerson, business, runtime);

		ScriptFactory.initialServiceScriptText().eval(scriptContext);

		CompiledScript compiledScript = this.getCompiledScriptOfScriptText(statement);
		Object o = compiledScript.eval(scriptContext);
		String text = ScriptFactory.asString(o);
		Class<? extends JpaObject> cls = this.clazz(business, statement);
		EntityManager em = business.entityManagerContainer().get(cls);
		Query query = em.createQuery(text);
		for (Parameter<?> p : query.getParameters()) {
			if (runtime.hasParameter(p.getName())) {
				query.setParameter(p.getName(), runtime.getParameter(p.getName()));
			}
		}
		query.setFirstResult((runtime.page - 1) * runtime.size);
		query.setMaxResults(runtime.size);
		if (StringUtils.equalsIgnoreCase(statement.getType(), Statement.TYPE_SELECT)) {
			data = query.getResultList();
		} else {
			business.entityManagerContainer().beginTransaction(cls);
			data = query.executeUpdate();
			business.entityManagerContainer().commit();
		}
		return data;
	}

	private Object jpql(EffectivePerson effectivePerson, Business business, Statement statement, Runtime runtime)
			throws Exception {
		Object data = null;
		Class<? extends JpaObject> cls = this.clazz(business, statement);
		EntityManager em = business.entityManagerContainer().get(cls);
		Query query = em.createQuery(statement.getData());
		for (Parameter<?> p : query.getParameters()) {
			if (runtime.hasParameter(p.getName())) {
				query.setParameter(p.getName(), runtime.getParameter(p.getName()));
			}
		}
		query.setFirstResult((runtime.page - 1) * runtime.size);
		query.setMaxResults(runtime.size);
		if (StringUtils.equalsIgnoreCase(statement.getType(), Statement.TYPE_SELECT)) {
			data = query.getResultList();
		} else {
			business.entityManagerContainer().beginTransaction(cls);
			data = query.executeUpdate();
			business.entityManagerContainer().commit();
		}
		return data;
	}

	@SuppressWarnings("unchecked")
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