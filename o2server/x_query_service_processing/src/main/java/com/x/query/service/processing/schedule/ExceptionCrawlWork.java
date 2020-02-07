package com.x.query.service.processing.schedule;

import com.x.base.core.project.exception.PromptException;

class ExceptionCrawlWork extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionCrawlWork(Exception e, String reference) {
		super(e, "工作索引失败:{}.", reference);
	}
}
