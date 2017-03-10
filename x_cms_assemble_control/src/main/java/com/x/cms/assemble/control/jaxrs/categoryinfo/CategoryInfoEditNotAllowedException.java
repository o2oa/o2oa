package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.exception.PromptException;

class CategoryInfoEditNotAllowedException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CategoryInfoEditNotAllowedException( String message ) {
		super( message );
	}
}
