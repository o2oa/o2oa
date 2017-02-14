package com.x.okr.assemble.control;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;

import com.x.base.core.application.task.ReportTask;
import com.x.base.core.project.AbstractThisApplication;
import com.x.base.core.project.server.Config;
import com.x.collaboration.core.message.Collaboration;
import com.x.okr.assemble.control.service.OkrConfigSystemService;
import com.x.okr.assemble.control.servlet.workimport.CacheImportFileStatus;
import com.x.okr.assemble.control.timertask.CenterWorkCountStatistic;
import com.x.okr.assemble.control.timertask.WorkProgressConfirm;
import com.x.okr.assemble.control.timertask.WorkReportCollectCreate;
import com.x.okr.assemble.control.timertask.WorkReportCreate;
import com.x.okr.assemble.control.timertask.WorkReportStatistic;

public class ThisApplication extends AbstractThisApplication {

	
	public static Map<String, CacheImportFileStatus> importFileStatusMap = new HashMap<String, CacheImportFileStatus>();
	private static Boolean centerWorkCountStatisticTaskRunning = false;
	private static Boolean workProgressConfirmTaskRunning = false;
	private static Boolean workReportCollectCreateTaskRunning = false;
	private static Boolean workReportCreateTaskRunning = false;
	private static Boolean workReportStatisticTaskRunning = false;

	public static void init() throws Exception {
		/* 启动报告任务 */
		scheduleWithFixedDelay(new ReportTask(), 1, 20);

		initDatasFromCenters();

		initStoragesFromCenters();

		Config.workTimeConfig().initWorkTime();

		Collaboration.start();
		
		initAllSystemConfig();

		initAllTimerTask();
		
	}

	private static void initAllTimerTask() {
		//LoggerFactory.getLogger( ThisApplication.class ).info("OKR registing timer task：对中心工作的完成情况以及中心工作的状态进行统计分析。.");
		// 注册定时器：定时代理，对中心工作的完成情况以及中心工作的状态进行统计分析。
		// 运行时间设置：启动后1分钟执行第一次，之后间隔30分钟执行
		scheduleWithFixedDelay( new WorkReportStatistic(), 60 * 1, 60 * 30 );
		//LoggerFactory.getLogger( ThisApplication.class ).info("OKR registing timer task：对中心工作的工作总数进行统计分析.");
		// 注册定时器：定时代理，对中心工作的工作总数进行统计分析。
		// 运行时间设置：启动后1分钟执行第一次，之后间隔30分钟执行
		scheduleWithFixedDelay( new CenterWorkCountStatistic(), 60 * 2, 60 * 30 );
		//LoggerFactory.getLogger( ThisApplication.class ).info("OKR registing timer task：定时对需要汇报的工作发起工作汇报拟稿的待办.");
		// 注册定时器：定时代理，定时对需要汇报的工作发起工作汇报拟稿的待办
		// 运行时间设置：启动后1分钟执行第一次，之后间隔1分钟执行
		scheduleWithFixedDelay( new WorkReportCreate(), 60 * 1, 60 * 1 );
		//LoggerFactory.getLogger( ThisApplication.class ).info("OKR registing timer task：定时分析所有未完成工作的进度情况.");
		// 注册定时器：定时代理，定时分析所有未完成工作的进度情况
		// 运行时间设置：启动后10分钟执行第一次，之后间隔6小时执行
		scheduleWithFixedDelay( new WorkProgressConfirm(), 60 * 10, 60 * 60  );
		//LoggerFactory.getLogger( ThisApplication.class ).info("OKR registing timer task：定时分析所有员工的工作汇报汇总待办是否正常.");
		// 注册定时器：定时代理，定时分析所有员工的工作汇报汇总待办是否正常
		// 运行时间设置：启动后5分钟执行第一次，之后间隔2小时执行
		scheduleWithFixedDelay( new WorkReportCollectCreate(), 60 * 5, 60 * 60 * 2 );
	}

	public static void destroy() throws Exception {
		Collaboration.stop();
	}

	private static void initAllSystemConfig() {
		//LoggerFactory.getLogger( ThisApplication.class ).info("OKR checking all system config......");
		new OkrConfigSystemService().initAllSystemConfig();
		//LoggerFactory.getLogger( ThisApplication.class ).info("OKR system config check completed.");
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
	
	
}
