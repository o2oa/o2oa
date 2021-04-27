package com.x.processplatform.assemble.surface.jaxrs.data;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionJobAccessDenied extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionJobAccessDenied(String person, String str) {
		super("用户:{} 没有权限访问文档 job: {}.", person, str);
	}

}
