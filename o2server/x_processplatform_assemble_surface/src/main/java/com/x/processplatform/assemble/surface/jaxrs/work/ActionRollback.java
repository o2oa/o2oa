package com.x.processplatform.assemble.surface.jaxrs.work;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControl;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

@Deprecated(forRemoval = true)
class ActionRollback extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			/* 校验work是否存在 */
			Work work = emc.find(id, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}

			Application application = business.application().pick(work.getApplication());

			if (null == application) {
				throw new ExceptionEntityNotExist(work.getApplication(), Application.class);
			}

			Process process = business.process().pick(work.getProcess());

			if (null == process) {
				throw new ExceptionEntityNotExist(work.getProcess(), Process.class);
			}

			if (!business.canManageApplicationOrProcess(effectivePerson, application, process)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}

			Wo wo = ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
					Applications.joinQueryUri("work", work.getId(), "rollback"), wi).getData(Wo.class);

			result.setData(wo);
			return result;

		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("工作日志")
		private String workLog;

		public String getWorkLog() {
			return workLog;
		}

		public void setWorkLog(String workLog) {
			this.workLog = workLog;
		}

	}

	public static class WoWorkControl extends WorkControl {

	}

	public static class Wo extends WoId {
	}

}