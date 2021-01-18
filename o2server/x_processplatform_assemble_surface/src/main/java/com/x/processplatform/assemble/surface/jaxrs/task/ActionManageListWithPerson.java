package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Task;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

class ActionManageListWithPerson extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String credential, Boolean isExcludeDraft)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			if (BooleanUtils.isTrue(business.canManageApplication(effectivePerson, null))) {
				String person = business.organization().person().get(credential);
				if (StringUtils.isNotEmpty(person)) {
					List<Task> taskList = business.task().listWithPersonObject(person, isExcludeDraft);
					List<Wo> wos = Wo.copier.copy(taskList);
					result.setData(wos);
					result.setCount((long)wos.size());
				}
			}
			return result;
		}
	}

	public static class Wo extends Task {

		static WrapCopier<Task, Wo> copier = WrapCopierFactory.wo(Task.class, Wo.class,
				JpaObject.singularAttributeField(Task.class, true, true), null);

	}

}
