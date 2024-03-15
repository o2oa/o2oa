package com.x.processplatform.assemble.surface.schedule;

import com.x.base.core.project.exception.PromptException;

class ExceptionPassExpired extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionPassExpired(Exception e, String id, String title) {
		super(e, "超时工作默认路由流转失败, id:{}, title:{}.", id, title);
	}

}
