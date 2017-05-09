package com.x.cms.assemble.control.jaxrs.appinfo.exception;

import com.x.base.core.exception.PromptException;

public class AppInfoCanNotDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AppInfoCanNotDeleteException( String message ) {
		super( message  );
	}
}
