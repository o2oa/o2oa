package com.x.processplatform.service.processing.jaxrs.data;

import java.util.List;
import java.util.concurrent.Callable;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ExecutorServiceFactory;
import com.x.query.core.entity.Item;

class ActionCreateWithWork extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Work work = emc.find(id, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			if (business.item().countWithJobWithPath(work.getJob()) > 0) {
				throw new ExceptionDataAlreadyExist(work.getTitle(), work.getId());
			}

			Callable<String> callable = new Callable<String>() {
				public String call() throws Exception {
					DataItemConverter<Item> converter = new DataItemConverter<>(Item.class);
					List<Item> adds = converter.disassemble(jsonElement);
					emc.beginTransaction(Item.class);
					emc.beginTransaction(Work.class);
					for (Item o : adds) {
						fill(o, work);
						business.entityManagerContainer().persist(o);
					}
					/* 标识数据已经被修改 */
					work.setDataChanged(true);
					emc.commit();
					return "";
				}
			};

			ExecutorServiceFactory.get(work.getJob()).submit(callable).get();

			Wo wo = new Wo();
			wo.setId(work.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

}