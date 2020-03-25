package com.x.bbs.assemble.control.jaxrs.sectioninfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSectionNameEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSectionNameEmpty() {
		super("版块名称为空， 无法进行查询." );
	}
}
