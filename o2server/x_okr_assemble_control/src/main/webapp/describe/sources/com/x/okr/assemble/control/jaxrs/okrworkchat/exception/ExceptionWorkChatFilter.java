package com.x.okr.assemble.control.jaxrs.okrworkchat.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkChatFilter extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkChatFilter( Throwable e ) {
		super("系统条件查询工作交流信息记录列表时发生异常。", e );
	}
}
