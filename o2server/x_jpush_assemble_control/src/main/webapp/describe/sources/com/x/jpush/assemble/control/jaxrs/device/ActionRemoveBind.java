package com.x.jpush.assemble.control.jaxrs.device;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.PropertyTools;
import com.x.jpush.assemble.control.Business;
import com.x.jpush.assemble.control.jaxrs.sample.BaseAction;
import com.x.jpush.assemble.control.jaxrs.sample.ExceptionSampleEntityClassFind;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class ActionRemoveBind extends BaseAction {

    private Logger logger = LoggerFactory.getLogger( ActionRemoveBind.class );

    protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String deviceName, String deviceType) throws Exception {
        logger.info("execute action 'ActionRemoveBind'......");
        ActionResult<Wo> result = new ActionResult<>();
        Wo wraps  = new Wo();
        if (deviceName == null || deviceType == null || deviceName.equals("") || deviceType.equals("")) {
            Exception exception = new ExceptionDeviceParameterEmpty();
            result.error(exception);
            return result;
        }
        deviceType = deviceType.toLowerCase();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            List<String> deviceList = business.organization().personAttribute()
                    .listAttributeWithPersonWithName(effectivePerson.getDistinguishedName(), ActionListAll.DEVICE_PERSON_ATTR_KEY);
            String device = deviceName+"_"+deviceType.toLowerCase();
            if(ListTools.isNotEmpty( deviceList ) ){
                if (deviceList.contains(device)) {
                    deviceList.remove(device);
                    wraps.setValue(business.organization().personAttribute()
                            .setWithPersonWithName(effectivePerson.getDistinguishedName(), ActionListAll.DEVICE_PERSON_ATTR_KEY, deviceList));
                }else {
                    wraps.setValue(true);
                    result.setMessage("当前设备不存在，无需解绑！");
                }
            }else {
                wraps.setValue(true);
                result.setMessage("当前设备不存在，无需解绑！");
            }
            result.setData(wraps);
        } catch (Exception e) {
            Exception exception = new ExceptionSampleEntityClassFind( e, "系统在设备解除绑定时发生异常!" );
            result.error( exception );
            logger.error(e);
        }

        logger.info("action 'ActionRemoveBind' execute completed!");
        return result;
    }




    public static class Wo extends WrapBoolean {

    }
}
