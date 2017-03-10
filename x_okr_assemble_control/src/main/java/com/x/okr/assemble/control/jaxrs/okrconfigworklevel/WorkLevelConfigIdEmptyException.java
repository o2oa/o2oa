package com.x.okr.assemble.control.jaxrs.okrconfigworklevel;

import com.x.base.core.exception.PromptException;

class WorkLevelConfigIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkLevelConfigIdEmptyException() {
		super("id为空，无法进行查询操作。");
	}
}
