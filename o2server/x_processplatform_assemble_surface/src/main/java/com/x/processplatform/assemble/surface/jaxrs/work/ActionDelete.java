package com.x.processplatform.assemble.surface.jaxrs.work;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.express.assemble.surface.jaxrs.work.ActionDeleteWo;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionDelete extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDelete.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		Work work = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			work = emc.find(id, Work.class);
			if (null == work) {
				throw new ExceptionWorkNotExist(id);
			}
			Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowManage()
					.enableAllowDelete().build();
			if (BooleanUtils.isNotTrue(control.getAllowManage()) && BooleanUtils.isNotTrue(control.getAllowDelete())) {
				throw new ExceptionWorkAccessDenied(effectivePerson.getDistinguishedName(), work.getTitle(),
						work.getId());
			}
		}
		com.x.processplatform.core.express.service.processing.jaxrs.work.ActionDeleteWo resp = ThisApplication.context()
				.applications()
				.deleteQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("work", work.getId()), work.getJob())
				.getData(com.x.processplatform.core.express.service.processing.jaxrs.work.ActionDeleteWo.class);
		wo.setId(resp.getId());
		result.setData(wo);
		return result;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.work.ActionDelete.Wo")
	public static class Wo extends ActionDeleteWo {

		private static final long serialVersionUID = 5791562285104343968L;

	}

}