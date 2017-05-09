package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.exception.PromptException;

public class SectionTypeCategoryEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public SectionTypeCategoryEmptyException( String sectionId ) {
		super("版块主题分类typeCategory为空， 无法发表主题.Section:" + sectionId);
	}
}
