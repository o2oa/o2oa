package com.x.processplatform.assemble.surface.jaxrs.record;

import com.x.base.core.project.exception.PromptException;

class ExceptionPersonEmpty extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionPersonEmpty() {
		super("没有指定用户.");
	}

}
