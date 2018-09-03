package com.x.program.center;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.ZhengwuDingding;
import com.x.program.center.schedule.CleanupCode;
import com.x.program.center.schedule.CleanupPromptErrorLog;
import com.x.program.center.schedule.CleanupSchedule;
import com.x.program.center.schedule.CleanupUnexpectedErrorLog;
import com.x.program.center.schedule.CleanupWarnLog;
import com.x.program.center.schedule.CollectLog;
import com.x.program.center.schedule.CollectPerson;
import com.x.program.center.schedule.FireSchedule;
import com.x.program.center.schedule.TriggerAgent;
import com.x.program.center.schedule.ZhengwuDingdingPullSyncOrganization;
import com.x.program.center.schedule.ZhengwuDingdingPullSyncOrganizationInit;
import com.x.program.center.schedule.ZhengwuDingdingPullSyncOrganizationTrigger;

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
			/* 启动报告队列 */
			reportQueue = new ReportQueue();
			context().startQueue(reportQueue);
			/* 启动日志队列 */
			logQueue = new LogQueue();
			context().startQueue(logQueue);

			// /* 如果使用钉钉拉入同步 */
			// if (StringUtils.equals(Dingding.SYNCORGANIZATIONDIRECTION_PULL,
			// Config.dingding().getSyncOrganizationDirection())) {
			// context().scheduleLocal(DingdingPullSyncOrganization.class, 150,
			// Config.dingding().getPullSyncOrganizationInterval() * 60);
			// context().scheduleLocal(DingdingPullSyncOrganizationTrigger.class, 180,
			// Config.dingding().getForcePullSyncOrganizationInterval() * 60);
			// }
			/* 政务钉钉拉入同步 */
			if (StringUtils.equals(ZhengwuDingding.SYNCORGANIZATIONDIRECTION_PULL,
					Config.zhengwuDingding().getSyncOrganizationDirection())) {
				/* 启动同步任务 */
				context().scheduleLocal(ZhengwuDingdingPullSyncOrganization.class,
						Config.zhengwuDingding().getPullCron());
				/* 添加一个强制同步任务 */
				context().scheduleLocal(ZhengwuDingdingPullSyncOrganizationTrigger.class,
						Config.zhengwuDingding().getForcePullCron());
				/* 初始化,注册回调 */
				context().scheduleLocal(ZhengwuDingdingPullSyncOrganizationInit.class);
			}
			// /* 如果使用企业微信拉入同步 */
			// if (StringUtils.equals(Qiyeweixin.SYNCORGANIZATIONDIRECTION_PULL,
			// Config.qiyeweixin().getSyncOrganizationDirection())) {
			// context().scheduleLocal(QiyeweixinPullSyncOrganization.class, 150,
			// Config.qiyeweixin().getPullSyncOrganizationInterval() * 60);
			// context().scheduleLocal(QiyeweixinPullSyncOrganizationTrigger.class, 180,
			// Config.qiyeweixin().getForcePullSyncOrganizationInterval() * 60);
			// }
			context().scheduleLocal(com.x.program.center.schedule.CleanupApplications.class, 0, 5);
			context().scheduleLocal(FireSchedule.class, 180, 300);
			context().scheduleLocal(CleanupSchedule.class, 10, 80);
			context().scheduleLocal(CleanupCode.class, 10, 60 * 30);
			context().scheduleLocal(CleanupPromptErrorLog.class, 10, 60 * 30);
			context().scheduleLocal(CleanupUnexpectedErrorLog.class, 10, 60 * 30);
			context().scheduleLocal(CleanupWarnLog.class, 10, 60 * 30);
			context().scheduleLocal(CollectPerson.class, 10, 60 * 30);
			context().scheduleLocal(CollectLog.class, 10, 60 * 30);
			context().scheduleLocal(TriggerAgent.class, 150, 5);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {

	}

}