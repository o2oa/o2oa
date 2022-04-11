package com.x.query.service.processing.jaxrs.table;

import org.apache.commons.lang3.StringUtils;

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

class ActionUpdateWithBundle extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdateWithBundle.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, String bundle, JsonElement jsonElement)
			throws Exception {

		LOGGER.debug("execute:{}, flag:{}, bundle:{}.", effectivePerson::getDistinguishedName, () -> flag,
				() -> bundle);

		ClassLoader classLoader = Business.getDynamicEntityClassLoader();
		Thread.currentThread().setContextClassLoader(classLoader);
		ActionResult<Wo> result = new ActionResult<>();

		if (StringUtils.isEmpty(bundle)) {
			throw new ExceptionBundleEmpty();
		}

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
			JpaObject o = update(jsonElement, cls);
			JpaObject obj = emc.find(bundle, cls);
			emc.beginTransaction(cls);
			if (null != obj) {
				o.copyTo(obj, JpaObject.FieldsUnmodify);
				emc.check(obj, CheckPersistType.all);
			} else {
				emc.persist(o);
				emc.check(o, CheckPersistType.all);
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