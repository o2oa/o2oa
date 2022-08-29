package com.x.jpush.assemble.control.jaxrs.message;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.jpush.assemble.control.Business;
import com.x.jpush.assemble.control.jaxrs.device.ActionListAll;

import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;


public class ActionSendMessageTest extends StandardJaxrsAction {

    private static Logger logger = LoggerFactory.getLogger( ActionSendMessageTest.class );


    protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        logger.info("execute action 'ActionSendMessageTest'......");
        ActionResult<Wo> result = new ActionResult<>();
        Wo wraps  = new Wo();
        if (jsonElement == null) {
            Exception exception = new ExceptionSendMessageEmpty();
            result.error(exception);
            return result;
        }

        Wi wi = convertToWrapIn(jsonElement, Wi.class);
        if (wi.getMessage() == null ||  wi.getMessage().equals("")) {
            Exception exception = new ExceptionSendMessageEmpty();
            result.error(exception);
            return result;
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            logger.info("person:"+ effectivePerson.getDistinguishedName());
            List<String> deviceList = business.organization().personAttribute()
                    .listAttributeWithPersonWithName(effectivePerson.getDistinguishedName(), ActionListAll.DEVICE_PERSON_ATTR_KEY);
            if(ListTools.isNotEmpty( deviceList ) ){
                 List<String> jiguangDeviceList = new ArrayList<>();
                for (int i = 0; i < deviceList.size(); i++) {
                    String deviceId = "";
                    try {
                        String[] split = deviceList.get(i).split("_");
                        deviceId = split[0];
                        logger.info("device Id:" + deviceId);
                    }catch (Exception e){
                        logger.error(e);
                    }
                    if (deviceId != null && !deviceId.isEmpty()) {
                        jiguangDeviceList.add(deviceId);
                    }
                }
                if (!jiguangDeviceList.isEmpty()) {
                    PushPayload pushPayload = PushPayload.newBuilder()
                            .setPlatform(Platform.all())
                            .setAudience(Audience.registrationId(jiguangDeviceList))
                            .setNotification(Notification.alert(wi.getMessage()))
                            .setOptions(Options.newBuilder().setApnsProduction(true).build()).build();
                    PushResult pushResult = business.pushDeviceFactory().jpushClient().sendPush(pushPayload);
                    logger.info("发送结果:{}.", pushResult);
                    wraps.setValue(true);
                    result.setData(wraps);
                }else {
                    ExceptionSendMessageDeviceEmpty empty = new ExceptionSendMessageDeviceEmpty();
                    result.error( empty );
                }

            }else {
                ExceptionSendMessageDeviceEmpty empty = new ExceptionSendMessageDeviceEmpty();
                result.error( empty );
            }

        } catch (Exception e) {
            Exception exception = new ExceptionSendMessage( e, "系统发送极光消息时异常!" );
            result.error( exception );
            logger.error(e);
        }

        logger.info("action 'ActionSendMessageTest' execute completed!");
        return result;
    }


    public static class Wi {

        @FieldDescribe("消息内容")
        private String message;


        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }


    public static class Wo extends WrapBoolean {

    }
}
