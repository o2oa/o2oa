package o2.collect.assemble.jaxrs.device;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;

import o2.collect.assemble.Business;
import o2.collect.core.entity.Device;
import o2.collect.core.entity.Unit;

public class ActionRemove extends BaseAction {

	public ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Device device = emc.find(id, Device.class);
			if (null == device) {
				throw new ExceptionDeviceNotExist(id);
			}
			Unit unit = emc.find(device.getUnit(), Unit.class);
			if (null == unit) {
				throw new ExceptionUnitNotExist(device.getUnit());
			}
			if (effectivePerson.isNotManager() && effectivePerson.isNotUser(unit.getName())) {
				throw new ExceptionAccessDenied(effectivePerson.getName());
			}
			business.entityManagerContainer().beginTransaction(Device.class);
			business.entityManagerContainer().remove(device);
			business.entityManagerContainer().commit();
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {
	}

}
