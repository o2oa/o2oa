package com.x.cms.assemble.control.jaxrs.view.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionViewIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionViewIdEmpty( String message ) {
		super( message );
	}
	
	public ExceptionViewIdEmpty() {
		super( "列表ID为空，无法继续进行列表数据查询。" );
	}
}
