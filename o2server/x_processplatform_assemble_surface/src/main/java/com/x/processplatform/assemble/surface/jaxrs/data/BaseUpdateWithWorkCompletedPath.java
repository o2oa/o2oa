package com.x.processplatform.assemble.surface.jaxrs.data;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

import io.swagger.v3.oas.annotations.media.Schema;

class BaseUpdateWithWorkCompletedPath extends BaseAction {

	protected WorkCompleted getWorkCompleted(EffectivePerson effectivePerson, String id) throws Exception {
		WorkCompleted workCompleted;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			workCompleted = emc.find(id, WorkCompleted.class);
			if (null == workCompleted) {
				throw new ExceptionEntityNotExist(id, WorkCompleted.class);
			}
			// 允许创建者在完成后再次修改内容,与前台的可修改不一致,所以单独判断,为的是不影响前台显示.
			Application application = business.application().pick(workCompleted.getApplication());
			Process process = business.process().pick(workCompleted.getProcess());
			if (BooleanUtils.isFalse(business.ifPersonCanManageApplicationOrProcess(effectivePerson, application, process))
					&& (!effectivePerson.isPerson(workCompleted.getCreatorPerson()))) {
				throw new ExceptionWorkCompletedAccessDenied(effectivePerson.getDistinguishedName(),
						workCompleted.getTitle(), workCompleted.getId());
			}
			if (BooleanUtils.isTrue(workCompleted.getMerged())) {
				throw new ExceptionModifyDataMerged(workCompleted.getId());
			}
		}
		return workCompleted;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.data.ActionUpdateWithWorkCompletedPath5$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = 5643635231905548097L;

	}

}
