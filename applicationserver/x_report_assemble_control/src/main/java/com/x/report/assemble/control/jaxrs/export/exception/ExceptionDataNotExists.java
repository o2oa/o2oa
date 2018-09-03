package com.x.report.assemble.control.jaxrs.export.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionDataNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionDataNotExists( String year ) {
		super("指定的数据信息不存在.year:" + year );
	}
}
