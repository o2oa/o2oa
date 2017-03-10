package com.x.okr.assemble.control;

import java.util.HashMap;
import java.util.Map;

import com.x.base.core.project.AbstractThisApplication;
import com.x.base.core.project.ReportTask;
import com.x.collaboration.core.message.Collaboration;
import com.x.okr.assemble.control.service.OkrConfigSystemService;
import com.x.okr.assemble.control.servlet.workimport.CacheImportFileStatus;
import com.x.okr.assemble.control.timertask.St_CenterWorkCount;
import com.x.okr.assemble.control.timertask.St_WorkReportContent;
import com.x.okr.assemble.control.timertask.St_WorkReportStatus;
import com.x.okr.assemble.control.timertask.WorkProgressConfirm;
import com.x.okr.assemble.control.timertask.WorkReportCollectCreate;
import com.x.okr.assemble.control.timertask.WorkReportCreate;

public class ThisApplication extends AbstractThisApplication {

	public static Map<String, CacheImportFileStatus> importFileStatusMap = new HashMap<String, CacheImportFileStatus>();
	private static Boolean centerWorkCountStatisticTaskRunning = false;
	private static Boolean workProgressConfirmTaskRunning = false;
	private static Boolean workReportCollectCreateTaskRunning = false;
	private static Boolean workReportCreateTaskRunning = false;
	private static Boolean workReportStatisticTaskRunning = false;
	private static Boolean workReportSubmitStatisticTaskRunning = false;

	public static void init() throws Exception {
		/* 启动报告任务 */
		timerWithFixedDelay(new ReportTask(), 1, 20);

		initDatasFromCenters();

		initStoragesFromCenters();

		Collaboration.start();

		initAllSystemConfig();

		initAllTimerTask();

	}

	private static void initAllTimerTask() throws Exception {

		// LoggerFactory.getLogger( ThisApplication.class ).info("OKR registing
		// timer task：对中心工作的工作总数进行统计分析.");
		// 注册定时器：定时代理，对中心工作的工作总数进行统计分析。
		// 运行时间设置：启动后1分钟执行第一次，之后间隔30分钟执行
		timerWithFixedDelay(new St_CenterWorkCount(), 60 * 15, 60 * 60);
		// LoggerFactory.getLogger( ThisApplication.class ).info("OKR registing
		// timer task：定时对需要汇报的工作发起工作汇报拟稿的待办.");
		// 注册定时器：定时代理，定时对需要汇报的工作发起工作汇报拟稿的待办
		// 运行时间设置：启动后1分钟执行第一次，之后间隔1分钟执行
		timerWithFixedDelay(new WorkReportCreate(), 60 * 25, 60 * 60);
		// LoggerFactory.getLogger( ThisApplication.class ).info("OKR registing
		// timer task：定时分析所有未完成工作的进度情况.");
		// 注册定时器：定时代理，定时分析所有未完成工作的进度情况
		// 运行时间设置：启动后10分钟执行第一次，之后间隔6小时执行
		timerWithFixedDelay(new WorkProgressConfirm(), 60 * 15, 60 * 60);
		// LoggerFactory.getLogger( ThisApplication.class ).info("OKR registing
		// timer task：定时分析所有员工的工作汇报汇总待办是否正常.");
		// 注册定时器：定时代理，定时分析所有员工的工作汇报汇总待办是否正常
		// 运行时间设置：启动后5分钟执行第一次，之后间隔2小时执行
		timerWithFixedDelay(new WorkReportCollectCreate(), 60 * 10, 60 * 60 * 2);

		// LoggerFactory.getLogger( ThisApplication.class ).info("OKR registing
		// timer task：对中心工作的完成情况以及中心工作的状态进行统计分析。.");
		// 注册定时器：定时代理，对中心工作的完成情况以及中心工作的状态进行统计分析。
		// 运行时间设置：启动后1分钟执行第一次，之后间隔30分钟执行
		timerWithFixedDelay(new St_WorkReportContent(), 60 * 30, 60 * 60 * 6);
		timerWithFixedDelay(new St_WorkReportStatus(), 60 * 20, 60 * 60 * 12);
	}

	public static void destroy() throws Exception {
		Collaboration.stop();
	}

	private static void initAllSystemConfig() {
		try {
			new OkrConfigSystemService().initAllSystemConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Map<String, CacheImportFileStatus> getImportFileStatusMap() {
		return importFileStatusMap;
	}

	public static void setImportFileStatusMap(Map<String, CacheImportFileStatus> importFileStatusMap) {
		ThisApplication.importFileStatusMap = importFileStatusMap;
	}

	/**
	 * 根据用户姓名，获取一个用户登录信息缓存
	 * 
	 * @param name
	 * @return
	 */
	public static CacheImportFileStatus getCacheImportFileStatusElementByKey(String key) {
		if (importFileStatusMap == null) {
			importFileStatusMap = new HashMap<String, CacheImportFileStatus>();
		}
		return importFileStatusMap.get(key);
	}

	public static void setWorkProgressConfirmTaskRunning(Boolean workProgressConfirmTaskRunning) {
		ThisApplication.workProgressConfirmTaskRunning = workProgressConfirmTaskRunning;
	}

	public static Boolean getCenterWorkCountStatisticTaskRunning() {
		return centerWorkCountStatisticTaskRunning;
	}

	public static void setCenterWorkCountStatisticTaskRunning(Boolean centerWorkCountStatisticTaskRunning) {
		ThisApplication.centerWorkCountStatisticTaskRunning = centerWorkCountStatisticTaskRunning;
	}

	public static Boolean getWorkReportCollectCreateTaskRunning() {
		return workReportCollectCreateTaskRunning;
	}

	public static void setWorkReportCollectCreateTaskRunning(Boolean workReportCollectCreateTaskRunning) {
		ThisApplication.workReportCollectCreateTaskRunning = workReportCollectCreateTaskRunning;
	}

	public static Boolean getWorkReportCreateTaskRunning() {
		return workReportCreateTaskRunning;
	}

	public static void setWorkReportCreateTaskRunning(Boolean workReportCreateTaskRunning) {
		ThisApplication.workReportCreateTaskRunning = workReportCreateTaskRunning;
	}

	public static Boolean getWorkReportStatisticTaskRunning() {
		return workReportStatisticTaskRunning;
	}

	public static void setWorkReportStatisticTaskRunning(Boolean workReportStatisticTaskRunning) {
		ThisApplication.workReportStatisticTaskRunning = workReportStatisticTaskRunning;
	}

	public static Boolean getWorkProgressConfirmTaskRunning() {
		return workProgressConfirmTaskRunning;
	}

	public static Boolean getWorkReportSubmitStatisticTaskRunning() {
		return workReportSubmitStatisticTaskRunning;
	}

	public static void setWorkReportSubmitStatisticTaskRunning(Boolean workReportSubmitStatisticTaskRunning) {
		ThisApplication.workReportSubmitStatisticTaskRunning = workReportSubmitStatisticTaskRunning;
	}

}
