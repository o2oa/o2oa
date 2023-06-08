package com.x.base.core.project.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Message.ActivemqConsumer;
import com.x.base.core.project.config.Message.ApiConsumer;
import com.x.base.core.project.config.Message.CalendarConsumer;
import com.x.base.core.project.config.Message.Consumer;
import com.x.base.core.project.config.Message.DingdingConsumer;
import com.x.base.core.project.config.Message.HadoopConsumer;
import com.x.base.core.project.config.Message.JdbcConsumer;
import com.x.base.core.project.config.Message.KafkaConsumer;
import com.x.base.core.project.config.Message.MailConsumer;
import com.x.base.core.project.config.Message.MpweixinConsumer;
import com.x.base.core.project.config.Message.PmsinnerConsumer;
import com.x.base.core.project.config.Message.QiyeweixinConsumer;
import com.x.base.core.project.config.Message.RestfulConsumer;
import com.x.base.core.project.config.Message.TableConsumer;
import com.x.base.core.project.config.Message.WelinkConsumer;
import com.x.base.core.project.config.Message.WsConsumer;
import com.x.base.core.project.config.Message.ZhengwudingdingConsumer;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.message.MessageConnector;

public class Messages extends ConfigObject {

    private static final long serialVersionUID = 7251058521510812856L;

    private static final Message MESSAGE_ALL = new Message(MessageConnector.CONSUME_WS,
            MessageConnector.CONSUME_PMS_INNER, MessageConnector.CONSUME_DINGDING, MessageConnector.CONSUME_WELINK,
            MessageConnector.CONSUME_ZHENGWUDINGDING, MessageConnector.CONSUME_QIYEWEIXIN,
            MessageConnector.CONSUME_MPWEIXIN, MessageConnector.CONSUME_CALENDAR, MessageConnector.CONSUME_KAFKA,
            MessageConnector.CONSUME_ACTIVEMQ, MessageConnector.CONSUME_RESTFUL, MessageConnector.CONSUME_MAIL,
            MessageConnector.CONSUME_API, MessageConnector.CONSUME_JDBC, MessageConnector.CONSUME_TABLE,
            MessageConnector.CONSUME_HADOOP);

    private static final Message MESSAGE_NOTICE = new Message(MessageConnector.CONSUME_WS,
            MessageConnector.CONSUME_PMS_INNER, MessageConnector.CONSUME_DINGDING, MessageConnector.CONSUME_WELINK,
            MessageConnector.CONSUME_ZHENGWUDINGDING, MessageConnector.CONSUME_QIYEWEIXIN,
            MessageConnector.CONSUME_MPWEIXIN);

    private static final Message MESSAGE_OUTER = new Message(MessageConnector.CONSUME_KAFKA,
            MessageConnector.CONSUME_ACTIVEMQ, MessageConnector.CONSUME_RESTFUL, MessageConnector.CONSUME_MAIL,
            MessageConnector.CONSUME_API, MessageConnector.CONSUME_JDBC, MessageConnector.CONSUME_TABLE,
            MessageConnector.CONSUME_HADOOP);

    public Messages() {
        super();
    }

    @FieldDescribe("消费器配置.")
    private Map<String, JsonElement> consumers = new LinkedHashMap<>();

    @FieldDescribe("装载器脚本.")
    private Map<String, String> loaders = new LinkedHashMap<>();

    @FieldDescribe("过滤器脚本.")
    private Map<String, String> filters = new LinkedHashMap<>();

    @FieldDescribe("创建应用.")
    private Message application_create;
    @FieldDescribe("更新应用.")
    private Message application_update;
    @FieldDescribe("删除应用.")
    private Message application_delete;
    @FieldDescribe("创建流程.")
    private Message process_create;
    @FieldDescribe("更新流程.")
    private Message process_update;
    @FieldDescribe("删除流程.")
    private Message process_delete;
    @FieldDescribe("有新的工作通过消息节点.")
    private Message activity_message;
    @FieldDescribe("工作完成转已完成工作.")
    private Message work_to_workCompleted;
    @FieldDescribe("创建工作.")
    private Message work_create;
    @FieldDescribe("删除工作.")
    private Message work_delete;
    @FieldDescribe("创建已完成工作.")
    private Message workCompleted_create;
    @FieldDescribe("删除已完成工作.")
    private Message workCompleted_delete;
    @FieldDescribe("待办完成转已办.")
    private Message task_to_taskCompleted;
    @FieldDescribe("创建待办.")
    private Message task_create;
    @FieldDescribe("删除待办.")
    private Message task_delete;
    @FieldDescribe("待办即将过期催办.")
    private Message task_urge;
    @FieldDescribe("待办过期.")
    private Message task_expire;
    @FieldDescribe("待办提醒.")
    private Message task_press;
    @FieldDescribe("创建已办.")
    private Message taskCompleted_create;
    @FieldDescribe("删除已办.")
    private Message taskCompleted_delete;
    @FieldDescribe("待阅转已阅.")
    private Message read_to_readCompleted;
    @FieldDescribe("创建待阅.")
    private Message read_create;
    @FieldDescribe("删除待阅.")
    private Message read_delete;
    @FieldDescribe("创建已阅.")
    private Message readCompleted_create;
    @FieldDescribe("删除已阅.")
    private Message readCompleted_delete;
    @FieldDescribe("创建参阅.")
    private Message review_create;
    @FieldDescribe("删除参阅.")
    private Message review_delete;
    @FieldDescribe("会议邀请.")
    private Message meeting_invite;
    @FieldDescribe("删除会议.")
    private Message meeting_delete;
    @FieldDescribe("会议邀请接受.")
    private Message meeting_accept;
    @FieldDescribe("会议邀请拒绝.")
    private Message meeting_reject;
    @FieldDescribe("创建附件.")
    private Message attachment_create;
    @FieldDescribe("删除附件.")
    private Message attachment_delete;
    @FieldDescribe("附件分享.")
    private Message attachment_share;
    @FieldDescribe("附件取消分享.")
    private Message attachment_shareCancel;
    @FieldDescribe("附件可编辑设置.")
    private Message attachment_editor;
    @FieldDescribe("附件可编辑取消.")
    private Message attachment_editorCancel;
    @FieldDescribe("附件可编辑修改.")
    private Message attachment_editorModify;
    @FieldDescribe("日历通知.")
    private Message calendar_alarm;
    @FieldDescribe("自定义消息创建.")
    private Message custom_create;
    @FieldDescribe("工作管理任务创建.")
    private Message teamwork_taskCreate;
    @FieldDescribe("工作管理任务更新.")
    private Message teamwork_taskUpdate;
    @FieldDescribe("工作管理任务删除.")
    private Message teamwork_taskDelete;
    @FieldDescribe("工作管理任务超时.")
    private Message teamwork_taskOvertime;
    @FieldDescribe("工作管理聊天.")
    private Message teamwork_chat;
    @FieldDescribe("内容管理发布.")
    private Message cms_publish;
    @FieldDescribe("内容管理发布创建者通知.")
    private Message cms_publish_to_creator;
    @FieldDescribe("论坛创建贴子.")
    private Message bbs_subjectCreate;
    @FieldDescribe("论坛创建回复.")
    private Message bbs_replyCreate;
    @FieldDescribe("脑图发送.")
    private Message mind_fileSend;
    @FieldDescribe("脑图分享.")
    private Message mind_fileShare;
    @FieldDescribe("聊聊消息.")
    private Message im_create;
    @FieldDescribe("聊聊消息.")
    private Message im_revoke;
    @FieldDescribe("聊聊消息.")
    private Message im_conversation_delete;
    @FieldDescribe("聊聊消息.")
    private Message im_conversation_update;
    @FieldDescribe("打卡提醒消息.")
    private Message attendance_checkInAlert;
    @FieldDescribe("打卡异常提醒消息.")
    private Message attendance_checkInException;
    @FieldDescribe("自定义消息.")
    private Map<String, Message> custom = new LinkedHashMap<>();

    public static Messages defaultInstance() {
        Messages o = new Messages();
        o.application_create = MESSAGE_OUTER.cloneThenSetDescription("创建应用.");
        o.application_update = MESSAGE_OUTER.cloneThenSetDescription("更新应用.");
        o.application_delete = MESSAGE_OUTER.cloneThenSetDescription("删除应用.");
        o.process_create = MESSAGE_OUTER.cloneThenSetDescription("创建流程.");
        o.process_update = MESSAGE_OUTER.cloneThenSetDescription("更新流程.");
        o.process_delete = MESSAGE_OUTER.cloneThenSetDescription("删除流程.");
        o.activity_message = MESSAGE_OUTER.cloneThenSetDescription("有新的工作通过消息节点.");
        o.work_to_workCompleted = MESSAGE_OUTER.cloneThenSetDescription("工作完成转已完成工作.");
        o.work_create = MESSAGE_OUTER.cloneThenSetDescription("创建工作.");
        o.work_delete = MESSAGE_OUTER.cloneThenSetDescription("删除工作.");
        o.workCompleted_create = MESSAGE_OUTER.cloneThenSetDescription("创建已完成工作.");
        o.workCompleted_delete = MESSAGE_OUTER.cloneThenSetDescription("删除已完成工作.");
        o.task_to_taskCompleted = MESSAGE_OUTER.cloneThenSetDescription("待办完成转已办.");
        o.task_create = MESSAGE_ALL.cloneThenSetDescription("创建待办.");
        o.task_delete = MESSAGE_OUTER.cloneThenSetDescription("删除待办.");
        o.task_urge = MESSAGE_ALL.cloneThenSetDescription("待办即将过期催办.");
        o.task_expire = MESSAGE_ALL.cloneThenSetDescription("待办过期.");
        o.task_press = MESSAGE_ALL.cloneThenSetDescription("待办提醒.");
        o.taskCompleted_create = MESSAGE_OUTER.cloneThenSetDescription("创建已办.");
        o.taskCompleted_delete = MESSAGE_OUTER.cloneThenSetDescription("删除已办.");
        o.read_to_readCompleted = MESSAGE_OUTER.cloneThenSetDescription("待阅转已阅.");
        o.read_create = MESSAGE_ALL.cloneThenSetDescription("创建待阅.");
        o.read_delete = MESSAGE_OUTER.cloneThenSetDescription("删除待阅.");
        o.readCompleted_create = MESSAGE_OUTER.cloneThenSetDescription("创建已阅.");
        o.readCompleted_delete = MESSAGE_OUTER.cloneThenSetDescription("删除已阅.");
        o.review_create = MESSAGE_OUTER.cloneThenSetDescription("创建参阅.");
        o.review_delete = MESSAGE_OUTER.cloneThenSetDescription("删除参阅.");
        o.meeting_invite = MESSAGE_ALL.cloneThenSetDescription("会议邀请.");
        o.meeting_delete = MESSAGE_OUTER.cloneThenSetDescription("删除会议.");
        o.meeting_accept = MESSAGE_ALL.cloneThenSetDescription("会议邀请接受.");
        o.meeting_reject = MESSAGE_ALL.cloneThenSetDescription("会议邀请拒绝.");
        o.attachment_create = MESSAGE_OUTER.cloneThenSetDescription("创建附件.");
        o.attachment_delete = MESSAGE_OUTER.cloneThenSetDescription("删除附件.");
        o.attachment_share = MESSAGE_ALL.cloneThenSetDescription("附件分享.");
        o.attachment_shareCancel = MESSAGE_ALL.cloneThenSetDescription("附件取消分享.");
        o.attachment_editor = MESSAGE_OUTER.cloneThenSetDescription("附件可编辑设置.");
        o.attachment_editorCancel = MESSAGE_OUTER.cloneThenSetDescription("附件可编辑取消.");
        o.attachment_editorModify = MESSAGE_OUTER.cloneThenSetDescription("附件可编辑修改.");
        o.calendar_alarm = MESSAGE_ALL.cloneThenSetDescription("日历通知.");
        o.custom_create = MESSAGE_ALL.cloneThenSetDescription("自定义消息创建.");
        o.teamwork_taskCreate = MESSAGE_ALL.cloneThenSetDescription("工作管理任务创建.");
        o.teamwork_taskUpdate = MESSAGE_ALL.cloneThenSetDescription("工作管理任务更新.");
        o.teamwork_taskDelete = MESSAGE_ALL.cloneThenSetDescription("工作管理任务删除.");
        o.teamwork_taskOvertime = MESSAGE_ALL.cloneThenSetDescription("工作管理任务超时.");
        o.teamwork_chat = MESSAGE_NOTICE.cloneThenSetDescription("工作管理聊天.");
        o.cms_publish = MESSAGE_OUTER.cloneThenSetDescription("内容管理发布.");
        o.cms_publish_to_creator = MESSAGE_NOTICE.cloneThenSetDescription("内容管理发布创建者通知.");
        o.bbs_subjectCreate = MESSAGE_ALL.cloneThenSetDescription("论坛创建贴子.");
        o.bbs_replyCreate = MESSAGE_ALL.cloneThenSetDescription("论坛创建回复.");
        o.mind_fileSend = MESSAGE_ALL.cloneThenSetDescription("脑图发送.");
        o.mind_fileShare = MESSAGE_ALL.cloneThenSetDescription("脑图分享.");
        o.im_create = new Message(MessageConnector.CONSUME_WS, MessageConnector.CONSUME_PMS_INNER)
                .cloneThenSetDescription("聊聊消息.");
        o.im_revoke = new Message(MessageConnector.CONSUME_WS).cloneThenSetDescription("聊聊消息.");
        o.im_conversation_delete = new Message(MessageConnector.CONSUME_WS).cloneThenSetDescription("聊聊消息.");
        o.im_conversation_update = new Message(MessageConnector.CONSUME_WS).cloneThenSetDescription("聊聊消息.");
        o.attendance_checkInAlert = MESSAGE_NOTICE.cloneThenSetDescription("打卡提醒消息.");
        o.attendance_checkInException = MESSAGE_NOTICE.cloneThenSetDescription("打卡异常提醒消息.");
        o.custom = new LinkedHashMap<>();
        o.custom.put("foo", MESSAGE_ALL.cloneThenSetDescription("自定义消息类型."));
        o.consumers = new LinkedHashMap<>();
        o.consumers.put("ws_demo", XGsonBuilder.instance().toJsonTree(new WsConsumer()));
        o.consumers.put("pmsinner_demo", XGsonBuilder.instance().toJsonTree(new PmsinnerConsumer()));
        o.consumers.put("calendar_demo", XGsonBuilder.instance().toJsonTree(new CalendarConsumer()));
        o.consumers.put("dingding_demo", XGsonBuilder.instance().toJsonTree(new DingdingConsumer()));
        o.consumers.put("welink_demo", XGsonBuilder.instance().toJsonTree(new WelinkConsumer()));
        o.consumers.put("qiyeweixin_demo", XGsonBuilder.instance().toJsonTree(new QiyeweixinConsumer()));
        o.consumers.put("mpweixin_demo", XGsonBuilder.instance().toJsonTree(new MpweixinConsumer()));
        o.consumers.put("kafka_demo", XGsonBuilder.instance().toJsonTree(KafkaConsumer.defaultInstance()));
        o.consumers.put("activemq_demo", XGsonBuilder.instance().toJsonTree(ActivemqConsumer.defaultInstance()));
        o.consumers.put("restful_demo", XGsonBuilder.instance().toJsonTree(RestfulConsumer.defaultInstance()));
        o.consumers.put("mail_demo", XGsonBuilder.instance().toJsonTree(MailConsumer.defaultInstance()));
//		o.consumers.put("api_demo", XGsonBuilder.instance().toJsonTree(ApiConsumer.defaultInstance()));
        o.consumers.put("jdbc_demo", XGsonBuilder.instance().toJsonTree(JdbcConsumer.defaultInstance()));
//		o.consumers.put("table_demo", XGsonBuilder.instance().toJsonTree(TableConsumer.defaultInstance()));
        o.consumers.put("hadoop_demo", XGsonBuilder.instance().toJsonTree(HadoopConsumer.defaultInstance()));
        o.consumers.put("andfx_demo", XGsonBuilder.instance().toJsonTree(new Message.AndFxConsumer()));
        o.loaders = new LinkedHashMap<>();
        o.filters = new LinkedHashMap<>();
        return o;
    }

    public String getLoader(String name) {
        String text = "";
        if ((null != this.loaders) && StringUtils.isNotEmpty(name)) {
            text = this.loaders.get(name);
        }
        return text;
    }

    public String getFilter(String name) {
        String text = "";
        if ((null != this.filters) && StringUtils.isNotEmpty(name)) {
            text = this.filters.get(name);
        }
        return text;
    }

    public List<Consumer> getConsumers(String type) {
        Optional<Message> o = this.getMessage(type);
        List<Consumer> list = new ArrayList<>();
        Gson gson = XGsonBuilder.instance();
        if (o.isPresent() && (null != o.get().getConsumers())) {
            for (JsonElement jsonElement : o.get().getConsumers()) {
                String key = XGsonBuilder.extractString(jsonElement, "consumer");
                if (StringUtils.isNotBlank(key)) {
                    JsonElement element = this.consumers.get(key);
                    if (null != element) {
                        jsonToConsumer(gson, element, list);
                    } else {
                        jsonToConsumer(gson, jsonElement, list);
                    }
                } else {
                    jsonToConsumer(gson, jsonElement, list);
                }
            }
        }
        return list;
    }

    private Message getApplicationCreate() {
        if (null == this.application_create) {
            this.application_create = MESSAGE_OUTER.cloneThenSetDescription("创建应用.");
        }
        return this.application_create;
    }

    private Message getApplicationUpdate() {
        if (null == this.application_update) {
            this.application_update = MESSAGE_OUTER.cloneThenSetDescription("更新应用.");
        }
        return this.application_update;
    }

    private Message getApplicationDelete() {
        if (null == this.application_delete) {
            this.application_delete = MESSAGE_OUTER.cloneThenSetDescription("删除应用.");
        }
        return this.application_delete;
    }

    private Message getProcessCreate() {
        if (null == this.process_create) {
            this.process_create = MESSAGE_OUTER.cloneThenSetDescription("创建流程.");
        }
        return this.process_create;
    }

    private Message getProcessUpdate() {
        if (null == this.process_update) {
            this.process_update = MESSAGE_OUTER.cloneThenSetDescription("更新流程.");
        }
        return this.process_update;
    }

    private Message getProcessDelete() {
        if (null == this.process_delete) {
            this.process_delete = MESSAGE_OUTER.cloneThenSetDescription("删除流程.");
        }
        return this.process_delete;
    }

    private Message getActivityMessage() {
        if (null == this.activity_message) {
            this.activity_message = MESSAGE_OUTER.cloneThenSetDescription("有新的工作通过消息节点.");
        }
        return this.activity_message;
    }

    private Message getWorkToWorkCompleted() {
        if (null == this.work_to_workCompleted) {
            this.work_to_workCompleted = MESSAGE_OUTER.cloneThenSetDescription("工作完成转已完成工作.");
        }
        return this.work_to_workCompleted;
    }

    private Message getWorkCreate() {
        if (null == this.work_create) {
            this.work_create = MESSAGE_OUTER.cloneThenSetDescription("创建工作.");
        }
        return this.work_create;
    }

    private Message getWorkDelete() {
        if (null == this.work_delete) {
            this.work_delete = MESSAGE_OUTER.cloneThenSetDescription("删除工作.");
        }
        return this.work_delete;
    }

    private Message getWorkCompletedCreate() {
        if (null == this.workCompleted_create) {
            this.workCompleted_create = MESSAGE_OUTER.cloneThenSetDescription("创建已完成工作.");
        }
        return this.workCompleted_create;
    }

    private Message getWorkCompletedDelete() {
        if (null == this.workCompleted_delete) {
            this.workCompleted_delete = MESSAGE_OUTER.cloneThenSetDescription("删除已完成工作.");
        }
        return this.workCompleted_delete;
    }

    private Message getTaskToTaskCompleted() {
        if (null == this.task_to_taskCompleted) {
            this.task_to_taskCompleted = MESSAGE_OUTER.cloneThenSetDescription("待办完成转已办.");
        }
        return this.task_to_taskCompleted;
    }

    private Message getTaskCreate() {
        if (null == this.task_create) {
            this.task_create = MESSAGE_ALL.cloneThenSetDescription("创建待办.");
        }
        return this.task_create;
    }

    private Message getTaskDelete() {
        if (null == this.task_delete) {
            this.task_delete = MESSAGE_OUTER.cloneThenSetDescription("删除待办.");
        }
        return this.task_delete;
    }

    private Message getTaskUrge() {
        if (null == this.task_urge) {
            this.task_urge = MESSAGE_ALL.cloneThenSetDescription("待办即将过期催办.");
        }
        return this.task_urge;
    }

    private Message getTaskExpire() {
        if (null == this.task_expire) {
            this.task_expire = MESSAGE_ALL.cloneThenSetDescription("待办过期.");
        }
        return this.task_expire;
    }

    private Message getTaskPress() {
        if (null == this.task_press) {
            this.task_press = MESSAGE_ALL.cloneThenSetDescription("待办提醒.");
        }
        return this.task_press;
    }

    private Message getTaskCompletedCreate() {
        if (null == this.taskCompleted_create) {
            this.taskCompleted_create = MESSAGE_OUTER.cloneThenSetDescription("创建已办.");
        }
        return this.taskCompleted_create;
    }

    private Message getTaskCompletedDelete() {
        if (null == this.taskCompleted_delete) {
            this.taskCompleted_delete = MESSAGE_OUTER.cloneThenSetDescription("删除已办.");
        }
        return this.taskCompleted_delete;
    }

    private Message getReadToReadCompleted() {
        if (null == this.read_to_readCompleted) {
            this.read_to_readCompleted = MESSAGE_OUTER.cloneThenSetDescription("待阅转已阅.");
        }
        return this.read_to_readCompleted;
    }

    private Message getReadCreate() {
        if (null == this.read_create) {
            this.read_create = MESSAGE_ALL.cloneThenSetDescription("创建待阅.");
        }
        return this.read_create;
    }

    private Message getReadDelete() {
        if (null == this.read_delete) {
            this.read_delete = MESSAGE_OUTER.cloneThenSetDescription("待阅删除.");
        }
        return this.read_delete;
    }

    private Message getReadCompletedCreate() {
        if (null == this.readCompleted_create) {
            this.readCompleted_create = MESSAGE_OUTER.cloneThenSetDescription("创建已阅.");
        }
        return this.readCompleted_create;
    }

    private Message getReadCompletedDelete() {
        if (null == this.readCompleted_delete) {
            this.readCompleted_delete = MESSAGE_OUTER.cloneThenSetDescription("已阅删除.");
        }
        return this.readCompleted_delete;
    }

    private Message getReviewCreate() {
        if (null == this.review_create) {
            this.review_create = MESSAGE_OUTER.cloneThenSetDescription("创建参阅.");
        }
        return this.review_create;
    }

    private Message getReviewDelete() {
        if (null == this.review_delete) {
            this.review_delete = MESSAGE_OUTER.cloneThenSetDescription("删除参阅.");
        }
        return this.review_delete;
    }

    private Message getMeetingInvite() {
        if (null == this.meeting_invite) {
            this.meeting_invite = MESSAGE_ALL.cloneThenSetDescription("会议邀请.");
        }
        return this.meeting_invite;
    }

    private Message getMeetingDelete() {
        if (null == this.meeting_delete) {
            this.meeting_delete = MESSAGE_OUTER.cloneThenSetDescription("删除会议.");
        }
        return this.meeting_delete;
    }

    private Message getMeetingAccept() {
        if (null == this.meeting_accept) {
            this.meeting_accept = MESSAGE_ALL.cloneThenSetDescription("会议邀请接受.");
        }
        return this.meeting_accept;
    }

    private Message getMeetingReject() {
        if (null == this.meeting_reject) {
            this.meeting_reject = MESSAGE_ALL.cloneThenSetDescription("会议邀请拒绝.");
        }
        return this.meeting_reject;
    }

    private Message getAttachmentCreate() {
        if (null == this.attachment_create) {
            this.attachment_create = MESSAGE_OUTER.cloneThenSetDescription("创建附件.");
        }
        return this.attachment_create;
    }

    private Message getAttachmentDelete() {
        if (null == this.attachment_delete) {
            this.attachment_delete = MESSAGE_OUTER.cloneThenSetDescription("删除附件.");
        }
        return this.attachment_delete;
    }

    private Message getAttachmentShare() {
        if (null == this.attachment_share) {
            this.attachment_share = MESSAGE_ALL.cloneThenSetDescription("附件分享.");
        }
        return this.attachment_share;
    }

    private Message getAttachmentShareCancel() {
        if (null == this.attachment_shareCancel) {
            this.attachment_shareCancel = MESSAGE_ALL.cloneThenSetDescription("附件取消分享.");
        }
        return this.attachment_shareCancel;
    }

    private Message getAttachmentEditor() {
        if (null == this.attachment_editor) {
            this.attachment_editor = MESSAGE_OUTER.cloneThenSetDescription("附件可编辑设置.");
        }
        return this.attachment_editor;
    }

    private Message getAttachmentEditorCancel() {
        if (null == this.attachment_editorCancel) {
            this.attachment_editorCancel = MESSAGE_OUTER.cloneThenSetDescription("附件可编辑取消.");
        }
        return this.attachment_editorCancel;
    }

    private Message getAttachmentEditorModify() {
        if (null == this.attachment_editorModify) {
            this.attachment_editorModify = MESSAGE_OUTER.cloneThenSetDescription("附件可编辑修改.");
        }
        return this.attachment_editorModify;
    }

    private Message getCalendarAlarm() {
        if (null == this.calendar_alarm) {
            this.calendar_alarm = MESSAGE_ALL.cloneThenSetDescription("日历通知.");
        }
        return this.calendar_alarm;
    }

    private Message getCustomCreate() {
        if (null == this.custom_create) {
            this.custom_create = MESSAGE_ALL.cloneThenSetDescription("自定义消息创建.");
        }
        return this.custom_create;
    }

    private Message getTeamworkTaskCreate() {
        if (null == this.teamwork_taskCreate) {
            this.teamwork_taskCreate = MESSAGE_ALL.cloneThenSetDescription("工作管理任务创建.");
        }
        return this.teamwork_taskCreate;
    }

    private Message getTeamworkTaskUpdate() {
        if (null == this.teamwork_taskUpdate) {
            this.teamwork_taskUpdate = MESSAGE_ALL.cloneThenSetDescription("工作管理任务更新.");
        }
        return this.teamwork_taskUpdate;
    }

    private Message getTeamworkTaskDelete() {
        if (null == this.teamwork_taskDelete) {
            this.teamwork_taskDelete = MESSAGE_ALL.cloneThenSetDescription("工作管理任务删除.");
        }
        return this.teamwork_taskDelete;
    }

    private Message getTeamworkTaskOvertime() {
        if (null == this.teamwork_taskOvertime) {
            this.teamwork_taskOvertime = MESSAGE_ALL.cloneThenSetDescription("工作管理任务超时.");
        }
        return this.teamwork_taskOvertime;
    }

    private Message getTeamworkChat() {
        if (null == this.teamwork_chat) {
            this.teamwork_chat = MESSAGE_NOTICE.cloneThenSetDescription("工作管理聊天.");
        }
        return this.teamwork_chat;
    }

    private Message getCmsPublish() {
        if (null == this.cms_publish) {
            this.cms_publish = MESSAGE_OUTER.cloneThenSetDescription("内容管理发布.");
        }
        return this.cms_publish;
    }

    private Message getCmsPublishToCreator() {
        if (null == this.cms_publish_to_creator) {
            this.cms_publish_to_creator = MESSAGE_NOTICE.cloneThenSetDescription("内容管理发布创建者通知.");
        }
        return this.cms_publish_to_creator;
    }

    private Message getBbsSubjectCreate() {
        if (null == this.bbs_subjectCreate) {
            this.bbs_subjectCreate = MESSAGE_ALL.cloneThenSetDescription("论坛创建贴子.");
        }
        return this.bbs_subjectCreate;
    }

    private Message getBbsReplyCreate() {
        if (null == this.bbs_replyCreate) {
            this.bbs_replyCreate = MESSAGE_ALL.cloneThenSetDescription("论坛创建回复.");
        }
        return this.bbs_replyCreate;
    }

    private Message getMindFileSend() {
        if (null == this.mind_fileSend) {
            this.mind_fileSend = MESSAGE_ALL.cloneThenSetDescription("脑图发送.");
        }
        return this.mind_fileSend;
    }

    private Message getMindFileShare() {
        if (null == this.mind_fileShare) {
            this.mind_fileShare = MESSAGE_ALL.cloneThenSetDescription("脑图分享.");
        }
        return this.mind_fileShare;
    }

    private Message getImCreate() {
        if (null == this.im_create) {
            this.im_create = MESSAGE_ALL.cloneThenSetDescription("聊聊消息.");
        }
        return this.im_create;
    }

    private Message getImRevoke() {
        if (null == this.im_revoke) {
            this.im_revoke = new Message(MessageConnector.CONSUME_WS).cloneThenSetDescription("聊聊消息.");
        }
        return this.im_revoke;
    }

    private Message getImConversationDelete() {
        if (null == this.im_conversation_delete) {
            this.im_conversation_delete = new Message(MessageConnector.CONSUME_WS).cloneThenSetDescription("聊聊消息.");
        }
        return this.im_conversation_delete;
    }

    private Message getImConversationUpdate() {
        if (null == this.im_conversation_update) {
            this.im_conversation_update = new Message(MessageConnector.CONSUME_WS).cloneThenSetDescription("聊聊消息.");
        }
        return this.im_conversation_update;
    }

    public Message getAttendanceCheckInAlert() {
        if (null == this.attendance_checkInAlert) {
            this.attendance_checkInAlert = MESSAGE_NOTICE.cloneThenSetDescription("打卡提醒消息.");
        }
        return this.attendance_checkInAlert;
    }

    private Message getAttendanceCheckInException() {
        if (null == this.attendance_checkInException) {
            this.attendance_checkInException = MESSAGE_NOTICE.cloneThenSetDescription("打卡异常提醒消息.");
        }
        return this.attendance_checkInException;
    }

    private Optional<Message> getMessage(String type) {
        switch (Objects.toString(type, "")) {
            case MessageConnector.TYPE_APPLICATION_CREATE:
                return Optional.of(getApplicationCreate());
            case MessageConnector.TYPE_APPLICATION_UPDATE:
                return Optional.of(getApplicationUpdate());
            case MessageConnector.TYPE_APPLICATION_DELETE:
                return Optional.of(getApplicationDelete());
            case MessageConnector.TYPE_PROCESS_CREATE:
                return Optional.of(getProcessCreate());
            case MessageConnector.TYPE_PROCESS_UPDATE:
                return Optional.of(getProcessUpdate());
            case MessageConnector.TYPE_PROCESS_DELETE:
                return Optional.of(getProcessDelete());
            case MessageConnector.TYPE_ACTIVITY_MESSAGE:
                return Optional.of(getActivityMessage());
            case MessageConnector.TYPE_WORK_TO_WORKCOMPLETED:
                return Optional.of(getWorkToWorkCompleted());
            case MessageConnector.TYPE_WORK_CREATE:
                return Optional.of(getWorkCreate());
            case MessageConnector.TYPE_WORK_DELETE:
                return Optional.of(getWorkDelete());
            case MessageConnector.TYPE_WORKCOMPLETED_CREATE:
                return Optional.of(getWorkCompletedCreate());
            case MessageConnector.TYPE_WORKCOMPLETED_DELETE:
                return Optional.of(getWorkCompletedDelete());
            case MessageConnector.TYPE_TASK_TO_TASKCOMPLETED:
                return Optional.of(getTaskToTaskCompleted());
            case MessageConnector.TYPE_TASK_CREATE:
                return Optional.of(getTaskCreate());
            case MessageConnector.TYPE_TASK_DELETE:
                return Optional.of(getTaskDelete());
            case MessageConnector.TYPE_TASK_URGE:
                return Optional.of(getTaskUrge());
            case MessageConnector.TYPE_TASK_EXPIRE:
                return Optional.of(getTaskExpire());
            case MessageConnector.TYPE_TASK_PRESS:
                return Optional.of(getTaskPress());
            case MessageConnector.TYPE_TASKCOMPLETED_CREATE:
                return Optional.of(getTaskCompletedCreate());
            case MessageConnector.TYPE_TASKCOMPLETED_DELETE:
                return Optional.of(getTaskCompletedDelete());
            case MessageConnector.TYPE_READ_TO_READCOMPLETED:
                return Optional.of(getReadToReadCompleted());
            case MessageConnector.TYPE_READ_CREATE:
                return Optional.of(getReadCreate());
            case MessageConnector.TYPE_READ_DELETE:
                return Optional.of(getReadDelete());
            case MessageConnector.TYPE_READCOMPLETED_CREATE:
                return Optional.of(getReadCompletedCreate());
            case MessageConnector.TYPE_READCOMPLETED_DELETE:
                return Optional.of(getReadCompletedDelete());
            case MessageConnector.TYPE_REVIEW_CREATE:
                return Optional.of(getReviewCreate());
            case MessageConnector.TYPE_REVIEW_DELETE:
                return Optional.of(getReviewDelete());
            case MessageConnector.TYPE_MEETING_INVITE:
                return Optional.of(getMeetingInvite());
            case MessageConnector.TYPE_MEETING_DELETE:
                return Optional.of(getMeetingDelete());
            case MessageConnector.TYPE_MEETING_ACCEPT:
                return Optional.of(getMeetingAccept());
            case MessageConnector.TYPE_MEETING_REJECT:
                return Optional.of(getMeetingReject());
            case MessageConnector.TYPE_ATTACHMENT_CREATE:
                return Optional.of(getAttachmentCreate());
            case MessageConnector.TYPE_ATTACHMENT_DELETE:
                return Optional.of(getAttachmentDelete());
            case MessageConnector.TYPE_ATTACHMENT_SHARE:
                return Optional.of(getAttachmentShare());
            case MessageConnector.TYPE_ATTACHMENT_SHARECANCEL:
                return Optional.of(getAttachmentShareCancel());
            case MessageConnector.TYPE_ATTACHMENT_EDITOR:
                return Optional.of(getAttachmentEditor());
            case MessageConnector.TYPE_ATTACHMENT_EDITORCANCEL:
                return Optional.of(getAttachmentEditorCancel());
            case MessageConnector.TYPE_ATTACHMENT_EDITORMODIFY:
                return Optional.of(getAttachmentEditorModify());
            case MessageConnector.TYPE_CALENDAR_ALARM:
                return Optional.of(getCalendarAlarm());
            case MessageConnector.TYPE_CUSTOM_CREATE:
                return Optional.of(getCustomCreate());
            case MessageConnector.TYPE_TEAMWORK_TASKCREATE:
                return Optional.of(getTeamworkTaskCreate());
            case MessageConnector.TYPE_TEAMWORK_TASKUPDATE:
                return Optional.of(getTeamworkTaskUpdate());
            case MessageConnector.TYPE_TEAMWORK_TASKDELETE:
                return Optional.of(getTeamworkTaskDelete());
            case MessageConnector.TYPE_TEAMWORK_TASKOVERTIME:
                return Optional.of(getTeamworkTaskOvertime());
            case MessageConnector.TYPE_TEAMWORK_CHAT:
                return Optional.of(getTeamworkChat());
            case MessageConnector.TYPE_CMS_PUBLISH:
                return Optional.of(getCmsPublish());
            case MessageConnector.TYPE_CMS_PUBLISH_TO_CREATOR:
                return Optional.of(getCmsPublishToCreator());
            case MessageConnector.TYPE_BBS_SUBJECTCREATE:
                return Optional.of(getBbsSubjectCreate());
            case MessageConnector.TYPE_BBS_REPLYCREATE:
                return Optional.of(getBbsReplyCreate());
            case MessageConnector.TYPE_MIND_FILESEND:
                return Optional.of(getMindFileSend());
            case MessageConnector.TYPE_MIND_FILESHARE:
                return Optional.of(getMindFileShare());
            case MessageConnector.TYPE_IM_CREATE:
                return Optional.of(getImCreate());
            case MessageConnector.TYPE_IM_REVOKE:
                return Optional.of(getImRevoke());
            case MessageConnector.TYPE_IM_CONVERSATION_DELETE:
                return Optional.of(getImConversationDelete());
            case MessageConnector.TYPE_IM_CONVERSATION_UPDATE:
                return Optional.of(getImConversationUpdate());
            case MessageConnector.TYPE_ATTENDANCE_CHECK_IN_ALERT:
                return Optional.of(getAttendanceCheckInAlert());
            case MessageConnector.TYPE_ATTENDANCE_CHECK_IN_EXCEPTION:
                return Optional.of(getAttendanceCheckInException());
            default:
                if ((null != this.custom) && type.startsWith(MessageConnector.TYPE_CUSTOM_PREFIX)) {
                    Message m = this.custom.get(StringUtils.substringAfter(type, MessageConnector.TYPE_CUSTOM_PREFIX));
                    if (null != m) {
                        return Optional.of(m);
                    }
                }
                return Optional.empty();
        }
    }

    private void jsonToConsumer(Gson gson, JsonElement jsonElement, List<Consumer> list) {
        if ((null != jsonElement) && jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonElement typeElement = jsonObject.get(Message.Consumer.FIELD_TYPE);
            if (null != typeElement && typeElement.isJsonPrimitive()) {
                switch (StringUtils.lowerCase(typeElement.getAsString())) {
                    case MessageConnector.CONSUME_WS:
                        list.add(gson.fromJson(jsonElement, WsConsumer.class));
                        break;
                    case MessageConnector.CONSUME_PMS_INNER:
                        list.add(gson.fromJson(jsonElement, PmsinnerConsumer.class));
                        break;
                    case MessageConnector.CONSUME_CALENDAR:
                        list.add(gson.fromJson(jsonElement, CalendarConsumer.class));
                        break;
                    case MessageConnector.CONSUME_DINGDING:
                        list.add(gson.fromJson(jsonElement, DingdingConsumer.class));
                        break;
                    case MessageConnector.CONSUME_WELINK:
                        list.add(gson.fromJson(jsonElement, WelinkConsumer.class));
                        break;
                    case MessageConnector.CONSUME_ZHENGWUDINGDING:
                        list.add(gson.fromJson(jsonElement, ZhengwudingdingConsumer.class));
                        break;
                    case MessageConnector.CONSUME_QIYEWEIXIN:
                        list.add(gson.fromJson(jsonElement, QiyeweixinConsumer.class));
                        break;
                    case MessageConnector.CONSUME_MPWEIXIN:
                        list.add(gson.fromJson(jsonElement, MpweixinConsumer.class));
                        break;
                    case MessageConnector.CONSUME_KAFKA:
                        list.add(gson.fromJson(jsonElement, KafkaConsumer.class));
                        break;
                    case MessageConnector.CONSUME_ACTIVEMQ:
                        list.add(gson.fromJson(jsonElement, ActivemqConsumer.class));
                        break;
                    case MessageConnector.CONSUME_RESTFUL:
                        list.add(gson.fromJson(jsonElement, RestfulConsumer.class));
                        break;
                    case MessageConnector.CONSUME_MAIL:
                        list.add(gson.fromJson(jsonElement, MailConsumer.class));
                        break;
                    case MessageConnector.CONSUME_API:
                        list.add(gson.fromJson(jsonElement, ApiConsumer.class));
                        break;
                    case MessageConnector.CONSUME_JDBC:
                        list.add(gson.fromJson(jsonElement, JdbcConsumer.class));
                        break;
                    case MessageConnector.CONSUME_TABLE:
                        list.add(gson.fromJson(jsonElement, TableConsumer.class));
                        break;
                    case MessageConnector.CONSUME_HADOOP:
                        list.add(gson.fromJson(jsonElement, HadoopConsumer.class));
                        break;
                    default:
                        list.add(gson.fromJson(jsonElement, Consumer.class));
                        break;
                }
            }
        }
    }

    @FieldDescribe("清理设置.")
    private Clean clean;

    public Clean clean() {
        return this.clean == null ? new Clean() : this.clean;
    }

    public static class Clean extends ConfigObject {

        private static final long serialVersionUID = -8460670679002519408L;

        public static Clean defaultInstance() {
            return new Clean();
        }

        public static final Boolean DEFAULT_ENABLE = true;

        public static final Integer DEFAULT_KEEP = 7;

        public static final String DEFAULT_CRON = "30 30 6 * * ?";

        @FieldDescribe("是否启用")
        private Boolean enable = DEFAULT_ENABLE;

        @FieldDescribe("定时cron表达式")
        private String cron = DEFAULT_CRON;

        @FieldDescribe("消息保留天数")
        private Integer keep = DEFAULT_KEEP;

        public Integer getKeep() {
            if ((null == this.keep) || (this.keep < 1)) {
                return DEFAULT_KEEP;
            } else {
                return this.keep;
            }
        }

        public String getCron() {
            if (StringUtils.isNotEmpty(this.cron) && CronExpression.isValidExpression(this.cron)) {
                return this.cron;
            } else {
                return DEFAULT_CRON;
            }
        }

        public Boolean getEnable() {
            return BooleanUtils.isTrue(this.enable);
        }

        public void setCron(String cron) {
            this.cron = cron;
        }

        public void setEnable(Boolean enable) {
            this.enable = enable;
        }
    }

    public static class Store extends ConfigObject {

        private static final long serialVersionUID = -476407490360557845L;

        public static Clean defaultInstance() {
            return new Clean();
        }

        public static final Boolean DEFAULT_ENABLE = true;

        public static final Integer DEFAULT_KEEP = 7;

        public static final String DEFAULT_CRON = "30 30 6 * * ?";

        @FieldDescribe("是否启用")
        private Boolean enable = DEFAULT_ENABLE;

        @FieldDescribe("定时cron表达式")
        private String cron = DEFAULT_CRON;

        @FieldDescribe("消息保留天数")
        private Integer keep = DEFAULT_KEEP;

        public Integer getKeep() {
            if ((null == this.keep) || (this.keep < 1)) {
                return DEFAULT_KEEP;
            } else {
                return this.keep;
            }
        }

        public String getCron() {
            if (StringUtils.isNotEmpty(this.cron) && CronExpression.isValidExpression(this.cron)) {
                return this.cron;
            } else {
                return DEFAULT_CRON;
            }
        }

        public Boolean getEnable() {
            return BooleanUtils.isTrue(this.enable);
        }

        public void setCron(String cron) {
            this.cron = cron;
        }

        public void setEnable(Boolean enable) {
            this.enable = enable;
        }
    }

}
