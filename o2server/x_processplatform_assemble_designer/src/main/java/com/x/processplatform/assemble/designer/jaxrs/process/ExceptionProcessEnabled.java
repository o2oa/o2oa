package com.x.processplatform.assemble.designer.jaxrs.process;

import com.x.base.core.project.exception.PromptException;

class ExceptionProcessEnabled extends PromptException {

	private static final long serialVersionUID = 3768001625178470667L;

	ExceptionProcessEnabled(String flag) {
		super("process: {} 存在多个版本，且当前版本处于启用状态，请启用其他版本后再删除.", flag);
	}
}
