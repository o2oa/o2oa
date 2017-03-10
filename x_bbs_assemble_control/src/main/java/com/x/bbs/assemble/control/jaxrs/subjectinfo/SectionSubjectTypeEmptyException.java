package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import com.x.base.core.exception.PromptException;

class SectionSubjectTypeEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;
	
	SectionSubjectTypeEmptyException( String sectionId ) {
		super("版块信息类别为空， 无法发表主题.Section:" + sectionId);
	}
}
