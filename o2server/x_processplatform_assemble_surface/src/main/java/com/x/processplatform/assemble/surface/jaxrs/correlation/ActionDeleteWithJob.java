package com.x.processplatform.assemble.surface.jaxrs.correlation;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_correlation_service_processing;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionDeleteTypeProcessPlatformWi;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionDeleteTypeProcessPlatformWo;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.JobControlBuilder;
import com.x.processplatform.assemble.surface.ThisApplication;

class ActionDeleteWithJob extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDeleteWithJob.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String job, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> jsonElement);

		ActionResult<Wo> result = new ActionResult<>();

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		ActionDeleteTypeProcessPlatformWi req = new ActionDeleteTypeProcessPlatformWi();

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			if (BooleanUtils.isNotTrue(
					new JobControlBuilder(effectivePerson, business, job).enableAllowSave().build().getAllowSave())) {
				throw new ExceptionAccessDenied(effectivePerson, job);
			}
			req.setIdList(wi.getIdList());
		}

		if (ListTools.isNotEmpty(wi.getIdList())) {
			Wo wo = ThisApplication.context().applications()
					.postQuery(effectivePerson.getDebugger(), x_correlation_service_processing.class,
							Applications.joinQueryUri("correlation", "delete", "type", "processplatform", "job", job),
							req, job)
					.getData(Wo.class);
			result.setData(wo);
		}
		return result;
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 6571915741994756275L;

		@FieldDescribe("取消关联目标.")
		private List<String> idList;

		public List<String> getIdList() {
			return idList;
		}

		public void setIdList(List<String> idList) {
			this.idList = idList;
		}

	}

	public static class Wo extends ActionDeleteTypeProcessPlatformWo {

		private static final long serialVersionUID = -6712098733513365965L;

	}

}