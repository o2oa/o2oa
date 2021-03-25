package com.x.processplatform.assemble.surface.jaxrs.job;

import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.express.service.processing.jaxrs.job.ActionExecutorStatusWo;

class ActionExecutorStatus extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionExecutorStatus.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String job) throws Exception {
		Req req = ThisApplication.context().applications().getQuery(x_processplatform_service_processing.class,
				Applications.joinQueryUri("job", job, "executor", "status"), job).getData(Req.class);
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setBusy(req.getBusy());
		wo.setSize(req.getSize());
		result.setData(wo);
		return result;
	}

	public static class Req extends ActionExecutorStatusWo {

		private static final long serialVersionUID = 1L;

	}

	public class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = 1L;
		@FieldDescribe("是否繁忙")
		private Boolean busy;
		@FieldDescribe("队列长度")
		private Integer size;

		public Boolean getBusy() {
			return busy;
		}

		public void setBusy(Boolean busy) {
			this.busy = busy;
		}

		public Integer getSize() {
			return size;
		}

		public void setSize(Integer size) {
			this.size = size;
		}

	}

}