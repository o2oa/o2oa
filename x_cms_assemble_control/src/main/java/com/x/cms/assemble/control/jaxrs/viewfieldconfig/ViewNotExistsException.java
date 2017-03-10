package com.x.cms.assemble.control.jaxrs.viewfieldconfig;

import com.x.base.core.exception.PromptException;

class ViewNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ViewNotExistsException( String id ) {
		super( "列表视图信息不存在。ID:" + id );
	}
}
