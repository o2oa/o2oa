package com.x.query.service.processing.jaxrs.table;

import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.StringTools;
import org.apache.commons.beanutils.PropertyUtilsBean;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.core.entity.schema.Table;
import com.x.query.service.processing.Business;

import java.util.List;

class ActionInsert extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionInsert.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, flag:{}.", effectivePerson::getDistinguishedName, () -> flag);

		ClassLoader classLoader = Business.getDynamicEntityClassLoader();
		Thread.currentThread().setContextClassLoader(classLoader);
		ActionResult<Wo> result = new ActionResult<>();

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			LOGGER.debug("execute:{}, flag:{}.", effectivePerson::getDistinguishedName, () -> flag);
			Table table = emc.flag(flag, Table.class);
			if (null == table) {
				throw new ExceptionEntityNotExist(flag, Table.class);
			}
			DynamicEntity dynamicEntity = new DynamicEntity(table.getName());
			@SuppressWarnings("unchecked")
			Class<? extends JpaObject> cls = (Class<? extends JpaObject>) classLoader
					.loadClass(dynamicEntity.className());
			List<? extends JpaObject> list = update(jsonElement, cls, null);
			emc.beginTransaction(cls);
			for(JpaObject o : list){
				emc.persist(o, CheckPersistType.all);
			}
			emc.commit();
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
		}
		return result;
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 7570720614789228246L;

	}

}
