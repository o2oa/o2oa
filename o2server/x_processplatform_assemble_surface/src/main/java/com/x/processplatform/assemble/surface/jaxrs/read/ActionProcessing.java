package com.x.processplatform.assemble.surface.jaxrs.read;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

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
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkCompletedControlBuilder;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.express.service.processing.jaxrs.read.ActionProcessingWi;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionProcessing extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionProcessing.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Read read;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			read = emc.find(id, Read.class);
			if (null == read) {
				throw new ExceptionEntityNotExist(id, Read.class);
			}
			if (BooleanUtils.isTrue(read.getCompleted())) {
				WorkCompleted workCompleted = emc.find(read.getWorkCompleted(), WorkCompleted.class);
				Control control = new WorkCompletedControlBuilder(effectivePerson, business, workCompleted)
						.enableAllowReadProcessing().build();
				if (BooleanUtils.isNotTrue(control.getAllowReadProcessing())) {
					throw new ExceptionAccessDenied(effectivePerson, read);
				}
			} else {
				Work work = emc.find(read.getWork(), Work.class);
				Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowReadProcessing()
						.build();
				if (BooleanUtils.isNotTrue(control.getAllowReadProcessing())) {
					throw new ExceptionAccessDenied(effectivePerson, read);
				}
			}
			emc.beginTransaction(Read.class);
			/* 如果有新的流程意见那么覆盖原有流程意见 */
			if (StringUtils.isNotEmpty(wi.getOpinion())) {
				read.setOpinion(wi.getOpinion());
			}
			emc.commit();
		}
		/* processing read */
		Wo wo = ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("read", read.getId(), "processing"), null, read.getJob())
				.getData(Wo.class);
		result.setData(wo);
		return result;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.read.ActionProcessing$Wi")
	public static class Wi extends ActionProcessingWi {

		private static final long serialVersionUID = -344390659094430761L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.read.ActionProcessing$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = 5308349035543235143L;
	}
}
