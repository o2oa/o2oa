package com.x.program.center.schedule;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAgentEval extends LanguagePromptException {

	private static final long serialVersionUID = -8597019540568284908L;

	ExceptionAgentEval(Throwable cause, String message, String id, String name, String alias, String text) {
		super(cause, "代理的id:{},名字:{},别名:{},eval错误:{},脚本:{}.", id, name, alias, message, text);
	}
}