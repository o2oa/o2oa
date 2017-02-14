package com.x.okr.assemble.control.timertask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.okr.assemble.control.ThisApplication;
import com.x.okr.assemble.control.service.OkrWorkReportTaskCollectService;

/**
 * 定时代理，定时分析所有员工的工作汇报汇总待办是否正常
 * 
 * @author LIYI
 *
 */
public class WorkReportCollectCreate implements Runnable {

	private Logger logger = LoggerFactory.getLogger( WorkReportCollectCreate.class );
	private OkrWorkReportTaskCollectService okrWorkReportTaskCollectService = new OkrWorkReportTaskCollectService();
	
	public void run() {
		if( ThisApplication.getWorkReportCollectCreateTaskRunning() ){
			logger.info( "Timertask[WorkReportTaskCollectCreate] service is running, wait for next time......" );
			return;
		}	
		ThisApplication.setWorkReportCollectCreateTaskRunning( true );
		
		//调用服务去核对汇总待办
		try {
			okrWorkReportTaskCollectService.checkAllReportCollectTask();
		} catch (Exception e) {
			logger.error( "系统核对工作汇报待办汇总发生异常。", e );
		}		
		
		ThisApplication.setWorkReportCollectCreateTaskRunning( false );
		logger.debug( "Timertask[WorkReportTaskCollectCreate] completed and excute success." );
	}
}