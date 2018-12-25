package com.x.cms.assemble.control.jaxrs.view.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionCategoryIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionCategoryIdEmpty( String message ) {
		super( message );
	}
	
	public ExceptionCategoryIdEmpty() {
		super( "视图所在的分类信息ID为空，无法继续进行查询。" );
	}
}
