package com.x.okr.assemble.control.jaxrs.identity.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionTableNameEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionTableNameEmpty() {
		super("需要替换的数据表名为空，无法继续进行身份替换操作。");
	}
}
