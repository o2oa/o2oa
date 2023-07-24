package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSubjecttypeEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSubjecttypeEmpty() {
		super("主题类别为空， 无法进行查询." );
	}
}
