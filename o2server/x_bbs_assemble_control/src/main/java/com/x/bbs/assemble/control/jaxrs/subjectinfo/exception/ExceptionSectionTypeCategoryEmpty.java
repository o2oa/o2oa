package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSectionTypeCategoryEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSectionTypeCategoryEmpty( String sectionId ) {
		super("版块主题分类typeCategory为空， 无法发表主题.Section:" + sectionId);
	}
}
