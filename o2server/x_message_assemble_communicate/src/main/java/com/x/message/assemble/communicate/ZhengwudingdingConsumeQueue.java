package com.x.message.assemble.communicate;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.message.assemble.communicate.message.ZhengwuDingdingMessage;
import com.x.message.core.entity.Message;

public class ZhengwudingdingConsumeQueue extends AbstractQueue<Message> {

	private static Logger logger = LoggerFactory.getLogger(ZhengwudingdingConsumeQueue.class);

	protected void execute(Message message) throws Exception {

		if (Config.zhengwuDingding().getEnable() && Config.zhengwuDingding().getMessageEnable()) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				ZhengwuDingdingMessage m = new ZhengwuDingdingMessage();
				m.setAgentId(Long.parseLong(Config.zhengwuDingding().getAgentId(), 10));
				m.setTouser(business.organization().person().getObject(message.getPerson()).getZhengwuDingdingId());
				m.getMsg().getText().setContent(message.getTitle());
				String address = Config.zhengwuDingding().getOapiAddress() + "/ent_message/send?access_token="
						+ Config.zhengwuDingding().appAccessToken();
				logger.debug("send zhengwuDingDing to address:{}, body:{}.", address, m.toString());
				ZhengwuDingdingMessageResp resp = HttpConnection.postAsObject(address, null, m.toString(),
						ZhengwuDingdingMessageResp.class);
				if (resp.getRetCode() != 0) {
					ExceptionZhengwuDingdingMessage e = new ExceptionZhengwuDingdingMessage(resp.getRetCode(),
							resp.getRetMessage());
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

	public static class ZhengwuDingdingMessageResp {

		private Integer retCode;
		private String retMessage;
		private RetData retData;

		public static class RetData {
			private String invaliduser;
			private String invalidparty;
			private String errorparty;
			private String erroruser;

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

			public String getErrorparty() {
				return errorparty;
			}

			public void setErrorparty(String errorparty) {
				this.errorparty = errorparty;
			}

			public String getErroruser() {
				return erroruser;
			}

			public void setErroruser(String erroruser) {
				this.erroruser = erroruser;
			}

		}

		public Integer getRetCode() {
			return retCode;
		}

		public void setRetCode(Integer retCode) {
			this.retCode = retCode;
		}

		public String getRetMessage() {
			return retMessage;
		}

		public void setRetMessage(String retMessage) {
			this.retMessage = retMessage;
		}

		public RetData getRetData() {
			return retData;
		}

		public void setRetData(RetData retData) {
			this.retData = retData;
		}

	}
}
