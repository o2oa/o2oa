package o2.collect.assemble.jaxrs.unit;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.ListTools;

import o2.collect.assemble.Business;
import o2.collect.core.entity.Unit;

class ActionControllerMobile extends BaseAction {

	ActionResult<Wo> execute(String name, String mobile) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Unit unit = this.unitExist(business, name, null);
			Wo wo = new Wo();
			if (null == unit) {
				wo.setValue(false);
			} else {
				if (ListTools.contains(unit.getControllerMobileList(), mobile)) {
					wo.setValue(true);
				} else {
					wo.setValue(false);
				}
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {
	}
}
