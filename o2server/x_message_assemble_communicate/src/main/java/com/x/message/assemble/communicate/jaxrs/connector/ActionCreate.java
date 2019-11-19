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
import com.x.message.core.entity.Instant;
import com.x.message.core.entity.Message;

class ActionCreate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCreate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		List<Message> messages = new ArrayList<>();
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			List<String> consumers = Config.messages().getConsumers(wi.getType());			
			Instant instant = this.instant(effectivePerson, business, wi, consumers);
			if (ListTools.isNotEmpty(consumers)) {
				for (String consumer : consumers) {
					Message message = null;
					switch (Objects.toString(consumer, "")) {
					case MessageConnector.CONSUME_WS:
						message = this.wsMessage(effectivePerson, business, wi, instant);
						break;
					case MessageConnector.CONSUME_PMS:
						message = this.pmsMessage(effectivePerson, business, wi, instant);
						break;
					case MessageConnector.CONSUME_PMS_INNER:
						message = this.pmsInnerMessage(effectivePerson, business, wi, instant);
						break;
					case MessageConnector.CONSUME_DINGDING:
						message = this.dingdingMessage(effectivePerson, business, wi, instant);
						break;
					case MessageConnector.CONSUME_ZHENGWUDINGDING:
						message = this.zhegnwudingdingMessage(effectivePerson, business, wi, instant);
						break;
					case MessageConnector.CONSUME_QIYEWEIXIN:
						message = this.qiyeweixinMessage(effectivePerson, business, wi, instant);
						break;
					case MessageConnector.CONSUME_CALENDAR:
						message = this.calendarMessage(effectivePerson, business, wi, instant);
						break;
					default:
						message = this.defaultMessage(effectivePerson, business, wi, consumer, instant);
						break;
					}
					messages.add(message);
				}
			}
			emc.beginTransaction(Instant.class);
			emc.persist(instant, CheckPersistType.all);
			if (ListTools.isNotEmpty(messages)) {
				emc.beginTransaction(Message.class);
				for (Message message : messages) {
					emc.persist(message, CheckPersistType.all);
				}
			}
			emc.commit();
			/* emc上下文根必须结束掉,下面要直接调用发送队列,发送队列中会再次开启emc */
		}
		/* 开始发送,由于要回写所以先要commit */
		for (Message message : messages) {
			switch (message.getConsumer()) {
			case MessageConnector.CONSUME_WS:
				ThisApplication.wsConsumeQueue.send(message);
				break;
			case MessageConnector.CONSUME_PMS:
				ThisApplication.pmsConsumeQueue.send(message);
				break;
			case MessageConnector.CONSUME_PMS_INNER:
					ThisApplication.pmsInnerConsumeQueue.send(message);
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
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	private Instant instant(EffectivePerson effectivePerson, Business business, Wi wi, List<String> consumers) {
		Instant instant = new Instant();
		instant.setBody(Objects.toString(wi.getBody()));
		instant.setType(wi.getType());
		instant.setPerson(wi.getPerson());
		instant.setTitle(wi.getTitle());
		instant.setConsumerList(consumers);
		instant.setConsumed(false);
		return instant;
	}

	private Message wsMessage(EffectivePerson effectivePerson, Business business, Wi wi, Instant instant) {
		Message message = new Message();
		message.setBody(Objects.toString(wi.getBody()));
		message.setType(wi.getType());
		message.setPerson(wi.getPerson());
		message.setTitle(wi.getTitle());
		message.setConsumer(MessageConnector.CONSUME_WS);
		message.setConsumed(false);
		message.setInstant(instant.getId());
		return message;
	}

	private Message pmsMessage(EffectivePerson effectivePerson, Business business, Wi wi, Instant instant) {
		Message message = new Message();
		message.setBody(Objects.toString(wi.getBody()));
		message.setType(wi.getType());
		message.setPerson(wi.getPerson());
		message.setTitle(wi.getTitle());
		message.setConsumer(MessageConnector.CONSUME_PMS);
		message.setConsumed(false);
		message.setInstant(instant.getId());
		return message;
	}
	private Message pmsInnerMessage(EffectivePerson effectivePerson, Business business, Wi wi, Instant instant) {
		Message message = new Message();
		message.setBody(Objects.toString(wi.getBody()));
		message.setType(wi.getType());
		message.setPerson(wi.getPerson());
		message.setTitle(wi.getTitle());
		message.setConsumer(MessageConnector.CONSUME_PMS_INNER);
		message.setConsumed(false);
		message.setInstant(instant.getId());
		return message;
	}

	private Message dingdingMessage(EffectivePerson effectivePerson, Business business, Wi wi, Instant instant) {
		Message message = new Message();
		message.setBody(Objects.toString(wi.getBody()));
		message.setType(wi.getType());
		message.setPerson(wi.getPerson());
		message.setTitle(wi.getTitle());
		message.setConsumer(MessageConnector.CONSUME_DINGDING);
		message.setConsumed(false);
		message.setInstant(instant.getId());
		return message;
	}

	private Message zhegnwudingdingMessage(EffectivePerson effectivePerson, Business business, Wi wi, Instant instant) {
		Message message = new Message();
		message.setBody(Objects.toString(wi.getBody()));
		message.setType(wi.getType());
		message.setPerson(wi.getPerson());
		message.setTitle(wi.getTitle());
		message.setConsumer(MessageConnector.CONSUME_ZHENGWUDINGDING);
		message.setConsumed(false);
		message.setInstant(instant.getId());
		return message;
	}

	private Message qiyeweixinMessage(EffectivePerson effectivePerson, Business business, Wi wi, Instant instant) {
		Message message = new Message();
		message.setBody(Objects.toString(wi.getBody()));
		message.setType(wi.getType());
		message.setPerson(wi.getPerson());
		message.setTitle(wi.getTitle());
		message.setConsumer(MessageConnector.CONSUME_QIYEWEIXIN);
		message.setConsumed(false);
		message.setInstant(instant.getId());
		return message;
	}

	private Message calendarMessage(EffectivePerson effectivePerson, Business business, Wi wi, Instant instant) {
		Message message = new Message();
		message.setBody(Objects.toString(wi.getBody()));
		message.setType(wi.getType());
		message.setPerson(wi.getPerson());
		message.setTitle(wi.getTitle());
		message.setConsumer(MessageConnector.CONSUME_CALENDAR);
		message.setConsumed(false);
		message.setInstant(instant.getId());
		return message;
	}

	private Message defaultMessage(EffectivePerson effectivePerson, Business business, Wi wi, String consumer,
			Instant instant) {
		Message message = new Message();
		message.setBody(Objects.toString(wi.getBody()));
		message.setType(wi.getType());
		message.setPerson(wi.getPerson());
		message.setTitle(wi.getTitle());
		message.setConsumer(consumer);
		message.setConsumed(false);
		message.setInstant(instant.getId());
		return message;
	}

	public static class Wi extends MessageConnector.Wrap {
	}

	public static class Wo extends WrapBoolean {

	}

}
