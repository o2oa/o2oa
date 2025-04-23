package com.x.query.assemble.designer.jaxrs.table;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapInteger;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.schema.Table;
import java.util.ArrayList;
import java.util.List;

class ActionRowSave extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionRowSave.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String tableFlag, JsonElement jsonElement)
			throws Exception {
		LOGGER.info("bach save execute:{}.", effectivePerson::getDistinguishedName);
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
			List<Object> os = new ArrayList<>();
			if (jsonElement.isJsonArray()) {
				jsonElement.getAsJsonArray().forEach(o -> {
					JpaObject jo = gson.fromJson(o, cls);
					if(o.getAsJsonObject().has("id")) {
						try {
							JpaObject oldjo = emc.find(jo.getId(), cls);
							if (oldjo != null) {
								jo.copyTo(oldjo, JpaObject.FieldsUnmodify);
								return;
							}
						} catch (Exception e) {
						}
					}
					os.add(jo);
				});
			} else if (jsonElement.isJsonObject()) {
				JpaObject jo = gson.fromJson(jsonElement, cls);
				try {
					JpaObject oldjo = emc.find(jo.getId(), cls);
					if (oldjo != null) {
						jo.copyTo(oldjo, JpaObject.FieldsUnmodify);
						jo = oldjo;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				os.add(jo);
			}
			int size = 0;
			try {
				for(List<Object> subOs : ListTools.batch(os, 200)) {
					emc.beginTransaction(cls);
					for (Object o : subOs) {
						emc.persist((JpaObject) o, CheckPersistType.all);
					}
					emc.commit();
					size = size + subOs.size();
					LOGGER.info("has insert {}/{} rows into table:{}.", size, os.size(), table.getName());
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			Wo wo = new Wo();
			wo.setValue(size);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapInteger {

		private static final long serialVersionUID = 6370333126842440871L;

	}

}
