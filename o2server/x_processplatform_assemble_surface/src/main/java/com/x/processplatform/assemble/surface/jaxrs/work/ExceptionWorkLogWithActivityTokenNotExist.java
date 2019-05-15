package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionWorkLogWithActivityTokenNotExist extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionWorkLogWithActivityTokenNotExist(String str) {
		super("无法根据活动令牌获取工作日志:{}.", str);
	}
}
