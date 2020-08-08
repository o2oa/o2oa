package com.x.message.assemble.communicate.jaxrs.connector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.script.ScriptFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.message.assemble.communicate.Business;
import com.x.message.assemble.communicate.ThisApplication;
import com.x.message.core.entity.Instant;
import com.x.message.core.entity.Message;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.SimpleScriptContext;

class ActionCreate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCreate.class);
	private static ConcurrentMap<String,CompiledScript> scriptMap = new ConcurrentHashMap<>();

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		List<Message> messages = new ArrayList<>();
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			List<String> consumers = Config.messages().getConsumers(wi.getType());
			Map<String,String> consumersV2 = Config.messages().getConsumersV2(wi.getType());
			for(String consumer: consumers){
				if(!consumersV2.containsKey(consumer)){
					consumersV2.put(consumer,"");
				}
			}
			Instant instant = this.instant(effectivePerson, business, wi, new ArrayList<>(consumersV2.keySet()));
			if (!consumersV2.isEmpty()) {
				for (String consumer : consumersV2.keySet()) {
					Wi cpwi = wi;
					String func = consumersV2.get(consumer);
					try {
						if(StringUtils.isNoneBlank(func)){
							cpwi = (Wi)BeanUtils.cloneBean(wi);
							JsonObject body = cpwi.getBody().deepCopy().getAsJsonObject();
							CompiledScript compiledScript = scriptMap.get(func);
							if(compiledScript == null) {
								String eval = Config.messageSendRuleScript();
								if(StringUtils.isNotEmpty(eval)) {
									eval = "function" + StringUtils.substringAfter(eval, "function") + " " + func + "();";
									compiledScript = ScriptFactory.compile(eval);
									scriptMap.put(func, compiledScript);
								}
							}
							if(compiledScript != null) {
								ScriptContext scriptContext = new SimpleScriptContext();
								Bindings bindings = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
								bindings.put("body", body);
								Object o = compiledScript.eval(scriptContext);
								cpwi.setBody(body);
								if (o != null) {
									if (o instanceof Boolean) {
										if (!((Boolean) o).booleanValue()) {
											logger.info("消息类型{}.{}的消息[{}]不满足发送条件，跳过...", wi.getType(), consumer, wi.getTitle());
											continue;
										}
									}
								}
							}
						}
					} catch (Exception e) {
						logger.warn("执行消息发送脚本[{}]方法异常:{}", func, e.getMessage());
					}
					Message message = null;
					switch (Objects.toString(consumer, "")) {
					case MessageConnector.CONSUME_WS:
						message = this.wsMessage(effectivePerson, business, cpwi, instant);
						break;
					case MessageConnector.CONSUME_PMS:
						message = this.pmsMessage(effectivePerson, business, cpwi, instant);
						break;
					case MessageConnector.CONSUME_PMS_INNER:
						message = this.pmsInnerMessage(effectivePerson, business, cpwi, instant);
						break;
					case MessageConnector.CONSUME_DINGDING:
						message = this.dingdingMessage(effectivePerson, business, cpwi, instant);
						break;
					case MessageConnector.CONSUME_ZHENGWUDINGDING:
						message = this.zhegnwudingdingMessage(effectivePerson, business, cpwi, instant);
						break;
					case MessageConnector.CONSUME_QIYEWEIXIN:
						message = this.qiyeweixinMessage(effectivePerson, business, cpwi, instant);
						break;
					case MessageConnector.CONSUME_CALENDAR:
						message = this.calendarMessage(effectivePerson, business, cpwi, instant);
						break;
					case MessageConnector.CONSUME_EMAIL:
						message = this.emailMessage(effectivePerson, business, cpwi, instant);
						break;
					default:
						message = this.defaultMessage(effectivePerson, business, cpwi, consumer, instant);
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
				if (Config.communicate().wsEnable()) {
					ThisApplication.wsConsumeQueue.send(message);
				}
				break;
			case MessageConnector.CONSUME_PMS:
				if (Config.communicate().pmsEnable()) {
					ThisApplication.pmsConsumeQueue.send(message);
				}
				break;
			case MessageConnector.CONSUME_CALENDAR:
				if (Config.communicate().calendarEnable()) {
					ThisApplication.calendarConsumeQueue.send(message);
				}
				break;
			case MessageConnector.CONSUME_DINGDING:
				if (Config.dingding().getEnable() && Config.dingding().getMessageEnable()) {
					ThisApplication.dingdingConsumeQueue.send(message);
				}
				break;
			case MessageConnector.CONSUME_ZHENGWUDINGDING:
				if (Config.zhengwuDingding().getEnable() && Config.zhengwuDingding().getMessageEnable()) {
					ThisApplication.zhengwuDingdingConsumeQueue.send(message);
				}
				break;
			case MessageConnector.CONSUME_QIYEWEIXIN:
				if (Config.qiyeweixin().getEnable() && Config.qiyeweixin().getMessageEnable()) {
					ThisApplication.qiyeweixinConsumeQueue.send(message);
				}
				break;
			case MessageConnector.CONSUME_PMS_INNER:
				if (Config.pushConfig().getEnable()) {
					ThisApplication.pmsInnerConsumeQueue.send(message);
				}
				break;
			case MessageConnector.CONSUME_EMAIL:
				if (Config.emailNotification().getEnable()) {
					ThisApplication.emailConsumeQueue.send(message);
				}
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

	private Message emailMessage(EffectivePerson effectivePerson, Business business, Wi wi, Instant instant) {
		Message message = new Message();
		message.setBody(Objects.toString(wi.getBody()));
		message.setType(wi.getType());
		message.setPerson(wi.getPerson());
		message.setTitle(wi.getTitle());
		message.setConsumer(MessageConnector.CONSUME_EMAIL);
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
