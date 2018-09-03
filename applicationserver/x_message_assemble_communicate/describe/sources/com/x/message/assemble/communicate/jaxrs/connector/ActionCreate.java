package com.x.message.assemble.communicate.jaxrs.connector;

import java.util.Objects;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.Application;
import com.x.base.core.project.x_calendar_assemble_control;
import com.x.base.core.project.x_message_assemble_communicate;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.ImMessage;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.message.PmsMessage;
import com.x.base.core.project.organization.Person;
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
			Message message = new Message();
			message.setBody(Objects.toString(wi.getBody()));
			message.setType(wi.getType());
			message.setPerson(wi.getPerson());
			message.setTitle(wi.getTitle());
			message.setConsumerList(Config.messages().getConsumers(wi.getType()));
			this.im_consume(effectivePerson, business, message);
			this.pms_consume(effectivePerson, business, message);
			this.calendar_consume(effectivePerson, business, message);
			if (!message.getConsumerList().isEmpty()) {
				emc.beginTransaction(Message.class);
				emc.persist(message, CheckPersistType.all);
				emc.commit();
			}
			Wo wo = new Wo();
			result.setData(wo);
			return result;
		}
	}

	private void im_consume(EffectivePerson effectivePerson, Business business, Message message) {
		try {
			if (ListTools.contains(message.getConsumerList(), MessageConnector.CONSUME_IM)) {
				ImMessage im = new ImMessage();
				im.setType(message.getType());
				im.setPerson(message.getPerson());
				im.setTitle(message.getTitle());
				im.setBody(gson.fromJson(message.getBody(), JsonElement.class));
				for (Application app : ThisApplication.context().applications()
						.get(x_message_assemble_communicate.class)) {
					WrapBoolean wrapBoolean = ThisApplication.context().applications()
							.postQuery(effectivePerson.getDebugger(), app, MessageConnector.CONSUME_IM, im)
							.getData(WrapBoolean.class);
					if (BooleanUtils.isTrue(wrapBoolean.getValue())) {
						message.getConsumerList().remove(MessageConnector.CONSUME_IM);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private void pms_consume(EffectivePerson effectivePerson, Business business, Message message) {
		try {
			if (ListTools.contains(message.getConsumerList(), MessageConnector.CONSUME_PMS)) {
				message.getConsumerList().remove(MessageConnector.CONSUME_PMS);
				Person person = business.organization().person().getObject(message.getPerson());
				if ((null != person) && StringUtils.isNotEmpty(person.getMobile())) {
					PmsMessage pms = new PmsMessage();
					pms.setAccount(person.getMobile());
					pms.setTitle(message.getTitle());
					pms.setText(message.getTitle());
					String url = Config.x_program_centerUrlRoot() + MessageConnector.CONSUME_PMS;
					CipherConnectionAction.post(effectivePerson.getDebugger(), url, pms);
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private void calendar_consume(EffectivePerson effectivePerson, Business business, Message message) {
		try {
			if (ListTools.contains(message.getConsumerList(), MessageConnector.CONSUME_CALENDAR)) {
				Application app = ThisApplication.context().applications()
						.randomWithWeight(x_calendar_assemble_control.class);
				if (null != app) {
					WrapBoolean wrapBoolean = ThisApplication.context().applications()
							.postQuery(effectivePerson.getDebugger(), app, "message", message)
							.getData(WrapBoolean.class);
					if (BooleanUtils.isTrue(wrapBoolean.getValue())) {
						message.getConsumerList().remove(MessageConnector.CONSUME_CALENDAR);
					}
				} else {
					throw new ExceptionCalendarApplicationNotFound();
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public static class Wi extends MessageConnector.Wrap {
	}

	public static class Wo extends WoId {

	}

}
