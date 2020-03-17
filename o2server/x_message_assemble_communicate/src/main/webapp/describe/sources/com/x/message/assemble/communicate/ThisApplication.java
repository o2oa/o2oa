package com.x.message.assemble.communicate;

import com.x.base.core.project.message.MessageConnector;
import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.Context;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.assemble.communicate.schedule.Clean;

public class ThisApplication {

	protected static Context context;

	public static WsConsumeQueue wsConsumeQueue = new WsConsumeQueue();

	public static PmsConsumeQueue pmsConsumeQueue = new PmsConsumeQueue();

	public static CalendarConsumeQueue calendarConsumeQueue = new CalendarConsumeQueue();

	public static QiyeweixinConsumeQueue qiyeweixinConsumeQueue = new QiyeweixinConsumeQueue();

	public static ZhengwuDingdingConsumeQueue zhengwuDingdingConsumeQueue = new ZhengwuDingdingConsumeQueue();

	public static DingdingConsumeQueue dingdingConsumeQueue = new DingdingConsumeQueue();

	public static PmsInnerConsumeQueue pmsInnerConsumeQueue = new PmsInnerConsumeQueue();

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			LoggerFactory.setLevel(Config.logLevel().x_message_assemble_communicate());
			if (Config.communicate().wsEnable()) {
				wsConsumeQueue.start();
			}
			if (Config.communicate().pmsEnable()) {
				pmsConsumeQueue.start();
			}
			if (Config.communicate().calendarEnable()) {
				calendarConsumeQueue.start();
			}
			if (BooleanUtils.isTrue(Config.communicate().clean().getEnable())) {
				context().schedule(Clean.class, Config.communicate().clean().getCron());
			}
			if (BooleanUtils.isTrue(Config.qiyeweixin().getEnable())
					&& BooleanUtils.isTrue(Config.qiyeweixin().getMessageEnable())) {
				qiyeweixinConsumeQueue.start();
			}
			if (BooleanUtils.isTrue(Config.zhengwuDingding().getEnable())
					&& BooleanUtils.isTrue(Config.zhengwuDingding().getMessageEnable())) {
				zhengwuDingdingConsumeQueue.start();
			}
			if (Config.dingding().getEnable() && Config.dingding().getMessageEnable()) {
				dingdingConsumeQueue.start();
			}
			if (BooleanUtils.isTrue(Config.pushConfig().getEnable())) {
				pmsInnerConsumeQueue.start();
			}

			MessageConnector.start(context());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
			wsConsumeQueue.stop();
			pmsConsumeQueue.stop();
			calendarConsumeQueue.stop();
			qiyeweixinConsumeQueue.stop();
			zhengwuDingdingConsumeQueue.stop();
			dingdingConsumeQueue.stop();
			pmsInnerConsumeQueue.stop();
			MessageConnector.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
