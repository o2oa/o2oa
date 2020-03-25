package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.PromptException;

class ExceptionFailureLocked extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionFailureLocked(String name, Integer minutes) {
		super("用户:{} 已经被锁定, 锁定时间 {} 分钟.", name, minutes);
	}
}
