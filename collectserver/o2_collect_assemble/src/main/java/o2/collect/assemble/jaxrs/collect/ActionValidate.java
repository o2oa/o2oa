package o2.collect.assemble.jaxrs.collect;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import o2.collect.assemble.Business;
import o2.collect.core.entity.Unit;

class ActionValidate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionValidate.class);

	ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Wo wo = new Wo();
			wo.setValue(false);
			Unit unit = business.validateUnit(wi.getName(), wi.getPassword());
			if (null != unit) {
				wo.setValue(true);
			}
			logger.print("接收到验证请求,源地址:{},名称:{},结果:{}.", request.getRemoteAddr(), wi.getName(), wo.getValue());
			result.setData(wo);
			return result;
		}
	}

	public class Wi extends GsonPropertyObject {

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

	public static class Wo extends WrapBoolean {

	}
}
