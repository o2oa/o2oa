package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionWorkCompletedAccessDenied extends LanguagePromptException {

	private static final long serialVersionUID = 9085364457175859374L;

	ExceptionWorkCompletedAccessDenied(String person, String title, String workCompletedId) {
		super("用户:{} 没有权限访问工作 title:{} id:{}.", person, title, workCompletedId);
	}

}
