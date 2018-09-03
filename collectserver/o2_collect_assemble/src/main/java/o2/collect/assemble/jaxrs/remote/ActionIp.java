package o2.collect.assemble.jaxrs.remote;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;

class ActionIp extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, HttpServletRequest request) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			wo.setValue(request.getRemoteAddr());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapString {

	}
}
