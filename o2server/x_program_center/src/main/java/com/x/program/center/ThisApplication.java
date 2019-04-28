package com.x.program.center;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.schedule.Area;
import com.x.program.center.schedule.CleanupCode;
import com.x.program.center.schedule.CleanupPromptErrorLog;
import com.x.program.center.schedule.CleanupSchedule;
import com.x.program.center.schedule.CleanupUnexpectedErrorLog;
import com.x.program.center.schedule.CleanupWarnLog;
import com.x.program.center.schedule.CollectLog;
import com.x.program.center.schedule.CollectPerson;
import com.x.program.center.schedule.DingdingSyncOrganization;
import com.x.program.center.schedule.DingdingSyncOrganizationTrigger;
import com.x.program.center.schedule.FireSchedule;
import com.x.program.center.schedule.QiyeweixinSyncOrganization;
import com.x.program.center.schedule.QiyeweixinSyncOrganizationTrigger;
import com.x.program.center.schedule.TriggerAgent;
import com.x.program.center.schedule.ZhengwuDingdingSyncOrganization;
import com.x.program.center.schedule.ZhengwuDingdingSyncOrganizationTrigger;

public class ThisApplication {

	protected static Context context;

	public static Context context() {
		return context;
	}

	public static ReportQueue reportQueue;

	public static LogQueue logQueue;

	public static CodeTransferQueue codeTransferQueue;

	public static List<Object> dingdingSyncOrganizationCallbackRequest = new ArrayList<>();

	public static List<Object> zhengwuDingdingSyncOrganizationCallbackRequest = new ArrayList<>();

	public static List<Object> qiyeweixinSyncOrganizationCallbackRequest = new ArrayList<>();

	public static void init() {
		try {
			LoggerFactory.setLevel(Config.logLevel().x_program_center());
			/* 启动报告队列 */
			reportQueue = new ReportQueue();
			context().startQueue(reportQueue);
			/* 启动日志队列 */
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
			context().scheduleLocal(com.x.program.center.schedule.CleanupApplications.class, 0, 5);
			context().scheduleLocal(FireSchedule.class, 180, 300);
			context().scheduleLocal(CleanupSchedule.class, 10, 80);
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

	}

}