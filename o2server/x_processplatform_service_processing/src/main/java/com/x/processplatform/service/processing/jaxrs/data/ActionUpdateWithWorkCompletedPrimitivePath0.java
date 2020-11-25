package com.x.processplatform.service.processing.jaxrs.data;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.dataitem.ItemPrimitiveType;
import com.x.base.core.entity.dataitem.ItemType;
import com.x.base.core.project.annotation.ActionLogger;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.service.processing.Business;
import com.x.query.core.entity.Item;

class ActionUpdateWithWorkCompletedPrimitivePath0 extends BaseAction {
	/*
	 * 更新workCompletedPath0 基本类型数据
	 */

	@ActionLogger
	private static Logger logger = LoggerFactory.getLogger(ActionUpdateWithWorkCompletedPrimitivePath0.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String path0, String val) throws Exception {

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		String executorSeed = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			WorkCompleted workCompleted = emc.fetch(id, WorkCompleted.class,
					ListTools.toList(WorkCompleted.job_FIELDNAME));
			if (null == workCompleted) {
				throw new ExceptionEntityNotExist(id, WorkCompleted.class);
			}
			executorSeed = workCompleted.getJob();
		}

		Callable<String> callable = new Callable<String>() {
			public String call() throws Exception {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					WorkCompleted workCompleted = emc.find(id, WorkCompleted.class);
					if (null == workCompleted) {
						throw new ExceptionEntityNotExist(id, WorkCompleted.class);
					}
					if (BooleanUtils.isTrue(workCompleted.getMerged())) {
						throw new ExceptionModifyMerged(workCompleted.getId());
					}

					Wo wo = new Wo();
					wo.setId(workCompleted.getId());
					List<Item> exists = business.item().listWithJobWithPath(workCompleted.getJob(), path0);

					if (1 == exists.size()) {
						Item existsItem = exists.get(0);
						if (existsItem.getItemType().equals(ItemType.p)
								|| existsItem.getItemType().equals(ItemType.n)) {

							if (existsItem.getItemPrimitiveType().equals(ItemPrimitiveType.s)) {
								// 字符
								existsItem.setStringShortValue(val);
							}

							if (existsItem.getItemPrimitiveType().equals(ItemPrimitiveType.b)) {
								// Boolean
								existsItem.setBooleanValue(Boolean.parseBoolean(val));
							}

							if (existsItem.getItemPrimitiveType().equals(ItemPrimitiveType.n)) {
								// number
								existsItem.setNumberValue(Double.parseDouble(val));
							}

							if (existsItem.getItemPrimitiveType().equals(ItemPrimitiveType.u)) {
								// 这是啥？问狗哥去吧。
								existsItem.setStringShortValue(val);
							}
							business.entityManagerContainer().beginTransaction(Item.class);
							business.entityManagerContainer().persist(existsItem);
							business.entityManagerContainer().commit();
						}
					} else {
						throw new ExceptionNotExistItem(path0);
					}

				}
				return "";
			}
		};

		ProcessPlatformExecutorFactory.get(executorSeed).submit(callable).get(300, TimeUnit.SECONDS);

		result.setData(wo);
		return result;
	}

	public static class Wo extends WoId {

	}

}
