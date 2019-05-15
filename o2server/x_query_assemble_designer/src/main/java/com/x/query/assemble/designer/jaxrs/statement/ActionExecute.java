package com.x.query.assemble.designer.jaxrs.statement;

import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.scripting.ScriptingEngine;
import com.x.query.assemble.designer.Business;
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
			this.check(effectivePerson, business, statement);
			Table table = emc.flag(statement.getTable(), Table.class);
			if (null == table) {
				throw new ExceptionEntityNotExist(statement.getTable(), Table.class);
			}
			Map<String, Object> parameter = XGsonBuilder.instance().fromJson(jsonElement,
					new TypeToken<Map<String, Object>>() {
					}.getType());

			this.beforeScript(business, effectivePerson, statement, parameter);

			DynamicEntity dynamicEntity = new DynamicEntity(table.getName());
			@SuppressWarnings("unchecked")
			Class<? extends JpaObject> cls = (Class<JpaObject>) Class.forName(dynamicEntity.className());
			EntityManager em = emc.get(cls);
			Query query = em.createQuery(statement.getData());
			for (Entry<String, Object> en : parameter.entrySet()) {
				if (StringUtils.equals(en.getKey(), "firstResult") && (en.getValue() != null)) {
					int firstResult = NumberUtils.toInt(en.getValue().toString(), -1);
					if (firstResult > 0) {
						query.setFirstResult(firstResult);
					}
				} else if (StringUtils.equals(en.getKey(), "maxResults") && (en.getValue() != null)) {
					int maxResults = NumberUtils.toInt(en.getValue().toString(), -1);
					if (maxResults > 0) {
						query.setMaxResults(maxResults);
					}
				} else {
					query.setParameter(en.getKey(), en.getValue());
				}
			}
			Object data = null;
			if (StringUtils.equalsIgnoreCase(statement.getType(), Statement.TYPE_SELECT)) {
				data = query.getResultList();
			} else {
				emc.beginTransaction(cls);
				data = query.executeUpdate();
				emc.commit();
			}
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