package com.x.report.assemble.control.jaxrs.workinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkInfoNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkInfoNotExists(String id ) {
		super("指定的工作信息不存在.reportId:" + id );
	}
}
