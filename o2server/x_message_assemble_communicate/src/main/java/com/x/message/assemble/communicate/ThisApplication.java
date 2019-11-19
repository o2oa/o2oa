package com.x.message.assemble.communicate;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.Context;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.assemble.communicate.schedule.Clean;

public class ThisApplication {

	protected static Context context;

	public static WsConsumeQueue wsConsumeQueue = new WsConsumeQueue();

	public static PmsConsumeQueue pmsConsumeQueue = new PmsConsumeQueue();

	public static PmsInnerConsumeQueue pmsInnerConsumeQueue = new PmsInnerConsumeQueue();

	public static CalendarConsumeQueue calendarConsumeQueue = new CalendarConsumeQueue();

	public static QiyeweixinConsumeQueue qiyeweixinConsumeQueue = new QiyeweixinConsumeQueue();

	public static ZhengwuDingdingConsumeQueue zhengwuDingdingConsumeQueue = new ZhengwuDingdingConsumeQueue();

	public static DingdingConsumeQueue dingdingConsumeQueue = new DingdingConsumeQueue();

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			LoggerFactory.setLevel(Config.logLevel().x_message_assemble_communicate());
			wsConsumeQueue.start();
			pmsConsumeQueue.start();
			calendarConsumeQueue.start();
			if (BooleanUtils.isTrue(Config.qiyeweixin().getEnable())
					&& BooleanUtils.isTrue(Config.qiyeweixin().getMessageEnable())) {
				qiyeweixinConsumeQueue.start();
			}
			if (BooleanUtils.isTrue(Config.zhengwuDingding().getEnable())
					&& BooleanUtils.isTrue(Config.zhengwuDingding().getMessageEnable())) {
				zhengwuDingdingConsumeQueue.start();
			}
			if (BooleanUtils.isTrue(Config.dingding().getEnable())
					&& BooleanUtils.isTrue(Config.dingding().getMessageEnable())) {
				dingdingConsumeQueue.start();
			}
			if (BooleanUtils.isTrue(Config.communicate().clean().getEnable())) {
				context().schedule(Clean.class, Config.communicate().clean().getCron());
			}
			if (BooleanUtils.isTrue(Config.pushConfig().getEnable())) {
				pmsInnerConsumeQueue.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
			wsConsumeQueue.stop();
			pmsConsumeQueue.stop();
			calendarConsumeQueue.stop();
			if (BooleanUtils.isTrue(Config.qiyeweixin().getEnable())
					&& BooleanUtils.isTrue(Config.qiyeweixin().getMessageEnable())) {
				qiyeweixinConsumeQueue.stop();
			}
			if (BooleanUtils.isTrue(Config.zhengwuDingding().getEnable())
					&& BooleanUtils.isTrue(Config.zhengwuDingding().getMessageEnable())) {
				zhengwuDingdingConsumeQueue.stop();
			}
			if (BooleanUtils.isTrue(Config.dingding().getEnable())
					&& BooleanUtils.isTrue(Config.dingding().getMessageEnable())) {
				dingdingConsumeQueue.stop();
			}
			if (BooleanUtils.isTrue(Config.pushConfig().getEnable())) {
				pmsInnerConsumeQueue.stop();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
