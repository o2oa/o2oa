package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSubjectWrapOut extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSubjectWrapOut( Throwable e ) {
		super("将查询结果转换为可以输出的数据信息时发生异常.", e );
	}
}
