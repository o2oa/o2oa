package com.x.strategydeploy.assemble.control.keywork.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionKeyWorkInfoTitleEmpty extends PromptException {
	private static final long serialVersionUID = -8841856984188869165L;

	public ExceptionKeyWorkInfoTitleEmpty() {
		super("重点工作标题，不能为空，且长度不能多于70个字。");
	}
}
