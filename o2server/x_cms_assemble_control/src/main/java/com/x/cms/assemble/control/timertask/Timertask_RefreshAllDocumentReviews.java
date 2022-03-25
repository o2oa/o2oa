package com.x.cms.assemble.control.timertask;

import org.quartz.JobExecutionContext;

import com.x.base.core.project.schedule.AbstractJob;
import com.x.cms.assemble.control.service.CmsBatchOperationPersistService;
import com.x.cms.assemble.control.service.DocumentPersistService;

/**
 * 重新计算所有文档的权限信息
 *
 */
public class Timertask_RefreshAllDocumentReviews extends AbstractJob {
	
	private CmsBatchOperationPersistService cmsBatchOperationPersistService;

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		DocumentPersistService documentPersistService = new DocumentPersistService();
		documentPersistService.refreshAllDocumentPermission(false);
	}

}