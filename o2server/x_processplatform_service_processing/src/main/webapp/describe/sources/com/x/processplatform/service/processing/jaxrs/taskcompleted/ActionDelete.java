package com.x.processplatform.service.processing.jaxrs.taskcompleted;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.core.entity.content.TaskCompleted;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			emc.beginTransaction(TaskCompleted.class);
			TaskCompleted taskCompleted = emc.find(id, TaskCompleted.class);
			if (null == taskCompleted) {
				throw new ExceptionEntityNotExist(id, TaskCompleted.class);
			}
			List<TaskCompleted> os = emc.listEqualAndEqual(TaskCompleted.class, TaskCompleted.job_FIELDNAME,
					taskCompleted.getJob(), TaskCompleted.person_FIELDNAME, taskCompleted.getPerson());
			TaskCompleted latest = os
					.stream().filter(o -> (!StringUtils.equals(o.getId(), taskCompleted.getId()))).sorted(Comparator
							.comparing(TaskCompleted::getStartTime, Comparator.nullsFirst(Date::compareTo)).reversed())
					.findFirst().orElse(null);
			if (null != latest) {
				latest.setLatest(true);
			}
			emc.remove(taskCompleted, CheckRemoveType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(taskCompleted.getId());
			result.setData(wo);
			return result;
		}

	}

	public static class Wo extends WoId {
	}

}