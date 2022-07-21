package com.x.query.service.processing.jaxrs.table;

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
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.PropertyTools;
import com.x.base.core.project.tools.StringTools;
import com.x.query.core.entity.schema.Table;
import com.x.query.service.processing.Business;

import java.util.List;

class ActionUpdateWithBundle extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdateWithBundle.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, String bundle, JsonElement jsonElement)
			throws Exception {

		LOGGER.info("execute :{}, table flag:{}, bundle:{}， data:{}.", effectivePerson::getDistinguishedName, () -> flag,
				() -> bundle, jsonElement::toString);

		ClassLoader classLoader = Business.getDynamicEntityClassLoader();
		Thread.currentThread().setContextClassLoader(classLoader);
		ActionResult<Wo> result = new ActionResult<>();

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Table table = emc.flag(flag, Table.class);
			if (null == table) {
				throw new ExceptionEntityNotExist(flag, Table.class);
			}
			DynamicEntity dynamicEntity = new DynamicEntity(table.getName());
			@SuppressWarnings("unchecked")
			Class<? extends JpaObject> cls = (Class<? extends JpaObject>) classLoader
					.loadClass(dynamicEntity.className());
			List<? extends JpaObject> list = update(jsonElement, cls, bundle);
			List<? extends JpaObject> bundleList = null;
			if (PropertyTools.hasField(cls, DynamicEntity.BUNDLE_FIELD)) {
				bundleList = emc.listEqual(cls, DynamicEntity.BUNDLE_FIELD, bundle);
			}
			emc.beginTransaction(cls);
			if(ListTools.isNotEmpty(bundleList)){
				for(JpaObject o : bundleList){
					emc.remove(o);
				}
			}
			for(JpaObject o : list){
				o.setId(StringTools.uniqueToken());
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
