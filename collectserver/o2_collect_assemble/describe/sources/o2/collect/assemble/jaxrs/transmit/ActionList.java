package o2.collect.assemble.jaxrs.transmit;

import java.util.List;
import java.util.Objects;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapStringList;

import o2.collect.assemble.Business;

class ActionList extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			List<?> keys = business.queueTransmitReceiveCache().getKeys();
			for (Object o : keys) {
				wo.getValueList().add(Objects.toString(o));
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapStringList {

	}
}