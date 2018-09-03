package o2.collect.assemble.jaxrs.module;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;

import o2.collect.assemble.Business;
import o2.collect.core.entity.Module;

class ActionGetData extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			/* 跳过认证 */
			// if (effectivePerson.isAnonymous()) {
			// if (null == business.validateUnit(wi.getName(), wi.getPassword())) {
			// throw new ExceptionValidateUnitError(wi.getName());
			// }
			// }
			ActionResult<Wo> result = new ActionResult<>();
			Module module = emc.find(id, Module.class);
			if (null == module) {
				throw new ExceptionEntityNotExist(id, Module.class);
			}
			Wo wo = new Wo();
			wo.setValue(module.getData());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("用户名")
		private String name;

		@FieldDescribe("密码")
		private String password;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}

	public static class Wo extends WrapString {

	}
}