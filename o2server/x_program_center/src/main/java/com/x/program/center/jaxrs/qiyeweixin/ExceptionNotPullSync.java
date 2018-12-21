package com.x.program.center.jaxrs.qiyeweixin;

import com.x.base.core.project.exception.PromptException;

class ExceptionNotPullSync extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionNotPullSync() {
		super("没有启用从企业微信的拉入同步.");
	}
}
