package com.x.message.assemble.communicate;

import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.message.assemble.communicate.message.DingdingMessage;
import com.x.message.core.entity.Message;

public class DingdingConsumeQueue extends AbstractQueue<Message> {

	private static final Logger LOGGER = LoggerFactory.getLogger(DingdingConsumeQueue.class);

	private static final Gson gson = XGsonBuilder.instance();

	protected void execute(Message message) throws Exception {

		if (Config.dingding().getEnable() && Config.dingding().getMessageEnable()) {
			DingdingMessage m = new DingdingMessage();
			Person person = this.getPerson(message.getPerson());
			m.setAgent_id(Long.parseLong(Config.dingding().getAgentId(), 10));
			m.setUserid_list(person.getDingdingId());
			if (StringUtils.isEmpty(m.getUserid_list())) {
				LOGGER.warn("没有接收钉钉消息的人员:{}.", message.getPerson());
				return;
			}
			String openUrl = getOpenUrl(message);
			if (needTransferLink(message.getType()) && StringUtils.isNotEmpty(openUrl)) {
					// dingtalk://dingtalkclient/action/openapp?corpid=免登企业corpId&container_type=work_platform&app_id=0_{应用agentid}&redirect_type=jump&redirect_url=跳转url
				String dingtalkUrl = "dingtalk://dingtalkclient/action/openapp?corpid="
						+ Config.dingding().getCorpId() + "&container_type=work_platform&app_id=0_"
						+ Config.dingding().getAgentId() + "&redirect_type=jump&redirect_url="
						+ URLEncoder.encode(openUrl, DefaultCharset.name);
				LOGGER.info("钉钉pc 打开消息 url：{}",  dingtalkUrl);
				// 内容管理
				String cardTitle = cardTitle(message.getTitle()); // message.getTitle() 使用uuid  消息重发的问题
				if (MessageConnector.TYPE_CMS_PUBLISH.equals(message.getType())
						|| MessageConnector.TYPE_CMS_PUBLISH_TO_CREATOR.equals(message.getType())) {
					String categoryName = DingdingConsumeQueue.OuterMessageHelper.getPropertiesFromBody("categoryName", message.getBody());
					if (StringUtils.isEmpty(categoryName)) {
						categoryName = "信息通知";
					}
					m.setActionCardMsg(cardTitle, "# 【"+categoryName+"】 \n " +message.getTitle(), dingtalkUrl);
				} else {
					String processName = DingdingConsumeQueue.OuterMessageHelper.getPropertiesFromBody("processName", message.getBody());
					if (StringUtils.isEmpty(processName)) {
						processName = "工作通知";
					}

					m.setActionCardMsg(cardTitle, "# 【"+processName+"】 \n " + message.getTitle(), dingtalkUrl);
				}
//					m.getMsg().setMsgtype("markdown");
//					m.getMsg().getMarkdown().setTitle(message.getTitle());
//					m.getMsg().getMarkdown().setText("[" + message.getTitle() + "](" + dingtalkUrl + ")");
			} else {
				m.setTextMsg(message.getTitle());
			}
			String address = Config.dingding().getOapiAddress()
					+ "/topapi/message/corpconversation/asyncsend_v2?access_token="
					+ Config.dingding().corpAccessToken();
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("钉钉消息体：{}", m::toString);
			}
			DingdingMessageResp resp = HttpConnection.postAsObject(address, null, m.toString(),
					DingdingMessageResp.class);
			if (resp.getErrcode() != 0) {
				ExceptionDingdingMessage e = new ExceptionDingdingMessage(resp.getErrcode(), resp.getErrmsg());
				LOGGER.error(e);
			} else {
				success(message.getId());
			}
		}
	}

	private String cardTitle(String title) {
		String retTitle = title;
		if (StringUtils.isNotEmpty(retTitle)) {
			if (retTitle.length() > 46) {
				retTitle = retTitle.substring(0, 46);
			}
			Random r = new SecureRandom();
			int i = r.nextInt(9999-1000) + 1000;
			retTitle = retTitle + i;
		}
		return retTitle;
	}

	/**
	 * 判断打开地址
	 *
	 * @param message
	 * @return
	 */
	private String getOpenUrl(Message message) {
		String openUrl = "";
		// cms 文档
		if (MessageConnector.TYPE_CMS_PUBLISH.equals(message.getType())
				|| MessageConnector.TYPE_CMS_PUBLISH_TO_CREATOR.equals(message.getType())) {
			openUrl = getDingdingOpenCMSDocumentUrl(message.getBody());
		} else { // 流程工作相关的
			openUrl = getDingdingOpenWorkUrl(message.getBody());
		}
		return openUrl;
	}

	private Person getPerson(String person) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			return business.organization().person().getObject(person);
		}
	}

	/**
	 * 标志消息消费成功
	 *
	 * @param id
	 * @throws Exception
	 */
	private void success(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Message message = emc.find(id, Message.class);
			if (null != message) {
				emc.beginTransaction(Message.class);
				message.setConsumed(true);
				emc.commit();
			}
		}
	}

	/**
	 * 文档打开的url
	 *
	 * @param messageBody
	 * @return
	 */
	private String getDingdingOpenCMSDocumentUrl(String messageBody) {
		String o2oaUrl = null;
		try {
			o2oaUrl = Config.dingding().getWorkUrl() + "ddsso.html?redirect=";
			return OuterMessageHelper.getOpenCMSDocumentUrl(o2oaUrl, messageBody);
		} catch (Exception e) {
			LOGGER.error(e);
			return null;
		}
	}

	/**
	 * 工作打开的url
	 *
	 * @param messageBody
	 * @return
	 */
	private String getDingdingOpenWorkUrl(String messageBody) {
		try {
			String openPage = OuterMessageHelper.getOpenPageUrl(messageBody);
			String o2oaUrl = Config.dingding().getWorkUrl() + "ddsso.html?redirect=";
			if (StringUtils.isEmpty(openPage)) {
				String work = OuterMessageHelper.getWorkIdFromBody(messageBody);
				if (StringUtils.isEmpty(work) || StringUtils.isEmpty(o2oaUrl)) {
					return null;
				}
				String workUrl = "workmobilewithaction.html?workid=" + work;
				String messageRedirectPortal = Config.dingding().getMessageRedirectPortal();
				if (messageRedirectPortal != null && !"".equals(messageRedirectPortal)) {
					String portal = "portalmobile.html?id=" + messageRedirectPortal;
					// 2021-11-1 钉钉那边无法使用了 不能进行encode 否则签名不通过
					workUrl += "&redirectlink=" + portal;
				}
				// 2021-11-1 钉钉那边无法使用了 不能进行encode 否则签名不通过
				LOGGER.debug("o2oa workUrl：" + workUrl);
				o2oaUrl = o2oaUrl + workUrl;
			} else {
				o2oaUrl = o2oaUrl + openPage;
			}
			LOGGER.info("o2oa 地址：" + o2oaUrl);
			return o2oaUrl;
		} catch (Exception e) {
			LOGGER.error(e);
		}

		return null;
	}

	/**
	 * 是否需要把钉钉消息转成markdown格式消息 根据是否配置了钉钉工作链接、是否是工作消息（目前只支持工作消息）
	 *
	 * @param messageType 消息类型 判断是否是工作消息
	 * @return
	 */
	private boolean needTransferLink(String messageType) {
		try {
			String workUrl = Config.dingding().getWorkUrl();
			if (StringUtils.isNotEmpty(workUrl)) {
				return true;
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return false;
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

	/**
	 * 一些公用方法
	 */
	public static class OuterMessageHelper {
		/**
		 * 文档打开的url
		 * @param messageBody
		 * @return
		 */
		public static String getOpenCMSDocumentUrl(String bizBaseUrl, String messageBody) {
			try {
				String openPage = getOpenPageUrl(messageBody);
				String o2oaUrl = bizBaseUrl;
				if (StringUtils.isEmpty(openPage)) {
					String id = getCmsDocumentId(messageBody);
					if (StringUtils.isEmpty(id)) {
						return null;
					}
					String docUrl = "cmsdocMobile.html?id=" + id;
					o2oaUrl = o2oaUrl + docUrl;
				} else {
					o2oaUrl = o2oaUrl + openPage;
				}
				LOGGER.info("o2oa 业务地址：" + o2oaUrl);
				return o2oaUrl;
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return null;
		}

		/**
		 * body里面是否有 openPageUrl 这个字段 有就用这个字段作为跳转页面
		 *
		 * @param messageBody
		 * @return
		 */
		public static String getOpenPageUrl(String messageBody) {
			try {
				JsonObject object = gson.fromJson(messageBody, JsonObject.class);
				if (object.get("openPageUrl") != null) {
					return object.get("openPageUrl").getAsString();
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return null;
		}

		/**
		 * 获取workid
		 *
		 * @param messageBody
		 * @return
		 */
		public static  String getWorkIdFromBody(String messageBody) {
			try {
				JsonObject object = gson.fromJson(messageBody, JsonObject.class);
				if (object.get("work") != null) {
					return object.get("work").getAsString();
				}
				if (object.get("workCompleted") != null) {
					return object.get("workCompleted").getAsString();
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return "";
		}

		/**
		 * 获取消息体内的某个字段值
		 * @param propertiesName  如：processName
		 * @param messageBody
		 * @return
		 */
		public static String getPropertiesFromBody(String propertiesName, String messageBody) {
			try {
				JsonObject object = gson.fromJson(messageBody, JsonObject.class);
				if (object.get(propertiesName) != null) {
					return object.get(propertiesName).getAsString();
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return "";
		}

		/**
		 * 这个执行的前提是 MessageConnector.TYPE_CMS_PUBLISH.equals(message.getType()) ||
		 * MessageConnector.TYPE_CMS_PUBLISH_TO_CREATOR.equals(message.getType()) cms的消息
		 * body获取文档id
		 *
		 * @param messageBody
		 * @return
		 */
		private static String getCmsDocumentId(String messageBody) {
			try {
				JsonObject object = gson.fromJson(messageBody, JsonObject.class);
				if (object.get("id") != null) {
					return object.get("id").getAsString();
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return null;
		}
	}
}
