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
import com.x.base.core.project.tools.StringTools;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.schema.Table;
import org.apache.commons.lang3.StringUtils;

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
			int size = 1;
			emc.beginTransaction(cls);
			if (jsonElement.isJsonArray()) {
				for (JsonElement o : jsonElement.getAsJsonArray()){
					JpaObject jo = gson.fromJson(o, cls);
					if(StringUtils.isBlank(jo.getId())){
						jo.setId(StringTools.uniqueToken());
					}
					if(o.getAsJsonObject().has("id")) {
						JpaObject oldJo = emc.find(jo.getId(), cls);
						if (oldJo != null) {
							jo.copyTo(oldJo, JpaObject.FieldsUnmodify);
							continue;
						}
					}
					emc.persist(jo, CheckPersistType.all);
				}
				size = jsonElement.getAsJsonArray().size();
			} else if (jsonElement.isJsonObject()) {
				JpaObject jo = gson.fromJson(jsonElement, cls);
				if(StringUtils.isBlank(jo.getId())){
					jo.setId(StringTools.uniqueToken());
				}
				JpaObject oldJo = emc.find(jo.getId(), cls);
				if (oldJo != null) {
					jo.copyTo(oldJo, JpaObject.FieldsUnmodify);
				}else{
					emc.persist(jo, CheckPersistType.all);
				}
			}
			emc.commit();

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
