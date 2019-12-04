package com.x.processplatform.service.processing.jaxrs.data;

import java.util.concurrent.Callable;

import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ExecutorServiceFactory;

class ActionUpdateWithWorkCompleted extends BaseAction {

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
			WorkCompleted workCompleted = emc.find(id, WorkCompleted.class);
			if (null == workCompleted) {
				throw new ExceptionEntityNotExist(id, WorkCompleted.class);
			}
			if (BooleanUtils.isTrue(workCompleted.getDataMerged())) {
				throw new ExceptionModifyDataMerged(workCompleted.getId());
			}

			Callable<String> callable = new Callable<String>() {
				public String call() throws Exception {
					updateData(business, workCompleted, jsonElement);
					/* updateTitleSerial 和 updateData 方法内进行了提交 */
					return "";
				}
			};

			ExecutorServiceFactory.get(workCompleted.getJob()).submit(callable).get();

			Wo wo = new Wo();
			wo.setId(workCompleted.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

}
