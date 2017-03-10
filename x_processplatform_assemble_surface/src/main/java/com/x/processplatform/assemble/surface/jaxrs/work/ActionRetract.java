package com.x.processplatform.assemble.surface.jaxrs.work;

import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.DefaultCharset;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.ProcessingType;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;

class ActionRetract extends ActionBase {

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			Business business = new Business(emc);
			TaskCompleted taskCompleted = null;
			WorkLog workLog = null;
			Work work = emc.find(id, Work.class);
			if (null == work) {
				throw new WorkNotExistedException(id);
			}
			String taskCompletedId = business.taskCompleted().getAllowRetract(effectivePerson.getName(), work);
			if (StringUtils.isEmpty(taskCompletedId)) {
				throw new RetractNoneTaskCompletedException(effectivePerson.getName(), work.getTitle(), work.getId());
			}
			taskCompleted = emc.find(taskCompletedId, TaskCompleted.class, ExceptionWhen.not_found);
			workLog = this.getWorkLog(business, taskCompleted);
			if (null == workLog) {
				throw new RetractNoneWorkLogException(effectivePerson.getName(), work.getTitle(), work.getId(),
						taskCompletedId);
			}
			emc.beginTransaction(TaskCompleted.class);
			taskCompleted.setProcessingType(ProcessingType.retract);
			taskCompleted.setRetractTime(new Date());
			emc.commit();
			ThisApplication.applications.putQuery(x_processplatform_service_processing.class,
					"work/" + URLEncoder.encode(work.getId(), DefaultCharset.name) + "/retract/worklog/"
							+ URLEncoder.encode(workLog.getId(), DefaultCharset.name),
					null);
			WrapOutId wrap = new WrapOutId(work.getId());
			result.setData(wrap);
			return result;
		}
	}

	private WorkLog getWorkLog(Business business, TaskCompleted taskCompleted) throws Exception {
		List<String> ids = business.workLog().listWithFromActivityToken(taskCompleted.getActivityToken());
		for (WorkLog o : business.entityManagerContainer().list(WorkLog.class, ids)) {
			if (StringUtils.equals(o.getWork(), taskCompleted.getWork())) {
				return o;
			}
		}
		return null;
	}
}