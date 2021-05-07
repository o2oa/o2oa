package com.x.processplatform.assemble.surface.jaxrs.data;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionWorkCompletedAccessDenied extends LanguagePromptException {

	private static final long serialVersionUID = -665095222445791960L;

	ExceptionWorkCompletedAccessDenied(String person, String title, String workCompletedId) {
		super("用户:{}, 没有权限访问文档 title:{} id:{}", person, title,
				workCompletedId);
	}
}
