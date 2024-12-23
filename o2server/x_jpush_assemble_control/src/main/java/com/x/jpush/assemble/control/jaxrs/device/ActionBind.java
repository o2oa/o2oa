package com.x.jpush.assemble.control.jaxrs.device;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.jpush.assemble.control.Business;
import com.x.jpush.core.entity.PushDevice;

public class ActionBind extends BaseAction {

	private Logger logger = LoggerFactory.getLogger(ActionBind.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {
		logger.info("execute action 'ActionBind'......");
		ActionResult<Wo> result = new ActionResult<>();
		Wo wraps = new Wo();
		if (jsonElement == null) {
			throw new ExceptionDeviceParameterEmpty();
		}
		Wi wi = convertToWrapIn(jsonElement, Wi.class);
		if (StringUtils.isEmpty(wi.getDeviceName()) || StringUtils.isEmpty(wi.getDeviceType())) {
			throw new ExceptionDeviceParameterEmpty();
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			String person = effectivePerson.getDistinguishedName();
			String unique = deviceUnique(wi.getDeviceType(), wi.getDeviceName(), PushDevice.PUSH_TYPE_JPUSH, person);
			if (business.pushDeviceFactory().existDeviceUnique(unique)) {
				wraps.setValue(true);
				result.setMessage("当前设备已存在！");
			} else {
				PushDevice pushDevice = new PushDevice();
				pushDevice.setDeviceId(wi.getDeviceName());
				pushDevice.setDeviceType(wi.getDeviceType());
				pushDevice.setPerson(person);
				pushDevice.setPushType(PushDevice.PUSH_TYPE_JPUSH);
				pushDevice.setUnique(unique);
				emc.beginTransaction(PushDevice.class);
				emc.persist(pushDevice, CheckPersistType.all);
				emc.commit();
				wraps.setValue(true);
			}
			result.setData(wraps);
		}
		logger.info("action 'ActionBind' execute completed!");
		return result;
	}

	public static class Wi {

		@FieldDescribe("设备号deviceName")
		private String deviceName;
		@FieldDescribe("设备类型deviceType：ios|android|hmos")
		private String deviceType;

		public String getDeviceName() {
			return deviceName;
		}

		public void setDeviceName(String deviceName) {
			this.deviceName = deviceName;
		}

		public String getDeviceType() {
			return deviceType;
		}

		public void setDeviceType(String deviceType) {
			this.deviceType = deviceType;
		}
	}

	public static class Wo extends WrapBoolean {

	}
}
