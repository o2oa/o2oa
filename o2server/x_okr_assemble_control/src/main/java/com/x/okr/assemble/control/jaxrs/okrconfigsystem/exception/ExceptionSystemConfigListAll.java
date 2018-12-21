package com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSystemConfigListAll extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSystemConfigListAll( Throwable e ) {
		super("查询所有的OKR系统配置时发生异常。", e );
	}
}
