package com.x.processplatform.assemble.surface.jaxrs.data;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.item.ItemConverter;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.core.entity.content.DataItem;
import com.x.processplatform.core.entity.content.DataLobItem;
import com.x.processplatform.core.entity.content.Work;

class ActionCreateWithWork extends ActionBase {
	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			Business business = new Business(emc);
			Work work = emc.find(id, Work.class);
			if (null == work) {
				throw new WorkNotExistedException(id);
			}
			Control control = business.getControlOfWorkComplex(effectivePerson, work);
			if (BooleanUtils.isNotTrue(control.getAllowSave())) {
				throw new WorkAccessDeniedException(effectivePerson.getName(), work.getTitle(), work.getId());
			}
			if (business.dataItem().countWithJobWithPath(work.getJob()) > 0) {
				throw new DataAlreadyExistedException(work.getTitle(), work.getId());
			}
			ItemConverter<DataItem> converter = new ItemConverter<>(DataItem.class);
			List<DataItem> adds = converter.disassemble(jsonElement);
			emc.beginTransaction(DataItem.class);
			emc.beginTransaction(DataLobItem.class);
			for (DataItem o : adds) {
				this.fill(o, work);
				if (o.isLobItem()) {
					DataLobItem lob = this.concreteDataLobItem(o);
					business.entityManagerContainer().persist(lob);
				}
				business.entityManagerContainer().persist(o);
			}
			emc.commit();
			WrapOutId wrap = new WrapOutId(work.getId());
			result.setData(wrap);
			return result;
		}
	}
}