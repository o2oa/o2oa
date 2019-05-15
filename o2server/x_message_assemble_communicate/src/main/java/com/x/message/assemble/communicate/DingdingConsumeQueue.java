package com.x.message.assemble.communicate;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.DingdingMessage;
import com.x.base.core.project.queue.AbstractQueue;
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
				m.getMsg().getText().setContent(message.getTitle());
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
