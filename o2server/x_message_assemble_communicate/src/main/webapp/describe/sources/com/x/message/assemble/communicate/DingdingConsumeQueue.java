package com.x.message.assemble.communicate;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.DingdingMessage;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.message.core.entity.Message;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class DingdingConsumeQueue extends AbstractQueue<Message> {

	private static Logger logger = LoggerFactory.getLogger(DingdingConsumeQueue.class);

	protected void execute(Message message) throws Exception {

		if (Config.dingding().getEnable() && Config.dingding().getMessageEnable()) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				DingdingMessage m = new DingdingMessage();
				m.setAgent_id(Long.parseLong(Config.dingding().getAgentId(), 10));
				m.setUserid_list(business.organization().person().getObject(message.getPerson()).getDingdingId());

				if (needTransferLink(message.getType())) {
					String workUrl = getDingdingOpenWorkUrl(message.getBody());
					if (workUrl != null && !"".equals(workUrl)) {
						m.getMsg().setMsgtype("markdown");
						m.getMsg().getMarkdown().setTitle(message.getTitle());
						m.getMsg().getMarkdown().setText("["+message.getTitle()+"]("+workUrl+")");
					}else {
						m.getMsg().getText().setContent(message.getTitle());
					}
				}else {
					m.getMsg().getText().setContent(message.getTitle());
				}

				// https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2?access_token=ACCESS_TOKEN
				String address = Config.dingding().getOapiAddress()
						+ "/topapi/message/corpconversation/asyncsend_v2?access_token="
						+ Config.dingding().corpAccessToken();
				DingdingMessageResp resp = HttpConnection.postAsObject(address, null, m.toString(),
						DingdingMessageResp.class);
				if (resp.getErrcode() != 0) {
					ExceptionDingdingMessage e = new ExceptionDingdingMessage(resp.getErrcode(), resp.getErrmsg());
					logger.error(e);
				} else {
					Message messageEntityObject = emc.find(message.getId(), Message.class);
					if (null != messageEntityObject) {
						emc.beginTransaction(Message.class);
						message.setConsumed(true);
						emc.commit();
					}
				}
			}
		}
	}


	/**
	 * 生成单点登录和打开工作的地址
	 * @param messageBody
	 * @return
	 */
	private String getDingdingOpenWorkUrl(String messageBody) {
		try {
			String work = getWorkIdFromBody(messageBody);
			String o2oaUrl = Config.dingding().getWorkUrl();
			if (work == null || "".equals(work) || o2oaUrl == null || "".equals(o2oaUrl)) {
				return null;
			}

			String workUrl = "workmobilewithaction.html?workid=" + work;
			String messageRedirectPortal = Config.dingding().getMessageRedirectPortal();
			if (messageRedirectPortal != null && !"".equals(messageRedirectPortal)) {
				String portal = "portalmobile.html?id="+messageRedirectPortal;
				portal = URLEncoder.encode(portal, DefaultCharset.name);
				workUrl += "&redirectlink=" + portal;
			}
			workUrl = URLEncoder.encode(workUrl, DefaultCharset.name);
			logger.info("o2oa workUrl："+workUrl);
			o2oaUrl = o2oaUrl + "ddsso.html?redirect=" + workUrl;
			logger.info("o2oa 地址："+o2oaUrl);
			return o2oaUrl;
		}catch (Exception e) {
			logger.error(e);
		}

		return "";
	}

	/**
	 * 获取workid
	 * @param messageBody
	 * @return
	 */
	private String getWorkIdFromBody(String messageBody) {
		try {
			JsonObject object =new JsonParser().parse(messageBody).getAsJsonObject();
			return object.get("work").getAsString();
		} catch (Exception e) {
			logger.error(e);
		}
		return "";
	}

	/**
	 * 是否需要把钉钉消息转成markdown格式消息
	 * 根据是否配置了钉钉工作链接、是否是工作消息（目前只支持工作消息）
	 * @param messageType 消息类型 判断是否是工作消息
	 * @return
	 */
	private boolean needTransferLink(String messageType) {
		try {
			String workUrl = Config.dingding().getWorkUrl();
			if (workUrl != null && !"".equals(workUrl) && workMessageTypeList().contains(messageType)) {
				return true;
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return false;
	}

	private List<String> workMessageTypeList() {
		List<String> list = new ArrayList<>();
		list.add(MessageConnector.TYPE_WORK_TO_WORKCOMPLETED);
		list.add(MessageConnector.TYPE_WORK_CREATE);
		list.add(MessageConnector.TYPE_WORK_DELETE);
		list.add(MessageConnector.TYPE_WORKCOMPLETED_CREATE);
		list.add(MessageConnector.TYPE_WORKCOMPLETED_DELETE);
		list.add(MessageConnector.TYPE_TASK_TO_TASKCOMPLETED);
		list.add(MessageConnector.TYPE_TASK_CREATE);
		list.add(MessageConnector.TYPE_TASK_DELETE);
		list.add(MessageConnector.TYPE_TASK_URGE);
		list.add(MessageConnector.TYPE_TASK_EXPIRE);
		list.add(MessageConnector.TYPE_TASK_PRESS);
		list.add(MessageConnector.TYPE_TASKCOMPLETED_CREATE);
		list.add(MessageConnector.TYPE_TASKCOMPLETED_DELETE);
		list.add(MessageConnector.TYPE_READ_TO_READCOMPLETED);
		list.add(MessageConnector.TYPE_READ_CREATE);
		list.add(MessageConnector.TYPE_READ_DELETE);
		list.add(MessageConnector.TYPE_READCOMPLETED_CREATE);
		list.add(MessageConnector.TYPE_READCOMPLETED_DELETE);
		list.add(MessageConnector.TYPE_REVIEW_CREATE);
		list.add(MessageConnector.TYPE_REVIEW_DELETE);

		return list;
	}

	public static class DingdingMessageResp {

		private Integer errcode;
		private String errmsg;
		private Long task_id;

		public String getErrmsg() {
			return errmsg;
		}

		public void setErrmsg(String errmsg) {
			this.errmsg = errmsg;
		}

		public Long getTask_id() {
			return task_id;
		}

		public void setTask_id(Long task_id) {
			this.task_id = task_id;
		}

		public Integer getErrcode() {
			return errcode;
		}

		public void setErrcode(Integer errcode) {
			this.errcode = errcode;
		}

	}
}
