package com.x.jpush.assemble.control.jaxrs.message;

import cn.jiguang.sdk.bean.push.PushSendParam;
import cn.jiguang.sdk.bean.push.PushSendResult;
import cn.jiguang.sdk.bean.push.audience.Audience;
import cn.jiguang.sdk.bean.push.message.notification.NotificationMessage;
import cn.jiguang.sdk.bean.push.options.Options;
import cn.jiguang.sdk.enums.platform.Platform;
import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.jpush.assemble.control.Business;
import com.x.jpush.assemble.control.JpushConst;
import com.x.jpush.assemble.control.jaxrs.device.ActionListAll;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;


public class ActionSendMessageTest extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionSendMessageTest.class);


    protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson,
            JsonElement jsonElement) throws Exception {
        logger.info("execute action 'ActionSendMessageTest'......");
        ActionResult<Wo> result = new ActionResult<>();
        Wo wraps = new Wo();
        if (jsonElement == null) {
            Exception exception = new ExceptionSendMessageEmpty();
            result.error(exception);
            return result;
        }

        Wi wi = convertToWrapIn(jsonElement, Wi.class);
        if (StringUtils.isEmpty(wi.getMessage())) {
            Exception exception = new ExceptionSendMessageEmpty();
            result.error(exception);
            return result;
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            logger.info("person:" + effectivePerson.getDistinguishedName());
            List<String> deviceList = business.organization().personAttribute()
                    .listAttributeWithPersonWithName(effectivePerson.getDistinguishedName(),
                            ActionListAll.DEVICE_PERSON_ATTR_KEY);
            if (ListTools.isNotEmpty(deviceList)) {
                List<String> jiguangDeviceList = new ArrayList<>();
                for (String s : deviceList) {
                    String deviceId = "";
                    try {
                        String[] split = s.split("_");
                        deviceId = split[0];
                        logger.info("device Id:" + deviceId);
                    } catch (Exception e) {
                        logger.error(e);
                    }
                    if (deviceId != null && !deviceId.isEmpty()) {
                        jiguangDeviceList.add(deviceId);
                    }
                }
                if (!jiguangDeviceList.isEmpty()) {
                    PushSendParam param = new PushSendParam();
                    NotificationMessage.Android android = new NotificationMessage.Android();
                    android.setAlert(wi.getMessage());
                    android.setPriority(0);
                    android.setBadgeClass(JpushConst.launchActivity);
                    android.setBadgeAddNumber(1);
                    android.setChannelId(androidChannelId);
                    NotificationMessage.IOS iOS = new NotificationMessage.IOS();
                    iOS.setAlert(wi.getMessage());
                    iOS.setBadge("+1");
                    NotificationMessage notificationMessage = new NotificationMessage();
                    notificationMessage.setAlert(wi.getMessage());
                    notificationMessage.setAndroid(android);
                    notificationMessage.setIos(iOS);
                    param.setNotification(notificationMessage);
                    // 设置推送的目标设备
                    Audience audience = new Audience();
                    audience.setRegistrationIdList(jiguangDeviceList);
                    param.setAudience(audience);
                    param.setPlatform(Arrays.asList(Platform.android, Platform.ios));
                    // 设置推送其他参数
                    Options options = new Options();
                    options.setApnsProduction(true);
                    options.setClassification(1); // 系统消息 1 运营消息 0

                    Map<String, Object> thirdMap =  Config.pushConfig().getThirdPartyChannelMap() ;
                    if (thirdMap != null && !thirdMap.isEmpty()) {
                        options.setThirdPartyChannel(thirdMap);
                    }
                    param.setOptions(options);
                    // 发送
                    PushSendResult pushResult = business.pushDeviceFactory().jpushClient()
                            .send(param);
                    logger.info("发送结果:{}.", pushResult);
                    wraps.setValue(true);
                    result.setData(wraps);
                } else {
                    ExceptionSendMessageDeviceEmpty empty = new ExceptionSendMessageDeviceEmpty();
                    result.error(empty);
                }

            } else {
                ExceptionSendMessageDeviceEmpty empty = new ExceptionSendMessageDeviceEmpty();
                result.error(empty);
            }

        } catch (Exception e) {
            Exception exception = new ExceptionSendMessage(e, "系统发送极光消息时异常!");
            result.error(exception);
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
