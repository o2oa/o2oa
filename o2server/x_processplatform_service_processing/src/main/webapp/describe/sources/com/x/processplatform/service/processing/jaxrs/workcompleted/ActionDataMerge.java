package com.x.processplatform.service.processing.jaxrs.workcompleted;

import java.util.List;
import java.util.concurrent.Callable;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.query.core.entity.Item;

class ActionDataMerge extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionDataMerge.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		DataItemConverter<Item> converter = new DataItemConverter<Item>(Item.class);

		String executorSeed = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			WorkCompleted workCompleted = emc.fetch(id, WorkCompleted.class,
					ListTools.toList(WorkCompleted.job_FIELDNAME));
			if (null == workCompleted) {
				throw new ExceptionEntityNotExist(id, WorkCompleted.class);
			}
			executorSeed = workCompleted.getJob();
		}

		Callable<ActionResult<Wo>> callable = new Callable<ActionResult<Wo>>() {
			public ActionResult<Wo> call() throws Exception {
				String workCompletedId = null;
				String workCompletedTitle = null;
				String workCompletedSequence = null;
				try {
					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						WorkCompleted workCompleted = emc.find(id, WorkCompleted.class);
						if (null != workCompleted) {
							workCompletedId = workCompleted.getId();
							workCompletedTitle = workCompleted.getTitle();
							workCompletedSequence = workCompleted.getSequence();
							List<Item> items = items(emc, workCompleted);
							JsonElement jsonElement = converter.assemble(items);
							emc.beginTransaction(WorkCompleted.class);
							workCompleted.setData(XGsonBuilder.toJson(jsonElement));
							workCompleted.setDataMerged(true);
							emc.commit();
							emc.beginTransaction(Item.class);
							for (Item item : items) {
								emc.remove(item, CheckRemoveType.all);
							}
							emc.commit();
							logger.print("已完成工作数据合并, id: {}, title:{}, sequence:{}.", workCompletedId,
									workCompletedTitle, workCompletedSequence);
						}
					} catch (Exception e) {
						throw new ExceptionDataMerge(e, workCompletedId, workCompletedTitle, workCompletedSequence);
					}
				} catch (Exception e) {
					logger.error(e);
				}
				Wo wo = new Wo();
				wo.setId(workCompletedId);
				ActionResult<Wo> result = new ActionResult<>();
				result.setData(wo);
				return result;
			}
		};
		ActionResult<Wo> result = ProcessPlatformExecutorFactory.get(executorSeed).submit(callable).get();
		return result;
	}

	private List<Item> items(EntityManagerContainer emc, WorkCompleted workCompleted) throws Exception {
		return emc.listEqualAndEqual(Item.class, Item.bundle_FIELDNAME, workCompleted.getJob(),
				Item.itemCategory_FIELDNAME, ItemCategory.pp);
	}

	public static class Wo extends WoId {
	}

}