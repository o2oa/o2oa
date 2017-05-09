package com.x.bbs.assemble.control.jaxrs.sectioninfo.exception;

import com.x.base.core.exception.PromptException;

public class SectionInfoProcessException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public SectionInfoProcessException( Throwable e, String message ) {
		super( message, e );
	}
}
