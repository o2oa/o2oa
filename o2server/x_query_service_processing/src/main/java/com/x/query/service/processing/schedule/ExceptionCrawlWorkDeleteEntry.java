package com.x.query.service.processing.schedule;

import com.x.base.core.project.exception.PromptException;
import com.x.cms.core.entity.Document;

class ExceptionCrawlWorkDeleteEntry extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionCrawlWorkDeleteEntry(Exception e, String reference) {
		super(e, "根据reference删除Work的索引Entry失败, reference:{}.", reference);
	}
}
