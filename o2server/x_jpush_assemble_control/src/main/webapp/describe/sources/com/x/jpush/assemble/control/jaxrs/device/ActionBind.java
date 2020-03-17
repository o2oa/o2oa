package com.x.jpush.assemble.control.jaxrs.device;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.jpush.assemble.control.Business;
import com.x.jpush.assemble.control.jaxrs.sample.BaseAction;
import com.x.jpush.assemble.control.jaxrs.sample.ExceptionSampleEntityClassFind;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class ActionBind extends BaseAction {

    private Logger logger = LoggerFactory.getLogger( ActionBind.class );

    protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        logger.info("execute action 'ActionBind'......");
        ActionResult<Wo> result = new ActionResult<>();
        Wo wraps  = new Wo();
        if (jsonElement == null) {
            Exception exception = new ExceptionDeviceParameterEmpty();
            result.error(exception);
            return result;
        }

        Wi wi = convertToWrapIn(jsonElement, Wi.class);
        if (wi.getDeviceName() == null || wi.getDeviceType() == null || wi.getDeviceName().equals("") || wi.getDeviceType().equals("")) {
            Exception exception = new ExceptionDeviceParameterEmpty();
            result.error(exception);
            return result;
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            List<String> deviceList = business.organization().personAttribute()
                    .listAttributeWithPersonWithName(effectivePerson.getDistinguishedName(), ActionListAll.DEVICE_PERSON_ATTR_KEY);
            String device = wi.getDeviceName()+"_"+wi.getDeviceType().toLowerCase();
            if(ListTools.isNotEmpty( deviceList ) ){
                if (deviceList.contains(device)) {
                    wraps.setValue(false);
                    result.setMessage("当前设备已存在！");
                }else {
                    deviceList.add(device);
                    wraps.setValue(business.organization().personAttribute()
                            .setWithPersonWithName(effectivePerson.getDistinguishedName(), ActionListAll.DEVICE_PERSON_ATTR_KEY, deviceList));
                }
            }else {
                deviceList = new ArrayList<>();
                deviceList.add(device);
                wraps.setValue(business.organization().personAttribute()
                        .setWithPersonWithName(effectivePerson.getDistinguishedName(), ActionListAll.DEVICE_PERSON_ATTR_KEY, deviceList));
            }
            result.setData(wraps);
        } catch (Exception e) {
            Exception exception = new ExceptionSampleEntityClassFind( e, "系统在绑定设备时发生异常!" );
            result.error( exception );
            logger.error(e);
        }

        logger.info("action 'ActionBind' execute completed!");
        return result;
    }


    public static class Wi {

        @FieldDescribe("设备号deviceName")
        private String deviceName;
        @FieldDescribe("设备类型deviceType：ios|android")
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
