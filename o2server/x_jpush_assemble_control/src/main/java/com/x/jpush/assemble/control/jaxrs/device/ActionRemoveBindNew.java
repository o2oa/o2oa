package com.x.jpush.assemble.control.jaxrs.device;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.jpush.assemble.control.Business;
import com.x.jpush.core.entity.PushDevice;

public class ActionRemoveBindNew extends BaseAction {

	private Logger logger = LoggerFactory.getLogger(ActionRemoveBindNew.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String deviceName,
			String deviceType, String pushType) throws Exception {
		logger.info("execute action 'ActionRemoveBindNew'......");
		ActionResult<Wo> result = new ActionResult<>();
		Wo wraps = new Wo();
		if (StringUtils.isEmpty(deviceName) || StringUtils.isEmpty(deviceType)) {
			throw new ExceptionDeviceParameterEmpty();
		}
		if (!deviceType.equals(PushDevice.DEVICE_TYPE_ANDROID) && !deviceType.equals(PushDevice.DEVICE_TYPE_IOS)) {
			throw new ExceptionDevicePushTypeError();
		}
		if (StringUtils.isEmpty(pushType)) {
			throw new ExceptionDevicePushTypeError();
		}
		if (!pushType.equals(PushDevice.PUSH_TYPE_JPUSH) && !pushType.equals(PushDevice.PUSH_TYPE_HUAWEI)) {
			throw new ExceptionDevicePushTypeError();
		}

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			String unique = deviceUnique(deviceType, deviceName, pushType, effectivePerson.getDistinguishedName());
			PushDevice pushDevice = business.pushDeviceFactory().findDeviceByUnique(unique);
			if (pushDevice != null) {
				EntityManager m = emc.beginTransaction(PushDevice.class);
				emc.delete(PushDevice.class, pushDevice.getId());
				m.getTransaction().commit();
				wraps.setValue(true);
			} else {
				wraps.setValue(true);
				result.setMessage("当前设备不存在，无需解绑！");
			}
//            List<String> deviceList = business.organization().personAttribute()
//                    .listAttributeWithPersonWithName(effectivePerson.getDistinguishedName(), ActionListAll.DEVICE_PERSON_ATTR_KEY);
//            String device = deviceName+"_"+deviceType.toLowerCase();
//            if(ListTools.isNotEmpty( deviceList ) ){
//                if (deviceList.contains(device)) {
//                    deviceList.remove(device);
//                    wraps.setValue(business.organization().personAttribute()
//                            .setWithPersonWithName(effectivePerson.getDistinguishedName(), ActionListAll.DEVICE_PERSON_ATTR_KEY, deviceList));
//                }else {
//                    wraps.setValue(true);
//                    result.setMessage("当前设备不存在，无需解绑！");
//                }
//            }else {
//                wraps.setValue(true);
//                result.setMessage("当前设备不存在，无需解绑！");
//            }
			result.setData(wraps);
		} catch (Exception e) {
			logger.error(e);
			throw new IllegalArgumentException("系统在设备解除绑定时发生异常!", e);
		}

		logger.info("action 'ActionRemoveBindNew' execute completed!");
		return result;
	}

	public static class Wo extends WrapBoolean {

	}
}
