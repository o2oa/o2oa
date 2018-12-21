package com.x.report.assemble.control.jaxrs.workprog.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionDeleteWorkProg extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionDeleteWorkProg( Throwable e, String id ) {
		super("系统根据工作完成情况ID删除工作完成情况信息时发生异常.id:" + id , e );
	}
}
