package com.x.processplatform.assemble.surface.jaxrs.work;

import org.apache.commons.lang3.BooleanUtils;

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
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.express.assemble.surface.jaxrs.work.V2TerminateWo;

class V2Terminate extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2Terminate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);
		ActionResult<Wo> result = new ActionResult<>();
		Param param = this.init(effectivePerson, id);
		Wo wo = new Wo();
		wo.setId(this.terminate(param.id, param.job));
		result.setData(wo);
		return result;
	}

	private Param init(EffectivePerson effectivePerson, String id) throws Exception {
		Param param = new Param();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Work work = emc.find(id, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowManage()
					.enableAllowTerminate().build();
			if (BooleanUtils.isFalse(control.getAllowManage()) && BooleanUtils.isFalse(control.getAllowTerminate())) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			param.id = work.getId();
			param.job = work.getJob();
		}
		return param;
	}

	private class Param {
		private String id;
		private String job;
	}

	private String terminate(String id, String job) throws Exception {
		com.x.processplatform.core.express.service.processing.jaxrs.work.V2TerminateWo resp = ThisApplication.context()
				.applications()
				.getQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", "v2", id, "terminate"), job)
				.getData(com.x.processplatform.core.express.service.processing.jaxrs.work.V2TerminateWo.class);
		return resp.getId();
	}

	public static class Wo extends V2TerminateWo {

		private static final long serialVersionUID = -8410749558739884101L;

	}

}