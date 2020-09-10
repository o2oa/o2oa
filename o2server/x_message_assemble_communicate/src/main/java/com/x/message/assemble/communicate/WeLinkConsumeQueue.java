package com.x.message.assemble.communicate;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.WeLink;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.message.WeLinkMessage;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.message.core.entity.Message;
import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WeLinkConsumeQueue extends AbstractQueue<Message> {

	private static Logger logger = LoggerFactory.getLogger(WeLinkConsumeQueue.class);

	protected void execute(Message message) throws Exception {

		if (Config.weLink().getEnable() && Config.weLink().getMessageEnable()) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				WeLinkMessage m = new WeLinkMessage();
				List<String> list = new ArrayList<>();
				logger.info("person :" + message.getPerson());
				String unique = business.organization().person().getObject(message.getPerson()).getWeLinkId();
				if (StringUtils.isEmpty(unique)) {
					logger.error(new ExceptionWeLinkMessage("-1", "没有找到对应welink用户id"));
					return;
				}
				list.add(unique);
				m.setToUserList(list);
				m.setMsgRange("0");
				//处理消息标题和内容
				if (message.getTitle().contains(":")) {
					int i = message.getTitle().indexOf(":");
					m.setMsgTitle(message.getTitle().substring(0, i));
					m.setMsgContent(message.getTitle().substring(i+1));
				}else {
					m.setMsgTitle(message.getTitle());
					m.setMsgContent(message.getTitle());
				}
				m.setMsgOwner(appOwnerByType(message.getType()));

				Date now = new Date();
				m.setCreateTime(now.getTime()+"");
				//是否添加超链接
				if (needTransferLink(message.getType())) {
					String workUrl = getDingdingOpenWorkUrl(message.getBody());
					if (workUrl != null && !"".equals(workUrl)) {
						m.setUrlType("html");
						m.setUrlPath(workUrl);
					}
				}
				logger.info("welink send body: " + m.toString());

				String address = Config.weLink().getOapiAddress() + "/messages/v3/send";
				logger.info("welink send url: " + address);
				List<NameValuePair> heads = new ArrayList<>();
				heads.add(new NameValuePair(WeLink.WeLink_Auth_Head_Key, Config.weLink().accessToken()));
				WeLinkMessageResp resp = HttpConnection.postAsObject(address, heads, m.toString(), WeLinkMessageResp.class);
				if ("0".equals(resp.getCode())) {
					ExceptionWeLinkMessage e = new ExceptionWeLinkMessage(resp.getCode(), resp.getMessage());
					logger.error(e);
				} else {
					Message messageEntityObject = emc.find(message.getId(), Message.class);
					if (null != messageEntityObject) {
						emc.beginTransaction(Message.class);
						messageEntityObject.setConsumed(true);
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
			String o2oaUrl = Config.weLink().getWorkUrl();
			if (work == null || "".equals(work) || o2oaUrl == null || "".equals(o2oaUrl)) {
				return null;
			}

			String workUrl = "workmobilewithaction.html?workid=" + work;
			String messageRedirectPortal = Config.weLink().getMessageRedirectPortal();
			if (messageRedirectPortal != null && !"".equals(messageRedirectPortal)) {
				String portal = "portalmobile.html?id="+messageRedirectPortal;
				portal = URLEncoder.encode(portal, DefaultCharset.name);
				workUrl += "&redirectlink=" + portal;
			}
			workUrl = URLEncoder.encode(workUrl, DefaultCharset.name);
			logger.info("o2oa workUrl："+workUrl);
			o2oaUrl = o2oaUrl + "welinksso.html?redirect=" + workUrl;
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
			String workUrl = Config.weLink().getWorkUrl();
			if (workUrl != null && !"".equals(workUrl) && workMessageTypeList().contains(messageType)) {
				return true;
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return false;
	}

	private String appOwnerByType(String messageType) {
		if (workMessageTypeList().contains(messageType)) {
			return "工作消息";
		}else {
			if (messageType.startsWith("meeting_")) {
				return "会议消息";
			}else if (messageType.startsWith("attachment_")) {
				return "文件消息";
			}else if (messageType.startsWith("calendar_")) {
				return "日历消息";
			}else if (messageType.startsWith("cms_")) {
				return "信息中心消息";
			}else if (messageType.startsWith("bbs_")) {
				return "论坛消息";
			}else {
				return "消息";
			}
		}
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

	public static class WeLinkMessageResp {

		private String code;
		private String message;
		private List<String> failedUserId;

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public List<String> getFailedUserId() {
			return failedUserId;
		}

		public void setFailedUserId(List<String> failedUserId) {
			this.failedUserId = failedUserId;
		}
	}
}
