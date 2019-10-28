package com.x.cms.assemble.control.timertask;

import org.quartz.JobExecutionContext;

import com.x.base.core.project.schedule.AbstractJob;
import com.x.cms.assemble.control.service.CmsBatchOperationPersistService;

/**
 * 检查是否有未执行的批处理任务
 *
 */
public class Timertask_InitOperationRunning extends AbstractJob {
	
	private CmsBatchOperationPersistService cmsBatchOperationPersistService;

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		cmsBatchOperationPersistService = new CmsBatchOperationPersistService();
		cmsBatchOperationPersistService.initOperationRunning();
	}

}