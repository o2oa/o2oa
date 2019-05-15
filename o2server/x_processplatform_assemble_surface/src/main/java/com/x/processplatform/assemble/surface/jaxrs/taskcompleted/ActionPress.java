package com.x.processplatform.assemble.surface.jaxrs.taskcompleted;

import java.util.Comparator;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapStringList;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.NumberTools;
import com.x.base.core.project.tools.PropertyTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Manual;

class ActionPress extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionPress.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Work work = emc.find(workId, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(workId, Work.class);
			}
			TaskCompleted taskCompleted = emc
					.listEqualAndEqual(TaskCompleted.class, TaskCompleted.person_FIELDNAME,
							effectivePerson.getDistinguishedName(), TaskCompleted.job_FIELDNAME, work.getJob())
					.stream().sorted(Comparator.comparing(TaskCompleted::getCompletedTime,
							Comparator.nullsLast(Date::compareTo)))
					.findFirst().orElse(null);
			if (null == taskCompleted) {
				throw new ExceptionPressNoneTaskCompleted(work.getId(), effectivePerson.getName());
			}
			if (!PropertyTools.getOrElse(business.getActivity(work), Manual.allowPress_FIELDNAME, Boolean.class,
					false)) {
				throw new ExceptionNotAllowPress(work.getActivityName());
			}
			if (StringUtils.equals(taskCompleted.getPressActivityToken(), work.getActivityToken())) {
				if (!NumberTools.nullOrLessThan(taskCompleted.getPressCount(),
						Config.processPlatform().getPress().getCount())) {
					if (DateTools.beforeNowMinutesNullIsFalse(taskCompleted.getPressTime(),
							Config.processPlatform().getPress().getIntervalMinutes())) {
						throw new ExceptionPressLimit(Config.processPlatform().getPress().getIntervalMinutes(),
								Config.processPlatform().getPress().getCount());
					}
				}
			}
			if (emc.countEqual(Task.class, Task.work_FIELDNAME, work.getId()) == 0) {
				throw new ExceptionPressNoneTask(work.getId());

			}
			Wo wo = ThisApplication
					.context().applications().getQuery(x_processplatform_service_processing.class, Applications
							.joinQueryUri("taskcompleted", taskCompleted.getId(), "press", "work", work.getId()))
					.getData(Wo.class);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapStringList {

	}

}
