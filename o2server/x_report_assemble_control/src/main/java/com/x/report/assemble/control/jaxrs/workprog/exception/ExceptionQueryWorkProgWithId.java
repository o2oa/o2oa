package com.x.report.assemble.control.jaxrs.workprog.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionQueryWorkProgWithId extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionQueryWorkProgWithId( Throwable e, String id ) {
		super("系统根据ID查询汇报工作完成情况信息列表时发生异常.progId:" + id , e );
	}
}
