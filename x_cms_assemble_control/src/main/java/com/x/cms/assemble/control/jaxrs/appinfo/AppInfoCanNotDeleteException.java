package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.exception.PromptException;

class AppInfoCanNotDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppInfoCanNotDeleteException( String message ) {
		super( message  );
	}
}
