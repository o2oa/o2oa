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
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Work;

class V2ManageTerminate extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2ManageTerminate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);
		ActionResult<Wo> result = new ActionResult<>();
		Work work = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			work = emc.find(id, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			// 判断条件和V2Manage不同,仅判断是否有管理权限.
			Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowManage().build();
			if (BooleanUtils.isNotTrue(control.getAllowManage())) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
		}
		Wo wo = this.terminate(work.getId(), work.getJob());
		result.setData(wo);
		return result;
	}

	private Wo terminate(String id, String job) throws Exception {
		return ThisApplication.context().applications().getQuery(x_processplatform_service_processing.class,
				Applications.joinQueryUri("work", "v2", id, "terminate"), job).getData(Wo.class);
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -8410749558739884101L;

	}

}