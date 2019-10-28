package com.x.cms.assemble.control.timertask;

import org.quartz.JobExecutionContext;

import com.x.base.core.project.schedule.AbstractJob;
import com.x.cms.assemble.control.service.CmsBatchOperationPersistService;

/**
 * 检查文档是否已经review过了
 *
 */
public class Timertask_CheckDocumentReviewStatus extends AbstractJob {
	
	private CmsBatchOperationPersistService cmsBatchOperationPersistService;

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		cmsBatchOperationPersistService = new CmsBatchOperationPersistService();
		cmsBatchOperationPersistService.checkDocumentReviewStatus();
	}

}