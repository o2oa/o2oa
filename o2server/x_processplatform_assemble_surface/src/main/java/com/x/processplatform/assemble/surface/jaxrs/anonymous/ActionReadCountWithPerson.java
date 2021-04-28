package com.x.processplatform.assemble.surface.jaxrs.anonymous;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;

class ActionReadCountWithPerson extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String credential) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wo wrap = new Wo();
			Business business = new Business(emc);
			String person = business.organization().person().get(credential);
			if (StringUtils.isNotEmpty(person)) {
				Long count = business.read().countWithPerson(person);
				wrap.setCount(count);
			}
			result.setData(wrap);
			return result;
		}
	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = -6282434800524535886L;

		@FieldDescribe("待阅数量")
		private Long count = 0L;

		public Long getCount() {
			return count;
		}

		public void setCount(Long count) {
			this.count = count;
		}

	}
}