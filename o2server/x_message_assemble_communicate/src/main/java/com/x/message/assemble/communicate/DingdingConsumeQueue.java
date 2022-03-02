package com.x.message.assemble.communicate;

import java.net.URLEncoder;

import com.x.base.core.project.message.MessageConnector;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.DingdingMessage;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.message.core.entity.Message;

public class DingdingConsumeQueue extends AbstractQueue<Message> {

	private static Logger logger = LoggerFactory.getLogger(DingdingConsumeQueue.class);

	protected void execute(Message message) throws Exception {

		if (Config.dingding().getEnable() && Config.dingding().getMessageEnable()) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				DingdingMessage m = new DingdingMessage();
				m.setAgent_id(Long.parseLong(Config.dingding().getAgentId(), 10));
				m.setUserid_list(business.organization().person().getObject(message.getPerson()).getDingdingId());
				if (StringUtils.isEmpty(m.getUserid_list())) {
					logger.info("没有接收钉钉消息的人员。。。。。。。。。。。。。");
					return;
				}
				if (needTransferLink(message.getType())) {
					String openUrl = "";
					// cms 文档
					if (MessageConnector.TYPE_CMS_PUBLISH.equals(message.getType()) || MessageConnector.TYPE_CMS_PUBLISH_TO_CREATOR.equals(message.getType())) {
						openUrl = getDingdingOpenCMSDocumentUrl(message.getBody());
					} else { // 流程工作相关的
						openUrl = getDingdingOpenWorkUrl(message.getBody());
					}

					if (StringUtils.isNotEmpty(openUrl)) {
						logger.debug("openUrl: "+openUrl);
						// dingtalk://dingtalkclient/action/openapp?corpid=免登企业corpId&container_type=work_platform&app_id=0_{应用agentid}&redirect_type=jump&redirect_url=跳转url
						String dingtalkUrl = "dingtalk://dingtalkclient/action/openapp?corpid=" + Config.dingding().getCorpId() +
							"&container_type=work_platform&app_id=0_" + Config.dingding().getAgentId() +
								"&redirect_type=jump&redirect_url="+ URLEncoder.encode(openUrl, DefaultCharset.name);
						logger.info("钉钉pc 打开消息 url："+dingtalkUrl);
						m.getMsg().setMsgtype("markdown");
						m.getMsg().getMarkdown().setTitle(message.getTitle());
						m.getMsg().getMarkdown().setText("["+message.getTitle()+"]("+dingtalkUrl+")");
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
				logger.debug("钉钉发送消息url：" + address);
				logger.debug("钉钉消息体：" + m.toString());
				DingdingMessageResp resp = HttpConnection.postAsObject(address, null, m.toString(),
						DingdingMessageResp.class);
				if (resp.getErrcode() != 0) {
					ExceptionDingdingMessage e = new ExceptionDingdingMessage(resp.getErrcode(), resp.getErrmsg());
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
	 * 文档打开的url
	 * @param messageBody
	 * @return
	 */
	private String getDingdingOpenCMSDocumentUrl(String messageBody) {
		try {
			String openPage = getOpenPageUrl(messageBody);
			String o2oaUrl = Config.dingding().getWorkUrl();
			if (StringUtils.isEmpty(openPage)) {
				String id = getCmsDocumentId(messageBody);
				if (StringUtils.isEmpty(id)) {
					return null;
				}
				String docUrl = "cmsdocMobile.html?id=" + id;
				o2oaUrl = o2oaUrl + "ddsso.html?redirect=" + docUrl;
			} else {
				o2oaUrl = o2oaUrl + "ddsso.html?redirect=" + openPage;
			}
			logger.info("o2oa 地址：" + o2oaUrl);
			return o2oaUrl;
		}catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	/**
	 * 工作打开的url
	 * @param messageBody
	 * @return
	 */
	private String getDingdingOpenWorkUrl(String messageBody) {
		try {
			String openPage = getOpenPageUrl(messageBody);
			String o2oaUrl = Config.dingding().getWorkUrl();
			if (StringUtils.isEmpty(openPage)) {
				String work = getWorkIdFromBody(messageBody);
				if (StringUtils.isEmpty(work) || StringUtils.isEmpty(o2oaUrl)) {
					return null;
				}
				String workUrl = "workmobilewithaction.html?workid=" + work;
				String messageRedirectPortal = Config.dingding().getMessageRedirectPortal();
				if (messageRedirectPortal != null && !"".equals(messageRedirectPortal)) {
					String portal = "portalmobile.html?id=" + messageRedirectPortal;
					// 2021-11-1 钉钉那边无法使用了 不能进行encode 否则签名不通过
//				portal = URLEncoder.encode(portal, DefaultCharset.name);
					workUrl += "&redirectlink=" + portal;
				}
				// 2021-11-1 钉钉那边无法使用了 不能进行encode 否则签名不通过
//			workUrl = URLEncoder.encode(workUrl, DefaultCharset.name);
				logger.debug("o2oa workUrl：" + workUrl);
				o2oaUrl = o2oaUrl + "ddsso.html?redirect=" + workUrl;
			} else {
				o2oaUrl = o2oaUrl + "ddsso.html?redirect=" + openPage;
			}
			logger.info("o2oa 地址："+o2oaUrl);
			return o2oaUrl;
		}catch (Exception e) {
			logger.error(e);
		}

		return null;
	}

	/**
	 * 获取workid or workCompleted
	 * @param messageBody
	 * @return
	 */
	private String getWorkIdFromBody(String messageBody) {
		try {
			JsonObject object =new JsonParser().parse(messageBody).getAsJsonObject();
			if (object.get("work") != null) {
				return object.get("work").getAsString();
			}
			if (object.get("workCompleted") != null) {
				return object.get("workCompleted").getAsString();
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	/**
	 * 这个执行的前提是 MessageConnector.TYPE_CMS_PUBLISH.equals(message.getType()) || MessageConnector.TYPE_CMS_PUBLISH_TO_CREATOR.equals(message.getType()) cms的消息
	 * body获取文档id
	 * @param messageBody
	 * @return
	 */
	private String getCmsDocumentId(String messageBody) {
		try {
			JsonObject object =new JsonParser().parse(messageBody).getAsJsonObject();
			if (object.get("id") != null) {
				return object.get("id").getAsString();
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	/**
	 * body里面是否有 openPageUrl 这个字段 有就用这个字段作为跳转页面
	 * @param messageBody
	 * @return
	 */
	private String getOpenPageUrl(String messageBody) {
		try {
			JsonObject object =new JsonParser().parse(messageBody).getAsJsonObject();
			if (object.get("openPageUrl") != null) {
				return object.get("openPageUrl").getAsString();
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
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
			if (StringUtils.isNotEmpty(workUrl)) {
				return true;
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return false;
	}

//	private List<String> workMessageTypeList() {
//		List<String> list = new ArrayList<>();
//		list.add(MessageConnector.TYPE_WORK_TO_WORKCOMPLETED);
//		list.add(MessageConnector.TYPE_WORK_CREATE);
//		list.add(MessageConnector.TYPE_WORK_DELETE);
//		list.add(MessageConnector.TYPE_WORKCOMPLETED_CREATE);
//		list.add(MessageConnector.TYPE_WORKCOMPLETED_DELETE);
//		list.add(MessageConnector.TYPE_TASK_TO_TASKCOMPLETED);
//		list.add(MessageConnector.TYPE_TASK_CREATE);
//		list.add(MessageConnector.TYPE_TASK_DELETE);
//		list.add(MessageConnector.TYPE_TASK_URGE);
//		list.add(MessageConnector.TYPE_TASK_EXPIRE);
//		list.add(MessageConnector.TYPE_TASK_PRESS);
//		list.add(MessageConnector.TYPE_TASKCOMPLETED_CREATE);
//		list.add(MessageConnector.TYPE_TASKCOMPLETED_DELETE);
//		list.add(MessageConnector.TYPE_READ_TO_READCOMPLETED);
//		list.add(MessageConnector.TYPE_READ_CREATE);
//		list.add(MessageConnector.TYPE_READ_DELETE);
//		list.add(MessageConnector.TYPE_READCOMPLETED_CREATE);
//		list.add(MessageConnector.TYPE_READCOMPLETED_DELETE);
//		list.add(MessageConnector.TYPE_REVIEW_CREATE);
//		list.add(MessageConnector.TYPE_REVIEW_DELETE);
//
//		return list;
//	}

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
