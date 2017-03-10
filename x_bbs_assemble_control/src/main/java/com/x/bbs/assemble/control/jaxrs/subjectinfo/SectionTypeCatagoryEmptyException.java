package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import com.x.base.core.exception.PromptException;

class SectionTypeCatagoryEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SectionTypeCatagoryEmptyException( String sectionId ) {
		super("版块主题分类typeCatagory为空， 无法发表主题.Section:" + sectionId);
	}
}
