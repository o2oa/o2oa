package com.x.bbs.assemble.control.jaxrs.sectioninfo;

import com.x.base.core.exception.PromptException;

class SectionNameEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SectionNameEmptyException() {
		super("版块名称为空， 无法进行查询." );
	}
}
