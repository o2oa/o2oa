package com.x.message.assemble.communicate;

import com.google.gson.*;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.message.QiyeweixinMessage;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.message.core.entity.Message;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class QiyeweixinConsumeQueue extends AbstractQueue<Message> {

	private static Logger logger = LoggerFactory.getLogger(QiyeweixinConsumeQueue.class);

	protected void execute(Message message) throws Exception {

		if (Config.qiyeweixin().getEnable() && Config.qiyeweixin().getMessageEnable()) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				QiyeweixinMessage m = new QiyeweixinMessage();
				m.setAgentid(Long.parseLong(Config.qiyeweixin().getAgentId(), 10));
				m.setTouser(business.organization().person().getObject(message.getPerson()).getQiyeweixinId());
				String content = message.getTitle();
				if (needTransferLink(message.getType())) {
					String workUrl = getQywxOpenWorkUrl(message.getBody());
					if (workUrl != null && !"".equals(workUrl)) {
						content = "<a href=\"" + workUrl +"\">" + message.getTitle() + "</a>";
					}
				}
				m.getText().setContent(content);
				logger.info("微信消息："+m.toString());
				String address = Config.qiyeweixin().getApiAddress() + "/cgi-bin/message/send?access_token="
						+ Config.qiyeweixin().corpAccessToken();
				QiyeweixinMessageResp resp = HttpConnection.postAsObject(address, null, m.toString(),
						QiyeweixinMessageResp.class);
				if (resp.getErrcode() != 0) {
					ExceptionQiyeweixinMessage e = new ExceptionQiyeweixinMessage(resp.getErrcode(), resp.getErrmsg());
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
	private String getQywxOpenWorkUrl(String messageBody) {
		try {
			String work = getWorkIdFromBody(messageBody);
			String o2oaUrl = Config.qiyeweixin().getWorkUrl();
			String corpId = Config.qiyeweixin().getCorpId();
			String agentId = Config.qiyeweixin().getAgentId();
			if (work == null || "".equals(work) || o2oaUrl == null || "".equals(o2oaUrl) || corpId == null
			 || "".equals(corpId) || agentId == null || "".equals(agentId)) {
				return null;
			}
			String workUrl = "workmobilewithaction.html?workid=" + work;
			String messageRedirectPortal = Config.qiyeweixin().getMessageRedirectPortal();
			if (messageRedirectPortal != null && !"".equals(messageRedirectPortal)) {
				String portal = "portalmobile.html?id="+messageRedirectPortal;
				portal = URLEncoder.encode(portal, DefaultCharset.name);
				workUrl += "&redirectlink=" + portal;
			}
			workUrl = URLEncoder.encode(workUrl, DefaultCharset.name);
			o2oaUrl = o2oaUrl+"qiyeweixinsso.html?redirect="+workUrl;
			logger.info("o2oa 地址："+o2oaUrl);
			o2oaUrl = URLEncoder.encode(o2oaUrl, DefaultCharset.name);
			logger.info("encode url :"+o2oaUrl);
			String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+corpId
					+"&response_type=code&scope=snsapi_base"
					+"&agentid="+agentId
					+"&redirect_uri="+o2oaUrl
					+"&#wechat_redirect" ;
			logger.info("final url :" +url);
			return url;
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
//			if (messageType.startsWith("task_")) {
//				JsonObject object =new JsonParser().parse(messageBody).getAsJsonObject();
//				return object.get("work").getAsString();
//			}else if (messageType.startsWith("taskCompleted_")) {
//				JsonObject object =new JsonParser().parse(messageBody).getAsJsonObject();
//				String work =  object.get("work").getAsString();
//				String workCompleted = object.get("workCompleted").getAsString();
//				if (workCompleted != null && !"".equals(workCompleted)) {
//					return  workCompleted;
//				}else {
//					return work;
//				}
//			}else if (messageType.startsWith("read_")) {
//				JsonObject object =new JsonParser().parse(messageBody).getAsJsonObject();
//				return object.get("work").getAsString();
//			}else if (messageType.startsWith("readCompleted_")) {
//				JsonObject object =new JsonParser().parse(messageBody).getAsJsonObject();
//				String work =  object.get("work").getAsString();
//				String workCompleted = object.get("workCompleted").getAsString();
//				if (workCompleted != null && !"".equals(workCompleted)) {
//					return  workCompleted;
//				}else {
//					return work;
//				}
//			}else if (messageType.startsWith("review_")) {
//				JsonObject object =new JsonParser().parse(messageBody).getAsJsonObject();
//				return object.get("work").getAsString();
//			}
		} catch (Exception e) {
			logger.error(e);
		}
		return "";
	}

	/**
	 * 是否需要把企业微信消息转成超链接消息
	 * 根据是否配置了企业微信应用链接、是否是工作消息（目前只支持工作消息）
	 * @param messageType 消息类型 判断是否是工作消息
	 * @return
	 */
	private boolean needTransferLink(String messageType) {
		try {
			String workUrl = Config.qiyeweixin().getWorkUrl();
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

	public static class QiyeweixinMessageResp {

		// {
		// "errcode" : 0,
		// "errmsg" : "ok",
		// "invaliduser" : "userid1|userid2", // 不区分大小写，返回的列表都统一转为小写
		// "invalidparty" : "partyid1|partyid2",
		// "invalidtag":"tagid1|tagid2"
		// }

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
