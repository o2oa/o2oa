package com.x.okr.assemble.control.timertask;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.Context;
import com.x.base.core.project.clock.ClockScheduleTask;
import com.x.okr.assemble.control.service.OkrSystemIdentityOperatorService;

/**
 * 定时代理，定时对系统中涉及到的身份信息进行检查
 * 
 * @author LIYI
 *
 */
public class ErrorIdentityCheckTask extends ClockScheduleTask {
	private Logger logger = LoggerFactory.getLogger(ErrorIdentityCheckTask.class);
	private OkrSystemIdentityOperatorService okrSystemIdentityOperatorService = new OkrSystemIdentityOperatorService();

	public ErrorIdentityCheckTask(Context context) {
		super(context);
	}
	
	public void execute() {
		try {
			okrSystemIdentityOperatorService.checkAllAbnormalIdentityInSystem();
			logger.info("Timertask_ErrorIdentityCheckTask completed and excute success.");
		} catch (Exception e) {
			logger.warn("Timertask_ErrorIdentityCheckTask excute got an exception.");
			logger.error(e);
		}

	}
}