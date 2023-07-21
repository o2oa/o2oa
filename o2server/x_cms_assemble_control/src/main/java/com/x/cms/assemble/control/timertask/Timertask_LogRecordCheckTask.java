package com.x.cms.assemble.control.timertask;

import org.quartz.JobExecutionContext;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.cms.assemble.control.service.DocumentViewRecordServiceAdv;
import com.x.cms.assemble.control.service.LogService;

/**
 * 定时代理:定期对操作日志以及文档访问日志进行检查 用户操作日志，保留3年，或者最大100万条 文档访问日志，保留3年，或者最大100万条
 * 
 * @author O2LEE
 *
 */
public class Timertask_LogRecordCheckTask extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(Timertask_LogRecordCheckTask.class);
	private LogService logService = new LogService();
	private DocumentViewRecordServiceAdv documentViewRecordServiceAdv = new DocumentViewRecordServiceAdv();
	private Integer stay_yearnum_operationLog = 1; // 保留1年
	private Integer stay_yeanumr_viewRecord = 1; // 保留1年
	private Integer stay_count_operationLog = 100000; // 保留100,000条
	private Integer stay_count_viewRecord = 100000; // 保留100,000条

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try {
			logService.clean(stay_yearnum_operationLog, stay_count_operationLog);
			logger.info("Timertask_LogRecordCheckTask -> clean operation logs excute success. stay_yearnum:" + stay_yearnum_operationLog + ",stay_count:" + stay_count_operationLog);
		} catch (Exception e) {
			logger.warn("Timertask_LogRecordCheckTask -> clean operation logs excute got an exception.");
			logger.error(e);
		}
		try {
			documentViewRecordServiceAdv.clean(stay_yeanumr_viewRecord, stay_count_viewRecord);
			logger.info("Timertask_LogRecordCheckTask -> clean view records excute success. stay_yeanumr:" + stay_yeanumr_viewRecord + ",stay_count:" + stay_count_viewRecord);
		} catch (Exception e) {
			logger.warn("Timertask_LogRecordCheckTask -> clean view records excute got an exception.");
			logger.error(e);
		}
	}

}