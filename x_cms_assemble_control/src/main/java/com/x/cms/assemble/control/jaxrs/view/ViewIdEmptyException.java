package com.x.cms.assemble.control.jaxrs.view;

import com.x.base.core.exception.PromptException;

class ViewIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ViewIdEmptyException( String message ) {
		super( message );
	}
	
	public ViewIdEmptyException() {
		super( "列表ID为空，无法继续进行列表数据查询。" );
	}
}
