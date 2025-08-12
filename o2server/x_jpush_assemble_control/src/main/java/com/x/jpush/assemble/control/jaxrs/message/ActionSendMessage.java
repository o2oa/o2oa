package com.x.jpush.assemble.control.jaxrs.message;


import cn.jiguang.sdk.api.PushApi;
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
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.jpush.assemble.control.Business;
import com.x.jpush.assemble.control.JpushConst;
import com.x.jpush.core.entity.PushDevice;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class ActionSendMessage extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionSendMessage.class);

    protected ActionResult<Wo> execute(JsonElement jsonElement) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("execute action 'ActionSendMessage' .");
        }
        ActionResult<Wo> result = new ActionResult<>();
        Wo wraps = new Wo();
        if (jsonElement == null) {
            throw new ExceptionSendMessageEmpty();
        }
        Wi wi = convertToWrapIn(jsonElement, Wi.class);
        if (StringUtils.isEmpty(wi.getPerson()) || StringUtils.isEmpty(wi.getMessage())) {
            throw new ExceptionSendMessageEmpty();
        }
        if (logger.isDebugEnabled()) {
            logger.debug("极光推送 消息：{}", wi.toString());
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            logger.info("极光推送通道启用中，消息发送到极光推送，人员：{}", wi.getPerson());
            List<PushDevice> pushDeviceList = business.pushDeviceFactory().listJpushDevice(wi.getPerson());
            if (pushDeviceList != null && !pushDeviceList.isEmpty()) {
                send2Jpush(pushDeviceList, wi, business.pushDeviceFactory().jpushClient());
            } else {
                logger.warn("极光推送设备为空，{}", wi.getPerson());
            }
            wraps.setValue(true);
            result.setData(wraps);
        } catch (Exception e) {
            logger.error(e);
            throw new ExceptionSendMessage(e, "系统发送推送消息时异常!");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("action 'ActionSendMessage' execute completed!");
        }
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
    private void send2Jpush(List<PushDevice> pushDeviceList, Wi wi, PushApi client) throws Exception {
        List<String> jiguangDeviceList = pushDeviceList.stream().map(PushDevice::getDeviceId)
                .collect(Collectors.toList());
        PushSendParam param = new PushSendParam();
        NotificationMessage.Android android = new NotificationMessage.Android();
        android.setAlert(wi.getMessage());
        android.setPriority(0);
        String badgeClass = Config.pushConfig().getBadgeClass();
        if (StringUtils.isEmpty(badgeClass)) {
            badgeClass = JpushConst.launchActivity;
        }
        android.setBadgeClass(badgeClass);
        android.setBadgeAddNumber(1);
        android.setChannelId(androidChannelId);
        NotificationMessage.IOS iOS = new NotificationMessage.IOS();
        iOS.setAlert(wi.getMessage());
        iOS.setBadge("+1");
        Map<String, Object> extras = new HashMap<>();
        if (wi.getStringExtras() != null) {
            extras.putAll(wi.getStringExtras());
        }
        if (wi.getNumberExtras() != null) {
            extras.putAll(wi.getNumberExtras());
        }
        if (wi.getBooleanExtras() != null) {
            extras.putAll(wi.getBooleanExtras());
        }
        if (wi.getJsonExtras() != null) {
            extras.putAll(wi.getJsonExtras());
        }
        if (!extras.isEmpty()) {
            android.setExtras(extras);
            iOS.setExtras(extras);
        }
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
        options.setApnsProduction(!BooleanUtils.isFalse(wi.getApnsProduction()));
        options.setClassification(1); // 系统消息 1 运营消息 0
        Map<String, Object> thirdMap =  Config.pushConfig().getThirdPartyChannelMap() ;
        if (thirdMap != null && !thirdMap.isEmpty()) {
            options.setThirdPartyChannel(thirdMap);
        }

        param.setOptions(options);
        logger.info("极光推送 body: {}", param.toString());
        // 发送
        PushSendResult pushResult = client.send(param);
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
        private Map<String, Object> jsonExtras;

        private Boolean apnsProduction;

        public Boolean getApnsProduction() {
            return apnsProduction;
        }

        public void setApnsProduction(Boolean apnsProduction) {
            this.apnsProduction = apnsProduction;
        }

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

        public Map<String, Object> getJsonExtras() {
            return jsonExtras;
        }

        public void setJsonExtras(Map<String, Object> jsonExtras) {
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
