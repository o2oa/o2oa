package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkQueryById extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkQueryById( Throwable e, String id ) {
		super("查询指定ID的具体工作信息时发生异常。ID：" + id, e );
	}
}
