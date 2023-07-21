package com.x.processplatform.service.processing.jaxrs.task;

import com.x.base.core.project.exception.PromptException;

class ExceptionPassExpiredRoute extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionPassExpiredRoute(String id, String title, String sequence) {
		super("无法找到待办过期默认的路由, id:{}, title:{}, sequence:{}.", id, title, sequence);
	}
}
