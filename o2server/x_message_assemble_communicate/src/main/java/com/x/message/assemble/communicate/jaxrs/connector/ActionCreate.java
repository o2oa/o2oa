package com.x.message.assemble.communicate.jaxrs.connector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.tools.ListTools;
import com.x.message.assemble.communicate.Business;
import com.x.message.assemble.communicate.ThisApplication;
import com.x.message.core.entity.Message;

class ActionCreate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCreate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			List<String> consumers = Config.messages().getConsumers(wi.getType());
			if (ListTools.isNotEmpty(consumers)) {
				List<Message> messages = new ArrayList<>();
				emc.beginTransaction(Message.class);
				for (String consumer : consumers) {
					Message message = null;
					switch (Objects.toString(consumer, "")) {
					case MessageConnector.CONSUME_IM:
						message = this.imMessage(effectivePerson, business, wi);
						break;
					case MessageConnector.CONSUME_PMS:
						message = this.pmsMessage(effectivePerson, business, wi);
						break;
					case MessageConnector.CONSUME_DINGDING:
						message = this.dingdingMessage(effectivePerson, business, wi);
						break;
					case MessageConnector.CONSUME_ZHENGWUDINGDING:
						message = this.zhegnwudingdingMessage(effectivePerson, business, wi);
						break;
					case MessageConnector.CONSUME_QIYEWEIXIN:
						message = this.qiyeweixinMessage(effectivePerson, business, wi);
						break;
					case MessageConnector.CONSUME_CALENDAR:
						message = this.calendarMessage(effectivePerson, business, wi);
						break;
					default:
						message = this.defaultMessage(effectivePerson, business, wi, consumer);
						break;
					}
					emc.persist(message, CheckPersistType.all);
					messages.add(message);
				}
				emc.commit();

				for (Message message : messages) {
					switch (message.getConsumer()) {
					case MessageConnector.CONSUME_IM:
						ThisApplication.imConsumeQueue.send(message);
						break;
					case MessageConnector.CONSUME_PMS:
						ThisApplication.pmsConsumeQueue.send(message);
						break;
					case MessageConnector.CONSUME_DINGDING:
						ThisApplication.dingdingConsumeQueue.send(message);
						break;
					case MessageConnector.CONSUME_ZHENGWUDINGDING:
						ThisApplication.zhengwuDingdingConsumeQueue.send(message);
						break;
					case MessageConnector.CONSUME_QIYEWEIXIN:
						ThisApplication.qiyeweixinConsumeQueue.send(message);
						break;
					case MessageConnector.CONSUME_CALENDAR:
						ThisApplication.calendarConsumeQueue.send(message);
						break;
					default:
						break;
					}
				}
			}

//			this.im_consume(effectivePerson, business, message);
//			this.pms_consume(effectivePerson, business, message);
//			this.calendar_consume(effectivePerson, business, message);
//			this.dingding_consume(effectivePerson, business, message);
//			this.qiyeweixin_consume(effectivePerson, business, message);
//			this.zhengwudingding_consume(effectivePerson, business, message);

			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	private Message imMessage(EffectivePerson effectivePerson, Business business, Wi wi) {
		Message message = new Message();
		message.setBody(Objects.toString(wi.getBody()));
		message.setType(wi.getType());
		message.setPerson(wi.getPerson());
		message.setTitle(wi.getTitle());
		message.setConsumer(MessageConnector.CONSUME_IM);
		return message;
	}

	private Message pmsMessage(EffectivePerson effectivePerson, Business business, Wi wi) {
		Message message = new Message();
		message.setBody(Objects.toString(wi.getBody()));
		message.setType(wi.getType());
		message.setPerson(wi.getPerson());
		message.setTitle(wi.getTitle());
		message.setConsumer(MessageConnector.CONSUME_PMS);
		return message;
	}

	private Message dingdingMessage(EffectivePerson effectivePerson, Business business, Wi wi) {
		Message message = new Message();
		message.setBody(Objects.toString(wi.getBody()));
		message.setType(wi.getType());
		message.setPerson(wi.getPerson());
		message.setTitle(wi.getTitle());
		message.setConsumer(MessageConnector.CONSUME_DINGDING);
		return message;
	}

	private Message zhegnwudingdingMessage(EffectivePerson effectivePerson, Business business, Wi wi) {
		Message message = new Message();
		message.setBody(Objects.toString(wi.getBody()));
		message.setType(wi.getType());
		message.setPerson(wi.getPerson());
		message.setTitle(wi.getTitle());
		message.setConsumer(MessageConnector.CONSUME_ZHENGWUDINGDING);
		return message;
	}

	private Message qiyeweixinMessage(EffectivePerson effectivePerson, Business business, Wi wi) {
		Message message = new Message();
		message.setBody(Objects.toString(wi.getBody()));
		message.setType(wi.getType());
		message.setPerson(wi.getPerson());
		message.setTitle(wi.getTitle());
		message.setConsumer(MessageConnector.CONSUME_QIYEWEIXIN);
		return message;
	}

	private Message calendarMessage(EffectivePerson effectivePerson, Business business, Wi wi) {
		Message message = new Message();
		message.setBody(Objects.toString(wi.getBody()));
		message.setType(wi.getType());
		message.setPerson(wi.getPerson());
		message.setTitle(wi.getTitle());
		message.setConsumer(MessageConnector.CONSUME_CALENDAR);
		return message;
	}

	private Message defaultMessage(EffectivePerson effectivePerson, Business business, Wi wi, String consumer) {
		Message message = new Message();
		message.setBody(Objects.toString(wi.getBody()));
		message.setType(wi.getType());
		message.setPerson(wi.getPerson());
		message.setTitle(wi.getTitle());
		message.setConsumer(consumer);
		return message;
	}

//	private void dingding_consume(EffectivePerson effectivePerson, Business business, Message message) {
//		try {
//			if (ListTools.contains(message.getConsumerList(), MessageConnector.CONSUME_DINGDING)) {
//				message.getConsumerList().remove(MessageConnector.CONSUME_DINGDING);
//				if (Config.dingding().getEnable() && Config.dingding().getMessageEnable()) {
//					DingdingMessage m = new DingdingMessage();
//					m.setAgent_id(Long.parseLong(Config.dingding().getAgentId(), 10));
//					m.setUserid_list(business.organization().person().getObject(message.getPerson()).getDingdingId());
//					m.getMsg().getText().setContent(message.getTitle());
//					// https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2?access_token=ACCESS_TOKEN
//					String address = Config.dingding().getOapiAddress()
//							+ "/topapi/message/corpconversation/asyncsend_v2?access_token="
//							+ Config.dingding().corpAccessToken();
//					DingdingMessageResp resp = HttpConnection.postAsObject(address, null, m.toString(),
//							DingdingMessageResp.class);
//					if (resp.getErrcode() != 0) {
//						throw new ExceptionDingdingMessage(resp.getErrcode(), resp.getErrmsg());
//					}
//				}
//			}
//		} catch (Exception e) {
//			logger.error(e);
//		}
//	}
//
//	private void zhengwudingding_consume(EffectivePerson effectivePerson, Business business, Message message) {
//		try {
//			if (ListTools.contains(message.getConsumerList(), MessageConnector.CONSUME_ZHENGWUDINGDING)) {
//				message.getConsumerList().remove(MessageConnector.CONSUME_ZHENGWUDINGDING);
//				if (Config.zhengwuDingding().getEnable() && Config.zhengwuDingding().getMessageEnable()) {
//					ZhengwuDingdingMessage m = new ZhengwuDingdingMessage();
//					m.setAgentId(Long.parseLong(Config.zhengwuDingding().getAgentId(), 10));
//					m.setTouser(business.organization().person().getObject(message.getPerson()).getZhengwuDingdingId());
//					m.getMsg().getText().setContent(message.getTitle());
//					String address = Config.zhengwuDingding().getOapiAddress() + "/ent_message/send?access_token="
//							+ Config.zhengwuDingding().appAccessToken();
//					ZhengwuDingdingMessageResp resp = HttpConnection.postAsObject(address, null, m.toString(),
//							ZhengwuDingdingMessageResp.class);
//					if (resp.getRetCode() != 0) {
//						throw new ExceptionZhengwuDingdingMessage(resp.getRetCode(), resp.getRetMessage());
//					}
//				}
//			}
//		} catch (Exception e) {
//			logger.error(e);
//		}
//	}
//
//	private void qiyeweixin_consume(EffectivePerson effectivePerson, Business business, Message message) {
//		try {
//			if (ListTools.contains(message.getConsumerList(), MessageConnector.CONSUME_QIYEWEIXIN)) {
//				message.getConsumerList().remove(MessageConnector.CONSUME_QIYEWEIXIN);
//				if (Config.qiyeweixin().getEnable() && Config.qiyeweixin().getMessageEnable()) {
//					QiyeweixinMessage m = new QiyeweixinMessage();
//					m.setAgentid(Long.parseLong(Config.qiyeweixin().getAgentId(), 10));
//					m.setTouser(business.organization().person().getObject(message.getPerson()).getQiyeweixinId());
//					m.getText().setContent(message.getTitle());
//					String address = Config.qiyeweixin().getApiAddress() + "/cgi-bin/message/send?access_token="
//							+ Config.qiyeweixin().corpAccessToken();
//					QiyeweixinMessageResp resp = HttpConnection.postAsObject(address, null, m.toString(),
//							QiyeweixinMessageResp.class);
//					if (resp.getErrcode() != 0) {
//						throw new ExceptionQiyeweixinMessage(resp.getErrcode(), resp.getErrmsg());
//					}
//				}
//			}
//		} catch (Exception e) {
//			logger.error(e);
//		}
//	}
//
//	private void im_consume(EffectivePerson effectivePerson, Business business, Message message) {
//		try {
//			if (ListTools.contains(message.getConsumerList(), MessageConnector.CONSUME_IM)) {
//				ImMessage im = new ImMessage();
//				im.setType(message.getType());
//				im.setPerson(message.getPerson());
//				im.setTitle(message.getTitle());
//				im.setBody(gson.fromJson(message.getBody(), JsonElement.class));
//				for (Application app : ThisApplication.context().applications()
//						.get(x_message_assemble_communicate.class)) {
//					WrapBoolean wrapBoolean = ThisApplication.context().applications()
//							.postQuery(effectivePerson.getDebugger(), app, MessageConnector.CONSUME_IM, im)
//							.getData(WrapBoolean.class);
//					if (BooleanUtils.isTrue(wrapBoolean.getValue())) {
//						message.getConsumerList().remove(MessageConnector.CONSUME_IM);
//					}
//				}
//			}
//		} catch (Exception e) {
//			logger.error(e);
//		}
//	}
//
//	private void pms_consume(EffectivePerson effectivePerson, Business business, Message message) {
//		try {
//			if (ListTools.contains(message.getConsumerList(), MessageConnector.CONSUME_PMS)) {
//				message.getConsumerList().remove(MessageConnector.CONSUME_PMS);
//				Person person = business.organization().person().getObject(message.getPerson());
//				if ((null != person) && StringUtils.isNotEmpty(person.getMobile())) {
//					PmsMessage pms = new PmsMessage();
//					pms.setAccount(person.getMobile());
//					pms.setTitle(message.getTitle());
//					pms.setText(message.getTitle());
//					String url = Config.x_program_centerUrlRoot() + MessageConnector.CONSUME_PMS;
//					CipherConnectionAction.post(effectivePerson.getDebugger(), url, pms);
//				}
//			}
//		} catch (Exception e) {
//			logger.error(e);
//		}
//	}
//
//	private void calendar_consume(EffectivePerson effectivePerson, Business business, Message message) {
//		try {
//			if (ListTools.contains(message.getConsumerList(), MessageConnector.CONSUME_CALENDAR)) {
//				Application app = ThisApplication.context().applications()
//						.randomWithWeight(x_calendar_assemble_control.class.getName());
//				if (null != app) {
//					WrapBoolean wrapBoolean = ThisApplication.context().applications()
//							.postQuery(effectivePerson.getDebugger(), app, "message", message)
//							.getData(WrapBoolean.class);
//					if (BooleanUtils.isTrue(wrapBoolean.getValue())) {
//						message.getConsumerList().remove(MessageConnector.CONSUME_CALENDAR);
//					}
//				} else {
//					throw new ExceptionCalendarApplicationNotFound();
//				}
//			}
//		} catch (Exception e) {
//			logger.error(e);
//		}
//	}

	public static class Wi extends MessageConnector.Wrap {
	}

	public static class Wo extends WrapBoolean {

	}

//	public static class ZhengwuDingdingMessageResp {
//
//		private Integer retCode;
//		private String retMessage;
//		private RetData retData;
//
//		public static class RetData {
//			private String invaliduser;
//			private String invalidparty;
//			private String errorparty;
//			private String erroruser;
//
//			public String getInvaliduser() {
//				return invaliduser;
//			}
//
//			public void setInvaliduser(String invaliduser) {
//				this.invaliduser = invaliduser;
//			}
//
//			public String getInvalidparty() {
//				return invalidparty;
//			}
//
//			public void setInvalidparty(String invalidparty) {
//				this.invalidparty = invalidparty;
//			}
//
//			public String getErrorparty() {
//				return errorparty;
//			}
//
//			public void setErrorparty(String errorparty) {
//				this.errorparty = errorparty;
//			}
//
//			public String getErroruser() {
//				return erroruser;
//			}
//
//			public void setErroruser(String erroruser) {
//				this.erroruser = erroruser;
//			}
//
//		}
//
//		public Integer getRetCode() {
//			return retCode;
//		}
//
//		public void setRetCode(Integer retCode) {
//			this.retCode = retCode;
//		}
//
//		public String getRetMessage() {
//			return retMessage;
//		}
//
//		public void setRetMessage(String retMessage) {
//			this.retMessage = retMessage;
//		}
//
//		public RetData getRetData() {
//			return retData;
//		}
//
//		public void setRetData(RetData retData) {
//			this.retData = retData;
//		}
//
//	}

//	public static class DingdingMessageResp {
//
//		private Integer errcode;
//		private String errmsg;
//		private Long task_id;
//
//		public String getErrmsg() {
//			return errmsg;
//		}
//
//		public void setErrmsg(String errmsg) {
//			this.errmsg = errmsg;
//		}
//
//		public Long getTask_id() {
//			return task_id;
//		}
//
//		public void setTask_id(Long task_id) {
//			this.task_id = task_id;
//		}
//
//		public Integer getErrcode() {
//			return errcode;
//		}
//
//		public void setErrcode(Integer errcode) {
//			this.errcode = errcode;
//		}
//
//	}

//	public static class QiyeweixinMessageResp {
//
//		// {
//		// "errcode" : 0,
//		// "errmsg" : "ok",
//		// "invaliduser" : "userid1|userid2", // 不区分大小写，返回的列表都统一转为小写
//		// "invalidparty" : "partyid1|partyid2",
//		// "invalidtag":"tagid1|tagid2"
//		// }
//
//		private Integer errcode;
//		private String errmsg;
//		private String invaliduser;
//		private String invalidparty;
//		private String invalidtag;
//
//		public String getErrmsg() {
//			return errmsg;
//		}
//
//		public void setErrmsg(String errmsg) {
//			this.errmsg = errmsg;
//		}
//
//		public String getInvaliduser() {
//			return invaliduser;
//		}
//
//		public void setInvaliduser(String invaliduser) {
//			this.invaliduser = invaliduser;
//		}
//
//		public String getInvalidparty() {
//			return invalidparty;
//		}
//
//		public void setInvalidparty(String invalidparty) {
//			this.invalidparty = invalidparty;
//		}
//
//		public String getInvalidtag() {
//			return invalidtag;
//		}
//
//		public void setInvalidtag(String invalidtag) {
//			this.invalidtag = invalidtag;
//		}
//
//		public Integer getErrcode() {
//			return errcode;
//		}
//
//		public void setErrcode(Integer errcode) {
//			this.errcode = errcode;
//		}
//
//	}

}
