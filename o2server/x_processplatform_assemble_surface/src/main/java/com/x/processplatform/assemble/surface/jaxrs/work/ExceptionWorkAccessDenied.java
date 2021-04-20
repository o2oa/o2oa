package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionWorkAccessDenied extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionWorkAccessDenied(String person, String title, String id) {
		super("用户:{} 没有权限访问工作 title :{}, id :{}.", person, title, id);
	}

}
