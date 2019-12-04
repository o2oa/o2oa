package com.x.query.service.processing.schedule;

import com.x.base.core.project.exception.PromptException;
import com.x.processplatform.core.entity.content.Work;

class ExceptionCrawlWork extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionCrawlWork(Exception e, Work work) {
		super(e, "工作索引失败,id:{}, title:{}.", work.getId(), work.getTitle());
	}
}
