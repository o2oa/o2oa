package com.x.processplatform.assemble.designer.jaxrs.mapping;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDuplicateTable extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionDuplicateTable() {
		super("待阅映射已存在.");
	}

}
