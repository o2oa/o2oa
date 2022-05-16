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
import java.util.stream.Collectors;

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
import com.x.base.core.project.script.AbstractResources;
import com.x.base.core.project.scripting.JsonScriptingExecutor;
import com.x.base.core.project.scripting.ScriptingFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.webservices.WebservicesClient;
import com.x.message.assemble.communicate.ThisApplication;
import com.x.message.core.entity.Instant;
import com.x.message.core.entity.Message;
import com.x.organization.core.express.Organization;

class ActionCreate extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreate.class);

	private static ConcurrentMap<String, CompiledScript> scriptMap = new ConcurrentHashMap<>();

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> jsonElement);

		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		List<Consumer> consumers = Config.messages().getConsumers(wi.getType());
		consumers = consumers.stream().filter(Consumer::getEnable).collect(Collectors.toList());
		if (!ListTools.isEmpty(consumers)) {
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

	@Deprecated
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

	@Deprecated
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

	@Deprecated
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

	@Deprecated
	private Message assembleMessage(String consumer, Wi cpWi, Instant instant) throws Exception {
		Message message = null;
		String type = StringUtils.lowerCase(Objects.toString(consumer, ""));
		switch (type) {
		case MessageConnector.CONSUME_WS:
			message = this.wsMessage(cpWi, instant);
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
		case MessageConnector.CONSUME_MPWEIXIN:
			message = this.mpweixinMessage(cpWi, instant);
			break;
		default:
			break;
		}
		return message;

	}

	@Deprecated
	private void sendMessage(List<Message> messages) throws Exception {
		for (Message message : messages) {
			switch (StringUtils.lowerCase(message.getConsumer())) {
			case MessageConnector.CONSUME_WS:
				sendMessageWs(message);
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
			case MessageConnector.CONSUME_MPWEIXIN:
				sendMessageMPWeixin(message);
				break;
			default:
				break;
			}
		}
	}

	@Deprecated
	private void sendMessageMPWeixin(Message message) throws Exception {
		if (BooleanUtils.isTrue(Config.mPweixin().getEnable())
				&& BooleanUtils.isTrue(Config.mPweixin().getMessageEnable())) {
			ThisApplication.mpweixinConsumeQueue.send(message);
		}
	}

	@Deprecated
	private void sendMessagePmsInner(Message message) throws Exception {
		if (BooleanUtils.isTrue(Config.pushConfig().getEnable())) {
			ThisApplication.pmsinnerConsumeQueue.send(message);
		}

	}

	@Deprecated
	private void sendMessageQiyeweixin(Message message) throws Exception {
		if (BooleanUtils.isTrue(Config.qiyeweixin().getEnable())
				&& BooleanUtils.isTrue(Config.qiyeweixin().getMessageEnable())) {
			ThisApplication.qiyeweixinConsumeQueue.send(message);
		}
	}

	@Deprecated
	private void sendMessageZhengwuDingding(Message message) throws Exception {
		if (BooleanUtils.isTrue(Config.zhengwuDingding().getEnable())
				&& BooleanUtils.isTrue(Config.zhengwuDingding().getMessageEnable())) {
			ThisApplication.zhengwudingdingConsumeQueue.send(message);
		}
	}

	@Deprecated
	private void sendMessageWeLink(Message message) throws Exception {
		if (BooleanUtils.isTrue(Config.weLink().getEnable())
				&& BooleanUtils.isTrue(Config.weLink().getMessageEnable())) {
			ThisApplication.welinkConsumeQueue.send(message);
		}
	}

	@Deprecated
	private void sendMessageDingding(Message message) throws Exception {
		if (BooleanUtils.isTrue(Config.dingding().getEnable())
				&& BooleanUtils.isTrue(Config.dingding().getMessageEnable())) {
			ThisApplication.dingdingConsumeQueue.send(message);
		}
	}

	@Deprecated
	private void sendMessageCalendar(Message message) throws Exception {
		if (BooleanUtils.isTrue(Config.communicate().calendarEnable())) {
			ThisApplication.calendarConsumeQueue.send(message);
		}
	}

	@Deprecated
	private void sendMessageWs(Message message) throws Exception {
		if (BooleanUtils.isTrue(Config.communicate().wsEnable())) {
			ThisApplication.wsConsumeQueue.send(message);
		}
	}

	@Deprecated
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

	@Deprecated
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

	@Deprecated
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

	@Deprecated
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

	@Deprecated
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

	@Deprecated
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

	@Deprecated
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

	@Deprecated
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

	@Deprecated
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
		for (Consumer consumer : consumers) {
			if (BooleanUtils.isTrue(consumer.getEnable())) {
				Message message = this.v3AssembleMessage(wi, consumer, instant);
				if (message != null) {
					messages.add(message);
				}
			}
		}
		return messages;
	}

	private Message v3AssembleMessage(Wi wi, Consumer consumer, Instant instant) {
		Message message = null;
		String type = StringUtils.lowerCase(Objects.toString(consumer.getType(), ""));
		switch (type) {
		case MessageConnector.CONSUME_WS:
			message = v3WsMessage(wi, consumer);
			break;
		case MessageConnector.CONSUME_PMS_INNER:
			message = v3PmsInnerMessage(wi, consumer);
			break;
		case MessageConnector.CONSUME_DINGDING:
			message = this.v3DingdingMessage(wi, consumer);
			break;
		case MessageConnector.CONSUME_ZHENGWUDINGDING:
			message = this.v3ZhengwuDingdingMessage(wi, consumer);
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
		case MessageConnector.CONSUME_KAFKA:
			message = this.v3Message(wi, consumer);
			break;
		case MessageConnector.CONSUME_ACTIVEMQ:
			message = this.v3Message(wi, consumer);
			break;
		case MessageConnector.CONSUME_RESTFUL:
			message = this.v3Message(wi, consumer);
			break;
		case MessageConnector.CONSUME_API:
			message = this.v3Message(wi, consumer);
			break;
		case MessageConnector.CONSUME_MAIL:
			message = this.v3Message(wi, consumer);
			break;
		case MessageConnector.CONSUME_JDBC:
			message = this.v3Message(wi, consumer);
			break;
		case MessageConnector.CONSUME_TABLE:
			message = this.v3Message(wi, consumer);
			break;
		case MessageConnector.CONSUME_HADOOP:
			message = this.v3Message(wi, consumer);
			break;
		// custom_消息需要判断事件类型
		default:
			if (StringUtils.startsWith(type, MessageConnector.CONSUME_CUSTOM_PREFIX)) {
				message = this.v3Message(wi, consumer);
			}
			break;
		}
		if (null != message) {
			message.setInstant(instant.getId());
		}
		return message;
	}

	private Message v3Message(Wi wi, Consumer consumer) {
		if (!v3Filter(wi, consumer.getFilter())) {
			return null;
		}
		Message message = new Message();
		EvalMessage evalMessage = this.v3Load(wi, consumer.getLoader());
		if (null != evalMessage) {
			JsonElement jsonElement = evalMessage.getBody();
			// 如果脚本直接返回HTML或者text那么直接输出
			if (jsonElement.isJsonPrimitive()) {
				message.setBody(jsonElement.getAsJsonPrimitive().getAsString());
			} else {
				message.setBody(gson.toJson(evalMessage.getBody()));
			}
			message.setTitle(evalMessage.getTitle());
			message.setPerson(evalMessage.getPerson());
		} else {
			message.setBody(gson.toJson(wi.getBody()));
			message.setTitle(wi.getTitle());
			message.setPerson(wi.getPerson());
		}
		message.setType(wi.getType());
		message.setConsumed(false);
		message.setConsumer(consumer.getType());
		message.getProperties().setConsumerJsonElement(gson.toJsonTree(consumer));
		return message;
	}

	private Message v3WsMessage(Wi wi, Consumer consumer) {
		Message message = null;
		try {
			if (BooleanUtils.isTrue(Config.communicate().wsEnable())) {
				message = this.v3Message(wi, consumer);
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return message;
	}

	private Message v3PmsInnerMessage(Wi wi, Consumer consumer) {
		Message message = null;
		try {
			if (BooleanUtils.isTrue(Config.pushConfig().getEnable())) {
				message = this.v3Message(wi, consumer);
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
					&& BooleanUtils.isTrue(Config.dingding().getMessageEnable())) {
				message = this.v3Message(wi, consumer);
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return message;
	}

	private Message v3ZhengwuDingdingMessage(Wi wi, Consumer consumer) {
		Message message = null;
		try {
			if (Config.zhengwuDingding().getEnable() && Config.zhengwuDingding().getMessageEnable()) {
				message = this.v3Message(wi, consumer);
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
					&& BooleanUtils.isTrue(Config.qiyeweixin().getMessageEnable())) {
				message = this.v3Message(wi, consumer);
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
					&& BooleanUtils.isTrue(Config.weLink().getMessageEnable())) {
				message = this.v3Message(wi, consumer);
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
					&& BooleanUtils.isTrue(Config.mPweixin().getMessageEnable())) {
				message = this.v3Message(wi, consumer);
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return message;
	}

	private Message v3CalendarMessage(Wi wi, Consumer consumer) {
		Message message = null;
		try {
			if (BooleanUtils.isTrue(Config.communicate().calendarEnable())) {
				message = this.v3Message(wi, consumer);
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return message;
	}

	private boolean v3Filter(Wi wi, String filter) {
		try {
			if (StringUtils.isNotBlank(filter)) {
				CacheKey cacheKey = new CacheKey(this.getClass(), filter);
				Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
				CompiledScript compiledScript = null;
				if (optional.isPresent()) {
					compiledScript = (CompiledScript) optional.get();
				} else {
					Path path = Config.dir_config().toPath().resolve(filter);
					compiledScript = ScriptingFactory
							.functionalizationCompile(Files.readString(path, StandardCharsets.UTF_8));
					CacheManager.put(cacheCategory, cacheKey, compiledScript);
				}
				if (compiledScript != null) {
					ScriptContext scriptContext = ScriptingFactory.scriptContextEvalInitialServiceScript();
					Bindings bindings = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
					Resources resources = new Resources();
					resources.setContext(ThisApplication.context());
					resources.setOrganization(new Organization(ThisApplication.context()));
					resources.setApplications(ThisApplication.context().applications());
					resources.setWebservicesClient(new WebservicesClient());
					bindings.put(ScriptingFactory.BINDING_NAME_SERVICE_RESOURCES, resources);
					bindings.put(ScriptingFactory.BINDING_NAME_SERVICE_MESSAGE, gson.toJson(new EvalMessage(wi)));
					Boolean result = JsonScriptingExecutor.evalBoolean(compiledScript, scriptContext);
					boolean value = BooleanUtils.isTrue(result);
					LOGGER.debug("message type:{}, title:{}, person:{}, filter:{}, result:{}.", wi::getType,
							wi::getTitle, wi::getPerson, () -> filter, () -> value);
					return value;
				}
			}
		} catch (Exception e) {
			LOGGER.warn("执行filter脚本 {} 异常:{}.", () -> filter, e::getMessage);
		}
		return true;
	}

	private EvalMessage v3Load(Wi wi, String loader) {
		try {
			if (StringUtils.isNotBlank(loader)) {
				CacheKey cacheKey = new CacheKey(this.getClass(), loader);
				Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
				CompiledScript compiledScript = null;
				if (optional.isPresent()) {
					compiledScript = (CompiledScript) optional.get();
				} else {
					Path path = Config.dir_config().toPath().resolve(loader);
					compiledScript = ScriptingFactory
							.functionalizationCompile(Files.readString(path, StandardCharsets.UTF_8));
					CacheManager.put(cacheCategory, cacheKey, compiledScript);
				}
				if (compiledScript != null) {
					ScriptContext scriptContext = ScriptingFactory.scriptContextEvalInitialServiceScript();
					Bindings bindings = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
					Resources resources = new Resources();
					resources.setContext(ThisApplication.context());
					resources.setOrganization(new Organization(ThisApplication.context()));
					resources.setApplications(ThisApplication.context().applications());
					resources.setWebservicesClient(new WebservicesClient());
					bindings.put(ScriptingFactory.BINDING_NAME_SERVICE_RESOURCES, resources);
					bindings.put(ScriptingFactory.BINDING_NAME_SERVICE_MESSAGE, gson.toJson(new EvalMessage(wi)));
					return JsonScriptingExecutor.eval(compiledScript, scriptContext, EvalMessage.class);
				}
			}
		} catch (Exception e) {
			LOGGER.warn("执行 loader 脚本 {} 异常:{}.", () -> loader, e::getMessage);
		}
		return null;
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
			String type = StringUtils.lowerCase(message.getConsumer());
			switch (type) {
			case MessageConnector.CONSUME_WS:
				ThisApplication.wsConsumeQueue.send(message);
				break;
			case MessageConnector.CONSUME_CALENDAR:
				ThisApplication.calendarConsumeQueue.send(message);
				break;
			case MessageConnector.CONSUME_DINGDING:
				ThisApplication.dingdingConsumeQueue.send(message);
				break;
			case MessageConnector.CONSUME_WELINK:
				ThisApplication.welinkConsumeQueue.send(message);
				break;
			case MessageConnector.CONSUME_ZHENGWUDINGDING:
				ThisApplication.zhengwudingdingConsumeQueue.send(message);
				break;
			case MessageConnector.CONSUME_QIYEWEIXIN:
				ThisApplication.qiyeweixinConsumeQueue.send(message);
				break;
			case MessageConnector.CONSUME_PMS_INNER:
				ThisApplication.pmsinnerConsumeQueue.send(message);
				break;
			case MessageConnector.CONSUME_MPWEIXIN:
				ThisApplication.mpweixinConsumeQueue.send(message);
				break;
			case MessageConnector.CONSUME_KAFKA:
				ThisApplication.kafkaConsumeQueue.send(message);
				break;
			case MessageConnector.CONSUME_ACTIVEMQ:
				ThisApplication.activemqConsumeQueue.send(message);
				break;
			case MessageConnector.CONSUME_RESTFUL:
				ThisApplication.restfulConsumeQueue.send(message);
				break;
			case MessageConnector.CONSUME_MAIL:
				ThisApplication.mailConsumeQueue.send(message);
				break;
			case MessageConnector.CONSUME_API:
				ThisApplication.apiConsumeQueue.send(message);
				break;
			case MessageConnector.CONSUME_JDBC:
				ThisApplication.jdbcConsumeQueue.send(message);
				break;
			case MessageConnector.CONSUME_TABLE:
				ThisApplication.tableConsumeQueue.send(message);
				break;
			case MessageConnector.CONSUME_HADOOP:
				ThisApplication.hadoopConsumeQueue.send(message);
				break;
			default:
				break;
			}
		}
	}

	public static class EvalMessage {

		public EvalMessage(Wi wi) {
			this.title = wi.getTitle();
			this.person = wi.getPerson();
			this.body = wi.getBody();
			this.type = wi.getType();
		}

		public EvalMessage() {

		}

		private String person;

		private String title;

		private String type;

		private JsonElement body;

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public JsonElement getBody() {
			return body;
		}

		public void setBody(JsonElement body) {
			this.body = body;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

	}

	public static class Resources extends AbstractResources {
		private Organization organization;

		public Organization getOrganization() {
			return organization;
		}

		public void setOrganization(Organization organization) {
			this.organization = organization;
		}

	}

}
