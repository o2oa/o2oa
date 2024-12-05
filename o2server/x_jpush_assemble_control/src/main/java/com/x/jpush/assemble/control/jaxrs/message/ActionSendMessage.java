package com.x.jpush.assemble.control.jaxrs.message;

import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.jpush.assemble.control.Business;
import com.x.jpush.assemble.control.JpushConst;
import com.x.jpush.core.entity.PushDevice;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class ActionSendMessage extends StandardJaxrsAction {

    private static Logger logger = LoggerFactory.getLogger(ActionSendMessage.class);

    protected ActionResult<Wo> execute(JsonElement jsonElement) throws Exception {
        logger.info("execute action 'ActionSendMessage' .");
        ActionResult<Wo> result = new ActionResult<>();
        Wo wraps = new Wo();
        if (jsonElement == null) {
            throw new ExceptionSendMessageEmpty();
        }
        Wi wi = convertToWrapIn(jsonElement, Wi.class);
        if (StringUtils.isEmpty(wi.getPerson()) || StringUtils.isEmpty(wi.getMessage())) {
            throw new ExceptionSendMessageEmpty();
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            logger.info("极光推送通道启用中，消息发送到极光推送，人员：{}", wi.getPerson());
            List<PushDevice> pushDeviceList = business.pushDeviceFactory().listJpushDevice(wi.getPerson());
            if (pushDeviceList != null && !pushDeviceList.isEmpty()) {
                send2Jpush(pushDeviceList, wi, business.pushDeviceFactory().jpushClient());
            } else {
                logger.info("极光推送设备为空，{}", wi.getPerson());
            }
            wraps.setValue(true);
            result.setData(wraps);
        } catch (Exception e) {
            logger.error(e);
            throw new ExceptionSendMessage(e, "系统发送推送消息时异常!");
        }
        logger.info("action 'ActionSendMessage' execute completed!");
        return result;
    }

    /**
     * 极光推送消息
     *
     * @param pushDeviceList
     * @param wi
     * @param client
     * @throws Exception
     */
    private void send2Jpush(List<PushDevice> pushDeviceList, Wi wi, JPushClient client) throws Exception {
        List<String> jiguangDeviceList = pushDeviceList.stream().map(PushDevice::getDeviceId)
                .collect(Collectors.toList());
        var iosBuilder = IosNotification
                .newBuilder()
                .setSound("") // 默认铃声
                .setBadge(1)
                .setAlert(wi.getMessage());
        var androidBuilder = AndroidNotification
                .newBuilder()
                .setPriority(0)
                .setBadgeClass(JpushConst.launchActivity)
                .setBadgeAddNum(1)
                .setAlert(wi.getMessage());
        if (wi.getStringExtras() != null) {
            wi.getStringExtras()
                    .forEach(iosBuilder::addExtra);
            wi.getStringExtras().forEach(androidBuilder::addExtra);
        }
        if (wi.getNumberExtras() != null) {
            wi.getNumberExtras()
                    .forEach(iosBuilder::addExtra);
            wi.getNumberExtras().forEach(androidBuilder::addExtra);
        }
        if (wi.getBooleanExtras() != null) {
            wi.getBooleanExtras()
                    .forEach(iosBuilder::addExtra);
            wi.getBooleanExtras().forEach(androidBuilder::addExtra);
        }
        if (wi.getJsonExtras() != null) {
            wi.getJsonExtras()
                    .forEach(iosBuilder::addExtra);
            wi.getJsonExtras().forEach(androidBuilder::addExtra);
        }
        Notification n = Notification.newBuilder()
                // ios 消息
                .addPlatformNotification(iosBuilder.build())
                // android 消息
                .addPlatformNotification(androidBuilder.build())
                .build();

        PushPayload pushPayload = PushPayload.newBuilder().setPlatform(Platform.all())
                .setAudience(Audience.registrationId(jiguangDeviceList))
                .setNotification(n)
				.setOptions(
                        Options
                                .newBuilder()
                                .setApnsProduction(true)
                                .setThirdPartyChannelV2(Config.pushConfig().getThirdPartyChannel()) // 第三方通道的特殊参数
                                .build()) // ios 发布证书
				.build();
        logger.info("极光推送 body: {}", pushPayload.toString());
        PushResult pushResult = client.sendPush(pushPayload);
        logger.info("极光推送 发送结果:{}.", pushResult);
    }

    public static class Wi extends GsonPropertyObject {

        @FieldDescribe("人员")
        private String person;

        @FieldDescribe("消息内容")
        private String message;

        @FieldDescribe("字符串扩展")
        private Map<String, String> stringExtras;
        @FieldDescribe("数字扩展")
        private Map<String, Number> numberExtras;
        @FieldDescribe("布尔串扩展")
        private Map<String, Boolean> booleanExtras;
        @FieldDescribe("对象扩展")
        private Map<String, JsonObject> jsonExtras;

        public Map<String, String> getStringExtras() {
            return stringExtras;
        }

        public void setStringExtras(Map<String, String> stringExtras) {
            this.stringExtras = stringExtras;
        }

        public Map<String, Number> getNumberExtras() {
            return numberExtras;
        }

        public void setNumberExtras(Map<String, Number> numberExtras) {
            this.numberExtras = numberExtras;
        }

        public Map<String, Boolean> getBooleanExtras() {
            return booleanExtras;
        }

        public void setBooleanExtras(Map<String, Boolean> booleanExtras) {
            this.booleanExtras = booleanExtras;
        }

        public Map<String, JsonObject> getJsonExtras() {
            return jsonExtras;
        }

        public void setJsonExtras(Map<String, JsonObject> jsonExtras) {
            this.jsonExtras = jsonExtras;
        }

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }

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
