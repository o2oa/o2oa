package com.x.message.assemble.communicate.jaxrs.connector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.graalvm.polyglot.Source;

import com.google.gson.JsonElement;
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
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.webservices.WebservicesClient;
import com.x.message.assemble.communicate.ThisApplication;
import com.x.message.core.entity.Instant;
import com.x.message.core.entity.Message;
import com.x.organization.core.express.Organization;

class ActionCreate extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> jsonElement);

		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		List<Consumer> consumers = Config.messages().getConsumers(wi.getType());
		consumers = consumers.stream().filter(Consumer::getEnable).collect(Collectors.toList());
		Instant instant = v3instant(wi);
		List<Message> messages = v3Assemble(wi, instant, consumers);
		v3Save(instant, messages);
		this.v3SendMessage(messages);
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	public static class Wi extends MessageConnector.Wrap {

		private static final long serialVersionUID = 4821344908870350699L;

	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 7383440916383187868L;

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
		case MessageConnector.CONSUME_ANDFX:
			message = this.v3AndFxMessage(wi, consumer);
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
			message = this.v3Message(wi, consumer);
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

	private Message v3AndFxMessage(Wi wi, Consumer consumer) {
		Message message = null;
		try {
			if (BooleanUtils.isTrue(Config.andFx().getEnable())
					&& BooleanUtils.isTrue(Config.andFx().getMessageEnable())) {
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
			if (BooleanUtils.isTrue(Config.mpweixin().getEnable())
					&& BooleanUtils.isTrue(Config.mpweixin().getMessageEnable())) {
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
			message = this.v3Message(wi, consumer);
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return message;
	}

	private boolean v3Filter(Wi wi, String filter) {
		try {
			if (StringUtils.isNotBlank(filter)) {
				CacheKey cacheKey = new CacheKey(this.getClass(), "filter:" + filter);
				Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
				Source source = null;
				if (optional.isPresent()) {
					source = (Source) optional.get();
				} else {
					String text = Config.messages().getFilter(filter);
					if (StringUtils.isNotBlank(text)) {
						source = GraalvmScriptingFactory.functionalization(StringEscapeUtils.unescapeJson(text));
						CacheManager.put(cacheCategory, cacheKey, source);
					}
				}
				if (source != null) {
//					ScriptContext scriptContext = ScriptingFactory.scriptContextEvalInitialServiceScript();
//					Bindings bindings = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
					Resources resources = new Resources();
					resources.setContext(ThisApplication.context());
					resources.setOrganization(new Organization(ThisApplication.context()));
					resources.setApplications(ThisApplication.context().applications());
					resources.setWebservicesClient(new WebservicesClient());
					GraalvmScriptingFactory.Bindings bindings = new GraalvmScriptingFactory.Bindings()
							.putMember(GraalvmScriptingFactory.BINDING_NAME_SERVICE_RESOURCES, resources)
							.putMember(GraalvmScriptingFactory.BINDING_NAME_SERVICE_MESSAGE,
									gson.toJson(new EvalMessage(wi)));
					Optional<Boolean> opt = GraalvmScriptingFactory.evalAsBoolean(source, bindings);
					AtomicBoolean value = new AtomicBoolean();
					value.set(false);
					if (opt.isPresent()) {
						value.set(BooleanUtils.isTrue(opt.get()));
					}
					LOGGER.debug("message type:{}, title:{}, person:{}, filter:{}, result:{}.", wi::getType,
							wi::getTitle, wi::getPerson, () -> filter, () -> value.get());
					return value.get();
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
				CacheKey cacheKey = new CacheKey(this.getClass(), "loader:" + loader);
				Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
				Source source = null;
				if (optional.isPresent()) {
					source = (Source) optional.get();
				} else {
					String text = Config.messages().getLoader(loader);
					if (StringUtils.isNotBlank(text)) {
						source = GraalvmScriptingFactory.functionalization(StringEscapeUtils.unescapeJson(text));
						CacheManager.put(cacheCategory, cacheKey, source);
					}
				}
				if (source != null) {
					Resources resources = new Resources();
					resources.setContext(ThisApplication.context());
					resources.setOrganization(new Organization(ThisApplication.context()));
					resources.setApplications(ThisApplication.context().applications());
					resources.setWebservicesClient(new WebservicesClient());
					GraalvmScriptingFactory.Bindings bindings = new GraalvmScriptingFactory.Bindings()
							.putMember(GraalvmScriptingFactory.BINDING_NAME_SERVICE_RESOURCES, resources)
							.putMember(GraalvmScriptingFactory.BINDING_NAME_SERVICE_MESSAGE,
									gson.toJson(new EvalMessage(wi)));
					return GraalvmScriptingFactory.eval(source, bindings, EvalMessage.class);
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
			case MessageConnector.CONSUME_ANDFX:
				ThisApplication.andFxConsumeQueue.send(message);
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
