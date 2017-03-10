package com.x.cms.assemble.control.jaxrs.view;

import com.x.base.core.exception.PromptException;

class CategoryIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public CategoryIdEmptyException( String message ) {
		super( message );
	}
	
	public CategoryIdEmptyException() {
		super( "视图所在的分类信息ID为空，无法继续进行查询。" );
	}
}
