package com.x.query.assemble.designer.jaxrs.table;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.schema.Table;

class ActionRowDelete extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionRowDelete.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String tableFlag, String id) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ClassLoader classLoader = Business.getDynamicEntityClassLoader();
		Thread.currentThread().setContextClassLoader(classLoader);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Table table = emc.flag(tableFlag, Table.class);
			Business business = new Business(emc);
			if (null == table) {
				throw new ExceptionEntityNotExist(tableFlag, Table.class);
			}
			this.check(effectivePerson, business, table);
			DynamicEntity dynamicEntity = new DynamicEntity(table.getName());
			@SuppressWarnings("unchecked")
			Class<? extends JpaObject> cls = (Class<JpaObject>) classLoader.loadClass(dynamicEntity.className());
			JpaObject o = emc.find(id, cls);
			Wo wo = new Wo();
			wo.setValue(false);
			if (null != o) {
				emc.beginTransaction(cls);
				emc.remove(o);
				emc.commit();
				wo.setValue(true);
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = -6967852502783946854L;

	}

}