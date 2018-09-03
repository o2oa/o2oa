package o2.collect.assemble.jaxrs.device;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;

import o2.collect.assemble.Business;
import o2.collect.core.entity.Device;

public class ActionUnbind extends BaseAction {

	public ActionResult<Wo> execute(EffectivePerson effectivePerson, String name) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			String deviceId = business.device().getWithName(name);
			if (StringUtils.isEmpty(deviceId)) {
				throw new ExceptionDeviceNotExist(name);
			}
			business.entityManagerContainer().beginTransaction(Device.class);
			Device device = business.entityManagerContainer().find(deviceId, Device.class);
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
