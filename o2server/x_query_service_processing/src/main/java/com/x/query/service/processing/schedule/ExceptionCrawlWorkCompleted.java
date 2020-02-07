package com.x.query.service.processing.schedule;

import com.x.base.core.project.exception.PromptException;
import com.x.processplatform.core.entity.content.WorkCompleted;

class ExceptionCrawlWorkCompleted extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionCrawlWorkCompleted(Exception e, String reference) {
		super(e, "已完成工作索引失败:{}.", reference);
	}
}
