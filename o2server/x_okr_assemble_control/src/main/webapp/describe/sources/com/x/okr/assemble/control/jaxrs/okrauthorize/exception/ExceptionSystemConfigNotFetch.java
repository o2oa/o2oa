package com.x.okr.assemble.control.jaxrs.okrauthorize.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSystemConfigNotFetch extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSystemConfigNotFetch( String code ) {
		super("系统异常，系统参数不存在:" + code );
	}
}
