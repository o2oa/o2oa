package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import com.x.base.core.exception.PromptException;

class AdminSuperviseInfoEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AdminSuperviseInfoEmptyException() {
		super("管理员督办信息为空，无法继续保存汇报信息。" );
	}
}
