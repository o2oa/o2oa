package com.x.program.center.jaxrs.andfx;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNotPullSync extends LanguagePromptException {

	private static final long serialVersionUID = -5403519407965886848L;

	ExceptionNotPullSync() {
		super("没有启用从移动办公的拉入同步.");
	}
}
