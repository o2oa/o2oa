package com.x.jpush.assemble.control.jaxrs.device;

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

public class ActionCheck extends BaseAction {

	private Logger logger = LoggerFactory.getLogger(ActionCheck.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String deviceName,
			String deviceType, String pushType) throws Exception {
		logger.info("execute action 'ActionCheck'......");
		ActionResult<Wo> result = new ActionResult<>();
		Wo wraps = new Wo();
		if (StringUtils.isEmpty(deviceName) || StringUtils.isEmpty(deviceType) || StringUtils.isEmpty(pushType)) {
			throw new ExceptionDeviceParameterEmpty();

		}
		deviceType = deviceType.toLowerCase();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			String unique = deviceUnique(deviceType, deviceName, pushType, effectivePerson.getDistinguishedName());
			if (business.pushDeviceFactory().existDeviceUnique(unique)) {
				wraps.setValue(true);
			} else {
				wraps.setValue(false);
			}
//            List<String> deviceList = business.organization().personAttribute()
//                    .listAttributeWithPersonWithName(effectivePerson.getDistinguishedName(), ActionListAll.DEVICE_PERSON_ATTR_KEY);
//            if( ListTools.isNotEmpty( deviceList ) ){
//                String device = deviceName+"_"+deviceType;
//                wraps.setValue(deviceList.contains(device));
//            }else {
//               wraps.setValue(false);
//            }
			result.setData(wraps);
		} catch (Exception e) {
			logger.error(e);
			throw new IllegalArgumentException("系统在检查绑定设备是否存在时发生异常!", e);
		}

		logger.info("action 'ActionCheck' execute completed!");
		return result;
	}

	public static class Wo extends WrapBoolean {

	}
}
