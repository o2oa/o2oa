package com.x.processplatform.service.processing.jaxrs.workcompleted;

import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.dataitem.DataItem;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.query.core.entity.Item;

class ActionMergeItem extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionMergeItem.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		String job = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			WorkCompleted workCompleted = emc.find(id, WorkCompleted.class);
			if (null == workCompleted) {
				throw new ExceptionEntityNotExist(id, WorkCompleted.class);
			}
			job = workCompleted.getJob();
			emc.beginTransaction(WorkCompleted.class);
			List<Item> os = emc.listEqualAndEqual(Item.class, DataItem.bundle_FIELDNAME, job,
					DataItem.itemCategory_FIELDNAME, ItemCategory.pp);
			DataItemConverter<Item> converter = new DataItemConverter<>(Item.class);
			JsonElement jsonElement = converter.assemble(os);
			workCompleted.setData(gson.fromJson(jsonElement, Data.class));
			emc.commit();
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			emc.beginTransaction(Item.class);
			List<Item> os = emc.listEqualAndEqual(Item.class, DataItem.bundle_FIELDNAME, job,
					DataItem.itemCategory_FIELDNAME, ItemCategory.pp);
			for (Item item : os) {
				emc.remove(item);
			}
			emc.commit();
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			WorkCompleted workCompleted = emc.find(id, WorkCompleted.class);
			emc.beginTransaction(WorkCompleted.class);
			workCompleted.setMerged(true);
			emc.commit();
		}
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setId(id);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = 8166148918001178788L;
	}

}
