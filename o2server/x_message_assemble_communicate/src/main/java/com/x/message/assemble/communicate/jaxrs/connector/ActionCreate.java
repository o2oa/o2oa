package com.x.message.assemble.communicate.jaxrs.connector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptContext;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
import com.x.base.core.project.scripting.JsonScriptingExecutor;
import com.x.base.core.project.scripting.ScriptingFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.message.assemble.communicate.ThisApplication;
import com.x.message.core.entity.Instant;
import com.x.message.core.entity.Message;

class ActionCreate extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreate.class);
	private static ConcurrentMap<String, CompiledScript> scriptMap = new ConcurrentHashMap<>();

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		LOGGER.debug(effectivePerson.getDistinguishedName());
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		List<String> consumers = Config.messages().getConsumers(wi.getType());
		Map<String, String> consumersV2 = Config.messages().getConsumersV2(wi.getType());
		for (String consumer : consumers) {
			if (!consumersV2.containsKey(consumer)) {
				consumersV2.put(consumer, "");
			}
		}
		Instant instant = this.instant(wi, new ArrayList<>(consumersV2.keySet()));
		List<Message> messages = new ArrayList<>();
		assemble(wi, consumersV2, instant, messages);
		save(instant, messages);
		this.sendMessage(messages);
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	private void save(Instant instant, List<Message> messages) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			emc.beginTransaction(Instant.class);
			emc.persist(instant, CheckPersistType.all);
			if (ListTools.isNotEmpty(messages)) {
				emc.beginTransaction(Message.class);
				for (Message message : messages) {
					emc.persist(message, CheckPersistType.all);
				}
			}
			emc.commit();
		}
	}

	private void assemble(Wi wi, Map<String, String> consumersV2, Instant instant, List<Message> messages)
			throws Exception {
		if (!consumersV2.isEmpty()) {
			for (Map.Entry<String, String> en : consumersV2.entrySet()) {
				String func = consumersV2.get(en.getKey());
				Wi cpWi = this.executeFun(wi, func, en.getKey());
				if (cpWi != null) {
					Message message = this.assembleMessage(en.getKey(), cpWi, instant);
					if (message != null) {
						messages.add(message);
					}
				}
			}
		}
	}

	private Wi executeFun(Wi wi, String func, String consumer) {
		Wi cpWi = wi;
		try {
			if (StringUtils.isNoneBlank(func)) {
				cpWi = (Wi) BeanUtils.cloneBean(wi);
				JsonObject body = cpWi.getBody().deepCopy().getAsJsonObject();
				CompiledScript compiledScript = scriptMap.get(func);
				if (compiledScript == null) {
					String eval = Config.messageSendRuleScript();
					if (StringUtils.isNotEmpty(eval)) {
						compiledScript = ScriptingFactory.functionalizationCompile(func);
						scriptMap.put(func, compiledScript);
					}
				}
				if (compiledScript != null) {
					ScriptContext scriptContext = ScriptingFactory.scriptContextEvalInitialServiceScript();
					Bindings bindings = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
					bindings.put(ScriptingFactory.BINDING_NAME_SERVICE_BODY, body);
					bindings.put(ScriptingFactory.BINDING_NAME_SERVICE_MESSAGE, cpWi);
					Boolean ifSend = JsonScriptingExecutor.evalBoolean(compiledScript, scriptContext);
					if (BooleanUtils.isNotTrue(ifSend)) {
						LOGGER.info("消息类型{}.{}的消息[{}]不满足发送条件.", wi.getType(), consumer, wi.getTitle());
						cpWi = null;
					} else {
						cpWi.setBody(body);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.warn("执行消息发送脚本[{}]方法异常:{}", func, e.getMessage());
		}
		return cpWi;
	}

	private Message assembleMessage(String consumer, Wi cpWi, Instant instant) throws Exception {
		Message message = null;
		switch (Objects.toString(consumer, "")) {
		case MessageConnector.CONSUME_WS:
			message = this.wsMessage(cpWi, instant);
			break;
		case MessageConnector.CONSUME_PMS:
			message = this.pmsMessage(cpWi, instant);
			break;
		case MessageConnector.CONSUME_PMS_INNER:
			message = this.pmsInnerMessage(cpWi, instant);
			break;
		case MessageConnector.CONSUME_DINGDING:
			message = this.dingdingMessage(cpWi, instant);
			break;
		case MessageConnector.CONSUME_ZHENGWUDINGDING:
			message = this.zhengwudingdingMessage(cpWi, instant);
			break;
		case MessageConnector.CONSUME_QIYEWEIXIN:
			message = this.qiyeweixinMessage(cpWi, instant);
			break;
		case MessageConnector.CONSUME_CALENDAR:
			message = this.calendarMessage(cpWi, instant);
			break;
		case MessageConnector.CONSUME_WELINK:
			message = this.weLinkMessage(cpWi, instant);
			break;
		case MessageConnector.CONSUME_MQ:
			message = this.mqMessage(cpWi, instant, consumer);
			break;
		case MessageConnector.CONSUME_MPWEIXIN:
			message = this.mpweixinMessage(cpWi, instant);
			break;
		default:
			if (consumer.startsWith(MessageConnector.CONSUME_MQ)) {
				message = this.mqMessage(cpWi, instant, consumer);
			} else {
				message = this.defaultMessage(cpWi, consumer, instant);
			}
			break;
		}
		return message;

	}

	private void sendMessage(List<Message> messages) throws Exception {
		for (Message message : messages) {
			switch (message.getConsumer()) {
			case MessageConnector.CONSUME_WS:
				sendMessageWs(message);
				break;
			case MessageConnector.CONSUME_PMS:
				sendMessagePms(message);
				break;
			case MessageConnector.CONSUME_CALENDAR:
				sendMessageCalendar(message);
				break;
			case MessageConnector.CONSUME_DINGDING:
				sendMessageDingding(message);
				break;
			case MessageConnector.CONSUME_WELINK:
				sendMessageWeLink(message);
				break;
			case MessageConnector.CONSUME_ZHENGWUDINGDING:
				sendMessageZhengwuDingding(message);
				break;
			case MessageConnector.CONSUME_QIYEWEIXIN:
				sendMessageQiyeweixin(message);
				break;
			case MessageConnector.CONSUME_PMS_INNER:
				sendMessagePmsInner(message);
				break;
			case MessageConnector.CONSUME_MQ:
				sendMessageMq(message);
				break;
			case MessageConnector.CONSUME_MPWEIXIN:
				sendMessageMPWeixin(message);
				break;
			default:
				if (message.getConsumer().startsWith(MessageConnector.CONSUME_MQ)
						&& BooleanUtils.isTrue(Config.mq().getEnable())) {
					ThisApplication.mqConsumeQueue.send(message);
				}
				break;
			}
		}
	}

	private void sendMessageMPWeixin(Message message) throws Exception {
		if (BooleanUtils.isTrue(Config.mPweixin().getEnable())
				&& BooleanUtils.isTrue(Config.mPweixin().getMessageEnable())) {
			ThisApplication.mpWeixinConsumeQueue.send(message);
		}
	}

	private void sendMessageMq(Message message) throws Exception {
		if (BooleanUtils.isTrue(Config.mq().getEnable())) {
			ThisApplication.mqConsumeQueue.send(message);
		}
	}

	private void sendMessagePmsInner(Message message) throws Exception {
		if (BooleanUtils.isTrue(Config.pushConfig().getEnable())) {
			ThisApplication.pmsInnerConsumeQueue.send(message);
		}
	}

	private void sendMessageQiyeweixin(Message message) throws Exception {
		if (BooleanUtils.isTrue(Config.qiyeweixin().getEnable())
				&& BooleanUtils.isTrue(Config.qiyeweixin().getMessageEnable())) {
			ThisApplication.qiyeweixinConsumeQueue.send(message);
		}
	}

	private void sendMessageZhengwuDingding(Message message) throws Exception {
		if (BooleanUtils.isTrue(Config.zhengwuDingding().getEnable())
				&& BooleanUtils.isTrue(Config.zhengwuDingding().getMessageEnable())) {
			ThisApplication.zhengwuDingdingConsumeQueue.send(message);
		}
	}

	private void sendMessageWeLink(Message message) throws Exception {
		if (BooleanUtils.isTrue(Config.weLink().getEnable())
				&& BooleanUtils.isTrue(Config.weLink().getMessageEnable())) {
			ThisApplication.weLinkConsumeQueue.send(message);
		}
	}

	private void sendMessageDingding(Message message) throws Exception {
		if (BooleanUtils.isTrue(Config.dingding().getEnable())
				&& BooleanUtils.isTrue(Config.dingding().getMessageEnable())) {
			ThisApplication.dingdingConsumeQueue.send(message);
		}
	}

	private void sendMessageCalendar(Message message) throws Exception {
		if (BooleanUtils.isTrue(Config.communicate().calendarEnable())) {
			ThisApplication.calendarConsumeQueue.send(message);
		}
	}

	private void sendMessagePms(Message message) throws Exception {
		if (BooleanUtils.isTrue(Config.communicate().pmsEnable())) {
			ThisApplication.pmsConsumeQueue.send(message);
		}
	}

	private void sendMessageWs(Message message) throws Exception {
		if (BooleanUtils.isTrue(Config.communicate().wsEnable())) {
			ThisApplication.wsConsumeQueue.send(message);
		}
	}

	private Instant instant(Wi wi, List<String> consumers) {
		Instant instant = new Instant();
		instant.setBody(Objects.toString(wi.getBody()));
		instant.setType(wi.getType());
		instant.setPerson(wi.getPerson());
		instant.setTitle(wi.getTitle());
		instant.setConsumerList(consumers);
		instant.setConsumed(false);
		return instant;
	}

	private Message wsMessage(Wi wi, Instant instant) throws Exception {
		Message message = null;
		if (BooleanUtils.isTrue(Config.communicate().wsEnable())) {
			message = new Message();
			message.setBody(Objects.toString(wi.getBody()));
			message.setType(wi.getType());
			message.setPerson(wi.getPerson());
			message.setTitle(wi.getTitle());
			message.setConsumer(MessageConnector.CONSUME_WS);
			message.setConsumed(false);
			message.setInstant(instant.getId());
		}
		return message;
	}

	private Message pmsMessage(Wi wi, Instant instant) {
		Message message = null;
		try {
			if (BooleanUtils.isTrue(Config.communicate().pmsEnable())) {
				message = new Message();
				message.setBody(Objects.toString(wi.getBody()));
				message.setType(wi.getType());
				message.setPerson(wi.getPerson());
				message.setTitle(wi.getTitle());
				message.setConsumer(MessageConnector.CONSUME_PMS);
				message.setConsumed(false);
				message.setInstant(instant.getId());
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return message;
	}

	private Message pmsInnerMessage(Wi wi, Instant instant) {
		Message message = null;
		try {
			if (BooleanUtils.isTrue(Config.pushConfig().getEnable())) {
				message = new Message();
				message.setBody(Objects.toString(wi.getBody()));
				message.setType(wi.getType());
				message.setPerson(wi.getPerson());
				message.setTitle(wi.getTitle());
				message.setConsumer(MessageConnector.CONSUME_PMS_INNER);
				message.setConsumed(false);
				message.setInstant(instant.getId());
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return message;
	}

	private Message dingdingMessage(Wi wi, Instant instant) {
		Message message = null;
		try {
			if (BooleanUtils.isTrue(Config.dingding().getEnable())
					&& BooleanUtils.isTrue(Config.dingding().getMessageEnable())) {
				message = new Message();
				message.setBody(Objects.toString(wi.getBody()));
				message.setType(wi.getType());
				message.setPerson(wi.getPerson());
				message.setTitle(wi.getTitle());
				message.setConsumer(MessageConnector.CONSUME_DINGDING);
				message.setConsumed(false);
				message.setInstant(instant.getId());
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return message;
	}

	private Message weLinkMessage(Wi wi, Instant instant) {
		Message message = null;
		try {
			if (BooleanUtils.isTrue(Config.weLink().getEnable())
					&& BooleanUtils.isTrue(Config.weLink().getMessageEnable())) {
				message = new Message();
				message.setBody(Objects.toString(wi.getBody()));
				message.setType(wi.getType());
				message.setPerson(wi.getPerson());
				message.setTitle(wi.getTitle());
				message.setConsumer(MessageConnector.CONSUME_WELINK);
				message.setConsumed(false);
				message.setInstant(instant.getId());
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return message;
	}

	private Message zhengwudingdingMessage(Wi wi, Instant instant) {
		Message message = null;
		try {
			if (Config.zhengwuDingding().getEnable() && Config.zhengwuDingding().getMessageEnable()) {
				message = new Message();
				message.setBody(Objects.toString(wi.getBody()));
				message.setType(wi.getType());
				message.setPerson(wi.getPerson());
				message.setTitle(wi.getTitle());
				message.setConsumer(MessageConnector.CONSUME_ZHENGWUDINGDING);
				message.setConsumed(false);
				message.setInstant(instant.getId());
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return message;
	}

	private Message qiyeweixinMessage(Wi wi, Instant instant) {
		Message message = null;
		try {
			if (BooleanUtils.isTrue(Config.qiyeweixin().getEnable())
					&& BooleanUtils.isTrue(Config.qiyeweixin().getMessageEnable())) {
				message = new Message();
				message.setBody(Objects.toString(wi.getBody()));
				message.setType(wi.getType());
				message.setPerson(wi.getPerson());
				message.setTitle(wi.getTitle());
				message.setConsumer(MessageConnector.CONSUME_QIYEWEIXIN);
				message.setConsumed(false);
				message.setInstant(instant.getId());
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return message;
	}

	private Message mpweixinMessage(Wi wi, Instant instant) {
		Message message = null;
		try {
			if (BooleanUtils.isTrue(Config.mPweixin().getEnable())
					&& BooleanUtils.isTrue(Config.mPweixin().getMessageEnable())) {
				message = new Message();
				message.setBody(Objects.toString(wi.getBody()));
				message.setType(wi.getType());
				message.setPerson(wi.getPerson());
				message.setTitle(wi.getTitle());
				message.setConsumer(MessageConnector.CONSUME_MPWEIXIN);
				message.setConsumed(false);
				message.setInstant(instant.getId());
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return message;
	}

	private Message calendarMessage(Wi wi, Instant instant) {
		Message message = null;
		try {
			if (BooleanUtils.isTrue(Config.communicate().calendarEnable())) {
				message = new Message();
				message.setBody(Objects.toString(wi.getBody()));
				message.setType(wi.getType());
				message.setPerson(wi.getPerson());
				message.setTitle(wi.getTitle());
				message.setConsumer(MessageConnector.CONSUME_CALENDAR);
				message.setConsumed(false);
				message.setInstant(instant.getId());
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return message;
	}

	private Message mqMessage(Wi wi, Instant instant, String consumer) {
		Message message = null;
		if (consumer.startsWith(MessageConnector.CONSUME_MQ)) {
			message = new Message();
			message.setBody(Objects.toString(wi.getBody()));
			message.setType(wi.getType());
			message.setPerson(wi.getPerson());
			message.setTitle(wi.getTitle());
			if (StringUtils.isNotBlank(consumer)) {
				message.setConsumer(consumer);
			} else {
				message.setConsumer(MessageConnector.CONSUME_MQ);
			}
			message.setConsumed(false);
			message.setInstant(instant.getId());
		}
		return message;
	}

	private Message defaultMessage(Wi wi, String consumer, Instant instant) {
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
		private static final long serialVersionUID = 1L;

	}

	public static class Wo extends WrapBoolean {
		private static final long serialVersionUID = 1L;

	}

}
