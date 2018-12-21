package com.x.strategydeploy.assemble.control.measures.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionMeasuresNameEmpty extends PromptException {

	private static final long serialVersionUID = 1061146620723646622L;

	public ExceptionMeasuresNameEmpty() {
		super("战略部署标题，不能为空，且长度不能多于70个字。");
	}
}
