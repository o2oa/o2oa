package com.x.report.assemble.control.jaxrs.workprog.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkProgNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkProgNotExists( String id ) {
		super("指定的工作完成情况信息不存在.reportId:" + id );
	}
}
