package com.x.message.assemble.communicate.jaxrs.im;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.message.assemble.communicate.Business;
import com.x.message.assemble.communicate.ThisApplication;
import com.x.message.core.entity.IMMsg;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class ActionDeleteMessageByConversation extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            ActionDeleteMessageByConversation.class);


    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement)
            throws Exception {
        if (!effectivePerson.isManager()) {
            throw new ExceptionConversationCheckError("没有权限，需要管理员操作");
        }
        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        if (StringUtils.isEmpty(wi.getConversationId()) && StringUtils.isEmpty(wi.getBeforeDay())) {
            throw new ExceptionConversationCheckError("至少需要一个参数");
        }
        if (StringUtils.isNotEmpty(wi.getBeforeDay())) {
            var date = DateTools.parseDate(wi.getBeforeDay());
            if (date == null) {
                throw new ExceptionConversationCheckError("日期不正确！");
            }
            if (date.after(new Date())) {
                throw new ExceptionConversationCheckError("日期不正确！");
            }
        }
        LOGGER.info("消息删除开始执行 执行人：{} ，删除条件：{}", effectivePerson.getDistinguishedName(), wi.toString());
        ActionResult<Wo> result = new ActionResult<>();
        Wo wo = new Wo();
        startThread(wi);
        wo.setMessage("删除任务已经开始执行，因数据大小不同所需时间不确定，可观察日志信息！");
        wo.setValue(true);
        result.setData(wo);
        return result;


    }

    private void startThread(Wi wi) {
        Thread thread = new Thread(() -> {
            try {
                deleteMessages(wi);
            } catch (Exception e) {
                LOGGER.info("删除消息任务失败，{}", wi.toString());
                LOGGER.error(e);
            }
        });
        thread.start();
    }

    private void deleteMessages(Wi wi) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            Date beforeDate = null;
            if (StringUtils.isNotEmpty(wi.getBeforeDay())) {
                beforeDate = DateTools.parseDate(wi.getBeforeDay());
            }
            int count = 0;
            boolean hasMore = true;
            while (hasMore) {
                // 因为正在删除数据，所以一直只查询第一页
                List<IMMsg> msgList = business.imConversationFactory()
                        .listMsgWithConversation(1, 20, wi.getConversationId(), beforeDate);
                if (null != msgList && !msgList.isEmpty()) {
                    for (IMMsg imMsg : msgList) {
                        ThisApplication.imMessageDeleteQueue.send(imMsg);
                        count++;
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    LOGGER.error(e);
                }
                if (msgList == null || msgList.size() < 20) {
                    hasMore = false;
                }
            }
            LOGGER.info("  删除消息数量 " + count + " ！！！！！！！！！");
        }
    }



    public static class Wi extends GsonPropertyObject {

        @FieldDescribe("会话id")
        private String conversationId;

        @FieldDescribe("截止日期(yyyy-MM-dd), 任务会删除这个截止日期以前的所有消息数据 .")
        private String beforeDay;

        public String getBeforeDay() {
            return beforeDay;
        }

        public void setBeforeDay(String beforeDay) {
            this.beforeDay = beforeDay;
        }

        public String getConversationId() {
            return conversationId;
        }

        public void setConversationId(String conversationId) {
            this.conversationId = conversationId;
        }
    }

    public static class Wo extends GsonPropertyObject {

        private Boolean value;

        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Boolean getValue() {
            return value;
        }

        public void setValue(Boolean value) {
            this.value = value;
        }
    }

}