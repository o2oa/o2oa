package com.x.processplatform.assemble.surface.jaxrs.data;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionWorkAccessDenied extends LanguagePromptException {

	private static final long serialVersionUID = -665095222445791960L;

	ExceptionWorkAccessDenied(String person, String title, String workId) {
		super("用户:{}, 没有权限访问文档 title:{} id:{}.", person, title,
				workId);
	}
}
