package com.x.processplatform.service.processing.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionWorkNotMatchWithWorkLog extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionWorkNotMatchWithWorkLog(String workTitle, String workId, String workJob, String workLogId,
			String workLogJob) {
		super("work title: {} id: {} job: {}, not match job with workLog: id: {}, job: {}.");
	}

}
