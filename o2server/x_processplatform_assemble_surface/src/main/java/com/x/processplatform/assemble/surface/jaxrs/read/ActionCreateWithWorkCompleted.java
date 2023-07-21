package com.x.processplatform.assemble.surface.jaxrs.read;

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
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkCompletedControlBuilder;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.express.service.processing.jaxrs.read.ActionCreateWithWorkCompletedWi;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionCreateWithWorkCompleted extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreateWithWorkCompleted.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String workCompletedId, JsonElement jsonElement)
			throws Exception {

		LOGGER.debug("execute:{}, workCompletedId:{}.", effectivePerson::getDistinguishedName, () -> workCompletedId);

		ActionResult<List<Wo>> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		WorkCompleted workCompleted = null;

		if (ListTools.isEmpty(wi.getIdentityList())) {
			throw new ExceptionEmptyIdentity();
		}

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			workCompleted = emc.find(workCompletedId, WorkCompleted.class);
			if (null == workCompleted) {
				throw new ExceptionEntityNotExist(workCompletedId, WorkCompleted.class);
			}
			Control control = new WorkCompletedControlBuilder(effectivePerson, business, workCompleted)
					.enableAllowVisit().build();
			if (BooleanUtils.isFalse(control.getAllowVisit())) {
				throw new ExceptionAccessDenied(effectivePerson, workCompleted);
			}
		}
		List<Wo> wos = ThisApplication.context().applications()
				.postQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
						Applications.joinQueryUri("read", "workcompleted", workCompleted.getId()), wi,
						workCompleted.getJob())
				.getDataAsList(Wo.class);
		result.setData(wos);
		return result;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.read.ActionCreateWithWorkCompleted$Wi")
	public static class Wi extends ActionCreateWithWorkCompletedWi {

		private static final long serialVersionUID = -3824171286310523782L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.read.ActionCreateWithWorkCompleted$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = -420123330513850240L;
	}

}
