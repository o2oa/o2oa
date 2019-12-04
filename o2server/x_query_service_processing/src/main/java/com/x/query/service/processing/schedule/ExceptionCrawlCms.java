package com.x.query.service.processing.schedule;

import com.x.base.core.project.exception.PromptException;
import com.x.cms.core.entity.Document;

class ExceptionCrawlCms extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionCrawlCms(Exception e, Document document) {
		super(e, "内容管理索引失败,id:{}, title:{}.", document.getId(), document.getTitle());
	}
}
