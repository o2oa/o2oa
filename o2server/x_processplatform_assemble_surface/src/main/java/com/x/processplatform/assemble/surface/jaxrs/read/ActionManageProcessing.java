package com.x.processplatform.assemble.surface.jaxrs.read;

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
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.express.service.processing.jaxrs.read.ActionManageProcessingWi;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionManageProcessing extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManageProcessing.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		ActionResult<Wo> result = new ActionResult<>();
		Read read;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			read = emc.find(id, Read.class);
			if (null == read) {
				throw new ExceptionEntityNotExist(id, Read.class);
			}
			Process process = business.process().pick(read.getProcess());
			Application application = business.application().pick(read.getApplication());
			// 需要对这个应用的管理权限
			if (!business.ifPersonCanManageApplicationOrProcess(effectivePerson, application, process)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			emc.beginTransaction(Read.class);
			/* 如果有新的流程意见那么覆盖原有流程意见 */
			if (StringUtils.isNotEmpty(wi.getOpinion())) {
				read.setOpinion(wi.getOpinion());
			}
			emc.commit();
			/* processing read */
		}
		Wo wo = ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("read", read.getId(), "processing"), null, read.getJob())
				.getData(Wo.class);
		result.setData(wo);
		return result;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.read.ActionManageProcessing$Wi")
	public static class Wi extends ActionManageProcessingWi {

		private static final long serialVersionUID = -1483312276089362848L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.read.ActionManageProcessing$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = 3266562181811434533L;
	}

}