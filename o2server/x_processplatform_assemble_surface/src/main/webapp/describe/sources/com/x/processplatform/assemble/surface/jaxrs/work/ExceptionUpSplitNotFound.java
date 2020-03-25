package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionUpSplitNotFound extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionUpSplitNotFound(String id) {
		super("无法找到工作:{}, 的上级拆分活动.", id);
	}

}
