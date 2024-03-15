package com.x.program.center.jaxrs.qiyeweixin;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.program.center.Business;

/**
 * Created by fancyLou on 2022/9/30.
 * Copyright © 2022 O2. All rights reserved.
 */
public class ActionSendGetPrivateInfoMessage extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionSendGetPrivateInfoMessage.class);


    ActionResult<Wo> execute(JsonElement jsonElement)  throws Exception {
        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        if (wi.getConsumerList() == null || wi.getConsumerList().isEmpty()) {
            throw new ExceptionNotEmpty("消息接收者");
        }
        if (StringUtils.isEmpty(wi.getMessageTitle())) {
            throw new ExceptionNotEmpty("消息标题");
        }
        if (StringUtils.isEmpty(wi.getMessageContent())) {
            throw new ExceptionNotEmpty("消息内容");
        }
        if (!Config.qiyeweixin().getEnable() || !Config.qiyeweixin().getMessageEnable()) {
            throw new ExceptionQywxNotEnable();
        }
        if (StringUtils.isEmpty(Config.qiyeweixin().getWorkUrl())) {
            throw new ExceptionQywxNoWorkUrl();
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            List<String> consumerPersonList = new ArrayList<>();
            Business business = new Business(emc);
            for (String e : wi.getConsumerList()) {
                if (e.endsWith( "@U" )) { // 组织
                    // 递归查询所有人员列表
                    List<String> persons = business.organization().person().listWithUnitSubNested( e );
                    if (persons != null && !persons.isEmpty()) {
                        consumerPersonList.addAll(persons);
                    }
                } else { // 人员直接加入
                    consumerPersonList.add(e);
                }
            }
            if (!consumerPersonList.isEmpty()) {
                removeDuplicationByHashSet(consumerPersonList);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("发送消息人员 ，{} ", Arrays.toString(consumerPersonList.toArray()));
            }
            QiyeweixinTextCardMessage m = generateMessage(business, consumerPersonList, wi.getMessageTitle(), wi.getMessageContent());
            String address = Config.qiyeweixin().getApiAddress() + "/cgi-bin/message/send?access_token="
                    + Config.qiyeweixin().corpAccessToken();
            QiyeweixinMessageResp resp = HttpConnection.postAsObject(address, null, m.toString(),
                    QiyeweixinMessageResp.class);
            if (resp.getErrcode() != 0) {
                throw new ExceptionQiyeweixinMessage(resp.getErrcode(), resp.getErrmsg());
            }
            logger.info("发送授权消息成功，{}.", resp.toString());
        }
        ActionResult<Wo> result = new ActionResult<>();
        Wo wo = new Wo();
        wo.setValue(true);
        return result;
    }

    private QiyeweixinTextCardMessage generateMessage(Business business, List<String> persons, String title, String content) throws Exception {
        QiyeweixinTextCardMessage cardMessage = new QiyeweixinTextCardMessage();
        cardMessage.setAgentid(Long.parseLong(Config.qiyeweixin().getAgentId(), 10));
        List<String> qywxIds = business.organization().person().listObject(persons).stream().map(Person::getQiyeweixinId).collect(Collectors.toList());
        cardMessage.setTouser(String.join("|", qywxIds));
        cardMessage.getTextcard().setTitle(title);
        cardMessage.getTextcard().setDescription(content);
        cardMessage.getTextcard().setUrl(messageOpenUrl());
        if (logger.isDebugEnabled()) {
            logger.debug("微信卡片消息：{}", cardMessage::toString);
        }
        return cardMessage;
    }

    private String messageOpenUrl() throws Exception {
        String corpId = Config.qiyeweixin().getCorpId();
        String agentId = Config.qiyeweixin().getAgentId();
        String o2oaUrl = Config.qiyeweixin().getWorkUrl() + "qiyeweixinsso.html?mode=snsapi_privateinfo";
        if (StringUtils.isEmpty(o2oaUrl) || StringUtils.isEmpty(corpId) || StringUtils.isEmpty(agentId)) {
            return null;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("o2oa 地址：{}" , o2oaUrl);
        }
        o2oaUrl = URLEncoder.encode(o2oaUrl, DefaultCharset.name);
        if (logger.isDebugEnabled()) {
            logger.debug("encode url : {}", o2oaUrl);
        }
        String oauthUrl = Config.qiyeweixin().getOauth2Address();
        String url = oauthUrl + "/connect/oauth2/authorize?appid=" + corpId
                + "&response_type=code&scope=snsapi_privateinfo&agentid=" + agentId + "&redirect_uri=" + o2oaUrl
                + "&#wechat_redirect";
        if (logger.isDebugEnabled()) {
            logger.debug("final url : {}" , url);
        }
        return url;
    }

    /**使用HashSet实现List去重(无序)
     *
     * @param list
     * */
    private List<String> removeDuplicationByHashSet(List<String> list) {
        HashSet<String> set = new HashSet<>(list);
        //把List集合所有元素清空
        list.clear();
        //把HashSet对象添加至List集合
        list.addAll(set);
        return list;
    }



    public static class Wo extends WrapBoolean {

    }
    public static class Wi extends GsonPropertyObject {

        @FieldDescribe("消息接收者.")
        private List<String> consumerList;
        @FieldDescribe("消息标题.")
        private String messageTitle;
        @FieldDescribe("消息内容.")
        private String messageContent;

        public String getMessageTitle() {
            return messageTitle;
        }

        public void setMessageTitle(String messageTitle) {
            this.messageTitle = messageTitle;
        }

        public String getMessageContent() {
            return messageContent;
        }

        public void setMessageContent(String messageContent) {
            this.messageContent = messageContent;
        }

        public List<String> getConsumerList() {
            return consumerList;
        }

        public void setConsumerList(List<String> consumerList) {
            this.consumerList = consumerList;
        }
    }


    /**
     * 文本卡片消息
     * 用于可以打开的工作消息发送
     */
    public static class QiyeweixinTextCardMessage extends QiyeweixinBaseMessage {


        private static final long serialVersionUID = -7805292712056929523L;

        /**
         * {
         *    "touser" : "UserID1|UserID2|UserID3",
         *    "toparty" : "PartyID1 | PartyID2",
         *    "totag" : "TagID1 | TagID2",
         *    "msgtype" : "textcard",
         *    "agentid" : 1,
         *    "textcard" : {
         *             "title" : "领奖通知",
         *             "description" : "<div class=\"gray\">2016年9月26日</div> <div class=\"normal\">恭喜你抽中iPhone 7一台，领奖码：xxxx</div><div class=\"highlight\">请于2016年10月10日前联系行政同事领取</div>",
         *             "url" : "URL",
         *                         "btntxt":"更多"
         *    },
         *    "enable_id_trans": 0,
         *    "enable_duplicate_check": 0,
         *    "duplicate_check_interval": 1800
         * }
         */

        public QiyeweixinTextCardMessage() {
            this.setMsgtype("textcard");
        }


        private TextCard textcard = new TextCard();
        private int enable_id_trans = 0;
        private int enable_duplicate_check = 0;
        private int duplicate_check_interval = 0;


        public TextCard getTextcard() {
            return textcard;
        }

        public void setTextcard(TextCard textcard) {
            this.textcard = textcard;
        }

        public int getEnable_id_trans() {
            return enable_id_trans;
        }

        public void setEnable_id_trans(int enable_id_trans) {
            this.enable_id_trans = enable_id_trans;
        }

        public int getEnable_duplicate_check() {
            return enable_duplicate_check;
        }

        public void setEnable_duplicate_check(int enable_duplicate_check) {
            this.enable_duplicate_check = enable_duplicate_check;
        }

        public int getDuplicate_check_interval() {
            return duplicate_check_interval;
        }

        public void setDuplicate_check_interval(int duplicate_check_interval) {
            this.duplicate_check_interval = duplicate_check_interval;
        }






        public static class TextCard {

            private String title = "";
            private String description = "";
            private String url = "";
            private String btntxt = "详情";

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getBtntxt() {
                return btntxt;
            }

            public void setBtntxt(String btntxt) {
                this.btntxt = btntxt;
            }
        }

    }


    public static class QiyeweixinBaseMessage extends GsonPropertyObject {


        private static final long serialVersionUID = 6612140970748517772L;
        private String touser = "";
        private String toparty = "";
        private String totag = "";
        private Long agentid = 0L;
        private String msgtype;


        public String getMsgtype() {
            return msgtype;
        }

        public void setMsgtype(String msgtype) {
            this.msgtype = msgtype;
        }

        public String getTouser() {
            return touser;
        }

        public void setTouser(String touser) {
            this.touser = touser;
        }

        public String getToparty() {
            return toparty;
        }

        public void setToparty(String toparty) {
            this.toparty = toparty;
        }

        public String getTotag() {
            return totag;
        }

        public void setTotag(String totag) {
            this.totag = totag;
        }

        public Long getAgentid() {
            return agentid;
        }

        public void setAgentid(Long agentid) {
            this.agentid = agentid;
        }
    }



    public static class QiyeweixinMessageResp extends GsonPropertyObject{

        /**
         * <code>	 {
         * "errcode" : 0,
         * "errmsg" : "ok",
         * "invaliduser" : "userid1|userid2", // 不区分大小写，返回的列表都统一转为小写
         * "invalidparty" : "partyid1|partyid2",
         * "invalidtag":"tagid1|tagid2"
         * }
         * </code>
         */

        private Integer errcode;
        private String errmsg;
        private String invaliduser;
        private String invalidparty;
        private String invalidtag;

        public String getErrmsg() {
            return errmsg;
        }

        public void setErrmsg(String errmsg) {
            this.errmsg = errmsg;
        }

        public String getInvaliduser() {
            return invaliduser;
        }

        public void setInvaliduser(String invaliduser) {
            this.invaliduser = invaliduser;
        }

        public String getInvalidparty() {
            return invalidparty;
        }

        public void setInvalidparty(String invalidparty) {
            this.invalidparty = invalidparty;
        }

        public String getInvalidtag() {
            return invalidtag;
        }

        public void setInvalidtag(String invalidtag) {
            this.invalidtag = invalidtag;
        }

        public Integer getErrcode() {
            return errcode;
        }

        public void setErrcode(Integer errcode) {
            this.errcode = errcode;
        }

    }
}




