package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Task;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionManageListWithPerson extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManageListWithPerson.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String credential, Boolean isExcludeDraft)
			throws Exception {
		LOGGER.debug("execute:{}, credential:{}, isExcludeDraft:{}.", effectivePerson::getDistinguishedName,
				() -> credential, () -> isExcludeDraft);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			if (BooleanUtils.isTrue(business.ifPersonCanManageApplicationOrProcess(effectivePerson, "", ""))) {
				String person = business.organization().person().get(credential);
				if (StringUtils.isNotEmpty(person)) {
					List<Task> taskList = business.task().listWithPersonObject(person, isExcludeDraft);
					List<Wo> wos = Wo.copier.copy(taskList);
					result.setData(wos);
					result.setCount((long) wos.size());
				}
			}
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.task.ActionManageListWithPerson.Wo")
	public static class Wo extends Task {

		static WrapCopier<Task, Wo> copier = WrapCopierFactory.wo(Task.class, Wo.class,
				JpaObject.singularAttributeField(Task.class, true, true), null);

	}

}
