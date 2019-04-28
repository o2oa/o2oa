package com.x.message.assemble.communicate;

import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.Context;
import com.x.base.core.project.config.Config;

public class ThisApplication {

	protected static Context context;

	public static ImConsumeQueue imConsumeQueue = new ImConsumeQueue();

	public static PmsConsumeQueue pmsConsumeQueue = new PmsConsumeQueue();

	public static CalendarConsumeQueue calendarConsumeQueue = new CalendarConsumeQueue();

	public static QiyeweixinConsumeQueue qiyeweixinConsumeQueue = new QiyeweixinConsumeQueue();

	public static ZhengwuDingdingConsumeQueue zhengwuDingdingConsumeQueue = new ZhengwuDingdingConsumeQueue();

	public static DingdingConsumeQueue dingdingConsumeQueue = new DingdingConsumeQueue();

	public static final ConcurrentHashMap<String, Session> connections = new ConcurrentHashMap<>();

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			imConsumeQueue.start();
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
			imConsumeQueue.stop();
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
