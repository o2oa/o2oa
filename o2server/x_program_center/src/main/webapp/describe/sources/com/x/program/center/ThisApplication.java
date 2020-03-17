package com.x.program.center;

import com.google.gson.internal.LinkedTreeMap;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.schedule.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThisApplication {

	protected static Context context;

	public static Context context() {
		return context;
	}

	public static CenterQueue centerQueue = new CenterQueue();

	public static LogQueue logQueue;

	// public static CodeTransferQueue codeTransferQueue;

	public static List<Object> dingdingSyncOrganizationCallbackRequest = new ArrayList<>();

	public static List<Object> zhengwuDingdingSyncOrganizationCallbackRequest = new ArrayList<>();

	public static List<Object> qiyeweixinSyncOrganizationCallbackRequest = new ArrayList<>();

	public static Map<String, Map<String, LinkedTreeMap>> metricsReportMap = new ConcurrentHashMap<>();

	public static void init() {
		try {
			LoggerFactory.setLevel(Config.logLevel().x_program_center());
			/* 20190927新报告机制 */
			context().startQueue(centerQueue);
			logQueue = new LogQueue();
			context().startQueue(logQueue);

			/* 政务钉钉拉入同步 */
			if (Config.zhengwuDingding().getEnable()) {
				/* 启动同步任务 */
				context().scheduleLocal(ZhengwuDingdingSyncOrganization.class, Config.zhengwuDingding().getSyncCron());
				/* 添加一个强制同步任务 */
				context().scheduleLocal(ZhengwuDingdingSyncOrganizationTrigger.class,
						Config.zhengwuDingding().getForceSyncCron());
			}
			/* 企业微信拉入同步 */
			if (Config.qiyeweixin().getEnable()) {
				/* 启动同步任务 */
				context().scheduleLocal(QiyeweixinSyncOrganization.class, Config.qiyeweixin().getSyncCron());
				/* 添加一个强制同步任务 */
				context().scheduleLocal(QiyeweixinSyncOrganizationTrigger.class,
						Config.qiyeweixin().getForceSyncCron());
			}
			/* 钉钉同步 */
			if (Config.dingding().getEnable()) {
				/* 启动同步任务 */
				context().scheduleLocal(DingdingSyncOrganization.class, Config.dingding().getSyncCron());
				/* 添加一个强制同步任务 */
				context().scheduleLocal(DingdingSyncOrganizationTrigger.class, Config.dingding().getForceSyncCron());
			}
			context().scheduleLocal(RefreshApplications.class, CenterQueue.REFRESHAPPLICATIONSINTERVAL,
					CenterQueue.REFRESHAPPLICATIONSINTERVAL);
			context().scheduleLocal(FireSchedule.class, 180, 300);
			context().scheduleLocal(CleanupScheduleLog.class, 10, 80);
			context().scheduleLocal(CleanupCode.class, 10, 60 * 30);
			context().scheduleLocal(CleanupPromptErrorLog.class, 10, 60 * 30);
			context().scheduleLocal(CleanupUnexpectedErrorLog.class, 10, 60 * 30);
			context().scheduleLocal(CleanupWarnLog.class, 10, 60 * 30);
			context().scheduleLocal(CollectPerson.class, 10, 60 * 30);
			context().scheduleLocal(CollectLog.class, 10, 60 * 30);
			context().scheduleLocal(TriggerAgent.class, 150, 60);
			/* 行政区域每周更新一次 */
			context().scheduleLocal(Area.class, 300, 60 * 60 * 24);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
			centerQueue.stop();
			logQueue.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}