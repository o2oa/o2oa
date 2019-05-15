package com.x.processplatform.service.processing.jaxrs.work;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.ThisApplication;

class ActionReroute extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionReroute.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String activityId, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Work work = emc.find(id, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			Activity activity = business.element().getActivity(activityId);
			if (!StringUtils.equals(work.getProcess(), activity.getProcess())) {
				throw new ExceptionProcessNotMatch();
			}
			emc.beginTransaction(Work.class);
			emc.beginTransaction(Task.class);
			work.setForceRoute(true);
			work.setDestinationActivity(activity.getId());
			work.setDestinationActivityType(activity.getActivityType());
			work.setDestinationRoute("");
			work.setDestinationRouteName("");
			emc.check(work, CheckPersistType.all);
			this.removeTask(business, work);
			emc.commit();
			ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
					Applications.joinQueryUri("work", work.getId(), "processing"), null);
			Wo wo = new Wo();
			wo.setId(work.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {
	}

	private void removeTask(Business business, Work work) throws Exception {
		/* 删除可能的待办 */
		List<Task> os = business.entityManagerContainer().listEqual(Task.class, Task.activityToken_FIELDNAME,
				work.getActivityToken());
		os.stream().forEach(o -> {
			try {
				business.entityManagerContainer().remove(o, CheckRemoveType.all);
				MessageFactory.task_delete(o);
			} catch (Exception e) {
				logger.error(e);
			}
		});
	}
}
