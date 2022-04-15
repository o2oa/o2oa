package com.x.message.assemble.communicate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.Context;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.message.MessageConnector;
import com.x.message.assemble.communicate.schedule.Clean;
import com.x.message.assemble.communicate.schedule.TriggerMessageConsumeQueue;

public class ThisApplication {

	private ThisApplication() {
		// nothing
	}

	protected static Context context;

	public static void setContext(Context context) {
		ThisApplication.context = context;
	}

	public static final WsConsumeQueue wsConsumeQueue = new WsConsumeQueue();

	public static final CalendarConsumeQueue calendarConsumeQueue = new CalendarConsumeQueue();

	public static final QiyeweixinConsumeQueue qiyeweixinConsumeQueue = new QiyeweixinConsumeQueue();

	public static final ZhengwudingdingConsumeQueue zhengwudingdingConsumeQueue = new ZhengwudingdingConsumeQueue();

	public static final DingdingConsumeQueue dingdingConsumeQueue = new DingdingConsumeQueue();

	public static final WelinkConsumeQueue welinkConsumeQueue = new WelinkConsumeQueue();

	public static final PmsinnerConsumeQueue pmsinnerConsumeQueue = new PmsinnerConsumeQueue();

	public static final MpweixinConsumeQueue mpweixinConsumeQueue = new MpweixinConsumeQueue();

	public static final ActivemqConsumeQueue activemqConsumeQueue = new ActivemqConsumeQueue();

	public static final KafkaConsumeQueue kafkaConsumeQueue = new KafkaConsumeQueue();

	public static final RestfulConsumeQueue restfulConsumeQueue = new RestfulConsumeQueue();

	public static final MailConsumeQueue mailConsumeQueue = new MailConsumeQueue();

	public static final ApiConsumeQueue apiConsumeQueue = new ApiConsumeQueue();

	public static final JdbcConsumeQueue jdbcConsumeQueue = new JdbcConsumeQueue();

	public static final TableConsumeQueue tableConsumeQueue = new TableConsumeQueue();

	public static final HadoopConsumeQueue hadoopConsumeQueue = new HadoopConsumeQueue();

	private static final Map<Session, String> WSCLIENTS = new ConcurrentHashMap<>();

	public static Map<Session, String> wsClients() {
		return WSCLIENTS;
	}

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			CacheManager.init(context.clazz().getSimpleName());
			MessageConnector.start(context());
			startQueue();
			if (BooleanUtils.isTrue(Config.communicate().clean().getEnable())) {
				context().schedule(Clean.class, Config.communicate().clean().getCron());
			}
			if (BooleanUtils.isTrue(Config.communicate().triggerMessageConsumeQueue().getEnable())) {
				context().schedule(TriggerMessageConsumeQueue.class,
						Config.communicate().triggerMessageConsumeQueue().getCron());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void startQueue() throws Exception {
		context().startQueue(kafkaConsumeQueue);
		context().startQueue(activemqConsumeQueue);
		context().startQueue(restfulConsumeQueue);
		context().startQueue(apiConsumeQueue);
		context().startQueue(mailConsumeQueue);
		context().startQueue(jdbcConsumeQueue);
		context().startQueue(tableConsumeQueue);
		context().startQueue(hadoopConsumeQueue);
		if (BooleanUtils.isTrue(Config.qiyeweixin().getEnable())
				&& BooleanUtils.isTrue(Config.qiyeweixin().getMessageEnable())) {
			context().startQueue(qiyeweixinConsumeQueue);
		}
		if (BooleanUtils.isTrue(Config.zhengwuDingding().getEnable())
				&& BooleanUtils.isTrue(Config.zhengwuDingding().getMessageEnable())) {
			context().startQueue(zhengwudingdingConsumeQueue);
		}
		if (Config.dingding().getEnable() && Config.dingding().getMessageEnable()) {
			context().startQueue(dingdingConsumeQueue);
		}
		if (BooleanUtils.isTrue(Config.pushConfig().getEnable())) {
			context().startQueue(pmsinnerConsumeQueue);
		}
		if (Config.weLink().getEnable() && Config.weLink().getMessageEnable()) {
			context().startQueue(welinkConsumeQueue);
		}
		if (BooleanUtils.isTrue(Config.mPweixin().getEnable())
				&& BooleanUtils.isTrue(Config.mPweixin().getMessageEnable())) {
			context().startQueue(mpweixinConsumeQueue);
		}
		if (BooleanUtils.isTrue(Config.communicate().wsEnable())) {
			context().startQueue(wsConsumeQueue);
		}
		if (BooleanUtils.isTrue(Config.communicate().calendarEnable())) {
			context().startQueue(calendarConsumeQueue);
		}
	}

	public static void destroy() {
		try {
			CacheManager.shutdown();
			MessageConnector.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
