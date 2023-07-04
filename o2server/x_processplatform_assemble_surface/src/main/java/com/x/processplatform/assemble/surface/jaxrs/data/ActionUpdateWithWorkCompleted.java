package com.x.processplatform.assemble.surface.jaxrs.data;

import com.x.processplatform.core.express.service.processing.jaxrs.data.DataWi;
import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionUpdateWithWorkCompleted extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdateWithWorkCompleted.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		ActionResult<Wo> result = new ActionResult<>();
		WorkCompleted workCompleted = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			/** 防止提交空数据清空data */
			if (null == jsonElement || (!jsonElement.isJsonObject())) {
				throw new ExceptionNotJsonObject();
			}
			if (jsonElement.getAsJsonObject().entrySet().isEmpty()) {
				throw new ExceptionEmptyData();
			}
			Business business = new Business(emc);
			workCompleted = emc.find(id, WorkCompleted.class);
			if (null == workCompleted) {
				throw new ExceptionEntityNotExist(id, WorkCompleted.class);
			}
			// 允许创建者在完成后再次修改内容,与前台的可修改不一致,所以单独判断,为的是不影响前台显示.
			Application application = business.application().pick(workCompleted.getApplication());
			Process process = business.process().pick(workCompleted.getProcess());
			if (BooleanUtils.isFalse(business.ifPersonCanManageApplicationOrProcess(effectivePerson, application, process))
					&& BooleanUtils.isFalse(effectivePerson.isPerson(workCompleted.getCreatorPerson()))) {
				throw new ExceptionWorkCompletedAccessDenied(effectivePerson.getDistinguishedName(),
						workCompleted.getTitle(), workCompleted.getId());
			}
			if (BooleanUtils.isTrue(workCompleted.getMerged())) {
				throw new ExceptionModifyDataMerged(workCompleted.getId());
			}
		}
		DataWi dataWi = new DataWi(effectivePerson.getDistinguishedName(), jsonElement);
		Wo wo = ThisApplication.context().applications()
				.putQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("data", "workcompleted", workCompleted.getId()), dataWi,
						workCompleted.getJob())
				.getData(Wo.class);
		result.setData(wo);
		return result;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.data.ActionUpdateWithWorkCompleted$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = -3426541497427662961L;

	}

}
