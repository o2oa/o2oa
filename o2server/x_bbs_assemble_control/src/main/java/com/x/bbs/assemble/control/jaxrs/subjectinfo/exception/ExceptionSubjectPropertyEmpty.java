package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSubjectPropertyEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSubjectPropertyEmpty( String name ) {
		super("传入的数据不完整，无法保存主题信息.属性:" + name );
	}
}
