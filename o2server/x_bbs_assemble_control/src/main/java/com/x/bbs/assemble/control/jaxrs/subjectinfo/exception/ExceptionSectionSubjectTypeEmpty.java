package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSectionSubjectTypeEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;
	
	public ExceptionSectionSubjectTypeEmpty( String sectionId ) {
		super("版块信息类别为空， 无法发表主题.Section:" + sectionId);
	}
}
