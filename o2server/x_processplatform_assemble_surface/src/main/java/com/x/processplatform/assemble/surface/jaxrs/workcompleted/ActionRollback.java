package com.x.processplatform.assemble.surface.jaxrs.workcompleted;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkCompletedControlBuilder;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.express.assemble.surface.jaxrs.workcompleted.ActionRollbackWi;
import com.x.processplatform.core.express.assemble.surface.jaxrs.workcompleted.ActionRollbackWo;

class ActionRollback extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionRollback.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, flag:{}, jsonElement:{}", effectivePerson::getDistinguishedName, () -> flag,
				() -> jsonElement);

		Param param = this.init(effectivePerson, flag, jsonElement);

		com.x.processplatform.core.express.assemble.surface.jaxrs.workcompleted.ActionRollbackWi req = new com.x.processplatform.core.express.assemble.surface.jaxrs.workcompleted.ActionRollbackWi();

		req.setWorkLog(param.getWorkLog());
		req.setDistinguishedNameList(param.getDistinguishedNameList());

		com.x.processplatform.core.express.assemble.surface.jaxrs.workcompleted.ActionRollbackWo resp = ThisApplication
				.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("workcompleted", param.getWorkCompleted(), "rollback"), req)
				.getData(
						com.x.processplatform.core.express.assemble.surface.jaxrs.workcompleted.ActionRollbackWo.class);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setId(resp.getId());
		result.setData(wo);
		return result;

	}

	private Param init(EffectivePerson effectivePerson, String flag, JsonElement jsonElement) throws Exception {
		Param param = new Param();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			WorkCompleted workCompleted = emc.flag(flag, WorkCompleted.class);
			if (null == workCompleted) {
				throw new ExceptionEntityNotExist(flag, WorkCompleted.class);
			}
			Application application = business.application().pick(workCompleted.getApplication());
			if (null == application) {
				throw new ExceptionEntityNotExist(workCompleted.getApplication(), Application.class);
			}
			Process process = business.process().pick(workCompleted.getProcess());
			if (null == process) {
				throw new ExceptionEntityNotExist(workCompleted.getProcess(), Process.class);
			}
			WorkLog workLog = emc.find(wi.getWorkLog(), WorkLog.class);
			if (null == workLog) {
				throw new ExceptionEntityNotExist(wi.getWorkLog(), WorkLog.class);
			}
			if (BooleanUtils.isTrue(workLog.getSplitting())) {
				throw new ExceptionSplittingNotRollback(workCompleted.getId(), workLog.getId());
			}
			Control control = new WorkCompletedControlBuilder(effectivePerson, business, workCompleted)
					.enableAllowRollback().enableAllowManage().build();
			if (BooleanUtils.isNotTrue(control.getAllowManage()) && BooleanUtils.isNotTrue(control.getAllowRollback())) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			param.setWorkCompleted(workCompleted.getId());
			param.setWorkLog(workLog.getId());
			param.setDistinguishedNameList(
					business.organization().distinguishedName().list(wi.getDistinguishedNameList()));
			return param;
		}
	}

	public class Param {
		private String workCompleted;
		private String workLog;
		private List<String> distinguishedNameList;

		public String getWorkCompleted() {
			return workCompleted;
		}

		public void setWorkCompleted(String workCompleted) {
			this.workCompleted = workCompleted;
		}

		public String getWorkLog() {
			return workLog;
		}

		public void setWorkLog(String workLog) {
			this.workLog = workLog;
		}

		public List<String> getDistinguishedNameList() {
			return distinguishedNameList;
		}

		public void setDistinguishedNameList(List<String> distinguishedNameList) {
			this.distinguishedNameList = distinguishedNameList;
		}

	}

	public static class Wi extends ActionRollbackWi {

		private static final long serialVersionUID = 1966814422721596072L;

	}

	public static class Wo extends ActionRollbackWo {

		private static final long serialVersionUID = -6048816634681644627L;

	}

}