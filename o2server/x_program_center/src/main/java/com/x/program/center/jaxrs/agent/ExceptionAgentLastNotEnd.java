package com.x.program.center.jaxrs.agent;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAgentLastNotEnd extends LanguagePromptException {

	private static final long serialVersionUID = -7954335762204386602L;

	ExceptionAgentLastNotEnd(String id, String name, String alias, String date) {
		super("代理 id:{}, name:{}, alias:{}, 上次运行尚未结束,上次开始时间:{}.", id, name, alias, date);
	}
}