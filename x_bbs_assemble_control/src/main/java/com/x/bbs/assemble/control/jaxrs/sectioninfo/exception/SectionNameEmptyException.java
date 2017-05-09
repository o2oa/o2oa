package com.x.bbs.assemble.control.jaxrs.sectioninfo.exception;

import com.x.base.core.exception.PromptException;

public class SectionNameEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public SectionNameEmptyException() {
		super("版块名称为空， 无法进行查询." );
	}
}
