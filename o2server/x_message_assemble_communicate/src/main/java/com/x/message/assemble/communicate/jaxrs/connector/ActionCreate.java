package com.x.message.assemble.communicate.jaxrs.connector;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptContext;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Message.Consumer;
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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

class ActionCreate extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreate.class);
	private static ConcurrentMap<String, CompiledScript> scriptMap = new ConcurrentHashMap<>();

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> jsonElement);

		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if (BooleanUtils.isTrue(Config.messages().v3Enable())) {
			List<Consumer> consumers = Config.messages().getConsumersV3(wi.getType());
			Instant instant = v3instant(wi);
			List<Message> messages = v3Assemble(wi, instant, consumers);
			v3Save(instant, messages);
			this.v3SendMessage(messages);
		} else {
			Map<String, String> consumersV2 = Config.messages().getConsumersV2(wi.getType());
			Instant instant = this.instant(wi, new ArrayList<>(consumersV2.keySet()));
			List<Message> messages = assemble(wi, consumersV2, instant);
			save(instant, messages);
			this.sendMessage(messages);
		}
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

	private List<Message> assemble(Wi wi, Map<String, String> consumersV2, Instant instant) throws Exception {
		List<Message> messages = new ArrayList<>();
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
		return messages;
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
						eval = eval + "\r\n return " + func + "();";
						compiledScript = ScriptingFactory.functionalizationCompile(eval);
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
		ThisApplication.mqConsumeQueue.send(message);
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

	private Instant v3instant(Wi wi) {
		Instant instant = new Instant();
		instant.setBody(Objects.toString(wi.getBody()));
		instant.setType(wi.getType());
		instant.setPerson(wi.getPerson());
		instant.setTitle(wi.getTitle());
		instant.setConsumed(false);
		return instant;
	}

	private List<Message> v3Assemble(Wi wi, Instant instant, List<Consumer> consumers) {
		List<Message> messages = new ArrayList<>();
		if (!consumers.isEmpty()) {
			for (Consumer consumer : consumers) {
				if (BooleanUtils.isTrue(consumer.getEnable())) {
					Message message = this.v3AssembleMessage(wi, consumer, instant);
					if (message != null) {
						messages.add(message);
					}

				}
			}
		}
		return messages;
	}

	private Message v3AssembleMessage(Wi wi, Consumer consumer, Instant instant) {
		Message message = null;
		String type = Objects.toString(consumer.getType(), "");
		switch (type) {
		case MessageConnector.CONSUME_WS:
			message = this.v3WsMessage(wi, consumer);
			break;
		case MessageConnector.CONSUME_PMS:
			message = this.v3PmsMessage(wi, consumer);
			break;
		case MessageConnector.CONSUME_PMS_INNER:
			message = this.v3PmsInnerMessage(wi, consumer);
			break;
		case MessageConnector.CONSUME_DINGDING:
			message = this.v3DingdingMessage(wi, consumer);
			break;
		case MessageConnector.CONSUME_ZHENGWUDINGDING:
			message = this.v3ZhengwudingdingMessage(wi, consumer);
			break;
		case MessageConnector.CONSUME_QIYEWEIXIN:
			message = this.v3QiyeweixinMessage(wi, consumer);
			break;
		case MessageConnector.CONSUME_WELINK:
			message = this.v3WeLinkMessage(wi, consumer);
			break;
		case MessageConnector.CONSUME_MPWEIXIN:
			message = this.v3MpweixinMessage(wi, consumer);
			break;
		case MessageConnector.CONSUME_CALENDAR:
			message = this.v3CalendarMessage(wi, consumer);
			break;
		case MessageConnector.CONSUME_RESTFUL:
			message = this.v3Message(wi, consumer);
			break;
		case MessageConnector.CONSUME_MQ:
			message = this.v3Message(wi, consumer);
			break;
		case MessageConnector.CONSUME_API:
			message = this.v3Message(wi, consumer);
			break;
		case MessageConnector.CONSUME_MAIL:
			message = this.v3Message(wi, consumer);
			break;
		default:
			message = this.v3DefaultMessage(wi, consumer);
			break;
		}
		if (null != message) {
			message.setInstant(instant.getId());
		}
		return message;
	}

	private Message v3Message(Wi wi, Consumer consumer) {
		Message message = new Message();
		message.setBody(Objects.toString(v3load(wi, consumer)));
		message.setType(wi.getType());
		message.setPerson(wi.getPerson());
		message.setTitle(wi.getTitle());
		message.setConsumed(false);
		message.setConsumer(consumer.getType());
		message.setItem(consumer.getItem());
		return message;
	}

	private Message v3WsMessage(Wi wi, Consumer consumer) {
		Message message = null;
		try {
			if (BooleanUtils.isTrue(Config.communicate().wsEnable()) && BooleanUtils.isTrue(v3Filter(wi, consumer))) {
				message = v3Message(wi, consumer);
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return message;
	}

	private Message v3PmsMessage(Wi wi, Consumer consumer) {
		Message message = null;
		try {
			if (BooleanUtils.isTrue(Config.communicate().pmsEnable()) && BooleanUtils.isTrue(v3Filter(wi, consumer))) {
				message = v3Message(wi, consumer);
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return message;
	}

	private Message v3PmsInnerMessage(Wi wi, Consumer consumer) {
		Message message = null;
		try {
			if (BooleanUtils.isTrue(Config.pushConfig().getEnable()) && BooleanUtils.isTrue(v3Filter(wi, consumer))) {
				message = v3Message(wi, consumer);
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return message;
	}

	private Message v3DingdingMessage(Wi wi, Consumer consumer) {
		Message message = null;
		try {
			if (BooleanUtils.isTrue(Config.dingding().getEnable())
					&& BooleanUtils.isTrue(Config.dingding().getMessageEnable())
					&& BooleanUtils.isTrue(v3Filter(wi, consumer))) {
				message = v3Message(wi, consumer);
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return message;
	}

	private Message v3ZhengwudingdingMessage(Wi wi, Consumer consumer) {
		Message message = null;
		try {
			if (Config.zhengwuDingding().getEnable() && Config.zhengwuDingding().getMessageEnable()
					&& BooleanUtils.isTrue(v3Filter(wi, consumer))) {
				message = v3Message(wi, consumer);
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return message;
	}

	private Message v3QiyeweixinMessage(Wi wi, Consumer consumer) {
		Message message = null;
		try {
			if (BooleanUtils.isTrue(Config.qiyeweixin().getEnable())
					&& BooleanUtils.isTrue(Config.qiyeweixin().getMessageEnable())
					&& BooleanUtils.isTrue(v3Filter(wi, consumer))) {
				message = v3Message(wi, consumer);
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return message;
	}

	private Message v3WeLinkMessage(Wi wi, Consumer consumer) {
		Message message = null;
		try {
			if (BooleanUtils.isTrue(Config.weLink().getEnable())
					&& BooleanUtils.isTrue(Config.weLink().getMessageEnable())
					&& BooleanUtils.isTrue(v3Filter(wi, consumer))) {
				message = v3Message(wi, consumer);
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return message;
	}

	private Message v3MpweixinMessage(Wi wi, Consumer consumer) {
		Message message = null;
		try {
			if (BooleanUtils.isTrue(Config.mPweixin().getEnable())
					&& BooleanUtils.isTrue(Config.mPweixin().getMessageEnable())
					&& BooleanUtils.isTrue(v3Filter(wi, consumer))) {
				message = v3Message(wi, consumer);
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return message;
	}

	private Message v3CalendarMessage(Wi wi, Consumer consumer) {
		Message message = null;
		try {
			if (BooleanUtils.isTrue(Config.communicate().calendarEnable())
					&& BooleanUtils.isTrue(v3Filter(wi, consumer))) {
				message = v3Message(wi, consumer);
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return message;
	}

	private Message v3DefaultMessage(Wi wi, Consumer consumer) {
		return v3Message(wi, consumer);
	}

	private boolean v3Filter(Wi wi, Consumer consumer) {
		try {
			if (StringUtils.isNotBlank(consumer.getFilter())) {
				CacheKey cacheKey = new CacheKey(this.getClass(), consumer.getFilter());
				Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
				CompiledScript compiledScript = null;
				if (optional.isPresent()) {
					compiledScript = (CompiledScript) optional.get();
				} else {
					Path path = Config.dir_config().toPath().resolve(consumer.getFilter());
					compiledScript = ScriptingFactory
							.functionalizationCompile(Files.readString(path, StandardCharsets.UTF_8));
					CacheManager.put(cacheCategory, cacheKey, compiledScript);
				}
				if (compiledScript != null) {
					ScriptContext scriptContext = ScriptingFactory.scriptContextEvalInitialServiceScript();
					Bindings bindings = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
					bindings.put(ScriptingFactory.BINDING_NAME_SERVICE_MESSAGE, wi.getBody());
					Boolean filter = JsonScriptingExecutor.evalBoolean(compiledScript, scriptContext);
					return BooleanUtils.isTrue(filter);
				}
			}
		} catch (Exception e) {
			LOGGER.warn("执行filter脚本 {} 异常:{}.", consumer.getLoader(), e.getMessage());
		}
		return true;
	}

	private JsonElement v3load(Wi wi, Consumer consumer) {
		JsonElement jsonElement = wi.getBody();
		try {
			if (StringUtils.isNotBlank(consumer.getLoader())) {
				CacheKey cacheKey = new CacheKey(this.getClass(), consumer.getLoader());
				Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
				CompiledScript compiledScript = null;
				if (optional.isPresent()) {
					compiledScript = (CompiledScript) optional.get();
				} else {
					Path path = Config.dir_config().toPath().resolve(consumer.getLoader());
					compiledScript = ScriptingFactory
							.functionalizationCompile(Files.readString(path, StandardCharsets.UTF_8));
					CacheManager.put(cacheCategory, cacheKey, compiledScript);
				}
				if (compiledScript != null) {
					ScriptContext scriptContext = ScriptingFactory.scriptContextEvalInitialServiceScript();
					Bindings bindings = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
					bindings.put(ScriptingFactory.BINDING_NAME_SERVICE_MESSAGE, wi.getBody());
					jsonElement = JsonScriptingExecutor.jsonElement(compiledScript, scriptContext);
				}
			}
		} catch (Exception e) {
			LOGGER.warn("执行loader脚本 {} 异常:{}.", consumer.getLoader(), e.getMessage());
		}
		return jsonElement;
	}

	private void v3Save(Instant instant, List<Message> messages) throws Exception {
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

	private void v3SendMessage(List<Message> messages) throws Exception {
		for (Message message : messages) {
			switch (message.getConsumer()) {
			case MessageConnector.CONSUME_WS:
				ThisApplication.wsConsumeQueue.send(message);
				break;
			case MessageConnector.CONSUME_PMS:
				ThisApplication.pmsConsumeQueue.send(message);
				break;
			case MessageConnector.CONSUME_CALENDAR:
				ThisApplication.calendarConsumeQueue.send(message);
				break;
			case MessageConnector.CONSUME_DINGDING:
				ThisApplication.dingdingConsumeQueue.send(message);
				break;
			case MessageConnector.CONSUME_WELINK:
				ThisApplication.weLinkConsumeQueue.send(message);
				break;
			case MessageConnector.CONSUME_ZHENGWUDINGDING:
				ThisApplication.zhengwuDingdingConsumeQueue.send(message);
				break;
			case MessageConnector.CONSUME_QIYEWEIXIN:
				ThisApplication.qiyeweixinConsumeQueue.send(message);
				break;
			case MessageConnector.CONSUME_PMS_INNER:
				ThisApplication.pmsInnerConsumeQueue.send(message);
				break;
			case MessageConnector.CONSUME_MPWEIXIN:
				ThisApplication.mpWeixinConsumeQueue.send(message);
				break;
			case MessageConnector.CONSUME_RESTFUL:
				ThisApplication.restfulConsumeQueue.send(message);
				break;
			case MessageConnector.CONSUME_MQ:
				ThisApplication.mqConsumeQueue.send(message);
				break;
			case MessageConnector.CONSUME_MAIL:
				ThisApplication.mailConsumeQueue.send(message);
				break;
			case MessageConnector.CONSUME_API:
				ThisApplication.apiConsumeQueue.send(message);
				break;
			default:
				break;
			}
		}
	}

}
