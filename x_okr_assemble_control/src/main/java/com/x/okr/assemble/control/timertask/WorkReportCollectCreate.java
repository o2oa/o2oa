package com.x.okr.assemble.control.timertask;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.Context;
import com.x.base.core.project.clock.ClockScheduleTask;
import com.x.okr.assemble.control.service.OkrWorkReportTaskCollectService;

/**
 * 定时代理，定时分析所有员工的工作汇报汇总待办是否正常
 * 
 * @author LIYI
 *
 */
public class WorkReportCollectCreate extends ClockScheduleTask {

	private Logger logger = LoggerFactory.getLogger( WorkReportCollectCreate.class );
	private OkrWorkReportTaskCollectService okrWorkReportTaskCollectService = new OkrWorkReportTaskCollectService();
	
	public WorkReportCollectCreate(Context context) {
		super(context);
	}
	
	public void execute() {
		
		//调用服务去核对汇总待办
		try {
			okrWorkReportTaskCollectService.checkAllReportCollectTask();
			logger.info( "Timertask OKR_WorkReportCollectCreate completed and excute success." );
		} catch (Exception e) {
			logger.warn( "系统核对工作汇报待办汇总发生异常。" );
			logger.error(e);
		}
	}
}