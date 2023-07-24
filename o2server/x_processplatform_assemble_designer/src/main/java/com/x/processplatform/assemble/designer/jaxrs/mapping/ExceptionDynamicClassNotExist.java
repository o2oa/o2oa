package com.x.processplatform.assemble.designer.jaxrs.mapping;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDynamicClassNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionDynamicClassNotExist(String className) {
		super("指定的类 {} 不存在.", className);
	}

}
