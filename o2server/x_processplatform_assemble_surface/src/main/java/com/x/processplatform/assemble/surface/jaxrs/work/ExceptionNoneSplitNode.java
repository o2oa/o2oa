package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNoneSplitNode extends LanguagePromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionNoneSplitNode(String workId) {
		super("无法找到工作经过的拆分节点:{}.", workId);
	}

}
