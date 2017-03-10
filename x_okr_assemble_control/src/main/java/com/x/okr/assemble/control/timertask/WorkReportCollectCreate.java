package com.x.okr.assemble.control.timertask;

import java.util.TimerTask;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.ThisApplication;
import com.x.okr.assemble.control.service.OkrWorkReportTaskCollectService;

/**
 * 定时代理，定时分析所有员工的工作汇报汇总待办是否正常
 * 
 * @author LIYI
 *
 */
public class WorkReportCollectCreate extends TimerTask {

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
			logger.warn( "系统核对工作汇报待办汇总发生异常。" );
			logger.error(e);
		}		
		
		ThisApplication.setWorkReportCollectCreateTaskRunning( false );
		logger.debug( "Timertask[WorkReportTaskCollectCreate] completed and excute success." );
	}
}