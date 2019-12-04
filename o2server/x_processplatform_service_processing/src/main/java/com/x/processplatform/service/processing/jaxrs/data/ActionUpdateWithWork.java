package com.x.processplatform.service.processing.jaxrs.data;

import java.util.concurrent.Callable;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ExecutorServiceFactory;

class ActionUpdateWithWork extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionUpdateWithWork.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			/** 防止提交空数据清空data */
			if (null == jsonElement || (!jsonElement.isJsonObject())) {
				throw new ExceptionNotJsonObject();
			}
			if (jsonElement.getAsJsonObject().entrySet().isEmpty()) {
				throw new ExceptionEmptyData();
			}
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Work work = emc.find(id, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}

			Callable<String> callable = new Callable<String>() {
				public String call() throws Exception {
					/* 先更新title和serial,再更新DataItem,因为旧的DataItem中也有title和serial数据. */
					updateTitleSerial(business, work, jsonElement);
					updateData(business, work, jsonElement);
					/* updateTitleSerial 和 updateData 方法内进行了提交 */
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
