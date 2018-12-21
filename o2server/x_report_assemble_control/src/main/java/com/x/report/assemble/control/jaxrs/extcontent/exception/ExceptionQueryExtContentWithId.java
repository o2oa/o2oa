package com.x.report.assemble.control.jaxrs.extcontent.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionQueryExtContentWithId extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionQueryExtContentWithId( Throwable e, String id ) {
		super("系统根据ID查询扩展信息时发生异常.id:" + id , e );
	}
}
