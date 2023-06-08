package com.x.base.core.project.message;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.project.Context;
import com.x.base.core.project.x_message_assemble_communicate;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class MessageConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageConnector.class);

    private MessageConnector() {
        // nothing
    }

    private static Gson gson = XGsonBuilder.instance();

    // 创建应用
    public static final String TYPE_APPLICATION_CREATE = "application_create";

    // 更新应用
    public static final String TYPE_APPLICATION_UPDATE = "application_update";

    // 删除应用
    public static final String TYPE_APPLICATION_DELETE = "application_delete";

    // 创建流程
    public static final String TYPE_PROCESS_CREATE = "process_create";

    // 更新流程
    public static final String TYPE_PROCESS_UPDATE = "process_update";

    // 删除流程
    public static final String TYPE_PROCESS_DELETE = "process_delete";

    // 有新的工作通过消息节点
    public static final String TYPE_ACTIVITY_MESSAGE = "activity_message";

    // 工作完成转已完成工作
    public static final String TYPE_WORK_TO_WORKCOMPLETED = "work_to_workCompleted";

    // 创建工作
    public static final String TYPE_WORK_CREATE = "work_create";

    // 删除工作
    public static final String TYPE_WORK_DELETE = "work_delete";

    // 创建已完成工作
    public static final String TYPE_WORKCOMPLETED_CREATE = "workCompleted_create";

    // 删除已完成工作
    public static final String TYPE_WORKCOMPLETED_DELETE = "workCompleted_delete";

    // 待办完成转已办
    public static final String TYPE_TASK_TO_TASKCOMPLETED = "task_to_taskCompleted";

    // 创建待办
    public static final String TYPE_TASK_CREATE = "task_create";

    // 删除待办
    public static final String TYPE_TASK_DELETE = "task_delete";

    // 待办即将过期催办
    public static final String TYPE_TASK_URGE = "task_urge";

    // 待办过期
    public static final String TYPE_TASK_EXPIRE = "task_expire";

    // 待办提醒
    public static final String TYPE_TASK_PRESS = "task_press";

    // 已办创建
    public static final String TYPE_TASKCOMPLETED_CREATE = "taskCompleted_create";

    // 删除已办
    public static final String TYPE_TASKCOMPLETED_DELETE = "taskCompleted_delete";

    // 待阅转已阅
    public static final String TYPE_READ_TO_READCOMPLETED = "read_to_readCompleted";

    // 待阅创建
    public static final String TYPE_READ_CREATE = "read_create";

    // 待阅删除
    public static final String TYPE_READ_DELETE = "read_delete";

    // 已阅创建
    public static final String TYPE_READCOMPLETED_CREATE = "readCompleted_create";

    // 已阅删除
    public static final String TYPE_READCOMPLETED_DELETE = "readCompleted_delete";

    // 创建参阅
    public static final String TYPE_REVIEW_CREATE = "review_create";

    // 删除参阅
    public static final String TYPE_REVIEW_DELETE = "review_delete";

    // 会议邀请
    public static final String TYPE_MEETING_INVITE = "meeting_invite";

    // 会议删除
    public static final String TYPE_MEETING_DELETE = "meeting_delete";

    // 会议邀请接受
    public static final String TYPE_MEETING_ACCEPT = "meeting_accept";

    // 会议邀请拒绝
    public static final String TYPE_MEETING_REJECT = "meeting_reject";

    // 创建附件
    public static final String TYPE_ATTACHMENT_CREATE = "attachment_create";

    // 删除附件
    public static final String TYPE_ATTACHMENT_DELETE = "attachment_delete";

    // 附件分享
    public static final String TYPE_ATTACHMENT_SHARE = "attachment_share";

    // 附件取消分享
    public static final String TYPE_ATTACHMENT_SHARECANCEL = "attachment_shareCancel";

    // 附件可编辑设置
    public static final String TYPE_ATTACHMENT_EDITOR = "attachment_editor";

    // 附件可编辑取消
    public static final String TYPE_ATTACHMENT_EDITORCANCEL = "attachment_editorCancel";

    // 附件可编辑修改
    public static final String TYPE_ATTACHMENT_EDITORMODIFY = "attachment_editorModify";

    // 日历通知
    public static final String TYPE_CALENDAR_ALARM = "calendar_alarm";

    // 自定义消息创建
    public static final String TYPE_CUSTOM_CREATE = "custom_create";

    // 工作管理任务创建
    public static final String TYPE_TEAMWORK_TASKCREATE = "teamwork_taskCreate";

    // 工作管理任务更新
    public static final String TYPE_TEAMWORK_TASKUPDATE = "teamwork_taskUpdate";

    // 工作管理任务删除
    public static final String TYPE_TEAMWORK_TASKDELETE = "teamwork_taskDelete";

    // 工作管理任务超时
    public static final String TYPE_TEAMWORK_TASKOVERTIME = "teamwork_taskOvertime";

    // 工作管理聊天
    public static final String TYPE_TEAMWORK_CHAT = "teamwork_chat";

    // 内容管理发布
    public static final String TYPE_CMS_PUBLISH = "cms_publish";

    // 内容管理发布创建者通知
    public static final String TYPE_CMS_PUBLISH_TO_CREATOR = "cms_publish_to_creator";

    // 论坛创建贴子
    public static final String TYPE_BBS_SUBJECTCREATE = "bbs_subjectCreate";

    // 论坛创建回复
    public static final String TYPE_BBS_REPLYCREATE = "bbs_replyCreate";

    // 脑图文件发送
    public static final String TYPE_MIND_FILESEND = "mind_fileSend";

    // 脑图文件分享
    public static final String TYPE_MIND_FILESHARE = "mind_fileShare";

    // IM 聊天消息发送
    public static final String TYPE_IM_CREATE = "im_create";
    // IM 消息撤回
    public static final String TYPE_IM_REVOKE = "im_revoke";
    // IM 会话删除
    public static final String TYPE_IM_CONVERSATION_DELETE = "im_conv_delete";
    // IM 会话本身更新
    public static final String TYPE_IM_CONVERSATION_UPDATE = "im_conv_update";

    // 考勤 打卡提醒消息
    public static final String TYPE_ATTENDANCE_CHECK_IN_ALERT = "attendance_checkInAlert";
    // 考勤 打卡异常提醒消息
    public static final String TYPE_ATTENDANCE_CHECK_IN_EXCEPTION = "attendance_checkInException";

    // 自定义消息
    public static final String TYPE_CUSTOM_PREFIX = "custom_";

    // 审计日志通知
    // public static final String TYPE_AUDIT_LOG = "audit_log";

    public static final String CONSUME_WS = "ws";

    public static final String CONSUME_PMS_INNER = "pmsinner";

    public static final String CONSUME_CALENDAR = "calendar";

    public static final String CONSUME_DINGDING = "dingding";

    public static final String CONSUME_WELINK = "welink";

    public static final String CONSUME_ANDFX = "andfx";

    public static final String CONSUME_ZHENGWUDINGDING = "zhengwudingding";

    public static final String CONSUME_QIYEWEIXIN = "qiyeweixin";

    public static final String CONSUME_MPWEIXIN = "mpweixin"; // 微信公众号

    public static final String CONSUME_KAFKA = "kafka";

    public static final String CONSUME_ACTIVEMQ = "activemq";
    // restful类型
    public static final String CONSUME_RESTFUL = "restful";
    // 邮件类型
    public static final String CONSUME_MAIL = "mail";
    // 系统内部借口调用
    public static final String CONSUME_API = "api";
    // jdbc写入
    public static final String CONSUME_JDBC = "jdbc";
    // 自建表
    public static final String CONSUME_TABLE = "table";
    // hadoop dfs
    public static final String CONSUME_HADOOP = "hadoop";
    // 自定消费者类型前缀
    public static final String CONSUME_CUSTOM_PREFIX = "custom_";

    public static final Set<String> TYPES = Collections.unmodifiableSet(Sets.newHashSet(TYPE_APPLICATION_CREATE,
            TYPE_APPLICATION_UPDATE, TYPE_APPLICATION_DELETE, TYPE_PROCESS_CREATE, TYPE_PROCESS_UPDATE,
            TYPE_PROCESS_DELETE, TYPE_ACTIVITY_MESSAGE, TYPE_WORK_TO_WORKCOMPLETED, TYPE_WORK_CREATE, TYPE_WORK_DELETE,
            TYPE_WORKCOMPLETED_CREATE, TYPE_WORKCOMPLETED_DELETE, TYPE_TASK_TO_TASKCOMPLETED, TYPE_TASK_CREATE,
            TYPE_TASK_DELETE, TYPE_TASK_URGE, TYPE_TASK_EXPIRE, TYPE_TASK_PRESS, TYPE_TASKCOMPLETED_CREATE,
            TYPE_TASKCOMPLETED_DELETE, TYPE_READ_TO_READCOMPLETED, TYPE_READ_CREATE, TYPE_READ_DELETE,
            TYPE_READCOMPLETED_CREATE, TYPE_READCOMPLETED_DELETE, TYPE_REVIEW_CREATE, TYPE_REVIEW_DELETE,
            TYPE_MEETING_INVITE, TYPE_MEETING_DELETE, TYPE_MEETING_ACCEPT, TYPE_MEETING_REJECT, TYPE_ATTACHMENT_CREATE,
            TYPE_ATTACHMENT_DELETE, TYPE_ATTACHMENT_SHARE, TYPE_ATTACHMENT_SHARECANCEL, TYPE_ATTACHMENT_EDITOR,
            TYPE_ATTACHMENT_EDITORCANCEL, TYPE_ATTACHMENT_EDITORMODIFY, TYPE_CALENDAR_ALARM, TYPE_CUSTOM_CREATE,
            TYPE_TEAMWORK_TASKCREATE, TYPE_TEAMWORK_TASKUPDATE, TYPE_TEAMWORK_TASKDELETE, TYPE_TEAMWORK_TASKOVERTIME,
            TYPE_TEAMWORK_CHAT, TYPE_CMS_PUBLISH, TYPE_CMS_PUBLISH_TO_CREATOR, TYPE_BBS_SUBJECTCREATE,
            TYPE_BBS_REPLYCREATE, TYPE_MIND_FILESEND, TYPE_MIND_FILESHARE, TYPE_IM_CREATE, TYPE_IM_REVOKE, TYPE_IM_CONVERSATION_DELETE, TYPE_IM_CONVERSATION_UPDATE,
            TYPE_ATTENDANCE_CHECK_IN_ALERT, TYPE_ATTENDANCE_CHECK_IN_EXCEPTION));

    private static Context context;

    private static LinkedBlockingQueue<Wrap> connectQueue = new LinkedBlockingQueue<>(10000);

    public static void start(Context context) {
        MessageConnector.context = context;
        ConnectorThread thread = new ConnectorThread();
        thread.setName("MessageConnector-" + context.clazz().getSimpleName());
        thread.start();
    }

    public static void stop() {
        try {
            connectQueue.put(new StopSignal());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    public static void send(String type, String title, String person, Object body) {
        Wrap wrap = new Wrap();
        wrap.setType(type);
        wrap.setTitle(title);
        wrap.setPerson(person);
        wrap.setBody(gson.toJsonTree(body));
        try {
            connectQueue.put(wrap);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            LOGGER.error(ie);
        }
    }

    private static class ConnectorThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Wrap o = connectQueue.take();
                    if (o instanceof StopSignal) {
                        break;
                    } else {
                        context.applications().postQuery(x_message_assemble_communicate.class, "connector", o);
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    LOGGER.error(ie);
                } catch (Exception e) {
                    LOGGER.error(e);
                }
            }
        }
    }

    public static class Wrap extends GsonPropertyObject {

        private static final long serialVersionUID = 2603938363315602487L;

        @FieldDescribe("类型")
        private String type;

        @FieldDescribe("人员")
        private String person;

        @FieldDescribe("标题")
        private String title;

        @FieldDescribe("推送内容")
        private JsonElement body;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }

        public JsonElement getBody() {
            return body;
        }

        public void setBody(JsonElement body) {
            this.body = body;
        }

    }

    private static class StopSignal extends Wrap {

        private static final long serialVersionUID = -5631247237688117035L;

    }

}
