package com.x.program.center.jaxrs.zhengwudingding;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNotPullSync extends LanguagePromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionNotPullSync() {
		super("没有启用从政务钉钉的拉入同步.");
	}
}
