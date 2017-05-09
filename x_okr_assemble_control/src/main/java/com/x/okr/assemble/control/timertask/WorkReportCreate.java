package com.x.okr.assemble.control.timertask;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.Context;
import com.x.base.core.project.clock.ClockScheduleTask;
import com.x.okr.assemble.control.service.ExcuteWorkReportCreateService;

/**
 * 定时代理，定时对需要汇报的工作发起工作汇报拟稿的待办
 * 
 * @author LIYI
 *
 */
public class WorkReportCreate extends ClockScheduleTask {

	private Logger logger = LoggerFactory.getLogger( ExcuteWorkReportCreateService.class );
	
	public WorkReportCreate(Context context) {
		super(context);
	}
	
	public void execute() {
		try {
			new ExcuteWorkReportCreateService().execute();
			logger.info("Timertask WorkReportCreate completed and excute success.");
		} catch (Exception e) {
			logger.error(e);
		}
		
	}
}