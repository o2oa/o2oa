package com.x.okr.assemble.control.jaxrs.workimport.exception;

import com.x.base.core.project.exception.PromptException;

public class NoDataException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public NoDataException() {
		super("未获取到任何需要保存的数据.");
	}
}
