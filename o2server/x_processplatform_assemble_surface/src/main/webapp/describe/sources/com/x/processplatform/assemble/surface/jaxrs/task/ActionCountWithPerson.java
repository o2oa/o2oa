package com.x.processplatform.assemble.surface.jaxrs.task;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;

class ActionCountWithPerson extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String credential) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wo wo = new Wo();
			Business business = new Business(emc);
			String person = business.organization().person().get(credential);
			if (StringUtils.isNotEmpty(person)) {
				Long count = business.task().countWithPerson(person);
				wo.setCount(count);
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("待办数量")
		private Long count = 0L;

		public Long getCount() {
			return count;
		}

		public void setCount(Long count) {
			this.count = count;
		}

	}
}