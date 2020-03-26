package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionSplitNodeNotExist extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionSplitNodeNotExist() {
		super("无法找到拆分节点.");
	}
}
