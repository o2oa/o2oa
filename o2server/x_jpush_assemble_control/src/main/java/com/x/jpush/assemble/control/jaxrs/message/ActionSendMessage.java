package com.x.jpush.assemble.control.jaxrs.message;

import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;
import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.HuaweiPushConfig;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.jpush.assemble.control.Business;
import com.x.jpush.assemble.control.huawei.model.Importance;
import com.x.jpush.assemble.control.huawei.model.Urgency;
import com.x.jpush.assemble.control.huawei.model.Visibility;
import com.x.jpush.core.entity.PushDevice;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


public class ActionSendMessage  extends StandardJaxrsAction {

    private static Logger logger = LoggerFactory.getLogger( ActionSendMessage.class );


    protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        logger.info("execute action 'ActionSendMessage'......");
        ActionResult<Wo> result = new ActionResult<>();
        Wo wraps  = new Wo();
        if (jsonElement == null) {
            throw new ExceptionSendMessageEmpty();
        }

        Wi wi = convertToWrapIn(jsonElement, Wi.class);
        if (wi.getPerson() == null || wi.getPerson().equals("") || wi.getMessage() == null ||  wi.getMessage().equals("")) {
            throw new ExceptionSendMessageEmpty();
        }

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            logger.info("person:"+ wi.getPerson());
            HuaweiPushConfig config = Config.pushConfig().getHuaweiPushConfig();
            if (config !=null && Config.pushConfig().getHuaweiPushEnable()) { // 华为推送通道
                logger.info("华为推送通道启用中，消息发送到华为");
                List<PushDevice> pushDeviceList = business.sampleEntityClassNameFactory().listHuaweiDevice(wi.getPerson());
                if (pushDeviceList !=null && !pushDeviceList.isEmpty()) {
                    send2HuaweiPush(pushDeviceList, wi.getMessage());
                    wraps.setValue(true);
                    result.setData(wraps);
                }else {
                    throw new ExceptionSendMessageDeviceEmpty();
                }

            } else {
                logger.info("极光推送通道启用中，消息发送到极光推送");
                List<PushDevice> pushDeviceList = business.sampleEntityClassNameFactory().listJpushDevice(wi.getPerson());
                if (pushDeviceList !=null && !pushDeviceList.isEmpty()) {
                    send2Jpush(pushDeviceList, wi.getMessage(), business.sampleEntityClassNameFactory().jpushClient());
                    wraps.setValue(true);
                    result.setData(wraps);
                }else {
                    throw new ExceptionSendMessageDeviceEmpty();
                }
            }
//            List<String> deviceList = business.organization().personAttribute()
//                    .listAttributeWithPersonWithName(wi.getPerson(), ActionListAll.DEVICE_PERSON_ATTR_KEY);
//            if(ListTools.isNotEmpty( deviceList ) ){
//                List<String> jiguangDeviceList = new ArrayList<>();
//                for (int i = 0; i < deviceList.size(); i++) {
//                    String deviceId = "";
//                    try {
//                        String[] split = deviceList.get(i).split("_");
//                        deviceId = split[0];
//                        logger.info("device Id:" + deviceId);
//                    }catch (Exception e){
//                        logger.error(e);
//                    }
//                    if (deviceId != null && !deviceId.isEmpty()) {
//                        jiguangDeviceList.add(deviceId);
//                    }
//                }
//                if(ListTools.isNotEmpty( jiguangDeviceList ) ) {
//                    //send
//                    wraps.setValue(true);
//                    result.setData(wraps);
//                }else {
//                    ExceptionSendMessageDeviceEmpty empty = new ExceptionSendMessageDeviceEmpty();
//                    result.error( empty );
//                }
//
//            }else {
//                ExceptionSendMessageDeviceEmpty empty = new ExceptionSendMessageDeviceEmpty();
//                result.error( empty );
//            }
        } catch (Exception e) {
            logger.error(e);
            throw new ExceptionSendMessage( e, "系统发送推送消息时异常!" );
//            result.error( exception );
        }
        logger.info("action 'ActionSendMessage' execute completed!");
        return result;
    }

    /**
     * 华为推送消息
     * @param pushDeviceList
     * @param message
     * @throws Exception
     */
    private void send2HuaweiPush(List<PushDevice> pushDeviceList, String message) throws Exception {
        logger.info("开始发送华为推送消息， "+message);
        List<String> iosList = pushDeviceList.stream().filter((p)-> p.getDeviceType().equals(PushDevice.DEVICE_TYPE_IOS)).map(PushDevice::getDeviceId).collect(Collectors.toList());
        List<String> androidList = pushDeviceList.stream().filter((p)-> p.getDeviceType().equals(PushDevice.DEVICE_TYPE_ANDROID)).map(PushDevice::getDeviceId).collect(Collectors.toList());

        if (!iosList.isEmpty()) {
            com.x.jpush.assemble.control.huawei.model.Message m = iosMessage(iosList, message);
            sendHuaweiMessage(m);
        } else {
          logger.info("没有ios设备需要发送消息");
        }
        if (!androidList.isEmpty()) {
            com.x.jpush.assemble.control.huawei.model.Message m = androidMessage(androidList, message);
            sendHuaweiMessage(m);
        } else {
            logger.info("没有Android 设备需要发送消息");
        }

    }

    private void sendHuaweiMessage(com.x.jpush.assemble.control.huawei.model.Message msg) throws Exception {
        HashMap<String, Object> sendBody = new HashMap<>();
        sendBody.put("validate_only", false);
        sendBody.put("message", msg);
        List<NameValuePair> heads = new ArrayList<>();
        String url = Config.pushConfig().getHuaweiPushConfig().getPushUrl();
        logger.info("华为推送地址："+url);
        String accessToken = Config.pushConfig().getHuaweiPushConfig().accessToken();
        logger.info("华为推送accessToken ："+accessToken);
        heads.add(new NameValuePair("Authorization", "Bearer " + accessToken));
        String body = XGsonBuilder.instance().toJson(sendBody);
        logger.info("发送消息："+body);
        HuaweiSendResponse result = HttpConnection.postAsObject(url, heads, body, HuaweiSendResponse.class);
        logger.info("华为消息发送完成，code：" + result.getCode() + ", msg:"+ result.getMsg() + ",  requestId: "+result.getRequestId());
    }

    /**
     * 华为Android消息
     * @param deviceList
     * @param message
     * @return
     */
    private com.x.jpush.assemble.control.huawei.model.Message androidMessage(List<String> deviceList, String message) {
        com.x.jpush.assemble.control.huawei.model.Notification notification
                = com.x.jpush.assemble.control.huawei.model.Notification
                .builder()
                .setTitle(message)
                .setBody(message)
                .build();
        // 添加一条角标
        com.x.jpush.assemble.control.huawei.android.BadgeNotification badgeNotification =
                com.x.jpush.assemble.control.huawei.android.BadgeNotification.builder()
                        .setAddNum(1)
                        .setBadgeClass("net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.launch.LaunchActivity")
                        .build();
        com.x.jpush.assemble.control.huawei.android.ClickAction clickAction =
                com.x.jpush.assemble.control.huawei.android.ClickAction.builder()
                        .setType(3)// 启动应用
                        .build();
        com.x.jpush.assemble.control.huawei.android.AndroidNotification androidNotification
                = com.x.jpush.assemble.control.huawei.android.AndroidNotification.builder()
                .setTitle(message)
                .setBody(message)
                .setDefaultSound(true)
                .setAutoCancel(false)
                .setBadge(badgeNotification)
                .setClickAction(clickAction)
                .setForegroundShow(true)
                .setVisibility(Visibility.PUBLIC.getValue())
                .setImportance(Importance.NORMAL.getValue())
                .build();

        com.x.jpush.assemble.control.huawei.android.AndroidConfig config = com.x.jpush.assemble.control.huawei.android.AndroidConfig
                .builder()
                .setCollapseKey(-1)
                .setUrgency(Urgency.HIGH.getValue())
                .setNotification(androidNotification)
                .build();

        return com.x.jpush.assemble.control.huawei.model.Message.builder()
                .addAllToken(deviceList)
                .setNotification(notification)
                .setAndroidConfig(config)
                .build();

    }

    /**
     * 华为ios消息
     * @param deviceList
     * @param message
     * @return
     */
    private com.x.jpush.assemble.control.huawei.model.Message iosMessage(List<String> deviceList, String message) {
        com.x.jpush.assemble.control.huawei.apns.Alert alert = com.x.jpush.assemble.control.huawei.apns.Alert.builder()
                .setTitle(message)
                .setBody(message)
                .build();
        com.x.jpush.assemble.control.huawei.apns.Aps aps = com.x.jpush.assemble.control.huawei.apns.Aps.builder()
                .setAlert(alert)
                .setBadge(1)
                .build();
        com.x.jpush.assemble.control.huawei.apns.ApnsHmsOptions apnsHmsOptions = com.x.jpush.assemble.control.huawei.apns.ApnsHmsOptions
                .builder()
                .setTargetUserType(2)//目标用户类型，取值如下： 1：测试用户 2：正式用户 3：VoIP用户
                .build();
        com.x.jpush.assemble.control.huawei.apns.ApnsConfig apns = com.x.jpush.assemble.control.huawei.apns.ApnsConfig
                .builder()
                .addPayloadAps(aps)
                .setHmsOptions(apnsHmsOptions)
                .build();
        return com.x.jpush.assemble.control.huawei.model.Message.builder()
                .addAllToken(deviceList)
                .setApns(apns)
                .build();
    }



    /**
     * 极光推送消息
     * @param pushDeviceList
     * @param message
     * @param client
     * @throws Exception
     */
    private void send2Jpush(List<PushDevice> pushDeviceList, String message, JPushClient client) throws Exception {
        List<String> jiguangDeviceList = pushDeviceList.stream().map(PushDevice::getDeviceId).collect(Collectors.toList());
        PushPayload pushPayload = PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.registrationId(jiguangDeviceList))
                .setNotification(Notification.alert(message))
                .setOptions(Options.newBuilder().setApnsProduction(true).build()).build();
        PushResult pushResult = client.sendPush(pushPayload);
        logger.info("发送结果:{}.", pushResult);
    }

    public static class HuaweiSendResponse {
        private String code;
        private String msg;
        private String requestId;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getRequestId() {
            return requestId;
        }

        public void setRequestId(String requestId) {
            this.requestId = requestId;
        }
    }


    public static class Wi extends GsonPropertyObject {



        @FieldDescribe("人员")
        private String person;

        @FieldDescribe("消息内容")
        private String message;

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
