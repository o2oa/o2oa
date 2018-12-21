package com.x.cms.assemble.control.jaxrs.data;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.DocumentDataHelper;
import com.x.cms.assemble.control.jaxrs.data.exception.ExceptionDataAlreadyExist;
import com.x.cms.assemble.control.jaxrs.data.exception.ExceptionDocumentNotExist;
import com.x.cms.core.entity.Document;

class ActionCreateWithDocument extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Document document = emc.find(id, Document.class);
			if (null == document) {
				throw new ExceptionDocumentNotExist(id);
			}
			if (business.itemFactory().countWithDocmentWithPath(document.getId()) > 0) {
				throw new ExceptionDataAlreadyExist(document.getTitle(), document.getId());
			}
			
			DocumentDataHelper documentDataHelper = new DocumentDataHelper( emc, document );
			documentDataHelper.update(jsonElement);
			emc.commit();
			
//			DataItemConverter<Item> converter = new DataItemConverter<>(Item.class);
//			List<Item> adds = converter.disassemble(jsonElement);
//			emc.beginTransaction(Item.class);
//			for (Item o : adds) {
//				this.fill(o, document);
//				business.entityManagerContainer().persist(o);
//			}
//			emc.commit();
			Wo wo = new Wo();
			wo.setId(document.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {
	}
}