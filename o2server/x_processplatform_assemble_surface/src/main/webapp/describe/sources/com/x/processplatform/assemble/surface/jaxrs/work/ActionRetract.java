package com.x.processplatform.assemble.surface.jaxrs.work;

import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.ProcessingType;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;

class ActionRetract extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionRetract.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			TaskCompleted taskCompleted = null;
			WorkLog workLog = null;
			Work work = emc.find(id, Work.class);
			if (null == work) {
				throw new ExceptionWorkNotExist(id);
			}
			String taskCompletedId = business.taskCompleted().getAllowRetract(effectivePerson.getDistinguishedName(),
					work);
			if (StringUtils.isEmpty(taskCompletedId)) {
				throw new ExceptionRetractNoneTaskCompleted(effectivePerson.getDistinguishedName(), work.getTitle(),
						work.getId());
			}
			taskCompleted = emc.find(taskCompletedId, TaskCompleted.class);
			workLog = this.getWorkLog(business, taskCompleted);
			if (null == workLog) {
				throw new ExceptionRetractNoneWorkLog(effectivePerson.getDistinguishedName(), work.getTitle(),
						work.getId(), taskCompletedId);
			}
			emc.beginTransaction(TaskCompleted.class);
			taskCompleted.setProcessingType(ProcessingType.retract);
			taskCompleted.setRetractTime(new Date());
			emc.commit();
			ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
					"work/" + URLEncoder.encode(work.getId(), DefaultCharset.name) + "/retract/worklog/"
							+ URLEncoder.encode(workLog.getId(), DefaultCharset.name),
					null);
			Wo wo = new Wo();
			wo.setId(work.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {
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