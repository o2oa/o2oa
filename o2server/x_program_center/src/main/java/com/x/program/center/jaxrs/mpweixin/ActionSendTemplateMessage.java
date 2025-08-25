package com.x.program.center.jaxrs.mpweixin;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Mpweixin;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.organization.core.entity.Person;
import com.x.program.center.Business;
import java.net.URLEncoder;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by fancyLou on 2024/4/9.
 * Copyright © 2024 O2. All rights reserved.
 */
public class ActionSendTemplateMessage extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(ActionSendTemplateMessage.class);


    ActionResult<Wo> execute(JsonElement jsonElement) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        logger.info(gson.toJson(jsonElement));
        Wo wo = new Wo();
        if (!Config.mpweixin().getEnable() || !Config.mpweixin().getMessageEnable()) {
            logger.warn("没有开启微信公众号消息功能！");
            throw new ExceptionConfigError();
        }
        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        if (StringUtils.isEmpty(wi.getReceiver())) {
            throw new ExceptionNotEmpty("receiver");
        }
        if (StringUtils.isEmpty(wi.getTempId())) {
            throw new ExceptionNotEmpty("tempId");
        }
        if (StringUtils.isEmpty(wi.getMsgUrl())) {
            throw new ExceptionNotEmpty("msgUrl");
        }
        if (wi.getFieldMap() == null || wi.getFieldMap().isEmpty()) {
            throw new ExceptionNotEmpty("fieldMap");
        }
        Person person = getPerson(wi.getReceiver());
        if (person == null) {
            logger.warn("没有找到用户.");
            throw new ExceptionNotExist();
        }
        String openId = person.getMpwxopenId();
        if (StringUtils.isEmpty(openId)) {
            logger.warn("用户 {} 没有绑定微信 openid ", person.getName());
            wo.setValue(false);
            result.setData(wo);
            return result;
        }
        String workUrl = getOpenWorkUrl(wi.getMsgUrl());
        if (StringUtils.isEmpty(workUrl)) {
            logger.warn("错误的打开地址 ", wi.getMsgUrl());
            throw new ExceptionNotEmpty("workUrl参数没有配置");
        }
        WeixinTempMessage wxMessage = new WeixinTempMessage();
        wxMessage.setTouser(openId);
        wxMessage.setUrl(workUrl);
        wxMessage.setTemplate_id(wi.getTempId());
        wxMessage.setData(wi.getFieldMap());
        logger.debug("发送的消息对象:{}.", wxMessage::toString);
        String url = Mpweixin.default_apiAddress + "/cgi-bin/message/template/send?access_token="
                + Config.mpweixin().accessToken();
        WeixinResponse response = HttpConnection.postAsObject(url, null, wxMessage.toString(),
                WeixinResponse.class);
        logger.debug("返回:{}.", response);
        if (response.getErrcode() != 0) {
            ExceptionQiyeweixinMessage e = new ExceptionQiyeweixinMessage(response.getErrcode(), response.getErrmsg());
            logger.error(e);
            throw e;
        }

        wo.setValue(true);
        result.setData(wo);
        return result;
    }

    private Person getPerson(String credential) throws Exception {
        Person person;
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            // 查询用户
            person = business.person().getWithCredential(credential);
        }
        return person;
    }

    private String getOpenWorkUrl(String url) {
        try {
            String workUrl = url;
            String o2oaUrl = Config.mpweixin().getWorkUrl();
            if (StringUtils.isEmpty(o2oaUrl)) {
                logger.warn( "没有获取到workUrl参数无法");
                return null;
            }
            workUrl = URLEncoder.encode(workUrl, DefaultCharset.name);
            o2oaUrl = o2oaUrl + "mpweixinsso.html?redirect=" + workUrl + "&type=login";
            o2oaUrl = URLEncoder.encode(o2oaUrl, DefaultCharset.name);
            String appId = Config.mpweixin().getAppid();
            String weixinUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appId + "&redirect_uri="
                    + o2oaUrl + "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
            if (logger.isDebugEnabled()) {
                logger.debug("final url:{}.", weixinUrl);
            }
            return weixinUrl;
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }


    public static class Wo extends WrapBoolean {

    }

    public static class Wi extends GsonPropertyObject {
        @FieldDescribe("接收者，人员 DN")
        private String receiver;

        @FieldDescribe("模板消息 id")
        private String tempId;

        @FieldDescribe("模板字段对应填充内容，key是模板字段的名称，value是对应字段填充的内容")
        private Map<String, WeixinTempMessageFieldObj> fieldMap;

        @FieldDescribe("点击消息打开的O2OA地址， x_desktop/ 后的地址。")
        private String msgUrl;

        public String getMsgUrl() {
            return msgUrl;
        }

        public void setMsgUrl(String msgUrl) {
            this.msgUrl = msgUrl;
        }

        public String getReceiver() {
            return receiver;
        }

        public void setReceiver(String receiver) {
            this.receiver = receiver;
        }

        public String getTempId() {
            return tempId;
        }

        public void setTempId(String tempId) {
            this.tempId = tempId;
        }

        public Map<String, WeixinTempMessageFieldObj> getFieldMap() {
            return fieldMap;
        }

        public void setFieldMap(Map<String, WeixinTempMessageFieldObj> fieldMap) {
            this.fieldMap = fieldMap;
        }
    }


    public static class WeixinTempMessageFieldObj extends GsonPropertyObject {


        @FieldDescribe("模板字段填充内容")
        private String value;
        @FieldDescribe("模板字段文字颜色，如 #173177")
        private String color;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }


    /**
     * 微信发送模版消息的对象
     */
    public class WeixinTempMessage extends GsonPropertyObject {

        private String touser;
        private String template_id;
        private String url;
        private String topcolor;
        private Map<String, WeixinTempMessageFieldObj> data; // 模版字段数据

        public String getTouser() {
            return touser;
        }

        public void setTouser(String touser) {
            this.touser = touser;
        }

        public String getTemplate_id() {
            return template_id;
        }

        public void setTemplate_id(String template_id) {
            this.template_id = template_id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTopcolor() {
            return topcolor;
        }

        public void setTopcolor(String topcolor) {
            this.topcolor = topcolor;
        }

        public Map<String, WeixinTempMessageFieldObj> getData() {
            return data;
        }

        public void setData(Map<String, WeixinTempMessageFieldObj> data) {
            this.data = data;
        }
    }


    public static class WeixinResponse {
        private Integer errcode;
        private String errmsg;
        private Long msgid;

        public Integer getErrcode() {
            return errcode;
        }

        public void setErrcode(Integer errcode) {
            this.errcode = errcode;
        }

        public String getErrmsg() {
            return errmsg;
        }

        public void setErrmsg(String errmsg) {
            this.errmsg = errmsg;
        }

        public Long getMsgid() {
            return msgid;
        }

        public void setMsgid(Long msgid) {
            this.msgid = msgid;
        }
    }



}
