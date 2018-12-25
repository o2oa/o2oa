package com.x.strategydeploy.assemble.control.configsys.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionConfigSysNameEmpty extends PromptException {

	private static final long serialVersionUID = 1061146620723646622L;

	public ExceptionConfigSysNameEmpty() {
		super("配置标题，不能为空，且长度不能多于70个字。");
	}
}
