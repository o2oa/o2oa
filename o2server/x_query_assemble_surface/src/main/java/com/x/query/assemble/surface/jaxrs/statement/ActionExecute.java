package com.x.query.assemble.surface.jaxrs.statement;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Parameter;
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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.scripting.ScriptingEngine;
import com.x.query.assemble.surface.Business;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Table;
import com.x.query.core.express.statement.Runtime;

class ActionExecute extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionExecute.class);

	private ScriptingEngine scriptingEngine;

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
			Map<String, Object> parameter = XGsonBuilder.instance().fromJson(jsonElement,
					new TypeToken<Map<String, Object>>() {
					}.getType());

			Runtime runtime = this.runtime(effectivePerson, business, parameter, this.adjustPage(page),
					this.adjustSize(size));

			Object data = null;

//			if (StringUtils.equalsIgnoreCase(statement.getTableType(), Statement.TABLETYPE_OFFICIAL)) {
//				data = official(effectivePerson, business, statement, runtime);
//			} else {
//			data = dynamic(effectivePerson, business, statement, runtime);
//			}

			data = dynamic(effectivePerson, business, statement, runtime);
			if (StringUtils.isNotBlank(statement.getAfterScriptText())) {
				this.initScriptingEngine(business, effectivePerson);
				scriptingEngine.bindingData(data);
				data = scriptingEngine.eval(statement.getAfterScriptText());
			}
			result.setData(data);
			return result;
		}
	}

//	private Object official(EffectivePerson effectivePerson, Business business, Statement statement, Runtime runtime)
//			throws Exception {
//		Object data = null;
//		Class<? extends JpaObject> cls = null;
//		switch (Objects.toString(statement.getTable())) {
//		case "Work":
//			cls = Work.class;
//			break;
//		case "WorkCompleted":
//			cls = WorkCompleted.class;
//			break;
//		case "Task":
//			cls = Task.class;
//			break;
//		case "TaskCompleted":
//			cls = TaskCompleted.class;
//			break;
//		case "Read":
//			cls = Read.class;
//			break;
//		case "ReadCompleted":
//			cls = ReadCompleted.class;
//			break;
//		case "Review":
//			cls = Review.class;
//			break;
//		default:
//			cls = (Class<JpaObject>) Class.forName(statement.getTable());
//			break;
//		}
//		EntityManager em = business.entityManagerContainer().get(cls);
//		Query query = em.createQuery(statement.getData());
//		for (Parameter<?> p : query.getParameters()) {
//			if (runtime.hasParameter(p.getName())) {
//				query.setParameter(p.getName(), runtime.getParameter(p.getName()));
//			}
//		}
//		query.setFirstResult((runtime.page - 1) * runtime.size);
//		query.setMaxResults(runtime.size);
//		if (StringUtils.equalsIgnoreCase(statement.getType(), Statement.TYPE_SELECT)) {
//			data = query.getResultList();
//		} else {
//			throw new ExceptionModifyOfficialTable();
//		}
//		return data;
//	}

	private Object dynamic(EffectivePerson effectivePerson, Business business, Statement statement, Runtime runtime)
			throws Exception {
		Object data = null;
		Table table = business.entityManagerContainer().flag(statement.getTable(), Table.class);
		if (null == table) {
			throw new ExceptionEntityNotExist(statement.getTable(), Table.class);
		}
		DynamicEntity dynamicEntity = new DynamicEntity(table.getName());
		@SuppressWarnings("unchecked")
		Class<? extends JpaObject> cls = (Class<JpaObject>) Class.forName(dynamicEntity.className());
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

	private void initScriptingEngine(Business business, EffectivePerson effectivePerson) {
		if (null == this.scriptingEngine) {
			this.scriptingEngine = business.createScriptEngine().bindingEffectivePerson(effectivePerson);
		}
	}

}