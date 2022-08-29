package com.x.jpush.assemble.control.jaxrs.device;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.jpush.assemble.control.Business;
import com.x.jpush.core.entity.PushDevice;

public class ActionListAll extends BaseAction {
	private Logger logger = LoggerFactory.getLogger(ActionListAll.class);
	public static final String DEVICE_PERSON_ATTR_KEY = "appBindDeviceList";

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson,
			String pushType) throws Exception {
		logger.info("execute action 'ActionListAll'......");
		ActionResult<List<Wo>> result = new ActionResult<>();
		if (StringUtils.isEmpty(pushType)) {
			throw new ExceptionDevicePushTypeError();
		}
		if (!pushType.equals(PushDevice.PUSH_TYPE_JPUSH) && !pushType.equals(PushDevice.PUSH_TYPE_HUAWEI)) {
			throw new ExceptionDevicePushTypeError();
		}
		List<Wo> wraps = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			if (pushType.equals(PushDevice.PUSH_TYPE_JPUSH)) {
				List<PushDevice> list = business.pushDeviceFactory()
						.listJpushDevice(effectivePerson.getDistinguishedName());
				wraps = Wo.copyFromPushDeviceList(list);
				result.setData(wraps);
				result.setCount(Long.parseLong(wraps.size() + ""));
			} else {
				List<PushDevice> list = business.pushDeviceFactory()
						.listHuaweiDevice(effectivePerson.getDistinguishedName());
				wraps = Wo.copyFromPushDeviceList(list);
				result.setData(wraps);
				result.setCount(Long.parseLong(wraps.size() + ""));
			}
//
//
//            List<String> deviceList = business.organization().personAttribute()
//                    .listAttributeWithPersonWithName(effectivePerson.getDistinguishedName(), DEVICE_PERSON_ATTR_KEY);
//            if( ListTools.isNotEmpty( deviceList ) ){
//                wraps =  Wo.copyFromAttributes( deviceList );
//                result.setCount(Long.parseLong( wraps.size() + "") );
//                result.setData( wraps );
//            }
		} catch (Exception e) {
			logger.error(e);
			throw new IllegalArgumentException("系统在查询绑定设备时发生异常!", e);
		}

		logger.info("action 'ActionListAll' execute completed!");
		return result;
	}

	/**
	 *
	 * 向外输出的结果对象包装类
	 *
	 */
	public static class Wo extends GsonPropertyObject {

		private String deviceName;
		private String deviceType;

		public static List<Wo> copyFromPushDeviceList(List<PushDevice> list) {
			List<Wo> ret = new ArrayList<>();
			if (list != null && !list.isEmpty()) {
				for (int i = 0; i < list.size(); i++) {
					PushDevice pushDevice = list.get(i);
					Wo wo = new Wo();
					wo.setDeviceName(pushDevice.getDeviceId());
					wo.setDeviceType(pushDevice.getDeviceType());
					ret.add(wo);
				}
			}
			return ret;
		}

		public static List<Wo> copyFromAttributes(List<String> attributes) {
			List<Wo> ret = new ArrayList<>();
			if (ListTools.isNotEmpty(attributes)) {
				for (int i = 0; i < attributes.size(); i++) {
					String[] device = attributes.get(i).split("_");
					if (device.length == 2) {
						Wo wo = new Wo();
						wo.setDeviceName(device[0]);
						wo.setDeviceType(device[1]);
						ret.add(wo);
					}
				}
			}
			return ret;
		}

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
}
