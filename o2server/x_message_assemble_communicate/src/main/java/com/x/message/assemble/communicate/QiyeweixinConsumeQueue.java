package com.x.message.assemble.communicate;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.QiyeweixinMessage;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.message.core.entity.Message;

public class QiyeweixinConsumeQueue extends AbstractQueue<Message> {

	private static Logger logger = LoggerFactory.getLogger(QiyeweixinConsumeQueue.class);

	protected void execute(Message message) throws Exception {

		if (Config.qiyeweixin().getEnable() && Config.qiyeweixin().getMessageEnable()) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				QiyeweixinMessage m = new QiyeweixinMessage();
				m.setAgentid(Long.parseLong(Config.qiyeweixin().getAgentId(), 10));
				m.setTouser(business.organization().person().getObject(message.getPerson()).getQiyeweixinId());
				m.getText().setContent(message.getTitle());
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
