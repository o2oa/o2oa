package com.x.file.assemble.control.jaxrs.share;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.file.assemble.control.Business;

class ActionList extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = new ArrayList<>();
			Business business = new Business(emc);
			for (String str : business.attachment().listPersonWithShare(effectivePerson.getDistinguishedName())) {
				Wo wo = new Wo();
				wo.setName(str);
				wo.setValue(str);
				wo.setCount(
						business.attachment().countWithPersonWithShare(str, effectivePerson.getDistinguishedName()));
				wos.add(wo);
			}
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends GsonPropertyObject {
		@FieldDescribe("名称")
		private String name;
		@FieldDescribe("值")
		private String value;
		@FieldDescribe("数量")
		private Long count;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public Long getCount() {
			return count;
		}

		public void setCount(Long count) {
			this.count = count;
		}

	}

}
