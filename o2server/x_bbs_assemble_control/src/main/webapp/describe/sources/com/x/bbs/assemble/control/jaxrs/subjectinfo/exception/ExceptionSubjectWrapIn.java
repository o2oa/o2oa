package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSubjectWrapIn extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSubjectWrapIn( Throwable e ) {
		super("系统将用户传入的数据转换为主题信息对象时发生异常.", e );
	}
}
