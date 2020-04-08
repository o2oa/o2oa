package com.x.okr.assemble.control;

import java.util.HashMap;
import java.util.Map;

import com.x.base.core.project.Context;
import com.x.okr.assemble.control.jaxrs.queue.QueueWorkDynamicRecord;
import com.x.okr.assemble.control.jaxrs.workimport.CacheImportFileStatus;
import com.x.okr.assemble.control.schedule.ErrorIdentityCheckTask;
import com.x.okr.assemble.control.schedule.St_CenterWorkCount;
import com.x.okr.assemble.control.schedule.St_WorkReportContent;
import com.x.okr.assemble.control.schedule.St_WorkReportStatus;
import com.x.okr.assemble.control.schedule.WorkProgressConfirm;
import com.x.okr.assemble.control.schedule.WorkReportCollectCreate;
import com.x.okr.assemble.control.schedule.WorkReportCreate;
import com.x.okr.assemble.control.service.OkrConfigSystemService;

public class ThisApplication {

	protected static Context context;

	public static Map<String, CacheImportFileStatus> importFileStatusMap = new HashMap<String, CacheImportFileStatus>();

	public static final String OKRMANAGER = "OKRManager";

	public static QueueWorkDynamicRecord queueWorkDynamicRecord;

	public static Context context() {
		return context;
	}

	public static void init() {
		try {

			queueWorkDynamicRecord = new QueueWorkDynamicRecord();
			context().startQueue(queueWorkDynamicRecord);

			new OkrConfigSystemService().initAllSystemConfig();

			// 每天凌晨2点执行一次
			context.schedule(St_WorkReportContent.class, "0 0 2 * * ?");
			// 每天凌晨2点30执行一次
			context.schedule(St_WorkReportStatus.class, "0 30 2 * * ?");
			// 每天5点至20点间，每10分钟执行一次
			context.schedule(St_CenterWorkCount.class, "0 0/10 5-20 * * ?");
			// 每天7点至17点间，每10分钟执行一次
			context.schedule(WorkReportCreate.class, "0 0/10 7-17 * * ?");
			// 每天7点至17点间，每小时执行一次
			context.schedule(WorkProgressConfirm.class, "0 0 7-17/1 * * ?");
			// 每天7点至17点间，每10分钟执行一次
			context.schedule(WorkReportCollectCreate.class, "0 0/10 7-17 * * ?");
			// 每天凌晨2点执行一次
			context.schedule(ErrorIdentityCheckTask.class, "0 0 2 * * ?");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getOkrUserInfoCacheKey(String personName) {
		return "OkrUserInfo.withPerson." + personName;
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
}
