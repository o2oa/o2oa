package com.x.query.assemble.surface.jaxrs.statement;

import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.scripting.ScriptingEngine;
import com.x.query.assemble.surface.Business;
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

			Map<String, Object> parameter = XGsonBuilder.instance().fromJson(jsonElement,
					new TypeToken<Map<String, Object>>() {
					}.getType());

			this.beforeScript(business, effectivePerson, statement, parameter);

			DynamicEntity dynamicEntity = new DynamicEntity(table.getName());
			@SuppressWarnings("unchecked")
			EntityManager em = emc.get((Class<JpaObject>) Class.forName(dynamicEntity.className()));
			Query query = em.createQuery(statement.getData());
			for (Entry<String, Object> en : parameter.entrySet()) {
				query.setParameter(en.getKey(), en.getValue());
			}
			Object data = query.getResultList();
			if (StringUtils.isNotBlank(statement.getAfterScriptText())) {
				this.initScriptingEngine(business, effectivePerson);
				scriptingEngine.bindingData(data);
				data = scriptingEngine.eval(statement.getAfterScriptText());
			}
			result.setData(data);
			return result;
		}
	}

	private void beforeScript(Business business, EffectivePerson effectivePerson, Statement statement,
			Map<String, Object> parameter) throws Exception {
		if (StringUtils.isNotBlank(statement.getBeforeScriptText())) {
			this.initScriptingEngine(business, effectivePerson);
			scriptingEngine.bindingParameter(parameter);
			scriptingEngine.eval(statement.getBeforeScriptText());
		}
	}

	private void initScriptingEngine(Business business, EffectivePerson effectivePerson) {
		if (null == this.scriptingEngine) {
			this.scriptingEngine = business.createScriptEngine().bindingEffectivePerson(effectivePerson);

		}
	}

}