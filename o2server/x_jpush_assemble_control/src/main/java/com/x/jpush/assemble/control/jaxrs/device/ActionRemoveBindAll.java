package com.x.jpush.assemble.control.jaxrs.device;

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
import com.x.jpush.assemble.control.Business;
import com.x.jpush.core.entity.PushDevice;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class ActionRemoveBindAll extends BaseAction {

	private final Logger logger = LoggerFactory.getLogger(ActionRemoveBindAll.class);

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		logger.info("execute action 'ActionRemoveBindAll'......");
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = convertToWrapIn(jsonElement, Wi.class);
			if (StringUtils.isEmpty(wi.getPerson())) {
				throw new ExceptionDeviceError("解绑设备时，人员信息不能为空！");
			}
			if (!effectivePerson.isManager()) {
				throw new ExceptionDeviceError("没有权限");
			}
			ActionResult<Wo> result = new ActionResult<>();
			Wo wraps = new Wo();
			Business business = new Business(emc);
			List<PushDevice> list = business.pushDeviceFactory().listJpushDevice(wi.getPerson());
			for (PushDevice pushDevice : list) {
				emc.beginTransaction(PushDevice.class);
				emc.delete(PushDevice.class, pushDevice.getId());
				emc.commit();
			}
			wraps.setValue(true);
			result.setData(wraps);
			logger.info("action 'ActionRemoveBindAll' execute completed!");
			return result;
		}

	}

	public static class Wi extends GsonPropertyObject {
		@FieldDescribe("人员 DN")
		private String person;
		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}
	}

	public static class Wo extends WrapBoolean {

	}
}
