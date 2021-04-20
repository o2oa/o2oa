package com.x.portal.assemble.designer.jaxrs.templatepage;

import com.x.base.core.project.exception.LanguagePromptException;

class TemplatePageNotExistedException extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	TemplatePageNotExistedException(String id) {
		super("指定的页面不存在:{}.", id);
	}
}
