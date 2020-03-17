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
import com.x.base.core.project.jaxrs.WrapStringList;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.schema.Table;

import java.util.ArrayList;
import java.util.List;

class ActionRowSave extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String tableFlag, JsonElement jsonElement)
			throws Exception {
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
			Class<? extends JpaObject> cls = (Class<JpaObject>) Class.forName(dynamicEntity.className());
			List<Object> os = new ArrayList<>();
			if (jsonElement.isJsonArray()) {
				jsonElement.getAsJsonArray().forEach(o -> {
					JpaObject jo = gson.fromJson(o, cls);
					try {
						JpaObject oldjo = emc.find(jo.getId(), cls);
						if(oldjo!=null) {
							jo.copyTo(oldjo, JpaObject.FieldsUnmodify);
							jo = oldjo;
						}
					} catch (Exception e) {
					}
					os.add(jo);
				});
			} else if (jsonElement.isJsonObject()) {
				JpaObject jo = gson.fromJson(jsonElement, cls);
				try {
					JpaObject oldjo = emc.find(jo.getId(), cls);
					if(oldjo!=null) {
						jo.copyTo(oldjo, JpaObject.FieldsUnmodify);
						jo = oldjo;
					}
				} catch (Exception e) {
				}
				os.add(jo);
			}
			emc.beginTransaction(cls);
			for (Object o : os) {
				emc.persist((JpaObject) o, CheckPersistType.all);
			}
			emc.commit();
			Wo wo = new Wo();
			for (Object o : os) {
				wo.addValue(((JpaObject)o).getId(), true);
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapStringList {

	}

}