package com.x.message.assemble.communicate;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.Context;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.message.assemble.communicate.schedule.Clean;
import com.x.message.assemble.communicate.schedule.TriggerMq;

public class ThisApplication {

	private ThisApplication() {
		// nothing
	}

	protected static Context context;

	public static final WsConsumeQueue wsConsumeQueue = new WsConsumeQueue();

	public static final PmsConsumeQueue pmsConsumeQueue = new PmsConsumeQueue();

	public static final CalendarConsumeQueue calendarConsumeQueue = new CalendarConsumeQueue();

	public static final QiyeweixinConsumeQueue qiyeweixinConsumeQueue = new QiyeweixinConsumeQueue();

	public static final ZhengwuDingdingConsumeQueue zhengwuDingdingConsumeQueue = new ZhengwuDingdingConsumeQueue();

	public static final DingdingConsumeQueue dingdingConsumeQueue = new DingdingConsumeQueue();

	public static final WeLinkConsumeQueue weLinkConsumeQueue = new WeLinkConsumeQueue();

	public static final PmsInnerConsumeQueue pmsInnerConsumeQueue = new PmsInnerConsumeQueue();

	public static final MQConsumeQueue mqConsumeQueue = new MQConsumeQueue();

	public static final MPWeixinConsumeQueue mpWeixinConsumeQueue = new MPWeixinConsumeQueue();

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			CacheManager.init(context.clazz().getSimpleName());
			LoggerFactory.setLevel(Config.logLevel().x_message_assemble_communicate());

			MessageConnector.start(context());
			startQueue();
			if (BooleanUtils.isTrue(Config.communicate().clean().getEnable())) {
				context().schedule(Clean.class, Config.communicate().clean().getCron());
			}
			if (BooleanUtils.isTrue(Config.communicate().cronMq().getEnable())) {
				context().schedule(TriggerMq.class, Config.communicate().cronMq().getCron());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void startQueue() throws Exception {
		if (BooleanUtils.isTrue(Config.communicate().wsEnable())) {
			context().startQueue(wsConsumeQueue);
		}
		if (BooleanUtils.isTrue(Config.communicate().pmsEnable())) {
			context().startQueue(pmsConsumeQueue);
		}
		if (BooleanUtils.isTrue(Config.communicate().calendarEnable())) {
			context().startQueue(calendarConsumeQueue);
		}

		if (BooleanUtils.isTrue(Config.qiyeweixin().getEnable())
				&& BooleanUtils.isTrue(Config.qiyeweixin().getMessageEnable())) {
			context().startQueue(qiyeweixinConsumeQueue);
		}
		if (BooleanUtils.isTrue(Config.zhengwuDingding().getEnable())
				&& BooleanUtils.isTrue(Config.zhengwuDingding().getMessageEnable())) {
			context().startQueue(zhengwuDingdingConsumeQueue);
		}
		if (Config.dingding().getEnable() && Config.dingding().getMessageEnable()) {
			context().startQueue(dingdingConsumeQueue);
		}
		if (BooleanUtils.isTrue(Config.pushConfig().getEnable())) {
			context().startQueue(pmsInnerConsumeQueue);
		}
		if (Config.weLink().getEnable() && Config.weLink().getMessageEnable()) {
			context().startQueue(weLinkConsumeQueue);
		}

		if (BooleanUtils.isTrue(Config.mq().getEnable())) {
			context().startQueue(mqConsumeQueue);
		}
		if (BooleanUtils.isTrue(Config.mPweixin().getEnable()) && BooleanUtils.isTrue(Config.mPweixin().getMessageEnable())) {
			context().startQueue(mpWeixinConsumeQueue);
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
