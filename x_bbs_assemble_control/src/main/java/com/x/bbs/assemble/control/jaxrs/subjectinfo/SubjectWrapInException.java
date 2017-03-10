package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import com.x.base.core.exception.PromptException;

class SubjectWrapInException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SubjectWrapInException( Throwable e ) {
		super("系统将用户传入的数据转换为主题信息对象时发生异常.", e );
	}
}
