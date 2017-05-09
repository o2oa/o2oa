package com.x.bbs.assemble.control.jaxrs.sectioninfo.exception;

import com.x.base.core.exception.PromptException;

public class SectionIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public SectionIdEmptyException() {
		super("版块ID为空， 无法进行查询." );
	}
}
