package com.x.strategydeploy.assemble.control.strategy;

import com.x.base.core.project.exception.PromptException;

class ExceptionResultNotFound extends PromptException {

	private static final long serialVersionUID = -6487092314061985146L;

	ExceptionResultNotFound(String flag) {
		super("找不到导入结果:{}.", flag);
	}
}
