package com.x.processplatform.assemble.surface.jaxrs.record;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

import org.apache.commons.lang3.BooleanUtils;

class ActionManageCreateWithJob extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionManageCreateWithJob.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String job, JsonElement jsonElement) throws Exception {

		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			wi = this.convertToWrapIn(jsonElement, Wi.class);
			if ((emc.countEqual(Work.class, Work.job_FIELDNAME, job) == 0)
					&& (emc.countEqual(WorkCompleted.class, WorkCompleted.job_FIELDNAME, job) == 0)) {
				throw new ExceptionEntityNotExist(job, "job");
			}
			Business business = new Business(emc);
			Application application = business.application().pick(wi.getApplication());
			Process process = business.process().pick(wi.getProcess());
			// 需要对这个应用的管理权限
			if (BooleanUtils.isFalse(business.canManageApplicationOrProcess(effectivePerson, application, process))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
		}
		WoId resp = ThisApplication.context().applications()
				.postQuery(x_processplatform_service_processing.class, Applications.joinQueryUri("record", "job", job), wi, job)
				.getData(WoId.class);
		Wo wo = new Wo();
		wo.setId(resp.getId());
		result.setData(wo);
		return result;
	}

	public static class Wi extends Record {

		private static final long serialVersionUID = 4179509440650818001L;

		static WrapCopier<Wi, Record> copier = WrapCopierFactory.wi(Wi.class, Record.class, null,
				JpaObject.FieldsUnmodify);

	}

	public static class Wo extends WoId {

	}

}