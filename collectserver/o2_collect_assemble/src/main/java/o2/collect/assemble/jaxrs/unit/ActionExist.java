package o2.collect.assemble.jaxrs.unit;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;

import o2.collect.assemble.Business;

class ActionExist extends BaseAction {

	ActionResult<Wo> execute(String name) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			String id = business.unit().getWithName(name, null);
			Wo wo = new Wo();
			if (StringUtils.isEmpty(id)) {
				// result.setData(WrapOutBoolean.falseInstance());
				wo.setValue(false);
			} else {
				// result.setData(WrapOutBoolean.trueInstance());
				wo.setValue(true);
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {
	}
}
