package com.x.query.assemble.surface.jaxrs.table;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.assemble.surface.Business;
import com.x.query.core.entity.schema.Table;

class ActionRowPartUpdate extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionRowPartUpdate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String tableFlag, String id, JsonElement jsonElement)
			throws Exception {
		LOGGER.debug("execute:{}, table:{}, id:{}.", effectivePerson::getDistinguishedName, () -> tableFlag, () -> id);
		ClassLoader classLoader = Business.getDynamicEntityClassLoader();
		Thread.currentThread().setContextClassLoader(classLoader);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Table table = emc.flag(tableFlag, Table.class);
			Business business = new Business(emc);
			if (null == table) {
				throw new ExceptionEntityNotExist(tableFlag, Table.class);
			}
			if (!business.editable(effectivePerson, table)) {
				throw new ExceptionAccessDenied(effectivePerson, table);
			}
			DynamicEntity dynamicEntity = new DynamicEntity(table.getName());
			@SuppressWarnings("unchecked")
			Class<? extends JpaObject> cls = (Class<? extends JpaObject>) classLoader
					.loadClass(dynamicEntity.className());
			JpaObject o = emc.find(id, cls);
			Wo wo = new Wo();
			wo.setValue(false);
			if (null != o) {
				JpaObject n = XGsonBuilder.instance().fromJson(jsonElement, cls);
				n.copyTo(o, true, JpaObject.FieldsUnmodify);
				emc.beginTransaction(cls);
				emc.check(o, CheckPersistType.all);
				emc.commit();
				wo.setValue(true);
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 7570720614789228246L;

	}

}
