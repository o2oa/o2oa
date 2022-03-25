package com.x.program.center.jaxrs.agent;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAgentEval extends LanguagePromptException {

	private static final long serialVersionUID = -8597019540568284908L;

	ExceptionAgentEval(Throwable cause, String message, String id, String name, String alias, String text) {
		super(cause, "代理 id:{}, 名称:{}, 别名:{}, 计算错误: {}, 脚本:{}.", id, name, alias, message, text);
	}
}