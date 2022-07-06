package com.x.processplatform.assemble.surface.jaxrs.data;

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

class ActionUpdateWithWorkCompletedPath6 extends BaseUpdateWithWorkCompletedPath {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdateWithWorkCompletedPath6.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String path0, String path1, String path2,
			String path3, String path4, String path5, String path6, JsonElement jsonElement) throws Exception {
		
		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);
		
		ActionResult<Wo> result = new ActionResult<>();
		WorkCompleted workCompleted = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			workCompleted = emc.find(id, WorkCompleted.class);
			if (null == workCompleted) {
				throw new ExceptionEntityNotExist(id, WorkCompleted.class);
			}
			/** 允许创建者在完成后再次修改内容,与前台的可修改不一致,所以单独判断,为的是不影响前台显示. */
			Application application = business.application().pick(workCompleted.getApplication());
			Process process = business.process().pick(workCompleted.getProcess());
			if (!business.canManageApplicationOrProcess(effectivePerson, application, process)
					&& (!effectivePerson.isPerson(workCompleted.getCreatorPerson()))) {
				throw new ExceptionWorkCompletedAccessDenied(effectivePerson.getDistinguishedName(),
						workCompleted.getTitle(), workCompleted.getId());
			}
			if (BooleanUtils.isTrue(workCompleted.getMerged())) {
				throw new ExceptionModifyDataMerged(workCompleted.getId());
			}
		}
		Wo wo = ThisApplication.context().applications()
				.putQuery(
						x_processplatform_service_processing.class, Applications.joinQueryUri("data", "workcompleted",
								workCompleted.getId(), path0, path1, path2, path3, path4, path5, path6),
						jsonElement, workCompleted.getJob())
				.getData(Wo.class);
		result.setData(wo);
		return result;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.data.ActionUpdateWithWorkCompletedPath6$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = 2770254338326110492L;

	}
}
