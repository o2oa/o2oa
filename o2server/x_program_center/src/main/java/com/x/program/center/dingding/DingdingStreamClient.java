package com.x.program.center.dingding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.dingtalk.open.app.api.GenericEventListener;
import com.dingtalk.open.app.api.OpenDingTalkStreamClientBuilder;
import com.dingtalk.open.app.api.message.GenericOpenDingTalkEvent;
import com.dingtalk.open.app.api.security.AuthClientCredential;
import com.dingtalk.open.app.stream.protocol.event.EventAckStatus;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.ThisApplication;

/**
 * 钉钉的  stream 事件订阅
 */
public class DingdingStreamClient {

  private static Logger logger = LoggerFactory.getLogger(DingdingStreamClient.class);

  private List<String> tags = new ArrayList<>(Arrays.asList("user_add_org", "user_modify_org", "user_leave_org",
      "user_active_org", "org_dept_create", "org_dept_modify", "org_dept_remove"));

  public void startClient() throws Exception {

    OpenDingTalkStreamClientBuilder
        .custom()
        .credential(new AuthClientCredential(Config.dingding().getAppKey(), Config.dingding().getAppSecret()))
        // 注册事件监听
        .registerAllEventListener(new GenericEventListener() {
          public EventAckStatus onEvent(GenericOpenDingTalkEvent event) {
            try {
              // 事件唯一Id
              String eventId = event.getEventId();
              // 事件类型
              String eventType = event.getEventType();
              // 事件产生时间
              Long bornTime = event.getEventBornTime();
              logger.info("来自钉钉的事件，eventId:{} eventType: {} eventTime: {}", eventId, eventType, "" + bornTime);
              // 获取事件体
              JSONObject bizData = event.getData();
              if (logger.isDebugEnabled() && bizData != null) {
                logger.debug("event 对象: {} ", bizData.toJSONString());
              }
              // 处理事件
              if (tags.contains(eventType)) {
                logger.info("通讯录变更。。。。添加定时任务的队列消息。event : {}", eventType);
                ThisApplication.dingdingSyncOrganizationCallbackRequest.add(new Object());
              }
              // 消费成功
              return EventAckStatus.SUCCESS;
            } catch (Exception e) {
              logger.error(e);
              // 消费失败
              return EventAckStatus.LATER;
            }
          }
        }).build().start();
        logger.info("启动钉钉事件回调监听成功！");
  }
}
