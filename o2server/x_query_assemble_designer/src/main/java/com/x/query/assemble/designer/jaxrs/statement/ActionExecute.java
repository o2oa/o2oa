package com.x.query.assemble.designer.jaxrs.statement;

import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.scripting.AbstractRuntime;
import com.x.base.core.project.scripting.ScriptingEngine;
import com.x.query.assemble.designer.Business;
import com.x.query.assemble.designer.DynamicEntity;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Table;

class ActionExecute extends BaseAction {

	private ScriptingEngine scriptingEngine;

	ActionResult<Object> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement)
			throws Exception {

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
			Table table = emc.find(statement.getTable(), Table.class);
			if (null == table) {
				throw new ExceptionEntityNotExist(statement.getTable(), Table.class);
			}

			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

			this.beforeScript(business, statement, wi);

			DynamicEntity dynamicEntity = new DynamicEntity(table.getName());
			EntityManager em = emc.get((Class<JpaObject>) Class.forName(dynamicEntity.className()));
			Query query = em.createQuery(statement.getData());
			for (Entry<String, Object> en : wi.getParameter().entrySet()) {
				query.setParameter(en.getKey(), en.getValue());
			}
			Object data = query.getResultList();
			if (StringUtils.isNotBlank(statement.getAfterScriptText())) {
				this.initScriptingEngine(business);
				scriptingEngine.bindingData(data);
				data = scriptingEngine.eval(statement.getBeforeScriptText());
			}
			result.setData(data);
			return result;
		}
	}

	private void beforeScript(Business business, Statement statement, Wi wi) throws Exception {
		if (StringUtils.isNotBlank(statement.getBeforeScriptText())) {
			this.initScriptingEngine(business);
			scriptingEngine.bindingParameter(wi);
			scriptingEngine.eval(statement.getBeforeScriptText());
		}
	}

	private void initScriptingEngine(Business business) {
		if (null == this.scriptingEngine) {
			this.scriptingEngine = business.createScriptEngine();
		}
	}

	public static class Wi extends AbstractRuntime {

	}

}