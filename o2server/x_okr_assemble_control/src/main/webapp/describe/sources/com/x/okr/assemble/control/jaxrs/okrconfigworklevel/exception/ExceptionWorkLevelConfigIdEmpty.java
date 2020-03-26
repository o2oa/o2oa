package com.x.okr.assemble.control.jaxrs.okrconfigworklevel.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkLevelConfigIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkLevelConfigIdEmpty() {
		super("id为空，无法进行查询操作。");
	}
}
