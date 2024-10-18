package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.message.assemble.communicate.ThisApplication;
import com.x.message.assemble.communicate.jaxrs.im.ActionImConfig.Wo;
import com.x.message.core.entity.IMConversation;
import com.x.message.core.entity.IMMsg;
import com.x.message.core.entity.Message;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

abstract class BaseAction extends StandardJaxrsAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseAction.class);

    public static final String IM_CONFIG_KEY_NAME = "imConfig"; // 这个配置会已对象写入到 web.json ，已imConfig作为key名称


    protected ConversationInvokeValue checkConversationInvoke(EffectivePerson effectivePerson, String operateType,
            String type, List<String> newMembers, List<String> oldMembers, String newTitle,
            String newNote) throws Exception {
        // 是否有配置检查脚本
        ActionResult<Wo> config = new ActionImConfig().execute(effectivePerson);
        String script = config.getData().getConversationCheckInvoke();
        if (script != null) {
            script = script.trim();
        }
        ConversationInvokeValue value = new ConversationInvokeValue();
        value.setResult(true);
        if (StringUtils.isNotEmpty(script)) {
            LOGGER.info("执行脚本校验 {}", script);
            ConversationInvokeWi wi = new ConversationInvokeWi();
            wi.setOperator(effectivePerson.getDistinguishedName());
            wi.setOperateType(operateType);
            wi.setType(type);
            if ("create".equals(operateType)) {
                wi.setAddMembers(newMembers);
            } else if ("update".equals(operateType)) {
                // 计算新增成员 (newMembers - oldMembers)
                List<String> addedMembers = new ArrayList<>(newMembers);
                addedMembers.removeAll(oldMembers);
                wi.setAddMembers(addedMembers);
                // 计算删除的成员 (oldMembers - newMembers)
                List<String> removedMembers = new ArrayList<>(oldMembers);
                removedMembers.removeAll(newMembers);
                wi.setRemoveMembers(removedMembers);
                wi.setTitle(newTitle);
                wi.setNote(newNote);
            }
            ActionResponse response = CipherConnectionAction.post(false, 4000, 8000,
                    Config.url_x_program_center_jaxrs("invoke", script, "execute"), wi);
            ConversationInvokeWo result = response.getData(ConversationInvokeWo.class);
            value.setResult( result == null || result.value == null || !BooleanUtils.isFalse(result.value.result) );
            String msg = "";
            if (result != null && result.value != null) {
                msg = result.value.msg;
            }
            value.setMsg(msg);
        }
        return value;
    }

    // 发送会话消息
    public void sendConversationMsg(List<String> persons, IMConversation conversation,
            String messageType) {
        for (String person : persons) {
            String title = "会话的消息";
            MessageConnector.send(messageType, title, person, conversation);
        }
    }

    // 发送聊天消息
    public void sendWsMessage(IMConversation conversation, IMMsg msg, String messageType,
            EffectivePerson effectivePerson) {
        // 发送消息
        List<String> persons = conversation.getPersonList();
        // 原来排除了自己 先不排除，因为有多端操作的可能，多端可以同步消息
//        persons.removeIf(s -> (effectivePerson.getDistinguishedName().equals(s)));
        String name = "";
        try {
            name = effectivePerson.getDistinguishedName().substring(0,
                    effectivePerson.getDistinguishedName().indexOf("@"));
        } catch (Exception e) {
            LOGGER.error(e);
        }
        for (String person : persons) {
            LOGGER.info("发送im消息， person: " + person + " messageType: " + messageType);
            String title = "您有一条来自 " + name + " 的消息";
            if (person.equals(effectivePerson.getDistinguishedName())) {
                title = "您有一条新消息";
            }
            Message message = new Message();
//            String body = imMessageBody(msg);
            message.setTitle(title);
            message.setPerson(person);
            message.setType(messageType);
            message.setId("");
            message.setBody(msg.toString());
            try {
                // 发送 websocket 消息
                ThisApplication.wsConsumeQueue.send(message);
            } catch (Exception e) {
                LOGGER.error(e);
            }
            // 发送聊天消息时候 如果没有在线 发送app推送消息
            if (!person.equals(effectivePerson.getDistinguishedName()) && MessageConnector.TYPE_IM_CREATE.equals(messageType)) {
                try {
                    if (!ThisApplication.wsClients().containsValue(person)) {
                        LOGGER.info("向app 推送im消息， person: " + person);

                        if (BooleanUtils.isTrue(Config.pushConfig().getEnable())) {
                            ThisApplication.pmsinnerConsumeQueue.send(message);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error(e);
                }
            }
        }
    }


    private String imMessageBody(IMMsg msg) {
        String json = msg.getBody();
        ActionMsgCreate.IMMessageBody body = gson.fromJson(json,
                ActionMsgCreate.IMMessageBody.class);
        if ("text".equals(body.getType())) {
            return body.getBody();
        } else if ("emoji".equals(body.getType())) {
            return "[表情]";
        } else if ("image".equals(body.getType())) {
            return "[图片]";
        } else if ("audio".equals(body.getType())) {
            return "[声音]";
        } else if ("location".equals(body.getType())) {
            return "[位置]";
        } else if ("file".equals(body.getType())) {
            return "[文件]";
        } else if ("process".equals(body.getType())) {
            return "[工作]";
        } else if ("cms".equals(body.getType())) {
            return "[信息]";
        } else {
            return "[其它]";
        }
    }


    public static class ConversationInvokeWo extends GsonPropertyObject {

        @FieldDescribe("invoke返回结果.")
        private ConversationInvokeValue value;

        public ConversationInvokeValue getValue() {
            return value;
        }

        public void setValue(
                ConversationInvokeValue value) {
            this.value = value;
        }
    }
    public static class ConversationInvokeValue extends GsonPropertyObject {
        @FieldDescribe("返回结果.")
        private Boolean result;
        @FieldDescribe("消息.")
        private String msg;

        public Boolean getResult() {
            return result;
        }

        public void setResult(Boolean result) {
            this.result = result;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }
    public static class ConversationInvokeWi extends GsonPropertyObject {

        @FieldDescribe("当前操作人.")
        private String operator;
        @FieldDescribe("操作类型，create|update.")
        private String operateType;

        @FieldDescribe("会话类型， single|group.")
        private String type;
        @FieldDescribe("增加人员列表.")
        private List<String> addMembers;
        @FieldDescribe("删除人员列表.")
        private List<String> removeMembers;
        @FieldDescribe("群聊标题.")
        private String title;
        @FieldDescribe("群聊公告.")
        private String note;

        public String getOperateType() {
            return operateType;
        }

        public void setOperateType(String operateType) {
            this.operateType = operateType;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<String> getAddMembers() {
            return addMembers;
        }

        public void setAddMembers(List<String> addMembers) {
            this.addMembers = addMembers;
        }

        public List<String> getRemoveMembers() {
            return removeMembers;
        }

        public void setRemoveMembers(List<String> removeMembers) {
            this.removeMembers = removeMembers;
        }
    }
}

