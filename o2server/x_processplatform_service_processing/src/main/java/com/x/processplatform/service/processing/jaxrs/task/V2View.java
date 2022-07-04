package com.x.processplatform.service.processing.jaxrs.task;

import java.util.Date;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Task;

public class V2View extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2View.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workId, String person) throws Exception {

		LOGGER.debug("execute:{}, workId:{}, person:{}.", effectivePerson::getDistinguishedName, () -> workId,
				() -> person);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setValue(false);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Task task = emc.firstEqualAndEqual(Task.class, Task.work_FIELDNAME, workId, Task.person_FIELDNAME, person);
			if ((null != task) && (null == task.getViewTime())) {
				emc.beginTransaction(Task.class);
				task.setViewTime(new Date());
				emc.check(task, CheckPersistType.all);
				emc.commit();
				wo.setValue(true);
			}
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 1363997520866820672L;

	}

}